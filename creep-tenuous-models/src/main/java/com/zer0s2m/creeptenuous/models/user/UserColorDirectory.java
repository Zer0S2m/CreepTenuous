package com.zer0s2m.creeptenuous.models.user;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "\"user_color_directories\"")
public class UserColorDirectory {

    @Id
    @SequenceGenerator(
            name = "UserColorDirectoriesSequence",
            sequenceName = "user_color_directories_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UserColorDirectoriesSequence")
    @Column(name = "id")
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "color")
    private String color;

    @Column(name = "directory")
    private UUID directory;

    public UserColorDirectory() {
    }

    public UserColorDirectory(User user, String color, UUID directory) {
        this.user = user;
        this.color = color;
        this.directory = directory;
    }

    public void setColor(String color) {
        this.color = color;
    }

}
