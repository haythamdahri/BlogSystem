package com.platform.blog.entities.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.platform.blog.entities.User;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "security_users")
@AllArgsConstructor
@NoArgsConstructor
//@Getter
//@Setter
public class SecurityUser implements Serializable {

    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Id
    @Column(name = "email", unique = true, nullable = false, updatable = true, insertable = true)
    private String email;

    @Column(name = "password", nullable = false, updatable = true, insertable = true)
    private String password;

    @Column(name = "enabled", insertable = true, updatable = true, nullable = false)
    private boolean enabled;

    @OneToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name = "security_users_roles", joinColumns = @JoinColumn(name = "user_email"), inverseJoinColumns = @JoinColumn(name = "role_role"))
    private List<SecurityRole> securityRoles;

    public SecurityUser(String email, String password, User user){
        this.email = email;
        this.password = password;
        this.user = user;
        this.enabled = true; // The reason behind false as enabled is that the user must activate his email before allowing him to be authenticated into the system
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Ignore the password on any json response(not included)
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    // Set the password throught a json request
    @JsonSetter
    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<SecurityRole> getSecurityRoles() {
        return securityRoles;
    }

    public void setSecurityRoles(List<SecurityRole> securityRoles) {
        this.securityRoles = securityRoles;
    }

    //Convenient method to add a new security role for this user
    public void addSecurityRole(SecurityRole securityRole) {
        if( this.securityRoles == null ){
            this.securityRoles = new ArrayList<>();
        }
        this.securityRoles.add(securityRole);
        securityRole.addSecurityUser(this);
    }


}
