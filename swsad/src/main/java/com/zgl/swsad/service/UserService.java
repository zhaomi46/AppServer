package com.zgl.swsad.service;


import com.zgl.swsad.authorization.annotation.Authorization;
import com.zgl.swsad.config.Constants;
import com.zgl.swsad.mapper.UserMapper;
import com.zgl.swsad.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Service
public class UserService {
    @Autowired
    UserMapper userMapper;

    // 获取单个用户信息
    public User selectUser(int id) {
        return userMapper.selectUser(id);
    }

    public User selectUserByname(String name) {
        return userMapper.selectUserByName(name);
    }

    //获取所有的用户信息
    public ArrayList<User> selectAllUser() {
        return userMapper.selectAllUser();
    }

    //创建用户
    public int insertUser(User user){

        int reNum = userMapper.insertUser(user);
        return reNum == 0 ? Constants.INSERT_FAIL : user.getUserId();
    }

    //修改用户
    public int updateUser(User user){
        return userMapper.updateUser(user);
    }

    //删除用户
    public int deleteUser(int id){
        return userMapper.deleteUser(id);
    }

    //update balance and creditVal
    public  int updateBnC(int userId,double balance,int creditVal){return userMapper.updateBnC(userId,balance,creditVal);}
}
