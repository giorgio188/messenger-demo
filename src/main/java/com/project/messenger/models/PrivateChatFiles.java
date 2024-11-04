package com.project.messenger.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.messenger.models.enums.FileType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "private_chat_files")
public class PrivateChatFiles {

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sentAt;

    @Column(name = "file")
    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 10)
    private FileType type;

    public PrivateChatFiles(PrivateChat privateChat, UserProfile sender, UserProfile receiver, LocalDateTime sentAt, String filePath, FileType type) {
        this.privateChat = privateChat;
        this.sender = sender;
        this.receiver = receiver;
        this.sentAt = sentAt;
        this.filePath = filePath;
        this.type = type;
    }
}
