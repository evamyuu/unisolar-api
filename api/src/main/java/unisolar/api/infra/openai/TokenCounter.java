package unisolar.api.infra.openai;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.ModelType;
import org.springframework.stereotype.Component;

/**
 * TokenCounter is a utility class that provides functionality to count tokens in a message
 * based on a specific model's encoding.
 * It is used to calculate the number of tokens in a message that will be processed by OpenAI's GPT-4 model.
 */
@Component
public class TokenCounter {

    private final Encoding encoding;

    /**
     * Constructs a TokenCounter instance, initializing the encoding for the GPT-4 model.
     * The encoding is used to count tokens in the provided message.
     */
    public TokenCounter() {
        var registry = Encodings.newDefaultEncodingRegistry();
        this.encoding = registry.getEncodingForModel(ModelType.GPT_4);
    }

    /**
     * Counts the number of tokens in the provided message based on the GPT-4 model's encoding.
     *
     * @param message The message whose tokens are to be counted.
     * @return The number of tokens in the message.
     */
    public int countTokens(String message) {
        return encoding.countTokens(message);
    }
}
