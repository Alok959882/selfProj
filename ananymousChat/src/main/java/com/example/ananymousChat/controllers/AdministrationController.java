package com.example.ananymousChat.controllers;

import com.example.ananymousChat.dto.ChatDataDto;
import com.example.ananymousChat.dto.ChatsActionDto;
import com.example.ananymousChat.models.Chat;
import com.example.ananymousChat.models.ChatRepository;
import com.example.ananymousChat.models.ChatUser;
import com.example.ananymousChat.models.ChatUserRepository;
import com.example.ananymousChat.utils.ChatsAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AdministrationController {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatUserRepository chatUserRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @RequestMapping("admin")
    public String login() {
        // Deny access to the login page to the users already logged in.
        // Redirect them to the dashboard view instead.
        org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "admin-login";
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("logout")
    public String logout() {
        return "admin-login";
    }

    @GetMapping("admin/dashboard")
    public String adminDashboard(Model model) {
        List<Chat> chats = chatRepository.findAll(Sort.by("id").ascending());
        model.addAttribute("chats", chats);
        return "admin-dashboard";
    }

    @GetMapping("admin/delete/{UUID}")
    public String deleteChat(@PathVariable("UUID") String UUID) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/";
        }

        Chat chat = chatRepository.findChatByUUID(UUID);
        List<ChatUser> chatUserList = chatUserRepository.findAllByChat_IdOrderByUsernameAsc(chat.getId());
        List<ChatDataDto> chatDataDtoList = new ArrayList<>();

        chatRepository.delete(chat);

        // Send command to remove chat from home page
        chatDataDtoList.add(new ChatDataDto(chat, 0));
        ChatsActionDto chatsActionDto = new ChatsActionDto(chatDataDtoList, ChatsAction.DELETE);
        simpMessagingTemplate.convertAndSend("/topic/chats", chatsActionDto);

        // Redirect every chat user to the home page
        for (ChatUser chatUser: chatUserList) {
            simpMessagingTemplate.convertAndSendToUser(
                    chatUser.getPrincipalUserName(),
                    "/topic/redirect",
                    "{\"action\": \"REDIRECT\", \"location\": \"/\"}"
            );
        }

        return "redirect:/admin/dashboard";
    }
}