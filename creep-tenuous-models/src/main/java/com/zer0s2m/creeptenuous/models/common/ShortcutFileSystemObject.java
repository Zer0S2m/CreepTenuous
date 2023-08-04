package com.zer0s2m.creeptenuous.models.common;

import com.zer0s2m.creeptenuous.models.user.User;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "\"shortcuts\"")
public class ShortcutFileSystemObject {

    @Id
    @Column(name = "id")
    @SequenceGenerator(
            name = "ShortcutSequence",
            sequenceName = "shortcuts_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ShortcutSequence")
    private Long id;

    @OneToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "attached_file_system_object")
    private UUID attachedFileSystemObject;

    @Column(name = "to_attached_file_system_object")
    private UUID toAttachedFileSystemObject;

    public ShortcutFileSystemObject() {}

    public ShortcutFileSystemObject(
            User user,
            UUID attachedFileSystemObject,
            UUID toAttachedFileSystemObject) {
        this.user = user;
        this.attachedFileSystemObject = attachedFileSystemObject;
        this.toAttachedFileSystemObject = toAttachedFileSystemObject;
    }

}
