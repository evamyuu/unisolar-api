package unisolar.api.domain.dto.openai;

/**
 * Data Transfer Object (DTO) representing a question for the OpenAI API.
 * Used for transferring question data between different layers of the application.
 *
 * @param question the text of the question to be processed.
 */
public record QuestionDTO(String question) {}

