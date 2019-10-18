package com.platform.blog.controllers;

import com.platform.blog.entities.Post;
import com.platform.blog.entities.User;
import com.platform.blog.entities.security.SecurityUser;
import com.platform.blog.forms.PasswordForm;
import com.platform.blog.services.EmailService;
import com.platform.blog.services.PostService;
import com.platform.blog.services.SecurityUserService;
import com.platform.blog.services.UserService;
import com.platform.blog.utils.BlogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class BlogController {

    @Autowired
    @Qualifier("postServiceImpl")
    private PostService postService;

    @Autowired
    @Qualifier("userServiceImpl")
    private UserService userService;

    @Autowired
    @Qualifier("securityUserServiceImpl")
    private SecurityUserService securityUserService;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private BlogUtils blogUtils;

    @Value("${posts_size}")
    private int posts_size;

    public static String uploadDirectory = System.getProperty("user.dir") + "/src/main/resources/static/media/";
    public static String usersUploadDirectory = System.getProperty("user.dir") + "/src/main/resources/static/media/users/";
    public static String postsUploadDirectory = System.getProperty("user.dir") + "/src/main/resources/static/media/posts/";
    public static String imagesPrefixDirectory = "/media/";
    public static String usersImagesPrefixDirectory = "/media/users/";
    public static String postsImagesPrefixDirectory = "/media/posts/";
    private static final List<String> imageContentTypes = Arrays.asList("image/png", "image/jpeg", "image/gif");

    /*
     * @Logged in user page
     */
    @GetMapping("/logged-user")
    @ResponseBody
    public Map<String, Object> loggedInUser() {
        SecurityContext securityContext = (SecurityContext) this.httpSession.getAttribute("SPRING_SECURITY_CONTEXT");
        String email = securityContext.getAuthentication().getName();
        List<String> roles = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : securityContext.getAuthentication().getAuthorities()) {
            roles.add(grantedAuthority.getAuthority());
        }
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("roles", roles);
        return params;
    }

    /*
     * @Administration Page
     * Only admins who can access
     */
    @Secured(value = {"ROLE_ADMIN"})
    @GetMapping("/administration")
    @ResponseBody
    public String administration() {
        return "Welcome Admin";
    }

    /*
     * @Login page
     */
    @GetMapping("/login")
    public String login(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth instanceof AnonymousAuthenticationToken)) {

            /* The user is logged in :) */
            return "redirect:/";
        }
        model.addAttribute("appTitle", "Login");
        return "login";
    }

    /*
     * @Access denied page
     */
    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("appTitle", "Access Denied");
        return "access-denied";
    }


    /*
     * @Secured define users with specific roles that are authorized to access the page
     * In this case we have: ADMIN or MANGER
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String home(@RequestParam(value = "page", defaultValue = "0") int page, Model model) {
        Page<Post> postsPage = this.postService.getActivePagedPosts(page, posts_size);
        model.addAttribute("appTitle", "Home");
        model.addAttribute("postsPage", postsPage);
        model.addAttribute("count", postsPage.getNumberOfElements());
        return "posts";
    }

    /*
     * @Connected User profile
     */
    @GetMapping("/profile")
    public String connectedUserProfile(Model model) {
        User user = this.blogUtils.getConnectedUser();
        long postsCount = user.getPosts().size();
        long postsViews = this.postService.getUserPostsViews(user);
        long approvedBlogsCount = this.postService.getApprouvedPostsCount(user);
        long rejectedBlogsCount = this.postService.getRejectedPostsCount(user);
        model.addAttribute("appTitle", user.getFullName());
        // user is always injected by all response so that needn't to send it as an object to the view
        model.addAttribute("postsCount", postsCount);
        model.addAttribute("postsViews", postsViews);
        model.addAttribute("approvedBlogsCount", approvedBlogsCount);
        model.addAttribute("rejectedBlogsCount", rejectedBlogsCount);
        return "profile";
    }


    /*
     * @GET user update form
     */
    @GetMapping("/settings")
    public String profileSettings(Model model) {
        model.addAttribute("appTitle", "Update account informations");
        model.addAttribute("isUpdate", true);
        model.addAttribute("update_user", this.blogUtils.getConnectedUser());
        return "user-update-form";
    }

    /*
     * @POST user update form
     */
    @PostMapping("/settings")
    public String profileSettingsUpdate(@ModelAttribute("update_user") User user, Model model, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("appTitle", "Update account informations");
            model.addAttribute("update_user", user);
            model.addAttribute("isError", true);
            return "post-form";
        }
        try {

            // Retrieve the user from the database
            User old_user = this.userService.getUser(user.getId());

            // Update user informations
            old_user.setUsername(user.getUsername());
            old_user.setFirstName(user.getFirstName());
            old_user.setLastName(user.getLastName());

            // Save the user
            user = this.userService.saveUser(old_user);

            if (user != null) {
                redirectAttributes.addFlashAttribute("successMessage", "Profile informations have been updated successflly!");
                return "redirect:/profile";
            }

            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred, please check your inputs and try again!");
            return "redirect:/settings";
        }
        catch(Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred, please check your inputs and try again!");
            return "redirect:/settings";
        }
    }

    /*
     * @Change profile image
     */
    @PostMapping("/profile/change-image")
    public String changeProfileImage(@RequestParam("image") MultipartFile file, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (file.isEmpty() || !imageContentTypes.contains(file.getContentType())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Selected image is not a valid file, please retry again!");
                return "redirect:/profile";
            }
            // Change the image name based on the unique user id
            User user = this.blogUtils.getConnectedUser();
            user.setImage(user.getId() + "." + this.blogUtils.getExtensionByApacheCommonLib(file.getOriginalFilename()));
            // Save the user on the database
            user = this.userService.saveUser(user);
            // Upload user image or update it if exists
            byte[] bytes = file.getBytes();
            Path path = Paths.get(usersUploadDirectory + user.getImage());
            Files.write(path, bytes);
            // Redirect tje user to his profile with a success message
            redirectAttributes.addFlashAttribute("successMessage", "Profile picture has been updated successflly");
            return "redirect:/profile";
        } catch (Exception ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Errror occurred, please try again!");
            return "redirect:/profile";
        }
    }

    /*
     * @Reset password
     */
    @GetMapping("/password-reset")
    public String resetPassword(Model model) {
        PasswordForm passwordForm = new PasswordForm("", "", "");
        model.addAttribute("appTitle", "Password reset");
        model.addAttribute("passwordForm", passwordForm);

        return "password-reset";
    }

    /*
     * @Reset password
     */
    @PostMapping("/password-reset")
    public String resetPasswordCommit(@Valid @ModelAttribute("passwordForm") PasswordForm passwordForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Incorrect password data!");
            model.addAttribute("appTitle", "Password reset");
            return "password-reset";
        }

        // Retrieve the current connected user
        User user = this.blogUtils.getConnectedUser();

        // Retrieve security user
        SecurityUser securityUser = this.securityUserService.getSecurityUser(user.getEmail());

        if (securityUser == null) {
            return "redirect:/";
        }
        // Construct bcrypt password encoder
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        // Encrypt the clear password from the password form sent by the user
        String hashedPassword = bCryptPasswordEncoder.encode(passwordForm.getCurrentPassword());

        // Compare the current password with the sent password
        if (!bCryptPasswordEncoder.matches(passwordForm.getCurrentPassword(), securityUser.getPassword())) {
            FieldError currentPasswordError = new FieldError("currentPassword", "currentPassword", "Current password does not match!");
            bindingResult.addError(currentPasswordError);
            redirectAttributes.addFlashAttribute("errorMessage", "Incorrect password data!");
            model.addAttribute("appTitle", "Password reset");
            return "password-reset";
        }

        // Check if the new password and the confirm new password are the same
        if (!passwordForm.getNewPassword1().equals(passwordForm.getNewPassword2())) {
            FieldError newPassword2 = new FieldError("newPassword2", "newPassword2", "Password confirmation does not match!");
            bindingResult.addError(newPassword2);
            redirectAttributes.addFlashAttribute("errorMessage", "Incorrect password data!");
            model.addAttribute("appTitle", "Password reset");
            return "password-reset";
        }

        // If all tests passed successflly, let's save the password for User and SecurityUser
        user.setPassword(passwordForm.getNewPassword1());
        securityUser.setPassword(bCryptPasswordEncoder.encode(passwordForm.getNewPassword1()));

        user = this.userService.saveUser(user);
        securityUser = this.securityUserService.saveSecurityUser(securityUser);

        redirectAttributes.addFlashAttribute("successMessage", "Password has been changed successflly!");
        return "redirect:/profile";
    }

    /*
     * @Connected User profile
     */
    @GetMapping("/users")
    public String users(Model model) {
        User user = this.blogUtils.getConnectedUser();
        // Filter the users to exclude current connected user
        // getRelatedUsers to fix image with local upload directory
        List<User> users = this.userService.getUsers().stream().filter(user1 -> user1.getId() != user.getId()).collect(Collectors.toList());
        System.out.println(users.size());
        model.addAttribute("appTitle", "Users");
        model.addAttribute("users", users);
        return "users";
    }

    /*
     * @ User profile
     */
    @GetMapping("/users/{userId}")
    public String userProfile(@PathVariable("userId") long id, Model model, RedirectAttributes redirectAttributes) {
        User user = this.userService.getUser(id);
        if (user != null && user != this.blogUtils.getConnectedUser()) {
            user = user;
            long postsCount = user.getPosts().size();
            long postsViews = this.postService.getUserPostsViews(user);
            long approvedBlogsCount = this.postService.getApprouvedPostsCount(user);
            long rejectedBlogsCount = this.postService.getRejectedPostsCount(user);
            model.addAttribute("appTitle", user.getFullName());
            // we are naming user by userProfile in order to solve the conflict between the sent user model with all response and our custom user
            model.addAttribute("userProfile", user);
            model.addAttribute("postsCount", postsCount);
            model.addAttribute("postsViews", postsViews);
            model.addAttribute("approvedBlogsCount", approvedBlogsCount);
            model.addAttribute("rejectedBlogsCount", rejectedBlogsCount);
            return "user-profile";
        } else if (user != null && user == this.blogUtils.getConnectedUser()) {
            return "redirect:/profile";
        }
        redirectAttributes.addFlashAttribute("errorMessage", "No user found!");
        return "redirect:/users";
    }

    /*
     * @Retrieve connected user posts
     */
    @GetMapping("/posts")
    public String userPosts(@RequestParam(value = "page", defaultValue = "0") int page, Model model) {
        User user = this.blogUtils.getConnectedUser();
        Page<Post> postsPage = this.postService.getPagedUserPosts(user.getId(), page, this.posts_size);
        model.addAttribute("appTitle", user.getFullName() + " posts");
        model.addAttribute("postsPage", postsPage);
        return "posts";
    }

    /*
     * @Add new post GET
     */
    @GetMapping("/posts/add")
    public String addPostGet(Model model) {
        Post post = new Post();
        model.addAttribute("appTitle", "Add new post");
        model.addAttribute("post", post);
        return "post-form";
    }

    /*
     * @Add or update a post POSTMETHOD
     */
    @PostMapping("/posts/save")
    public String savePostWithPostMethod(@Valid @ModelAttribute("post") Post post, BindingResult bindingResult, @RequestParam("cover_image") MultipartFile file, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("appTitle", "Add new post");
            model.addAttribute("post", post);
            model.addAttribute("isError", true);
            return "post-form";
        } else if (file.isEmpty() || !imageContentTypes.contains(file.getContentType())) {
            model.addAttribute("appTitle", "Add new post");
            model.addAttribute("post", post);
            model.addAttribute("isError", true);
            FieldError coverError = new FieldError("coverError", "cover", "Cover file is not valid");
            bindingResult.addError(coverError);
            return "post-form";
        }
        try {
            // Saving post into database to retrieve its id for cover unique name
            // We will set a temprory value for the cover
            post.setCover(file.getOriginalFilename());
            post.setCreator(this.blogUtils.getConnectedUser());
            post.setActive(true);
            // Save the post for the first if it does exist(Creating new post)
            if (post.getId() == null) {
                post = this.postService.savePost(post);
            }
            // Set the file name of the cover
            post.setCover(post.getId() + "." + this.blogUtils.getExtensionByApacheCommonLib(file.getOriginalFilename()));
            // Retrieve file bytes and store it on the server for later uses
            byte[] bytes = file.getBytes();
            // Store the file with the post id to ensure a unique name
            Path path = Paths.get(postsUploadDirectory + post.getCover());
            Files.write(path, bytes);
            // Update the post after file storage
            post = this.postService.savePost(post);
            if (post != null) {
                String message = "Post has been saved successflly.";
                message += !post.isActive() ? "The post is now displayed only for the creator and it will be public as soon as approved!": "";
                redirectAttributes.addFlashAttribute("successMessage", message);
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "An error occurred, please try again!");
            }
            return "redirect:/posts/" + post.getId();
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Errror occurred, please try again!");
            return "redirect:/posts/add";
        }

    }

    /*
     * @Active User posts
     */
    @GetMapping("/users/{userId}/posts")
    public String userPosts(@PathVariable("userId") long id, @RequestParam(value = "page", defaultValue = "0") int page, Model model, RedirectAttributes redirectAttributes) {
        User user = this.userService.getUser(id);
        if (user == this.blogUtils.getConnectedUser()) {
            return "redirect:/posts";
        }
        if (user != null) {
            Page<Post> postsPage = this.postService.getActivePagedUserPosts(user, page, this.posts_size);
            model.addAttribute("appTitle", user.getFullName() + " posts");
            model.addAttribute("theUser", user);
            model.addAttribute("postsPage", postsPage);
            return "user-posts";
        }
        redirectAttributes.addFlashAttribute("errorMessage", "No user found!");
        return "redirect:/users";
    }

    /*
     * @Single Current user Post Page
     */
    @GetMapping(value = "/users/{userId}/posts/{postId}")
    public String singleUserPost(@PathVariable("userId") long userId, @PathVariable(value = "postId") long postId, Model model, RedirectAttributes redirectAttributes) {

        Post post = this.postService.getPostByCreatorId(postId, userId);
        if (post == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Searched post was not found!");
            return "redirect:/";
        } else if (!post.isActive()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Searched post has not been approved yet!");
            return "redirect:/";
        }
        post.setViews(post.getViews() + 1);
        post = this.postService.savePost(post);
        // After update, we prefix the image of the post and the writer user with its directory
        post.setCover(postsImagesPrefixDirectory + post.getCover());
        post.setCreator(post.getCreator());
        model.addAttribute("appTitle", post.getTitle());
        model.addAttribute("post", post);
        return "post";
    }

    /*
     * @Single Current user Post Page
     */
    @GetMapping(value = "/posts/{postId}")
    public String singlePost(@PathVariable(value = "postId") long postId, Model model, RedirectAttributes redirectAttributes) {
        Post post = this.postService.getPost(postId);
        if (post == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Searched post was not found!");
            return "redirect:/";
        } else if (!post.isActive() && post.getCreator().getId() != this.blogUtils.getConnectedUser().getId()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Searched post has not been approved yet!");
            return "redirect:/";
        }
        post.setViews(post.getViews() + 1);
        post = this.postService.savePost(post);
        // After update, we prefix the image of the post and the writer user with its directory
        post.setCover(postsImagesPrefixDirectory + post.getCover());
        post.setCreator(post.getCreator());
        model.addAttribute("appTitle", post.getTitle());
        model.addAttribute("post", post);
        return "post";
    }

    /*
     * @Edit user post GET
     */
    @GetMapping("/posts/{postId}/edit")
    public String editPostGet(@PathVariable("postId") long postId, Model model, RedirectAttributes redirectAttributes) {
        Post post = this.postService.getPost(postId);
        if (post == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Post does not exists!");
            return "redirect:/";
        }
        // Check if the connected user is the owner of the edit post. if not he will be redirected with error message;
        if (!this.blogUtils.checkPostOwner(post)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You are not the owner of the post to edit it!");
            return "redirect:/posts/" + post.getId();
        }
        model.addAttribute("appTitle", "Edit: " + post.getTitle());
        model.addAttribute("post", post);
        return "post-form";
    }

    /*
     * @Delete user post
     */
    @PostMapping("/posts/delete")
    public String deletePost(@RequestParam("id") String postId, Model model, RedirectAttributes redirectAttributes) {
        Post post = this.postService.getPost(Long.parseLong(postId));
        if (post == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Post does not exists!");
            return "redirect:/";
        }
        // Check if the connected user is the owner of the edit post. if not he will be redirected with error message;
        if (!this.blogUtils.checkPostOwner(post)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You are not the owner of the post to delete it!");
            return "redirect:/posts/" + post.getId();
        }
        boolean deleted = this.postService.deletePost(post.getId());
        if (!deleted) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred, please try again!");
            return "redirect:/posts/" + post.getId();
        }
        redirectAttributes.addFlashAttribute("successMessage", "Post has been deleted successflly!");
        return "redirect:/posts";
    }

    /*
     * @GET user registration form
     */
    @GetMapping("/register")
    public String registerUserGet(Model model) {
        if (this.blogUtils.getConnectedUser() != null) {
            return "redirect:/";
        }
        model.addAttribute("appTitle", "Create new account");
        model.addAttribute("register_user", new User());
        return "user-form";
    }

    /*
     * @POST user registration form
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("register_user") User user, BindingResult bindingResult, RedirectAttributes redirectAttributes, @RequestParam("image_file") MultipartFile file, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("appTitle", "Create new account");
            model.addAttribute("user", user);
            model.addAttribute("isError", true);
            return "user-form";
        } else if (file.isEmpty() || !imageContentTypes.contains(file.getContentType())) {
            model.addAttribute("appTitle", "Create new account");
            model.addAttribute("user", user);
            model.addAttribute("isError", true);
            FieldError imageError = new FieldError("imageError", "image", "Image Profile is not valid");
            bindingResult.addError(imageError);
            return "user-form";
        }
        boolean is_update = user.getId() != null ? false : true;
        try {
            // Persist the user if we are creation a new account
            // Update the user if we are updating an existing account
            // Set the image name to allow the persistence into the database if we have inserting operation
            user.setImage(file.getOriginalFilename());
            user = this.userService.saveUser(user);

            // Set the unique file name as the name of user image
            user.setImage(user.getId() + "." + this.blogUtils.getExtensionByApacheCommonLib(file.getOriginalFilename()));

            // Retrieve the image file and save it with the current user id
            byte[] bytes = file.getBytes();
            Path path = Paths.get(usersUploadDirectory + user.getImage());
            Files.write(path, bytes);

            // Save the user after file writing
            user = this.userService.saveUser(user);

            // Delegate the security user and role creation for a method(Long treatment)
            if (!this.blogUtils.createSecurityUserAndUpdatePassword(user)) {
                // Revert the created user
                this.blogUtils.revertUser(user);
                //  Displaying error message for created user
                redirectAttributes.addFlashAttribute("errorMessage", "Errror occurred, please try again!");
                return "redirect:/register";
            }

            //  Displaying success message for created or updated user
            if (is_update) {
                redirectAttributes.addFlashAttribute("successMessage", "Account has been updated successflly");
                return "redirect:/profile";
            }
            redirectAttributes.addFlashAttribute("successMessage", "Account has been created successflly, welcome to our blog");
            return "redirect:/login";
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Errror occurred, please try again!");
            return "redirect:/register";
        }
    }

}

