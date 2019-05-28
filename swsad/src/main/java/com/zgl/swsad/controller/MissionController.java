package com.zgl.swsad.controller;

import com.zgl.swsad.authorization.annotation.Authorization;
import com.zgl.swsad.authorization.annotation.CurrentUser;
import com.zgl.swsad.model.Mission;
import com.zgl.swsad.model.Task;
import com.zgl.swsad.model.User;
import com.zgl.swsad.service.MissionService;
import com.zgl.swsad.service.UserService;
import com.zgl.swsad.util.ReturnMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.zgl.swsad.config.Constants;

import java.util.ArrayList;

@RestController
public class MissionController {
    @Autowired
    MissionService missionService;

    @Autowired
    private UserService userService;

    @CrossOrigin
    @Authorization
    @RequestMapping(value="/missions", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object createMission (@RequestBody Mission mission, @CurrentUser User currentUser) {
        int userId = mission.getUserId();
        if (userService.selectUser(userId) == null) {
            return new ResponseEntity(new ReturnMsg("User not found."), HttpStatus.NOT_FOUND);
        }

        if (mission.getUserId() != currentUser.getUserId()) {
            return new ResponseEntity(new ReturnMsg("Unauthorized."), HttpStatus.UNAUTHORIZED);
        }

        int missionId = missionService.insertMission(mission);
        if(missionId  != Constants.INSERT_FAIL)
        {
            Mission newMission = missionService.selectMission(missionId);
            return new ResponseEntity(newMission, HttpStatus.CREATED);
        }  else {
            return new ResponseEntity(new ReturnMsg("Server error."), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @CrossOrigin
    @RequestMapping(value="/missions", method=RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object getAllMissions() {
        ArrayList<Mission> missions = missionService.selectAllMissions();
        return new ResponseEntity(missions, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value="/missions/{missionId}", method=RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object getMissionById(@PathVariable int missionId) {
        Mission mission = missionService.selectMission(missionId);

        if (mission == null) {
            return new ResponseEntity(new ReturnMsg("Mission not found."), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(mission, HttpStatus.OK);
    }

    @CrossOrigin
    @Authorization
    @RequestMapping(value = "/missions/{missionId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    //missionId和userId不需要传过来
    public Object modifyMission(@RequestBody Mission mission, @PathVariable int missionId, @CurrentUser User currentUser) {
        Mission curMission = missionService.selectMission(missionId);
        if (curMission == null) {
            return new ResponseEntity(new ReturnMsg("Mission not found!"), HttpStatus.NOT_FOUND);
        }

        if (curMission.getUserId() != currentUser.getUserId()) {
            return  new ResponseEntity(new ReturnMsg("Unanthorized, this is not your mission."), HttpStatus.UNAUTHORIZED);
        }

        mission.setMissionId(missionId);
        mission.setUserId(curMission.getUserId());
        int count = missionService.updateMission(mission);
        if (count == 1) {
            return  new ResponseEntity(new ReturnMsg("Modify mission success."), HttpStatus.OK);
        }

        return new ResponseEntity(new ReturnMsg("Server error."), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @CrossOrigin
    @Authorization
    @RequestMapping(value = "/missions/{missionId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object deleteMission(@PathVariable int missionId, @CurrentUser User currentUser) {
        Mission curMission = missionService.selectMission(missionId);
        if (curMission == null) {
            return new ResponseEntity(new ReturnMsg("Mission not found!"), HttpStatus.NOT_FOUND);
        }

        if (curMission.getUserId() != currentUser.getUserId()) {
            return new ResponseEntity(new ReturnMsg("Unauthorized, this is not your mission."), HttpStatus.UNAUTHORIZED);
        }

        int count = missionService.deleteMission(missionId);
        if (count == 1) {
            return  new ResponseEntity(new ReturnMsg("Delete mission success."), HttpStatus.OK);
        }
        return new ResponseEntity(new ReturnMsg("Server error."), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @CrossOrigin
    @Authorization
    @RequestMapping(value="/missions/{missionId}/tasks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object selectTasksByMissionId(@PathVariable int missionId, @CurrentUser User currentUser) {
        Mission mission = missionService.selectMission(missionId);
        if (mission == null) {
            return new ResponseEntity(new ReturnMsg("Mission not found!"), HttpStatus.NOT_FOUND);
        }

        if (mission.getUserId() != currentUser.getUserId()) {
            return new ResponseEntity(new ReturnMsg("Unauthorized, these tasks are not yours."), HttpStatus.UNAUTHORIZED);
        }

        ArrayList<Task> tasks = missionService.selectTasksByMissionId(missionId);
        if (tasks != null) {
            return new ResponseEntity(tasks, HttpStatus.OK);
        }
        return new ResponseEntity(new ReturnMsg("Server error."), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
