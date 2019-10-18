package com.platform.blog.dao;

import com.platform.blog.entities.Comment;
import com.platform.blog.entities.security.RoleType;
import com.platform.blog.entities.security.SecurityRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import javax.management.relation.Role;
import java.util.List;

@Repository
@RepositoryRestResource(path = "/security-roles")
public interface SecurityRoleRepository extends JpaRepository<SecurityRole, Long> {

    public SecurityRole findByRole(@Param("role") RoleType roleType);
}
