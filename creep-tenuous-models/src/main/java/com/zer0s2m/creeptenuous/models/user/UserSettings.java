package com.zer0s2m.creeptenuous.models.user;

import jakarta.persistence.*;

@Entity
@Table(name = "\"user_settings\"")
public class UserSettings {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "UserSettingsSequence", sequenceName = "user_settings_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UserSettingsSequence")
    private Long id;

    @OneToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToOne()
    @JoinColumn(name = "transferred_user_id", referencedColumnName = "id")
    private User transferredUser;

    @Column(name = "is_deleting_file_objects")
    private boolean isDeletingFileObjects;

    public UserSettings(User user, boolean isDeletingFileObjects) {
        this.user = user;
        this.isDeletingFileObjects = isDeletingFileObjects;
    }

    public UserSettings(User user, User transferredUser) {
        this.user = user;
        this.transferredUser = transferredUser;
    }

    public UserSettings() {
    }

    public void setIsDeletingFileObjects(boolean isDeletingFileObjects) {
        this.isDeletingFileObjects = isDeletingFileObjects;
    }

    public void setTransferredUser(User transferredUser) {
        this.transferredUser = transferredUser;
    }

}
