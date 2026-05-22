package ru.kpfu.itis.snetkov.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kpfu.itis.snetkov.entity.User;
import ru.kpfu.itis.snetkov.repository.UserRepository;

import java.time.Instant;
import java.util.NoSuchElementException;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            log.error("Username is already in use");
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }

        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setRole(User.UserRole.USER);
        user.setIsActive(true);

        return userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public User changeRole(Integer userId, User.UserRole newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));
        user.setRole(newRole);
        user.setUpdatedAt(Instant.now());
        return userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void toggleActive(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));
        user.setIsActive(!user.getIsActive());
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
    }
}