package com.zer0s2m.creeptenuous.models.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "\"user_color_directories\"")
public class UserColorDirectory {

    @Id
    @Getter
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

    @Getter
    @Setter
    @ManyToOne()
    @JoinColumn(name = "user_color_id", referencedColumnName = "id")
    private UserColor color;

    @Getter
    @Column(name = "directory")
    private UUID directory;

    public UserColorDirectory() {
    }

    public UserColorDirectory(User user, UserColor color, UUID directory) {
        this.user = user;
        this.color = color;
        this.directory = directory;
    }

}
