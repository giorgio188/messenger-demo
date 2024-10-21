package com.project.messenger.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "group_chat_messages")
@RequiredArgsConstructor
public class GroupChatMessages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "group_chat_id", referencedColumnName = "id")
    @JsonBackReference
    private GroupChat groupChat;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    @JsonBackReference
    private UserProfile sender;

    @NotNull
    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @NotNull
    @Column(name = "message")
    private String message;
}
