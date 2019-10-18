package com.platform.blog.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class User implements Serializable {

    @OneToMany(mappedBy = "creator")
    List<Post> posts;
    @OneToMany(mappedBy = "creator")
    List<Comment> comments;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "username", insertable = true, updatable = true, nullable = false, length = 50, unique = true)
    @NotNull(message = "Username cannot be null")
    @NotEmpty(message = "Username cannot be empty")
    @Size(min = 6, max = 20, message = "Username length must be between 6 and 20 character")
    private String username;
    @Column(name = "email", insertable = true, updatable = true, nullable = false, unique = true)
    @NotNull(message = "Email cannot be null")
    @NotEmpty(message = "Email name cannot be empty")
    @Email(message = "Email field must be a valid email address!")
    private String email;
    @Column(name = "first_name", insertable = true, updatable = true, nullable = false, length = 50)
    @NotNull(message = "First name cannot be null")
    @NotEmpty(message = "First name cannot be empty")
    @Size(min = 3, max = 20, message = "First name length must be between 3 and 20 character")
    private String firstName;
    @Column(name = "last_name", insertable = true, updatable = true, nullable = false, length = 50)
    @NotNull(message = "Last name cannot be null")
    @NotEmpty(message = "Last name cannot be empty")
    @Size(min = 3, max = 20, message = "Last name length must be between 3 and 20 character")
    private String lastName;
    @Column(name = "password", insertable = true, updatable = true, nullable = false, length = 560)
    @NotNull(message = "Password cannot be null")
    @NotEmpty(message = "Password cannot be empty")
    @Size(min = 5, max = 560, message = "Password length must be between 5 and 560 character")
    private String password;
    @Column(name = "image", insertable = true, updatable = true, nullable = false, length = 50000)
    private String image;
    @Temporal(TemporalType.DATE)
    @Column(name = "creation_date")
    private Date creationDate;
    @Column(name = "active")
    private boolean active;

    // Default constructor is generated by Lombok
    // Construtor with necessary args
    public User(Long id, String username, String email, String firstName, String lastName, String password, String image, Date creationDate) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.image = image;
        this.creationDate = creationDate;
        this.active = true;
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    /*
     * @toString for display purposes
     * */
    @Override
    public String toString() {
        return "Id: " + this.id + " | Username: " + this.username + " | Email: " + this.email + " | First Name: " + this.firstName + " | Last Name: "
                + this.lastName + " | Password: " + this.password + " | Image: " + this.image + " | Creation Date: " + this.creationDate + " | Is Active: "
                + this.active;
    }

    // Convenient method to add new post to the user posts list whatever we set this user as a creator for a post
    public void addPost(Post post) {
        if (this.posts == null) {
            this.posts = new ArrayList<>();
        }
        this.posts.add(post);
    }

    // Convenient method to add comment to the user comments list whatever we set this user as a creator for a comment
    public void addComment(Comment comment) {
        if (this.comments == null) {
            this.comments = new ArrayList<>();
        }
        this.comments.add(comment);
    }

    @PrePersist
    private void setDefaultValues() {
        this.creationDate = new Date();
    }


    @Override
    public boolean equals(Object object) {
        if (!(object instanceof User)) {
            return false;
        }
        User user = (User) object;
        if (user.id == this.id) {
            return true;
        } else if (user.id > this.id) {
            return false;
        }
        return true;
    }

}
