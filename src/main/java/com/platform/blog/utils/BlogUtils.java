package com.platform.blog.utils;

import com.platform.blog.entities.Post;
import com.platform.blog.entities.User;
import com.platform.blog.entities.security.RoleType;
import com.platform.blog.entities.security.SecurityRole;
import com.platform.blog.entities.security.SecurityUser;
import com.platform.blog.exceptions.SecurityRoleException;
import com.platform.blog.exceptions.SecurityUserException;
import com.platform.blog.services.EmailService;
import com.platform.blog.services.SecurityRoleService;
import com.platform.blog.services.SecurityUserService;
import com.platform.blog.services.UserService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class BlogUtils {

    @Autowired
    private HttpSession httpSession;

    @Autowired
    @Qualifier("userServiceImpl")
    private UserService userService;

    @Autowired
    @Qualifier("securityUserServiceImpl")
    private SecurityUserService securityUserService;

    @Autowired
    @Qualifier("securityRoleServiceImpl")
    private SecurityRoleService securityRoleService;

    @Autowired
    private EmailService emailService;

    public static String uploadDirectory = System.getProperty("user.dir") + "/src/main/resources/static/media/";
    public static String usersUploadDirectory = System.getProperty("user.dir") + "/src/main/resources/static/media/users/";
    public static String postsUploadDirectory = System.getProperty("user.dir") + "/src/main/resources/static/media/posts/";

    private static final List<String> imageContentTypes = Arrays.asList("image/png", "image/jpeg", "image/gif");

    /*
     * @Current connected user
     */
    public User getConnectedUser() {
        SecurityContext securityContext = (SecurityContext) this.httpSession.getAttribute("SPRING_SECURITY_CONTEXT");
        if (securityContext != null) {
            String email = securityContext.getAuthentication().getName();
            User user = this.userService.getUser(email);
            return user;
        }
        return null;
    }


    /*
     * @Check if the connected user is the owner of the post
     * */
    public boolean checkPostOwner(Post post) {
        if (!this.getConnectedUser().getPosts().stream().filter(postItem -> postItem.getId() == post.getId()).findAny().isPresent()) {
            return false;
        }
        return true;
    }

    /*
     * @Retrieve file extension from a file name
     * */
    public String getExtensionByApacheCommonLib(String filename) {
        return FilenameUtils.getExtension(filename);
    }

    /*
     * @Create security user
     * */
    public boolean createSecurityUserAndUpdatePassword(User user) {
        SecurityUser securityUser = null;
        SecurityRole securityRole = null;

        try{
            // Create a system user for login
            // New hash method instance
            // Update user password with the secured one(Two way to retrieve user password from database (User and SecurityUser entities))
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = bCryptPasswordEncoder.encode(user.getPassword());
            user = this.userService.saveUser(user);
            securityUser = this.securityUserService.saveSecurityUser(new SecurityUser(user.getEmail(), hashedPassword, user));

            // Check if security user is created
            if( securityUser != null ) {
                securityRole = this.securityRoleService.getSecurityRoleByRoleType(RoleType.ROLE_USER);
                if( securityRole != null ) {
                    securityUser.addSecurityRole(securityRole);
                    securityUser = this.securityUserService.saveSecurityUser(securityUser);
                    if( securityUser != null ) {
                        return true;
                    }
                    throw new SecurityUserException("Security User Exception, Not updated");
                }
                throw new SecurityRoleException("Security Role exception");
            }
            throw new SecurityUserException("Security User Exception, Not inserted");

        }
        catch(Exception e) {
            this.userService.deleteUser(user.getId());
            if( securityUser != null ){
                this.securityUserService.deleteSecurityUser(securityUser.getEmail());
            }
            if( securityRole != null ){
                this.securityRoleService.deleteSecurityRoleByRoleType(securityRole.getRole());
            }
            return false;
        }
    }

    public void revertUser(User user) {
        try{
            // Deleted the created user and the stored imgae
            Path path = Paths.get(uploadDirectory + user.getImage());
            Files.delete(path);
        }
        catch(Exception e) {

        }
        finally {
            // Either if the image file was deleted or not, we have to delete te user
            this.userService.deleteUser(user.getId());
        }
    }


    /*
     * @Send simple text email
     */
    public boolean sendTextEmail(String to,String subject, String text) {
        return this.emailService.sendSimpleMessage(to, subject, text);
    }

    /*
     * @Send file attachment email
     */
    public boolean sendAttachmentEmail(String to,String subject, String text) {
        User user = this.getConnectedUser();
        return this.emailService.sendMessageWithAttachment(to, subject, text, usersUploadDirectory + user.getImage());

    }

    /*
     * @Send templated email
     */
    public boolean sendProfessionalEmail(String to, String subject, String text, Map<String, Object> data) {
        User user = this.getConnectedUser();
        return this.emailService.sendProfessionalMessage(user.getFullName(), to, subject, text, data);
    }

}
