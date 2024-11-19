package unisolar.api.infra.openai;

/**
 * ChatCompletionRequestData is a record that represents the data sent to an API for a chat completion request.
 * It contains the system prompt, which provides context or instructions for the AI system,
 * and the user prompt, which contains the message or query input by the user.
 *
 * Attributes:
 * - systemPrompt (String): A string that sets the context or provides instructions to the AI system.
 * - userPrompt (String): A string representing the message or query provided by the user to the AI system.
 */
public record ChatCompletionRequestData(String systemPrompt, String userPrompt) {}
