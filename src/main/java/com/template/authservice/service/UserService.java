package com.template.authservice.service;

import com.template.authservice.entity.User;

public interface UserService {

    User getByEmail(String email);

    User save(User user);

}
