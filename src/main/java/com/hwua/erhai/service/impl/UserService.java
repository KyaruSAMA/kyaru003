package com.hwua.erhai.service.impl;

import com.hwua.erhai.entity.User;
import com.hwua.erhai.entity.UserExample;
import com.hwua.erhai.mapper.UserMapper;
import com.hwua.erhai.mapper.UserMapper4Ext;
import com.hwua.erhai.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserMapper userMapper;
    private UserMapper4Ext userMapper4Ext;

    public UserService(UserMapper4Ext userMapper4Ext) {
        this.userMapper4Ext = userMapper4Ext;
    }


    @Override
    public List<User> queryUserConditions(Map<String, Object> conditions) {

        return userMapper4Ext.selectByConditions(conditions);

    }

    @Autowired
    public long countUser4Ext(Map<String, Object> conditions) {
        return userMapper4Ext.countByConditions(conditions);
    }
    @Override
    public User login(String userName, String password) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andUsernameEqualTo(userName).andPasswordEqualTo(password);
        List<User> userList = userMapper.selectByExample(userExample);
        if (userList == null || userList.size() == 0) {
            return null;
        }
        if (userList.size() > 1) {
            throw new RuntimeException(String.format("找到多个用户, userName[%s]", userName));
        }
        return userList.get(0);
    }



    @Override
    public User queryUserName(String username) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andUsernameEqualTo(username);
        List<User> users = userMapper.selectByExample(userExample);
        if (users == null || users.size() == 0){
            return  null;
        }
        if (users.size() >1 ){
            throw  new IllegalArgumentException(String.format("找到多个用户,userName[%s]",username));
        }
        return users.get(0);
    }

    @Override
    public User queryUserIdNumber(String idNumber) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdNumberEqualTo(idNumber);
        List<User> users = userMapper.selectByExample(userExample);
        if (users == null || users.size() == 0){
            return  null;
        }
        if (users.size() >1 ){
            throw  new IllegalArgumentException(String.format("找到多个重复idNumber,idNumber[%s]",idNumber));
        }
        return users.get(0);
    }

    @Override
    public User queryUserById(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null){
            return null;
        }
        return user;
    }

    @Override
    public User addUser(User user) {
        int record = userMapper.insert(user);
        if (record == 0){
            return null;
        }
        return userMapper.selectByPrimaryKey(user.getId());
    }

    @Override
    public User updateUser(User user) {
        int record = userMapper.updateByPrimaryKey(user);
        if (record == 0){
            return null;
        }else {
            return userMapper.selectByPrimaryKey(user.getId());
        }
    }

    @Override
    public User deleteUser(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null){
            return null;
        }
        userMapper.deleteByPrimaryKey(userId);
        return user;
    }
}
