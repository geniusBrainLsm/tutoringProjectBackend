package com.lsm.backend.model;
import com.lsm.backend.model.AuthProvider;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USER")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME", length = 100)
    @NotNull
    @Size(max = 100)
    private String name;

    @JsonIgnore
    @Column(name = "PASSWORD", length = 128)
    @NotNull
    @Size(max = 128)
    private String password;

    @Column(name = "EMAIL", length = 512, unique = true)
    @NotNull
    @Size(max = 512)
    private String email;

    @Column(name = "EMAIL_VERIFIED_YN", length = 1)
    @NotNull
    @Size(min = 1, max = 1)
    private String emailVerifiedYn;

    @Column(name = "PROFILE_IMAGE_URL", length = 512)
    @NotNull
    @Size(max = 512)
    private String profileImageUrl;

    @Column(name = "AuthProvider", length = 20)
    @Enumerated(EnumType.STRING)
    @NotNull
    private AuthProvider authProvider;
    @Column(name = "CREATED_AT")
    @NotNull
    private LocalDateTime createdAt;

    @Column(name = "MODIFIED_AT")
    @NotNull
    private LocalDateTime modifiedAt;

    public User(
            @NotNull @Size(max = 64) String userId,
            @NotNull @Size(max = 100) String name,
            @NotNull @Size(max = 512) String email,
            @NotNull @Size(max = 1) String emailVerifiedYn,
            @NotNull @Size(max = 512) String profileImageUrl,
            @NotNull AuthProvider authProvider,
            @NotNull LocalDateTime createdAt,
            @NotNull LocalDateTime modifiedAt
    ) {
        this.id = id;
        this.name = name;
        this.password = "NO_PASS";
        this.email = email != null ? email : "NO_EMAIL";
        this.emailVerifiedYn = emailVerifiedYn;
        this.profileImageUrl = profileImageUrl != null ? profileImageUrl : "";
        this.authProvider = authProvider;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}