package com.zgl.swsad.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zgl.swsad.authorization.annotation.Authorization;
import com.zgl.swsad.authorization.annotation.CurrentUser;
import com.zgl.swsad.model.*;
import com.zgl.swsad.service.*;
import com.zgl.swsad.util.ReturnMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.zgl.swsad.config.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

@RestController
public class MissionController {
    @Autowired
    MissionService missionService;
    @Autowired
    TaskService taskService;
    @Autowired
    QuestionareService questionareService;
    @Autowired
    QuestionService questionService;
    @Autowired
    ErrandService errandService;

    @Autowired
    private UserService userService;

    @CrossOrigin
    @Authorization
    @RequestMapping(value="/missions/questionares", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object createMissionQA (@RequestBody JSONObject param, @CurrentUser User currentUser) {


        JSONObject mission_json = param.getJSONObject("mission");
        //System.out.println("mis json" + mission_json);
        //Mission mission = JSONObject.toJavaObject(mission_json,Mission.class);

        int userId = (int) mission_json.get("userId");//mission.getUserId();
        if (userService.selectUser(userId) == null) {
            return new ResponseEntity(new ReturnMsg("User not found."), HttpStatus.NOT_FOUND);
        }

        if (userId != currentUser.getUserId()) {
            return new ResponseEntity(new ReturnMsg("Unauthorized."), HttpStatus.UNAUTHORIZED);
        }
        //System.out.println("xxx");
        Mission mission = (Mission) JSONObject.toJavaObject(mission_json, Mission.class);
        //System.out.println("xxx2");
        int missionId = missionService.insertMission(mission);
        if (missionId == Constants.INSERT_FAIL) {
            return new ResponseEntity(new ReturnMsg("Server error.mission creat fail"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        int loopTime = (int)mission_json.get("taskNum");
        for(int count = 0; count < loopTime;count++)
        {
            JSONObject task_json = param.getJSONObject("task");
            task_json.put("MissionId",missionId);
            if (task_json.get("pubUserId") != currentUser.getUserId()) {
                return new ResponseEntity(new ReturnMsg("PubUserId invalid !"), HttpStatus.UNAUTHORIZED);
            }
            JSONObject questionare_json = param.getJSONObject("questionare");
            JSONArray questions_json = questionare_json.getJSONArray("questions");
            //int count = 0;
            //int num =  questionare_json.size() - 3;
            //注意这里的questionare_size得到的长度不是以JsonObject作为单位，而是以键值对作为单位,所以还要加上前面的3个键值对

            Task task = (Task) JSONObject.toJavaObject(task_json, Task.class);
            int opNum1 = taskService.insertTask(task);
            if (opNum1 != Constants.INSERT_FAIL) {
                //count++;
                questionare_json.put("taskId", opNum1);
            } else {
                return new ResponseEntity(new ReturnMsg("Task creat fail !"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            Questionare questionare = (Questionare) JSONObject.toJavaObject(questionare_json, Questionare.class);
            int opNum2 = questionareService.insertQuestionare(questionare);
            if (opNum2 == Constants.INSERT_FAIL) {
                //count++;
                return new ResponseEntity(new ReturnMsg("Questionare creat fail !"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            for (int i = 0; i < questions_json.size(); i++) {
                JSONObject question_json = (JSONObject) questions_json.getJSONObject(i); //这里不能是get(i),get(i)只会得到键值对
                question_json.put("questionareId", opNum2);
                Question question = (Question) JSONObject.toJavaObject(question_json, Question.class);
                int opNum3 = questionService.insertQuestion(question);
                if (opNum3 != 1) {
                    //count++;
                    return new ResponseEntity(new ReturnMsg("Some questions creat fail !"), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }

        return new ResponseEntity(new ReturnMsg("create task successfully!"), HttpStatus.OK);


    }

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
    @Authorization
    @RequestMapping(value="/missions/errands", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object createMissionER (@RequestBody JSONObject param, @CurrentUser User currentUser) {

        JSONObject mission_json = param.getJSONObject("mission");
        //System.out.println("mis json" + mission_json);
        //Mission mission = JSONObject.toJavaObject(mission_json,Mission.class);

        int userId = (int)mission_json.get("userId");//mission.getUserId();
        if (userService.selectUser(userId) == null) {
            return new ResponseEntity(new ReturnMsg("User not found."), HttpStatus.NOT_FOUND);
        }

        if (userId != currentUser.getUserId()) {
            return new ResponseEntity(new ReturnMsg("Unauthorized."), HttpStatus.UNAUTHORIZED);
        }
        //System.out.println("xxx");
        Mission mission = (Mission)JSONObject.toJavaObject(mission_json,Mission.class);
        //System.out.println("xxx2");
        int missionId = missionService.insertMission(mission);
        if(missionId  == Constants.INSERT_FAIL)
        {
            return new ResponseEntity(new ReturnMsg("Server error.mission creat fail"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        JSONObject task_json = param.getJSONObject("task");
        task_json.put("MissionId",missionId);
        //System.out.println("xxx"+missionId);
        if(task_json.get("pubUserId") != currentUser.getUserId())
        {
            return new ResponseEntity(new ReturnMsg("PubUserId invalid !"),HttpStatus.UNAUTHORIZED);
        }
        JSONObject errand_json = param.getJSONObject("errand");
        int count = 0;

        Task task = (Task)JSONObject.toJavaObject(task_json,Task.class);
        int opNum1 = taskService.insertTask(task);
        if( opNum1 == Constants.INSERT_FAIL ){
            return new ResponseEntity(new ReturnMsg("Task creat fail !"),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        else
        {
            errand_json.put("taskId",opNum1);
        }

        Errand errand = (Errand)JSONObject.toJavaObject(errand_json,Errand.class);
        int opNum2 = errandService.insertErrand(errand);
        if( opNum2 == Constants.INSERT_FAIL  ){
            return new ResponseEntity(new ReturnMsg("Errand creat fail !"),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        else
            return new ResponseEntity(new ReturnMsg("create task successfully!"), HttpStatus.OK);


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


    @CrossOrigin
    @Authorization
    @RequestMapping(value="/missions/{missionId}/QAsummary", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object getMissionQASum (@PathVariable int missionId, @CurrentUser User currentUser)
    {
        Mission mission = missionService.selectMission(missionId);
        if (mission == null) {
            return new ResponseEntity(new ReturnMsg("Mission not found!"), HttpStatus.NOT_FOUND);
        }

        if (mission.getUserId() != currentUser.getUserId()) {
            return new ResponseEntity(new ReturnMsg("Unauthorized, this Questionare are not yours."), HttpStatus.UNAUTHORIZED);
        }

        //返回的json对象
        JSONObject ReSum = new JSONObject(new LinkedHashMap());
        ReSum.put("missionId",missionId);
        ReSum.put("taskNum",mission.getTaskNum());
        ReSum.put("finishNum",0);

        ArrayList<Task> tasks = missionService.selectTasksByMissionId(missionId);
        //return new ResponseEntity(tasks,HttpStatus.OK);
        int finishNum = 0;
        ArrayList<Questionare> QuesList = new ArrayList<Questionare>();
        //System.out.println("tasksize"+tasks.size()+"listsize"+QuesList.size());
        if (tasks != null) {
            for(int i=0; i < tasks.size();i++)
            {
                Task buff = tasks.get(i);
                if(buff.getAccUserId() != null)
                {
                    finishNum++;
                    System.out.println("xxx"+finishNum);
                }
                QuesList.add(questionareService.selectQuestionareByTaskID(buff.getTaskId()));

            }

        }
        ArrayList<Question> Answers = questionService.selectQuestionByQuestionareID(QuesList.get(0).getQuestionareId());

        //初始化格式
        ReSum.put("QATitle",QuesList.get(0).getTitle());
        ReSum.put("QADes",QuesList.get(0).getDescription());

        if(QuesList != null)
        {
            for(int i=1; i < QuesList.size();i++)
            {
                Questionare Ques = QuesList.get(i);

                ArrayList<Question> AnsSum = questionService.selectQuestionByQuestionareID(Ques.getQuestionareId());
                for(int j=0;j < AnsSum.size();j++)
                {
                    Question QueBuff = AnsSum.get(j);
                    //选择题
                    if(QueBuff.getQuestionType() == 0 || QueBuff.getQuestionType() == 1)
                    {
                        Question AnsIndex = Answers.get(j);
                        AnsIndex.setAnswer(AnsIndex.getAnswer()+QueBuff.getAnswer());
                        Answers.set(j,AnsIndex);
                    }


                    //问答题
                    if(QueBuff.getQuestionType() == 2)
                    {
                        Question AnsIndex = Answers.get(j);
                        AnsIndex.setAnswer(AnsIndex.getAnswer()+"; "+QueBuff.getAnswer());
                        Answers.set(j,AnsIndex);
                    }
                }


            }
        }

        JSONArray Ans = new JSONArray();
        for(int i=0;i < Answers.size();i++)
        {

            Question Que = Answers.get(i);
            Que.setQuestionId(i+1);
            Que.setQuestionareId(0);
            Ans.add(Que);
        }
        ReSum.put("questions",Ans);
        //String questionareList_str = JSONObject.toJSONString(QuesList);
        ReSum.put("finishNum",finishNum);

        return new ResponseEntity(ReSum,HttpStatus.OK);



    //return 0;
    }

}
