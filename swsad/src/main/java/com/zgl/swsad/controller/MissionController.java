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


    //新建问卷任务
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

            mission_json.put("missionStatus",0);
            mission_json.put("userId",currentUser.getUserId());

            Mission mission = (Mission) JSONObject.toJavaObject(mission_json, Mission.class);

            if(currentUser.getBalance()-mission.getMoney() < 0)
            {
                //missionService.deleteMission(missionId);
                return new ResponseEntity(new ReturnMsg("Your balance is not enough!"), HttpStatus.INTERNAL_SERVER_ERROR);
            }

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
                task_json.put("taskStatus",0);
                task_json.put("pubUserId",currentUser.getUserId());
                JSONObject questionare_json = param.getJSONObject("questionare");
                JSONArray questions_json = questionare_json.getJSONArray("questions");
                questionare_json.put("questionNum",questions_json.size());

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
                    question_json.remove("answer");

                    //根据queType调整choiceNum和choiceStr
                    if((int)question_json.get("questionType") == 2)//问答题
                    {
                        question_json.put("choiceNum",0);
                        question_json.remove("choiceStr");
                    }
                    else if((int)question_json.get("questionType") == 1)//多选
                    {
                        //多选应该是选多少个都行，包括1个，所以对choiceNum无更多要求
                        if((int)question_json.get("choiceNum") == 0)
                        {
                            return new ResponseEntity(new ReturnMsg("ChoiceNum cannot be 0 !"), HttpStatus.BAD_REQUEST);
                        }
                        if(question_json.get("choiceStr") == null)
                        {
                            return new ResponseEntity(new ReturnMsg("choiceStr cannot be null !"), HttpStatus.BAD_REQUEST);
                        }

                    }
                    else if((int)question_json.get("questionType") == 0)//单选
                    {
                        if((int)question_json.get("choiceNum") != 1)
                        {
                            return new ResponseEntity(new ReturnMsg("choiceNum of single-choice question must be 1 !"), HttpStatus.BAD_REQUEST);
                        }
                        if(question_json.get("choiceStr") == null)
                        {
                            return new ResponseEntity(new ReturnMsg("choiceStr cannot be null !"), HttpStatus.BAD_REQUEST);
                        }
                    }


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

        userService.updateBnC(user.getUserId(),user.getBalance()-mission.getMoney(),user.getCreditVal()+1);
        return new ResponseEntity(new ReturnMsg("create task successfully!"), HttpStatus.OK);


    }

    //新建mission
