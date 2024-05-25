package com.example.boot.SpringBootApp.service;

import com.example.boot.SpringBootApp.Dao.UserDao;
import com.example.boot.SpringBootApp.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getAllUser() {
        return userDao.getAllUser();
    }

    @Transactional(readOnly = true)
    @Override
    public User show(long s) {
        return userDao.show(s);
    }

    @Transactional(readOnly = false)
    @Override
    public void save(User user) {
        userDao.save(user);
    }

    @Transactional(readOnly = false)
    @Override
    public void update(long id, User updateUser) {
        userDao.update(id, updateUser);
    }

    @Transactional(readOnly = false)
    @Override
    public void delete(long id) {
        userDao.delete(id);
    }
}
