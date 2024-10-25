package com.project.messenger.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.project.messenger.models.enums.MessageStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "private_chat_messages")
public class PrivateChatMessage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "private_chat_id")
    @JsonBackReference
    private PrivateChat privateChat;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    @JsonBackReference
    private UserProfile sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "id")
    @JsonBackReference
    private UserProfile receiver;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "message")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MessageStatus status;

}
