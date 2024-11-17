package unisolar.api.infra.openai;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.ModelType;
import org.springframework.stereotype.Component;

@Component
public class TokenCounter {

    private final Encoding encoding;

    public TokenCounter() {
        var registry = Encodings.newDefaultEncodingRegistry();
        this.encoding = registry.getEncodingForModel(ModelType.GPT_4);
    }

    public int countTokens(String message) {
        return encoding.countTokens(message);
    }
}