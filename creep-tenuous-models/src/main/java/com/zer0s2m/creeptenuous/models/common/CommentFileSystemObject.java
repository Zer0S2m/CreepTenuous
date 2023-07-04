package com.zer0s2m.creeptenuous.models.common;

import com.zer0s2m.creeptenuous.models.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "\"comments\"")
public class CommentFileSystemObject {

    @Id
    @Column(name = "id")
    @SequenceGenerator(
            name = "CommentFileSystemObjectSequence",
            sequenceName = "comments_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CommentFileSystemObjectSequence")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "comment")
    private String comment;

    @Column(name = "file_system_object")
    private String fileSystemObject;

}
