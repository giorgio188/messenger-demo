package com.project.messenger.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "group_chat")
@RequiredArgsConstructor
public class GroupChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotNull
    @Column(name = "name")
    @Size(min = 3, max = 64)
    private String name;

    @NotNull
    @Column(name = "description")
    @Size(max = 256)
    private String description;

    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "groupChat", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<GroupChatMembers> groupChatList;

    @OneToMany(mappedBy = "groupChat", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<GroupChatMessage> groupChatMessages;

    @OneToMany(mappedBy = "groupChat", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<GroupChatFiles> groupChatFiles;

    @OneToMany(mappedBy = "groupChat", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<GroupChatMembers> groupChatMembers;
}
