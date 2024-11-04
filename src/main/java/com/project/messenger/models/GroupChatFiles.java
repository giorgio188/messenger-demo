package com.project.messenger.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.project.messenger.models.enums.FileType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "group_chat_files")
public class GroupChatFiles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_chat_id", referencedColumnName = "id")
    private GroupChat groupChat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    @JsonBackReference
    private UserProfile sender;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "file")
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 10)
    private FileType type;

    public GroupChatFiles(GroupChat groupChat, LocalDateTime sentAt, UserProfile sender, String fileUrl, FileType type) {
        this.groupChat = groupChat;
        this.sentAt = sentAt;
        this.sender = sender;
        this.fileUrl = fileUrl;
        this.type = type;
    }
}
