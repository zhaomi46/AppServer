package com.zgl.swsad.controller;

import com.alibaba.fastjson.JSONObject;
import com.zgl.swsad.authorization.annotation.Authorization;
import com.zgl.swsad.authorization.annotation.CurrentUser;
import com.zgl.swsad.authorization.manager.TokenManager;
import com.zgl.swsad.authorization.model.TokenModel;
import com.zgl.swsad.model.User;
import com.zgl.swsad.service.UserService;
import com.zgl.swsad.util.ReturnMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tokens")
public class TokenController {

    @Autowired
    private UserService userRepository;

    @Autowired
    private TokenManager tokenManager;

//    @CrossOrigin
//    @RequestMapping(method = RequestMethod.POST)
//    public ResponseEntity<Object> login(@RequestParam String username, @RequestParam String password) throws Exception {
//        Assert.notNull(username, "username can not be empty");
//        Assert.notNull(password, "password can not be empty");
//
//        User user = userRepository.selectUserByname(username);
//        if(user == null || !user.getPassword().equals(password))
//            return new ResponseEntity("invlid username/password", HttpStatus.UNAUTHORIZED);
//        //生成一个token，保存用户登录状态
//        String tokenString = tokenManager.createToken(user.getUserId().longValue(), true);
//        return new ResponseEntity(tokenString, HttpStatus.OK);
//    }
    @CrossOrigin
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Object> login(@RequestParam String username, @RequestParam String password) throws Exception {
        Assert.notNull(username, "username can not be empty");
        Assert.notNull(password, "password can not be empty");

        User user = userRepository.selectUserByname(username);
        if(user == null || !user.getPassword().equals(password))
            return new ResponseEntity("invlid username/password", HttpStatus.UNAUTHORIZED);
        //生成一个token，保存用户登录状态
        String tokenString = tokenManager.createToken(user.getUserId().longValue(), true);
        Integer userID = user.getUserId();
        JSONObject result = new JSONObject();
        result.put("useId", userID);
        result.put("token", tokenString);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET)
    @Authorization
    public ResponseEntity<Object> check(@CurrentUser User user){
        return new ResponseEntity(new ReturnMsg("ok"), HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.DELETE)
    @Authorization
    public ResponseEntity<Object> logout(@CurrentUser User user) throws Exception {
        String tokenString = tokenManager.createToken(user.getUserId().longValue(), false);
        return new ResponseEntity(tokenString, HttpStatus.OK);
    }

}

