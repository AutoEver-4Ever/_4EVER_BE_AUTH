package org.ever._4ever_be_auth.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestVo {

    private String email;
    private String username;
    private String password;
    private String phoneNumber;
    private String role;
}
