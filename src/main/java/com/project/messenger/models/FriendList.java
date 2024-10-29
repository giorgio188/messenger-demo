package com.project.messenger.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "friendlist")
@RequiredArgsConstructor
public class FriendList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserProfile userId;

    @ManyToOne
    @JoinColumn(name = "friend_id", referencedColumnName = "id")
    private UserProfile friendId;

    @NotNull
    @Column(name = "added_at")
    private LocalDateTime addedAt;

}
