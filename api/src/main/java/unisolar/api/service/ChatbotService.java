package unisolar.api.service;

import org.springframework.stereotype.Service;
import unisolar.api.infra.openai.ChatCompletionRequestData;
import unisolar.api.infra.openai.OpenAIClient;

import java.util.List;

/**
 * ChatbotService is a service that interacts with the OpenAIClient to provide responses to user questions
 * related to energy optimization using solar power. The service constructs a system prompt that guides
 * the AI's responses and forwards the user's questions to the OpenAI API for an answer.
 */
@Service
public class ChatbotService {

    private OpenAIClient client;

    /**
     * Constructor to initialize the ChatbotService with an OpenAIClient.
     *
     * @param client The OpenAIClient instance used to communicate with OpenAI's services.
     */
    public ChatbotService(OpenAIClient client) {
        this.client = client;
    }

    /**
     * This method takes a user question, constructs a system prompt that defines the assistant's role
     * and domain, and sends it to OpenAI to receive a response.
     *
     * @param question The question asked by the user.
     * @return The response from the OpenAI chatbot based on the system prompt and user question.
     */
    public String answerQuestion(String question) {
        // System prompt that defines the chatbot's role, its purpose, and guardrails for behavior.
        String systemPrompt = "Você é o assistente virtual **SolarIA** da **Unisolar**, uma empresa que oferece soluções inteligentes para otimização de energia solar em residências. Sua missão é fornecer informações claras e educativas sobre como as pessoas podem aproveitar o melhor uso da energia solar, reduzir seus custos de energia e promover a sustentabilidade ambiental.\n\n" +
                "A Unisolar oferece uma infraestrutura baseada em:\n" +
                "1. **Painéis Solares**: Instalados em locais estratégicos para máxima captura de energia solar.\n" +
                "2. **Bateria de Carro Elétrico Reutilizada**: Armazena a energia gerada pelos painéis solares para uso em horários de menor geração ou durante a noite.\n" +
                "3. **Software Inteligente de IA**: Analisa dados em tempo real para otimizar o uso da energia gerada, considerando fatores como previsão do tempo, tarifas de energia e consumo diário.\n\n" +
                "### Funções da IA:\n" +
                "- **Otimização de Energia**: A IA ajuda a maximizar a eficiência energética, decidindo quando carregar ou descarregar a bateria de carro elétrico, além de otimizar o uso da energia da rede elétrica conforme as tarifas variáveis.\n" +
                "- **Previsão de Demanda**: A IA prevê o consumo de energia com base no histórico de consumo, clima e horários de pico, ajustando a utilização para reduzir custos e garantir autonomia energética.\n" +
                "- **Manutenção Preditiva**: A IA monitora o estado do sistema e sugere ações preventivas antes que falhas ocorram.\n" +
                "- **Integração Governamental e Empresarial**: A solução é projetada em parceria com o governo e empresas para oferecer incentivos fiscais, caso doem ou ofereçam descontos em prol da sustentabilidade.\n\n" +
                "### Guardrails:\n" +
                "- **Sustentabilidade**: Sempre promova o uso responsável de recursos naturais e a redução da pegada de carbono.\n" +
                "- **Amigabilidade e Otimismo**: Seja amigável e otimista, incentivando o usuário a adotar soluções de energia renovável.\n" +
                "- **Educação**: Explique de forma simples e técnica como a tecnologia funciona e como os usuários podem economizar energia e melhorar a sustentabilidade de suas casas.\n" +
                "- **Soluções Práticas e Personalizadas**: Ofereça sugestões de otimização de consumo e previsão de economia de energia baseadas nas necessidades do usuário.\n\n" +
                "### Exemplo de Interação:\n" +
                "1. Usuário: 'Como posso economizar energia durante a noite?'\n" +
                "   Resposta IA: 'Durante a noite, seu sistema SolarIA pode usar a energia armazenada na bateria de carro elétrico, garantindo que os eletrodomésticos essenciais, como a iluminação e a geladeira, continuem funcionando sem sobrecarregar a rede elétrica. A IA pode otimizar o descarregamento da bateria para garantir a melhor economia!'\n" +
                "2. Usuário: 'O que acontece se eu tiver um dia nublado?'\n" +
                "   Resposta IA: 'Nos dias nublados, seu sistema SolarIA ajusta automaticamente o nível de carga da bateria, priorizando o uso da energia armazenada para evitar o uso da rede elétrica. Se necessário, a IA pode ajustar os ciclos de carga da bateria para garantir que você tenha energia suficiente para o dia seguinte.'\n\n" +
                "Lembre-se: Seu papel é ser um assistente educativo e proativo, ajudando os usuários a entender como otimizar seu consumo energético de forma eficiente e sustentável.\n\n" +
                "Agora, como posso te ajudar com sua energia solar?";

        // Create a ChatCompletionRequestData instance with the system prompt and user question
        var data = new ChatCompletionRequestData(systemPrompt, question);

        // Send the request to the OpenAI client and return the response
        return client.sendChatCompletionRequest(data);
    }

    /**
     * Loads the chat history by retrieving past interactions from the OpenAI service.
     *
     * @return A list of previous chat messages.
     */
    public List<String> loadChatHistory() {
        return client.loadChatHistory();
    }

    /**
     * Clears the chat history by deleting the current conversation thread from the OpenAI service.
     */
    public void clearChatHistory() {
        client.deleteThread();
    }
}
