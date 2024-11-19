package unisolar.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import unisolar.api.domain.dto.openai.QuestionDTO;
import unisolar.api.service.ChatbotService;

/**
 * Controller responsible for managing chatbot interactions and chat page rendering.
 */
@Controller
@RequestMapping({"/", "chat"})
public class ChatController {

    private static final String CHAT_PAGE = "chat"; // Name of the HTML template for the chat page.

    private ChatbotService service; // Service responsible for chatbot functionalities.

    /**
     * Constructor for ChatController.
     *
     * @param service the ChatbotService instance to handle chatbot operations.
     */
    public ChatController(ChatbotService service) {
        this.service = service;
    }

    /**
     * Loads the chat page and initializes the chat history.
     *
     * @param model the Model object to pass attributes to the view.
     * @return the name of the chat page template.
     */
    @GetMapping
    public String loadChatPage(Model model) {
        var messages = service.loadChatHistory(); // Retrieve chat history from the service.

        model.addAttribute("history", messages); // Add chat history to the model.

        return CHAT_PAGE; // Return the chat page template name.
    }

    /**
     * Handles user questions by sending them to the chatbot service and returning the response.
     *
     * @param dto the QuestionDTO containing the user question.
     * @return the chatbot's response as a string.
     */
    @PostMapping
    @ResponseBody
    public String answerQuestion(@RequestBody QuestionDTO dto) {
        return service.answerQuestion(dto.question()); // Process the question and return the response.
    }

    /**
     * Clears the chat history and redirects to the chat page.
     *
     * @return a redirect to the chat page.
     */
    @GetMapping("clear")
    public String clearConversation() {
        service.clearChatHistory(); // Clear the chat history in the service.
        return "redirect:/chat"; // Redirect to the chat page.
    }
}
