package com.medicine.pharmacy.service.impl;

import com.medicine.pharmacy.model.Role;
import com.medicine.pharmacy.model.User;
import com.medicine.pharmacy.repository.RoleRepository;
import com.medicine.pharmacy.repository.UserRepository;
import com.medicine.pharmacy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void
    saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(1);
        Role role = roleRepository.findByRole("ROLE_USER");
        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    public void saveAdmin(User admin){
        admin.setPassword(bCryptPasswordEncoder.encode(admin.getPassword()));
        admin.setActive(1);
        Role role = roleRepository.findByRole("ROLE_ADMIN");
        admin.setRole(role);
        userRepository.save(admin);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElse(null);
    }

    @Override
    public List<User> findAllByRole(String role) {
        return userRepository.findAllByRoleRole(role);
    }

    @Override
    public User update(User user) {
        User updateUser = userRepository.findById(user.getId()).orElse(null);
        if (updateUser != null){
            updateUser.setName(user.getName());
            updateUser.setEmail(user.getEmail());
            updateUser.setPhoneNumber(user.getPhoneNumber());
        }
        return userRepository.save(updateUser);
    }
}
