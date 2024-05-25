package com.example.boot.SpringBootApp.Dao;


import com.example.boot.SpringBootApp.Model.User;

import java.util.List;

public interface UserDao {
    List<User> getAllUser();
    User show(long id);
    void save(User user);
    void update(long id,User user);
    void delete(long id);
}
