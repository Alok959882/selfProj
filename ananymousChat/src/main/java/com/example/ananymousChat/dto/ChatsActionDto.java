package com.example.ananymousChat.dto;

import com.example.ananymousChat.utils.ChatsAction;

import java.util.List;

public record ChatsActionDto(List<ChatDataDto> chatDataDtoList, ChatsAction action) { }