package com.project.messenger.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.project.messenger.models.enums.Roles;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "group_chat_members")
@RequiredArgsConstructor
public class GroupChatMembers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_chat_id", referencedColumnName = "id")
    @JsonBackReference
    private GroupChat groupChat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    @JsonBackReference
    private UserProfile member;

    @NotNull
    @Column(name = "role")
    private Roles role;

}
