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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    public Object createMissionQA (@RequestBody JSONObject param, @CurrentUser User currentUser) throws ParseException {



            JSONObject mission_json = param.getJSONObject("mission");
            Calendar calendar = Calendar.getInstance();
            String date = calendar.get(Calendar.YEAR)+"-";
            if(calendar.get(Calendar.MONTH)+1 < 10)
            {
                date = date + "0" +(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH);
            }
            else
            {
                date = date +(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH);
            }
            mission_json.put("publishTime",date);

            mission_json.put("userId",currentUser.getUserId());

            Mission mission = (Mission) JSONObject.toJavaObject(mission_json, Mission.class);

            //检测deadline在publishtime之后
            String strDeadline = mission.getDeadLine();
            SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
            Date dateDeadline =sdf.parse(strDeadline);
            Date datePublish = sdf.parse(date);
            Calendar calDeadLine = Calendar.getInstance();
            Calendar calPublish = Calendar.getInstance();
            calDeadLine.setTime(dateDeadline);
            calPublish.setTime(datePublish);

            boolean validDeadline = (calDeadLine.equals(calPublish) || calDeadLine.after(calPublish));
            if(!validDeadline)
            {
                return new ResponseEntity(new ReturnMsg("Deadline error !"), HttpStatus.BAD_REQUEST);
            }

            int missionId = missionService.insertMission(mission);
            if (missionId == Constants.INSERT_FAIL) {
                return new ResponseEntity(new ReturnMsg("Server error.mission creat fail"), HttpStatus.INTERNAL_SERVER_ERROR);
                //String ReStr = "Server error.mission creat fail";
            }


        try {
            int loopTime = (int) mission_json.get("taskNum");
            if(loopTime <= 0){
                missionService.deleteMission(missionId);
                return new ResponseEntity(new ReturnMsg("taskNum should be greater than 0 !"), HttpStatus.BAD_REQUEST);
            }
            for (int count = 0; count < loopTime; count++) {
                JSONObject task_json = param.getJSONObject("task");
                task_json.remove("accUserId");
                task_json.remove("finishTime");
                task_json.put("MissionId", missionId);
                task_json.put("pubUserId",currentUser.getUserId());
                JSONObject questionare_json = param.getJSONObject("questionare");
                JSONArray questions_json = questionare_json.getJSONArray("questions");
                //int count = 0;
                //int num =  questionare_json.size() - 3;
                //注意这里的questionare_size得到的长度不是以JsonObject作为单位，而是以键值对作为单位,所以还要加上前面的3个键值对

                Task task = (Task) JSONObject.toJavaObject(task_json, Task.class);

                if(task.getTaskType() != 1)
                {
                    missionService.deleteMission(missionId);
                    return new ResponseEntity(new ReturnMsg("Wrong taskType !"), HttpStatus.UNAUTHORIZED);
                }
                int opNum1 = taskService.insertTask(task);
                if (opNum1 != Constants.INSERT_FAIL) {

                    //count++;
                    questionare_json.put("taskId", opNum1);
                } else {
                    System.out.println("task fail");
                    missionService.deleteMission(missionId);
                    return new ResponseEntity(new ReturnMsg("Task creat fail !"), HttpStatus.INTERNAL_SERVER_ERROR);
                }
                Questionare questionare = (Questionare) JSONObject.toJavaObject(questionare_json, Questionare.class);
                int opNum2 = questionareService.insertQuestionare(questionare);
                if (opNum2 == Constants.INSERT_FAIL) {
                    missionService.deleteMission(missionId);
                    //count++;
                    return new ResponseEntity(new ReturnMsg("Questionare creat fail !"), HttpStatus.INTERNAL_SERVER_ERROR);
                }
                for (int i = 0; i < questions_json.size(); i++) {
                    JSONObject question_json = (JSONObject) questions_json.getJSONObject(i); //这里不能是get(i),get(i)只会得到键值对
                    question_json.put("questionareId", opNum2);
                    Question question = (Question) JSONObject.toJavaObject(question_json, Question.class);
                    int opNum3 = questionService.insertQuestion(question);
                    if (opNum3 != 1) {
                        missionService.deleteMission(missionId);
                        //count++;
                        return new ResponseEntity(new ReturnMsg("Some questions creat fail !"), HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("尝试回滚！");
            missionService.deleteMission(missionId);
            return new ResponseEntity(new ReturnMsg("Error: creat fail !\n"+e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        User user = userService.selectUser(mission.getUserId());
        if(user.getBalance()-mission.getMoney() < 0)
        {
            missionService.deleteMission(missionId);
            return new ResponseEntity(new ReturnMsg("Your balance is not enough!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        userService.updateBnC(user.getUserId(),user.getBalance()-mission.getMoney(),user.getCreditVal()+1);
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
    public Object createMissionER (@RequestBody JSONObject param, @CurrentUser User currentUser) throws ParseException {

        JSONObject mission_json = param.getJSONObject("mission");

        Calendar calendar = Calendar.getInstance();
        String date = calendar.get(Calendar.YEAR)+"-";
        if(calendar.get(Calendar.MONTH)+1 < 10)
        {
            date = date + "0" +(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH);
        }
        else
        {
            date = date +(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH);
        }
        mission_json.put("publishTime",date);



        mission_json.put("userId",currentUser.getUserId());


        Mission mission = (Mission)JSONObject.toJavaObject(mission_json,Mission.class);

        //检测deadline在publishtime之后
        String strDeadline = mission.getDeadLine();
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        Date dateDeadline =sdf.parse(strDeadline);
        Date datePublish = sdf.parse(date);
        Calendar calDeadLine = Calendar.getInstance();
        Calendar calPublish = Calendar.getInstance();
        calDeadLine.setTime(dateDeadline);
        calPublish.setTime(datePublish);

        boolean validDeadline = (calDeadLine.equals(calPublish) || calDeadLine.after(calPublish));
        if(!validDeadline)
        {
            return new ResponseEntity(new ReturnMsg("Deadline error !"), HttpStatus.BAD_REQUEST);
        }

        int missionId = missionService.insertMission(mission);
        if(missionId  == Constants.INSERT_FAIL)
        {
            return new ResponseEntity(new ReturnMsg("Server error.mission creat fail"), HttpStatus.INTERNAL_SERVER_ERROR);
        }



        try {
            int loopTime = (int) mission_json.get("taskNum");
            if(loopTime <= 0){
                missionService.deleteMission(missionId);
                return new ResponseEntity(new ReturnMsg("taskNum should be greater than 0 !"), HttpStatus.BAD_REQUEST);
            }

            for(int count = 0; count < loopTime;count++) {
                JSONObject task_json = param.getJSONObject("task");

                task_json.put("MissionId", missionId);
                task_json.remove("accUserId");
                task_json.remove("finishTime");
                task_json.put("pubUserId",currentUser.getUserId());
                JSONObject errand_json = param.getJSONObject("errand");


                Task task = (Task) JSONObject.toJavaObject(task_json, Task.class);
                if (task.getTaskType() != 0) {
                    missionService.deleteMission(missionId);
                    return new ResponseEntity(new ReturnMsg("Wrong taskType !"), HttpStatus.UNAUTHORIZED);
                }
                int opNum1 = taskService.insertTask(task);
                if (opNum1 == Constants.INSERT_FAIL) {
                    missionService.deleteMission(missionId);
                    return new ResponseEntity(new ReturnMsg("Task creat fail !"), HttpStatus.INTERNAL_SERVER_ERROR);
                } else {
                    errand_json.put("taskId", opNum1);
                }

                Errand errand = (Errand) JSONObject.toJavaObject(errand_json, Errand.class);
                int opNum2 = errandService.insertErrand(errand);
                if (opNum2 == Constants.INSERT_FAIL) {
                    missionService.deleteMission(missionId);
                    return new ResponseEntity(new ReturnMsg("Errand creat fail !"), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("尝试回滚！");
            missionService.deleteMission(missionId);
            return new ResponseEntity(new ReturnMsg("Error: creat fail !"+e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }


        User user = userService.selectUser(mission.getUserId());
        if(user.getBalance()-mission.getMoney() < 0)
        {
            missionService.deleteMission(missionId);
            return new ResponseEntity(new ReturnMsg("Your balance is not enough!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        userService.updateBnC(user.getUserId(),user.getBalance()-mission.getMoney(),user.getCreditVal()+1);
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

        if (mission.getUserId() == currentUser.getUserId()) {
            ArrayList<Task> tasks = missionService.selectTasksByMissionId(missionId);
            return new ResponseEntity(tasks, HttpStatus.OK);
        } else {
            ArrayList<Task> tasks = missionService.selectTasksByMissionId(missionId);
            ArrayList<Task> ret = new ArrayList<Task>();
            for (Task t : tasks) {
                if (t.getAccUserId() == currentUser.getUserId()) {
                    ret.add(t);
                }
            }
            if (ret.size() != 0) {
                return new ResponseEntity(ret, HttpStatus.OK);
            }
        }

        return new ResponseEntity(new ReturnMsg("Unauthorized."), HttpStatus.UNAUTHORIZED);
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
                if(buff.getTaskStatus() == 3)
                {
                    finishNum++;

                }

                Questionare BuffQA = questionareService.selectQuestionareByTaskID(buff.getTaskId());
                if(BuffQA == null)
                {
                    return new ResponseEntity(new ReturnMsg("This mission type is errand !"), HttpStatus.BAD_REQUEST);
                }

                QuesList.add(BuffQA);


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

    @CrossOrigin
    @Authorization
    @RequestMapping(value="/missions/AllMissions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object GetAllMissions(@CurrentUser User currentUser) {
        ArrayList<Mission> AllMissions = missionService.selectAllMissions();
        //System.out.println("missions"+AllMissions.size());
        ArrayList<JSONObject> ReMissions = new ArrayList();


        for(int i=0; i < AllMissions.size();i++)
        {

            Mission BuffMission = AllMissions.get(i);
            JSONObject BuffJson = new JSONObject(new LinkedHashMap());
            BuffJson.put("missionId",BuffMission.getMissionId());
            BuffJson.put("title",BuffMission.getTitle());
            BuffJson.put("publishTime",BuffMission.getPublishTime());
            BuffJson.put("deadLine",BuffMission.getDeadLine());


            ArrayList<Task> TaskFromBuffMission = taskService.selectTaskByMissionId(BuffMission.getMissionId());
            if(TaskFromBuffMission == null)
            {
                return new ReturnMsg("The mission"+BuffMission.getMissionId()+" have no tasks !");
            }
            boolean myAccMission = false;
            for(int ite = 0; ite < TaskFromBuffMission.size();ite++)
            {
                if(TaskFromBuffMission.get(ite).getAccUserId() == currentUser.getUserId())
                {
                    myAccMission = true;
                    break;
                }
            }

            Task BuffTask = TaskFromBuffMission.get(0);

            if(BuffTask.getTaskType() == 0)
            {
                BuffJson.put("taskType",0);
                Errand BuffErrand = errandService.selectErrandByTaskID(BuffTask.getTaskId());
                BuffJson.put("description",BuffErrand.getDescription());
            }
            else if(BuffTask.getTaskType() == 1)
            {
                BuffJson.put("taskType",1);
                Questionare BuffQA = questionareService.selectQuestionareByTaskID(BuffTask.getTaskId());
                BuffJson.put("description",BuffQA.getDescription());
            }

            User BuffUser = userService.selectUser(BuffTask.getPubUserId());
            BuffJson.put("avator",BuffUser.getAvator());
            BuffJson.put("myAccept",myAccMission);

            boolean myPubMission = (currentUser.getUserId() == BuffMission.getUserId());
            BuffJson.put("myPub",myPubMission);
            BuffJson.put("aveMoney",BuffMission.getMoney()/BuffMission.getTaskNum());

            if( !ReMissions.add(BuffJson))
            {
                return new ReturnMsg("MissionId "+BuffMission.getMissionId()+" cannot get !");
            }

        }

        JSONObject ReJson = new JSONObject(new LinkedHashMap());
        ReJson.put("MissionNum",AllMissions.size());
        ReJson.put("AllMissions",ReMissions);

        return new ResponseEntity(ReJson,HttpStatus.OK);

    }


    @CrossOrigin
    @Authorization
    @RequestMapping(value="/missions/{missionId}/accept", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object acceptMission (@PathVariable int missionId, @CurrentUser User currentUser) throws ParseException {

        if(currentUser.getCreditVal() < 0)
        {
            return new ResponseEntity(new ReturnMsg("Sorry, the least credit value to accept task is 0 !"), HttpStatus.NOT_FOUND);
        }

        Mission mission = missionService.selectMission(missionId);
        if(mission == null)
        {
            return new ResponseEntity(new ReturnMsg("The mission doesn't exist !"), HttpStatus.NOT_FOUND);
        }
        if(mission.getMissionStatus() == 1)//接受人数已满
        {
            return new ResponseEntity(new ReturnMsg("The mission has been accepted !"), HttpStatus.BAD_REQUEST);
        }
        else if (mission.getMissionStatus() == 2)//已过期
        {
            return new ResponseEntity(new ReturnMsg("The mission is over the deadline !"), HttpStatus.BAD_REQUEST);
        }
        Calendar calendar = Calendar.getInstance();

        String  strNow = calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH);
        String strDeadline = mission.getDeadLine();
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        Date dateDeadline =sdf.parse(strDeadline);
        Date dataNow = sdf.parse(strNow);
        Calendar calDeadLine = Calendar.getInstance();
        Calendar calNow = Calendar.getInstance();
        calDeadLine.setTime(dateDeadline);
        calNow.setTime(dataNow);

        //检测是否任务已过期
        boolean validMission = (calDeadLine.equals(calNow) || calDeadLine.after(calNow));
        //System.out.println(calDeadLine+"xxx"+calendar);
        if(!validMission)
        {
            mission.setMissionStatus(2);
            return new ResponseEntity(new ReturnMsg("The mission is over the deadline !"), HttpStatus.BAD_REQUEST);
        }

        ArrayList<Task> Tasks = taskService.selectTaskByMissionId(missionId);
        int i;
        boolean acc = false;
        for( i=0; i < Tasks.size(); i++)
        {
            if(Tasks.get(i).getAccUserId() == null)
            {
                Task buff = Tasks.get(i);
                buff.setAccUserId(currentUser.getUserId());
                buff.setTaskStatus(1);
                taskService.updateTask(buff);
                acc = true;
                i++;
                break;
            }
        }

        //mission接受人数已满
        if(i == Tasks.size() && acc == true)
        {
            mission.setMissionStatus(1);
            missionService.updateMission(mission);
        }

            return new ResponseEntity(new ReturnMsg("Accept successfully !"), HttpStatus.OK);



        //return new ResponseEntity(new ReturnMsg("The mission has been accepted !"), HttpStatus.BAD_REQUEST);
    }



}
