package com.platform.blog.entities.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "security_roles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SecurityRole implements Serializable {

    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Id
    @Column(name = "role", nullable = false, insertable = true, updatable = true, unique = true)
    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Column(name = "description", length = 5000, columnDefinition = "TEXT", updatable = true, insertable = true)
    private String description;

    @ManyToMany(mappedBy = "securityRoles")
    private List<SecurityUser> securityUsers;

    public SecurityRole(RoleType role, String description) {
        this.role = role;
        this.description = description;
    }

    // Convenient method to assign the current role for a user
    // We will not add the current security role to the user because we already added it from the other side
    public void addSecurityUser(SecurityUser securityUser) {
        if( this.securityUsers == null ){
            this.securityUsers = new ArrayList<>();
        }
        this.securityUsers.add(securityUser);
    }


}
