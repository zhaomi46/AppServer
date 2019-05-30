package com.zgl.swsad.controller;

import com.zgl.swsad.authorization.annotation.Authorization;
import com.zgl.swsad.authorization.annotation.CurrentUser;
import com.zgl.swsad.model.User;
import com.zgl.swsad.service.UserService;
import com.zgl.swsad.util.ReturnMsg;
import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;



@RestController
//@Controller
//@ResponseBody
//restController返回的不是页面,Controller返回的是页面  restController=Controller + ResponseBody
public class UserController {
    @Autowired
    private UserService userService;
    /**
     * 根据userId查询用户信息
     * 获取用户之前必须要已经登录
     * 判断登录用户与查询用户id是否相同，分情况返回不同的信息，别人不等查看别的客户敏感信息
     * 用的是同一个model，但是可以将一些敏感属性设置为空
     */
    //解决跨域问题
    @CrossOrigin
    @Authorization
    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> queryUser (@PathVariable int id, @CurrentUser User currentUser){
        User user = userService.selectUser(id);
        if(user == null)
            return new ResponseEntity(new ReturnMsg("invalid userId"), HttpStatus.NOT_FOUND);

        //不是当前登录用户则需要隐藏敏感信息
        if(currentUser.getUserId() != id)
            user.setPassword("");
            user.setBalance(0);

        return new ResponseEntity(user, HttpStatus.OK);

    }

    /*
    @CrossOrigin
    @RequestMapping(value = "/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object selectAllUser(){
        return userService.selectAllUser();
    }
    */

    /**
     * 创建用户
     * //https://blog.csdn.net/justry_deng/article/details/80972817，注意requestParm注解的使用方法不同
     * @param user requestBody json格式转化为model中的user
     * @return ResponseEntity
     */
    @CrossOrigin
    @RequestMapping(value = "/users", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object createUser (@RequestBody User user){
        User oldUser = userService.selectUserByname(user.getName());

        if(oldUser != null)
            return new ResponseEntity(new ReturnMsg("userName existed"), HttpStatus.CONFLICT);

        int opNum = userService.insertUser(user);
        if(opNum == 1)
        {
            User currentUser = userService.selectUserByname(user.getName());
            return new ResponseEntity(currentUser, HttpStatus.CREATED);
        }
        else
            return new ResponseEntity(new ReturnMsg("server error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 修改用户
     * @param user 新的用户信息
     * @param currentUser 当前登录用户信息
     * @param oldPassword 原来的密码
     * @return
     * 修改用户要验证是否是当前登录的用户，以及密码是否正确
     */
    @CrossOrigin
    @Authorization
    @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object modifyUser (@RequestBody User user, @CurrentUser User currentUser, @RequestParam String oldPassword){
        System.out.println(currentUser.getUserId());
        System.out.println(user.getUserId());
        if(currentUser.getUserId() == user.getUserId() && currentUser.getPassword().equals(oldPassword))
        {
            int opNum = userService.updateUser(user);
            if(opNum == 1)
            {
                return new ResponseEntity(new ReturnMsg("modify user success"), HttpStatus.OK);
            }
            return new ResponseEntity(new ReturnMsg("server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(new ReturnMsg("invalid userId or password"), HttpStatus.BAD_REQUEST);
    }


    /*
    @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object deleteUser (@PathVariable int id){
        return userService.deleteUser(id);
    }
    */
}