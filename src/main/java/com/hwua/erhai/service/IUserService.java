package com.hwua.erhai.service;

import com.hwua.erhai.entity.User;

import java.util.List;
import java.util.Map;

public interface IUserService {


    long countUser4Ext(Map<String,Object> conditions);
    User login(String userName, String password);
    List<User> queryUserConditions(Map<String,Object> conditions);
    User queryUserName(String username);

    User queryUserIdNumber(String idNumber);

    User queryUserById(Integer userId);

    User addUser(User user);

    User updateUser(User user);

    User deleteUser(Integer userId);
}
