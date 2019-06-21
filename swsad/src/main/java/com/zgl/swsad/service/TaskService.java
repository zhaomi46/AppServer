package com.zgl.swsad.service;

import com.zgl.swsad.config.Constants;
import com.zgl.swsad.mapper.TaskMapper;
import com.zgl.swsad.model.Mission;
import com.zgl.swsad.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class TaskService {
    @Autowired
    TaskMapper taskMapper;

    //新建task
    public int insertTask(Task task) {
        int count = taskMapper.insertTask(task);
        return count == 0 ? Constants.INSERT_FAIL : task.getTaskId();
    }

    // 通过id获得某个任务详情
    public Task selectTask(int taskId) {
        return taskMapper.selectTask(taskId);
    }

    //用户通过自己的ID来获得自己接收的任务
    public ArrayList<Task> selectTaskByAccUserID(int accUserId)
    {
        return taskMapper.selectTaskByAccUserId(accUserId);
    }

    //Get tasks by missionId
    public ArrayList<Task> selectTaskByMissionId(int missionId) {return taskMapper.selectTaskByMissionId(missionId);}

    //调整某个任务
    public int updateTask(Task task) { return taskMapper.updateTask(task); }

    //删除任务
    public int deleteTask(int taskId){ return taskMapper.deleteTask(taskId);}


}

