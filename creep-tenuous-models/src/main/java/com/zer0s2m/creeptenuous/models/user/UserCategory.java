package com.zer0s2m.creeptenuous.models.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Entity
@Table(name = "\"user_categories\"")
public class UserCategory {

    @Id
    @SequenceGenerator(name = "UserCategoriesSequence", sequenceName = "user_categories_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UserCategoriesSequence")
    @Column(name = "id")
    private Long id;

    @Setter
    @Column(name = "title")
    private String title;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "userCategory", cascade = CascadeType.ALL)
    private List<CategoryFileSystemObject> categoryFileSystemObjects;

    @OneToOne(mappedBy = "userCategory")
    private UserColorCategory userColorCategory;

    public UserCategory(String title, User user) {
        this.title = title;
        this.user = user;
    }

    public UserCategory() {
    }

}
