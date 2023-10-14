package com.zer0s2m.creeptenuous.models.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zer0s2m.creeptenuous.models.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"comments\"")
public class CommentFileSystemObject {

    @Id
    @Column(name = "id")
    @JsonProperty
    @SequenceGenerator(
            name = "CommentFileSystemObjectSequence",
            sequenceName = "comments_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CommentFileSystemObjectSequence")
    @lombok.Getter
    private Long id;

    @OneToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @lombok.Getter
    @lombok.Setter
    @Column(name = "comment")
    @JsonProperty
    private String comment;

    @lombok.Getter
    @lombok.Setter
    @Column(name = "file_system_object")
    @JsonProperty
    private UUID fileSystemObject;

    @lombok.Getter
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    @JsonProperty
    @CreationTimestamp
    private LocalDateTime createdAt;

    public CommentFileSystemObject(User user, String comment, UUID fileSystemObject) {
        this.user = user;
        this.comment = comment;
        this.fileSystemObject = fileSystemObject;
        this.createdAt = LocalDateTime.now();
    }

    public CommentFileSystemObject() {
        this.createdAt = LocalDateTime.now();
    }

}
