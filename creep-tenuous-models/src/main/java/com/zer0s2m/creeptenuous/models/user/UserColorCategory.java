package com.zer0s2m.creeptenuous.models.user;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "\"user_color_categories\"")
public class UserColorCategory {

    @Id
    @SequenceGenerator(
            name = "UserColorCategoriesSequence",
            sequenceName = "user_color_categories_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UserColorCategoriesSequence")
    @Column(name = "id")
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "file_system_object")
    private UUID fileSystemObject;

}
