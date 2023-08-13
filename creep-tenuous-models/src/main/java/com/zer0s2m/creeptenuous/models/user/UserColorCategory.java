package com.zer0s2m.creeptenuous.models.user;

import jakarta.persistence.*;

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

    @ManyToOne()
    @JoinColumn(name = "user_color_id", referencedColumnName = "id")
    private UserColor userColor;

    @OneToOne()
    @JoinColumn(name = "user_category_id", referencedColumnName = "id")
    private UserCategory userCategory;

    public UserColorCategory() {
    }

    public UserColorCategory(User user, UserColor userColor, UserCategory userCategory) {
        this.user = user;
        this.userColor = userColor;
        this.userCategory = userCategory;
    }

    public UserColor getUserColor() {
        return userColor;
    }

    public Long getId() {
        return id;
    }

}
