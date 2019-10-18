package com.platform.blog.config;

import com.platform.blog.entities.Comment;
import com.platform.blog.entities.Post;
import com.platform.blog.entities.User;
import com.platform.blog.entities.security.SecurityRole;
import com.platform.blog.entities.security.SecurityUser;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

import javax.persistence.EntityManager;

@Configuration
public class GlobalRestRepositoryConfigurer extends RepositoryRestConfigurerAdapter {


    /*
    * @Expose the id to be sent in the request
    * @Allow origins
    * @Allow mapping
    * @Allow Headers
    * @Before adding JWT Authentication
    */
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(Post.class, User.class, Comment.class, SecurityUser.class, SecurityRole.class);
        config.getCorsRegistry()
                .addMapping("/**")
                .allowedOrigins("*")
                .allowedHeaders("*")
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS", "PATCH", "TRACE");
                .allowedMethods("*");

    }


}
