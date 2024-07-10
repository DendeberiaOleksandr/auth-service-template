package com.template.authservice.service.impl;

import com.template.authservice.dto.user.UserDetails;
import com.template.authservice.entity.User;
import com.template.authservice.exception.EntityNotFoundException;
import com.template.authservice.repository.UserRepository;
import com.template.authservice.security.UserDetailsImpl;
import com.template.authservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public UserDetails getMe(Authentication authentication) {
        String email = ((UserDetailsImpl) authentication.getPrincipal()).getUsername();
        User user = getByEmail(email);
        return new UserDetails(user.getId(), email);
    }

    @Transactional(readOnly = true)
    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User is not found by email: %s".formatted(email)));
    }

    @Transactional
    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
}
