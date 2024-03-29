package com.zer0s2m.creeptenuous.models.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "\"user_colors\"")
public class UserColor {

    @Getter
    @Id
    @SequenceGenerator(
            name = "UserColorsSequence",
            sequenceName = "user_colors_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UserColorsSequence")
    @Column(name = "id")
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Getter
    @Setter
    @Column(name = "color")
    private String color;

    public UserColor() {
    }

    public UserColor(User user, String color) {
        this.user = user;
        this.color = color;
    }

}
