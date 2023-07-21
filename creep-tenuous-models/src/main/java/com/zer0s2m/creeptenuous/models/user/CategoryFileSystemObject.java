package com.zer0s2m.creeptenuous.models.user;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "\"category_file_system_objects\"")
public class CategoryFileSystemObject {

    @Id
    @SequenceGenerator(
            name = "CategoryFileSystemObjectsSequence",
            sequenceName = "category_file_system_objects_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CategoryFileSystemObjectsSequence")
    @Column(name = "id")
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne()
    @JoinColumn(name = "user_category_id", referencedColumnName = "id")
    private UserCategory userCategory;

    @Column(name = "file_system_object")
    private UUID fileSystemObject;

    public CategoryFileSystemObject() {
    }

    public CategoryFileSystemObject(User user, UserCategory userCategory, UUID fileSystemObject) {
        this.user = user;
        this.userCategory = userCategory;
        this.fileSystemObject = fileSystemObject;
    }

    public Long getId() {
        return id;
    }

    public UUID getFileSystemObject() {
        return fileSystemObject;
    }

}
