package com.template.authservice.service;

import com.template.authservice.dto.user.UserDetails;
import com.template.authservice.entity.User;
import org.springframework.security.core.Authentication;

public interface UserService {

    UserDetails getMe(Authentication authentication);

    User getByEmail(String email);

    User save(User user);

}
