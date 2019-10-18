package com.platform.blog.dao;

import com.platform.blog.entities.Comment;
import com.platform.blog.entities.security.SecurityUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(path = "/security-users")
public interface SecurityUserRepository extends JpaRepository<SecurityUser, Long> {

    public boolean deleteByEmail(@Param("email") String email);

    public SecurityUser findByEmail(String email);
}
