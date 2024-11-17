package unisolar.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import unisolar.api.domain.dto.openai.QuestionDTO;
import unisolar.api.service.ChatbotService;

@Controller
@RequestMapping({"/", "chat"})
public class ChatController {

    private static final String CHAT_PAGE = "chat";

    private ChatbotService service;

    public ChatController(ChatbotService service) {
        this.service = service;
    }

    @GetMapping
    public String loadChatPage(Model model) {
        var messages = service.loadChatHistory();

        model.addAttribute("history", messages);

        return CHAT_PAGE;
    }

    @PostMapping
    @ResponseBody
    public String answerQuestion(@RequestBody QuestionDTO dto) {
        return service.answerQuestion(dto.question());
    }

    @GetMapping("clear")
    public String clearConversation() {
        service.clearChatHistory();
        return "redirect:/chat";
    }

}
