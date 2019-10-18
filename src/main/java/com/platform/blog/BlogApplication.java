package com.platform.blog;

import com.platform.blog.config.JWTAuthorizationFilter;
import com.platform.blog.dao.PostRepository;
import com.platform.blog.entities.Post;
import com.platform.blog.entities.User;
import com.platform.blog.entities.security.RoleType;
import com.platform.blog.entities.security.SecurityRole;
import com.platform.blog.entities.security.SecurityUser;
import com.platform.blog.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Date;

@SpringBootApplication
@EnableAspectJAutoProxy
@EntityScan(basePackages = {"com.platform.blog.entities"})
public class BlogApplication implements CommandLineRunner {

    @Autowired
    @Qualifier("postServiceImpl")
    private PostService postService;

    @Autowired
    @Qualifier("userServiceImpl")
    private UserService userService;

    @Autowired
    @Qualifier("commentServiceImpl")
    private CommentService commentService;

    @Autowired
    @Qualifier("securityUserServiceImpl")
    private SecurityUserService securityUserService;

    @Autowired
    @Qualifier("securityRoleServiceImpl")
    private SecurityRoleService securityRoleService;


    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }

    /*
    * CORS FILTER
    */
//    @Bean
//    public FilterRegistrationBean corsFilterRegistration() {
//        FilterRegistrationBean registrationBean = new FilterRegistrationBean(new JWTAuthorizationFilter());
//        registrationBean.setName("CORS Filter");
//        registrationBean.addUrlPatterns("/*");
//        registrationBean.setOrder(1);
//        return registrationBean;
//    }

    @Override
    public void run(String... args) throws Exception {

//        /*
//         * @Create new users for the first time
//         * */
//        User user1 = this.userService.saveUser(new User(null, "haytham_dahri", "haytham.dahri@gmail.com", "Haytham", "Dahri", "toortoor", "https://media.licdn.com/dms/image/C4D03AQGvqYSLIMnocA/profile-displayphoto-shrink_200_200/0?e=1561593600&v=beta&t=o4989umcyuMPy_IqPBnvd1J8Af9OJ1y3hWDHGRWTy1o", new Date()));
//        User user2 = this.userService.saveUser(new User(null, "imrane_dahri", "imrane.dahri@gmail.com", "Imrane", "Dahri", "toortoor", "data:image/png;base64,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", new Date()));
//        User user3 = this.userService.saveUser(new User(null, "asmae_dahri", "asmae_dahri@gmail.com", "Asmae", "Dahri", "toortoor", "data:image/png;base64,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", new Date()));
//
//        /*
//         * @Create new security users for the first time
//         * */
//        SecurityUser securityUser1 = this.securityUserService.saveSecurityUser(new SecurityUser(user1.getEmail(), "$2a$10$mkCrFyIlr16y532bZ8ELMOc1QKqKwZdHmi43vKMye2FErRzkkjD3e", user1));
//        SecurityUser securityUser2 = this.securityUserService.saveSecurityUser(new SecurityUser(user2.getEmail(), "$2a$10$mkCrFyIlr16y532bZ8ELMOc1QKqKwZdHmi43vKMye2FErRzkkjD3e", user2));
//        SecurityUser securityUser3 = this.securityUserService.saveSecurityUser(new SecurityUser(user3.getEmail(), "$2a$10$mkCrFyIlr16y532bZ8ELMOc1QKqKwZdHmi43vKMye2FErRzkkjD3e", user3));
//
//        /*
//         * @Create new security users for the first time
//         * */
//        SecurityRole securityRole1 = this.securityRoleService.saveSecurityRole(new SecurityRole(RoleType.ROLE_ADMIN, "ADMINISTRATE THE SYSTEM"));
//        SecurityRole securityRole2 = this.securityRoleService.saveSecurityRole(new SecurityRole(RoleType.ROLE_MANAGER, "MANAGE THE SYSTEM"));
//        SecurityRole securityRole3 = this.securityRoleService.saveSecurityRole(new SecurityRole(RoleType.ROLE_MODERATOR, "SUB ADMINISTRATE THE SYSTEM"));
//        SecurityRole securityRole4 = this.securityRoleService.saveSecurityRole(new SecurityRole(RoleType.ROLE_SUPERVISOR, "SUPERVISE THE SYSTEM"));
//        SecurityRole securityRole5 = this.securityRoleService.saveSecurityRole(new SecurityRole(RoleType.ROLE_USER, "NORMAL USER OF THE SYSTEM"));
//
//        /*
//         * @Set security roles for different security users
//         * */
//        securityUser1.addSecurityRole(securityRole1);
//        securityUser1.addSecurityRole(securityRole2);
//        securityUser1.addSecurityRole(securityRole3);
//        securityUser1.addSecurityRole(securityRole4);
//        securityUser1.addSecurityRole(securityRole5);
//
//        securityUser2.addSecurityRole(securityRole2);
//        securityUser2.addSecurityRole(securityRole3);
//
//        securityUser3.addSecurityRole(securityRole5);
//
//        this.securityUserService.saveSecurityUser(securityUser1);
//        this.securityUserService.saveSecurityUser(securityUser2);
//        this.securityUserService.saveSecurityUser(securityUser3);
//
//
//        /*
//        * @Retrieve the hashed password with Bcrypt encryption
//        */
//        String clairText = "toortoor";
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        String hashedPassword = passwordEncoder.encode(clairText);
//        System.out.println(hashedPassword);

    }
}
