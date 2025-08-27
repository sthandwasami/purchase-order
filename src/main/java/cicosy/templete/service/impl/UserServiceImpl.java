package cicosy.templete.service.impl;

import cicosy.templete.domain.User;
import cicosy.templete.repository.UserRepository;
import cicosy.templete.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(String username, String email, String password) {
        if (existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        user.setRole(User.Role.USER);
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) { return userRepository.findByUsername(username).orElse(null); }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) { return userRepository.findByEmail(email).orElse(null); }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) { return userRepository.existsByUsername(username); }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) { return userRepository.existsByEmail(email); }

    @Override
    public void saveUser(User user) { userRepository.save(user); }
}


