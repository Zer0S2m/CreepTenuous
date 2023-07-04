package com.zer0s2m.creeptenuous.models.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zer0s2m.creeptenuous.models.user.User;
import jakarta.persistence.*;

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
    private Long id;

    @OneToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "comment")
    @JsonProperty
    private String comment;

    @Column(name = "file_system_object")
    @JsonProperty
    private UUID fileSystemObject;

    public CommentFileSystemObject(User user, String comment, UUID fileSystemObject) {
        this.user = user;
        this.comment = comment;
        this.fileSystemObject = fileSystemObject;
    }

    public CommentFileSystemObject() {
    }

}
