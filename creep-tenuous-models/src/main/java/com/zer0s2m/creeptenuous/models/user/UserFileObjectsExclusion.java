package com.zer0s2m.creeptenuous.models.user;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Entity
@Table(name = "\"user_file_objects_exclusions\"")
public class UserFileObjectsExclusion {

    @Getter
    @Id
    @SequenceGenerator(
            name = "UserFileObjectsExclusionsSequence",
            sequenceName = "user_file_objects_exclusions_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UserFileObjectsExclusionsSequence")
    @Column(name = "id")
    private Long id;

    @Column(name = "file_system_object")
    private UUID fileSystemObject;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public UserFileObjectsExclusion() {
    }

    public UserFileObjectsExclusion(UUID fileSystemObject, User user) {
        this.fileSystemObject = fileSystemObject;
        this.user = user;
    }

}
