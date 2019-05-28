package com.zgl.swsad.controller;

import com.zgl.swsad.authorization.annotation.Authorization;
import com.zgl.swsad.model.Mission;
import com.zgl.swsad.model.User;
import com.zgl.swsad.service.MissionService;
import com.zgl.swsad.service.UserService;
import com.zgl.swsad.util.ReturnMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MissionController {
    @Autowired
    MissionService missionService;
    /*
    @Autowired
    private UserService userService;
    */
    @CrossOrigin
    @Authorization
    @RequestMapping(value="/missions", method = RequestMethod.POST)
    public Object createMission (@RequestBody Mission mission) {
        /*
        int num = missionService.insertMission(mission);
        if(num == 1)
        {
            Mission newMission = missionService.selectMission();
            return new ResponseEntity(newMission, HttpStatus.CREATED);
        }
        else
            return new ResponseEntity(new ReturnMsg("server error"), HttpStatus.INTERNAL_SERVER_ERROR);

         */
        return null;
    }




}
