package com.platform.blog.forms;

import lombok.*;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter @ToString
public class PasswordForm {
    @NotNull
    @NotEmpty
    @Size(max = 560, min=5, message = "Invalid password, please set a good password")
    private String currentPassword;

    @NotEmpty
    @NotNull(message = "Password cannot be null")
    @Size(max = 560, min=5, message = "Invalid password, please set a good password")
    @Pattern(regexp = "^[a-zA-Z]+[0-9]+$", message = "Invalid password format[Upper case letters followed by lower case  letters and number]")
    private String newPassword1;

    @NotEmpty
    @Size(max = 255, min=4, message = "Invalid password, please set a good password")
    @Pattern(regexp = "^[a-zA-Z]+[0-9]+$", message = "Invalid password format[Upper case letters followed by lower case  letters and number]")
    private String newPassword2;
}
