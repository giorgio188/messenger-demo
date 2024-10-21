package com.project.messenger.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.parameters.P;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "private_chat")
public class PrivateChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "member", referencedColumnName = "id")
    @JsonBackReference
    private UserProfile member;


    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "privateChat", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<PrivateChatFiles> privateChatFiles;

    @OneToMany(mappedBy = "privateChat", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<PrivateChatMessages> privateChatMessages;
}
