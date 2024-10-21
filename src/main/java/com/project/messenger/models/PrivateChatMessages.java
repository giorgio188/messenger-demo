package com.project.messenger.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "private_chat_messages")
public class PrivateChatMessages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "private_chat_id")
    @JsonBackReference
    private PrivateChat privateChat;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    @JsonBackReference
    private UserProfile sender;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "message")
    private String message;
}
