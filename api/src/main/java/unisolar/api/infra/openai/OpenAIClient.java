package unisolar.api.infra.openai;

import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.messages.Message;
import com.theokanning.openai.messages.MessageRequest;
import com.theokanning.openai.runs.RunCreateRequest;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.threads.ThreadRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * OpenAIClient is a component responsible for interacting with the OpenAI API.
 * It manages sending chat completion requests, retrieving chat history, and handling threads.
 * The client maintains an API key and an assistant ID, and supports communication with OpenAI's models via the OpenAiService.
 */
@Component
public class OpenAIClient {

    private final String apiKey;
    private final String assistantId;
    private String threadId;
    private final OpenAiService service;

    /**
     * Constructs an OpenAIClient instance with the provided API key and assistant ID.
     *
     * @param apiKey The API key for authentication with the OpenAI API.
     * @param assistantId The assistant ID used for interacting with a specific assistant model.
     */
    public OpenAIClient(@Value("${app.openai.api.key}") String apiKey, @Value("${app.openai.assistant.id}") String assistantId) {
        this.apiKey = apiKey;
        this.service = new OpenAiService(apiKey, Duration.ofSeconds(60));
        this.assistantId = assistantId;
    }

    /**
     * Sends a chat completion request to OpenAI, either creating a new thread or continuing an existing one.
     * It processes the user's prompt and returns the assistant's response.
     *
     * @param data The data containing system and user prompts to send to the OpenAI API.
     * @return The response from the assistant as a String.
     */
    public String sendChatCompletionRequest(ChatCompletionRequestData data) {
        var messageRequest = MessageRequest
                .builder()
                .role(ChatMessageRole.USER.value())
                .content(data.userPrompt())
                .build();

        if (this.threadId == null) {
            var threadRequest = ThreadRequest
                    .builder()
                    .messages(Arrays.asList(messageRequest))
                    .build();

            var thread = service.createThread(threadRequest);
            this.threadId = thread.getId();
        } else {
            service.createMessage(this.threadId, messageRequest);
        }

        // Create run request with the assistant ID and model
        var runRequest = RunCreateRequest
                .builder()
                .assistantId(assistantId)
                .model("gpt-3.5-turbo") // Set the desired model
                .build();
        var run = service.createRun(threadId, runRequest);

        var isCompleted = false;
        var needsFunctionCall = false;
        try {
            while (!isCompleted && !needsFunctionCall) {
                Thread.sleep(1000 * 10);
                run = service.retrieveRun(threadId, run.getId());
                isCompleted = run.getStatus().equalsIgnoreCase("completed");
                needsFunctionCall = run.getRequiredAction() != null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Retrieve and return the latest response from the assistant
        var messages = service.listMessages(threadId);
        return messages
                .getData()
                .stream()
                .sorted(Comparator.comparingInt(Message::getCreatedAt).reversed())
                .findFirst().get().getContent().get(0).getText().getValue()
                .replaceAll("\\\u3010.*?\\\u3011", "");
    }

    /**
     * Loads the chat history for the current thread.
     * If a thread exists, it returns all messages in the thread, sorted by timestamp.
     *
     * @return A list of message strings representing the chat history.
     */
    public List<String> loadChatHistory() {
        var messages = new ArrayList<String>();

        if (this.threadId != null) {
            messages.addAll(
                    service
                            .listMessages(this.threadId)
                            .getData()
                            .stream()
                            .sorted(Comparator.comparingInt(Message::getCreatedAt))
                            .map(m -> m.getContent().get(0).getText().getValue())
                            .collect(Collectors.toList())
            );
        }

        return messages;
    }

    /**
     * Deletes the current thread, if it exists, and clears the thread ID.
     */
    public void deleteThread() {
        if (this.threadId != null) {
            service.deleteThread(this.threadId);
            this.threadId = null;
        }
    }
}