//    @CrossOrigin
//    @Authorization
//    @RequestMapping(value="/missions", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public Object createMission (@RequestBody Mission mission, @CurrentUser User currentUser) {
//        int userId = mission.getUserId();
//        if (userService.selectUser(userId) == null) {
//            return new ResponseEntity(new ReturnMsg("User not found."), HttpStatus.NOT_FOUND);
//        }
//
//        if (mission.getUserId() != currentUser.getUserId()) {
//            return new ResponseEntity(new ReturnMsg("Unauthorized."), HttpStatus.UNAUTHORIZED);
//        }
//
//        int missionId = missionService.insertMission(mission);
//        if(missionId  != Constants.INSERT_FAIL)
//        {
//            Mission newMission = missionService.selectMission(missionId);
//            return new ResponseEntity(newMission, HttpStatus.CREATED);
//        }  else {
//            return new ResponseEntity(new ReturnMsg("Server error."), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//    }

    //新建errand任务
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
        mission_json.put("missionStatus",0);
        mission_json.put("userId",currentUser.getUserId());


        Mission mission = (Mission)JSONObject.toJavaObject(mission_json,Mission.class);

        if(currentUser.getBalance()-mission.getMoney() < 0)
        {
            //missionService.deleteMission(missionId);
            return new ResponseEntity(new ReturnMsg("Your balance is not enough!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

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
            if(loopTime != 1){
                missionService.deleteMission(missionId);
                return new ResponseEntity(new ReturnMsg("taskNum should be 1 !"), HttpStatus.BAD_REQUEST);
            }

            for(int count = 0; count < loopTime;count++) {
                JSONObject task_json = param.getJSONObject("task");

                task_json.put("MissionId", missionId);
                task_json.put("taskStatus",0);
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
            return new ResponseEntity(new ReturnMsg("Error: creat fail !\n"+e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }


        User user = userService.selectUser(mission.getUserId());

        userService.updateBnC(user.getUserId(),user.getBalance()-mission.getMoney(),user.getCreditVal()+1);
        return new ResponseEntity(new ReturnMsg("create task successfully!"), HttpStatus.OK);


    }

    //得到所有的mission
    @CrossOrigin
    @RequestMapping(value="/missions", method=RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object getAllMissions() {
        ArrayList<Mission> missions = missionService.selectAllMissions();
        return new ResponseEntity(missions, HttpStatus.OK);
    }

    //通过missionId查找mission
    @CrossOrigin
    @RequestMapping(value="/missions/{missionId}", method=RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object getMissionById(@PathVariable int missionId) {
        Mission mission = missionService.selectMission(missionId);

        if (mission == null) {
            return new ResponseEntity(new ReturnMsg("Mission not found."), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(mission, HttpStatus.OK);
    }

    //通过missionId修改mission
//    @CrossOrigin
//    @Authorization
//    @RequestMapping(value = "/missions/{missionId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    //missionId和userId不需要传过来
//    public Object modifyMission(@RequestBody Mission mission, @PathVariable int missionId, @CurrentUser User currentUser) {
//        Mission curMission = missionService.selectMission(missionId);
//        if (curMission == null) {
//            return new ResponseEntity(new ReturnMsg("Mission not found!"), HttpStatus.NOT_FOUND);
//        }
//
//        if (curMission.getUserId() != currentUser.getUserId()) {
//            return  new ResponseEntity(new ReturnMsg("Unanthorized, this is not your mission."), HttpStatus.UNAUTHORIZED);
//        }
//
//        mission.setMissionId(missionId);
//        mission.setUserId(curMission.getUserId());
//        int count = missionService.updateMission(mission);
//        if (count == 1) {
//            return  new ResponseEntity(new ReturnMsg("Modify mission success."), HttpStatus.OK);
//        }
//
//        return new ResponseEntity(new ReturnMsg("Server error."), HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    //通过missionId删除mission
//    @CrossOrigin
//    @Authorization
//    @RequestMapping(value = "/missions/{missionId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public Object deleteMission(@PathVariable int missionId, @CurrentUser User currentUser) {
//        Mission curMission = missionService.selectMission(missionId);
//        if (curMission == null) {
//            return new ResponseEntity(new ReturnMsg("Mission not found!"), HttpStatus.NOT_FOUND);
//        }
//
//        if (curMission.getUserId() != currentUser.getUserId()) {
//            return new ResponseEntity(new ReturnMsg("Unauthorized, this is not your mission."), HttpStatus.UNAUTHORIZED);
//        }
//
//        int count = missionService.deleteMission(missionId);
//        if (count == 1) {
//            return  new ResponseEntity(new ReturnMsg("Delete mission success."), HttpStatus.OK);
//        }
//        return new ResponseEntity(new ReturnMsg("Server error."), HttpStatus.INTERNAL_SERVER_ERROR);
//    }


    //通过missionId返回对应的所有task
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


    //通过missionId返回问卷完成情况
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
        int finishNum = 0;
        ArrayList<Questionare> QuesList = new ArrayList<Questionare>();
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

        for(int i=0; i < Answers.size();i++)
        {
            Answers.get(i).setAnswer("");
        }

        //初始化格式
        ReSum.put("QATitle",QuesList.get(0).getTitle());
        ReSum.put("QADes",QuesList.get(0).getDescription());

        if(QuesList != null)
        {
            for(int i=0; i < QuesList.size();i++)
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
                        //AnsIndex.setAnswer("");
                        //System.out.println("ans"+AnsIndex.getAnswer()+" QueBuff"+QueBuff.getAnswer());
                        if(QueBuff.getAnswer() != null)
                        {
                            AnsIndex.setAnswer(AnsIndex.getAnswer()+QueBuff.getAnswer());
                            Answers.set(j,AnsIndex);
                        }

                    }


                    //问答题
                    if(QueBuff.getQuestionType() == 2)
                    {
                        Question AnsIndex = Answers.get(j);
                        //AnsIndex.setAnswer("");
                        if(QueBuff.getAnswer() != null)
                        {
                            AnsIndex.setAnswer(AnsIndex.getAnswer()+QueBuff.getAnswer()+"; ");
                            Answers.set(j,AnsIndex);
                        }


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
            if(Que.getAnswer() == "")
                Que.setAnswer(null);
            Ans.add(Que);
        }
        ReSum.put("questions",Ans);
        ReSum.put("finishNum",finishNum);

        return new ResponseEntity(ReSum,HttpStatus.OK);
    }

    //返回所有的mission以及task详情
    @CrossOrigin
    @Authorization
    @RequestMapping(value="/missions/AllMissions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object GetAllMissions(@CurrentUser User currentUser) throws ParseException {
        ArrayList<Mission> AllMissions = missionService.selectAllMissions();
        //System.out.println("missions"+AllMissions.size());
        ArrayList<JSONObject> ReMissions = new ArrayList();


        for(int i=0; i < AllMissions.size();i++)
        {

            Mission BuffMission = AllMissions.get(i);

            Calendar calendar = Calendar.getInstance();
            String  strNow = calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH);
            String strDeadline = BuffMission.getDeadLine();
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
                BuffMission.setMissionStatus(2);
                missionService.updateMission(BuffMission);
            }
            
            
            JSONObject BuffJson = new JSONObject(new LinkedHashMap());
            BuffJson.put("missionId",BuffMission.getMissionId());
            BuffJson.put("title",BuffMission.getTitle());
            BuffJson.put("publishTime",BuffMission.getPublishTime());
            BuffJson.put("deadLine",BuffMission.getDeadLine());
            BuffJson.put("reportNum",BuffMission.getReportNum());
            BuffJson.put("missionStatus",BuffMission.getMissionStatus());
            BuffJson.put("tags",BuffMission.getTags());


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

//            int finishNum = 0;
//            for(int k=0;k < TaskFromBuffMission.size();k++)
//            {
//                if(TaskFromBuffMission.get(k).getTaskStatus() == 3)
//                {
//                    finishNum++;
//                }
//            }

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
            BuffJson.put("aveMoney",BuffMission.getMoney()/BuffMission.getTaskNum() );

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


    //通过missionId接受任务
    @CrossOrigin
    @Authorization
    @RequestMapping(value="/missions/{missionId}/accept", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object acceptMission (@PathVariable int missionId, @CurrentUser User currentUser) throws ParseException {

        if(currentUser.getCreditVal() < 0)
        {
            return new ResponseEntity(new ReturnMsg("Sorry, the least credit value to accept task is 0 !"), HttpStatus.NOT_FOUND);
        }

        Mission mission = missionService.selectMission(missionId);

        if(mission.getUserId() == currentUser.getUserId())
        {
            return new ResponseEntity(new ReturnMsg("You can't accept your published task!"), HttpStatus.BAD_REQUEST);
        }
        
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

    @CrossOrigin
    @Authorization
    @RequestMapping(value="/report/{missionId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object reportMission (@PathVariable int missionId,@RequestBody JSONObject param)  {
        Mission BuffMission = missionService.selectMission(missionId);

        if(BuffMission == null)
        {
            return new ResponseEntity(new ReturnMsg("mission not found!"), HttpStatus.NOT_FOUND);
        }

        BuffMission.setReportNum(BuffMission.getReportNum()+1);

        missionService.updateMission(BuffMission);
        return new ResponseEntity(new ReturnMsg("report successfully !"), HttpStatus.OK);
    }



}