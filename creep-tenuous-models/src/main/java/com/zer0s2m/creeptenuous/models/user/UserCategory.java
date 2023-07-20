package com.zer0s2m.creeptenuous.models.user;

import jakarta.persistence.*;

@Entity
@Table(name = "\"user_categories\"")
public class UserCategory {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

}
