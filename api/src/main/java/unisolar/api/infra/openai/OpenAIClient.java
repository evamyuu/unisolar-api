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

@Component
public class OpenAIClient {

    private final String apiKey;
    private final String assistantId;
    private String threadId;
    private final OpenAiService service;

    public OpenAIClient(@Value("${app.openai.api.key}") String apiKey, @Value("${app.openai.assistant.id}") String assistantId) {
        this.apiKey = apiKey;
        this.service = new OpenAiService(apiKey, Duration.ofSeconds(60));
        this.assistantId = assistantId;
    }

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

        // Modificar aqui para incluir o modelo
        var runRequest = RunCreateRequest
                .builder()
                .assistantId(assistantId)
                .model("gpt-3.5-turbo") // Defina o modelo desejado
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

        var messages = service.listMessages(threadId);
        return messages
                .getData()
                .stream()
                .sorted(Comparator.comparingInt(Message::getCreatedAt).reversed())
                .findFirst().get().getContent().get(0).getText().getValue()
                .replaceAll("\\\u3010.*?\\\u3011", "");
    }

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

    public void deleteThread() {
        if (this.threadId != null) {
            service.deleteThread(this.threadId);
            this.threadId = null;
        }
    }
}