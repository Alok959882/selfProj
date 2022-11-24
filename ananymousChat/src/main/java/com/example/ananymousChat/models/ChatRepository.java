package com.example.ananymousChat.models;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends CrudRepository<Chat, Long> {
    Chat findChatByUUID(String UUID);
    List<Chat> findAll(Sort sort);

    void delete(Chat chat);
}