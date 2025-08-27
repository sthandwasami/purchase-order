package cicosy.templete.service;

import cicosy.templete.domain.User;

public interface UserService {
    User registerUser(String username, String email, String password);
    User findByUsername(String username);
    User findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    void saveUser(User user);
}


