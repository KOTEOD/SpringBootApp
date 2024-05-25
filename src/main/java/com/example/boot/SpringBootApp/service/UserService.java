package com.example.boot.SpringBootApp.service;

import com.example.boot.SpringBootApp.Model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUser();
    User show(long s);
    void save(User user);
    void update(long id, User user);
    void delete(long id);
}
