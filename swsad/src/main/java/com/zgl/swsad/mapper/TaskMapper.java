package com.zgl.swsad.mapper;

import  com.zgl.swsad.model.Task;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface TaskMapper {

    @Insert("INSERT INTO task (taskType, taskStatus, finishTime, pubUserId, missionId, accUserId)" +
            " Values(#{taskType}, #{taskStatus}, #{finishTime}, #{pubUserId}, #{missionId}, #{accUserId})")
    @Options(useGeneratedKeys = true, keyProperty = "taskId")
    int insertTask(Task task);

    @Update("UPDATE task set taskType=#{taskType}, taskStatus=#{taskStatus}, finishTime=#{finishTime}, pubUserId=#{pubUserId}, missionId=#{missionId}, " +
            "accUserId=#{accUserId} WHERE taskId=#{taskId}")
    int updateTask(Task task);

    @Select("SELECT * FROM task WHERE taskId = #{taskId}")
    Task selectTask(int taskId);

    @Select("SELECT * FROM task WHERE accUserId = #{accUserId}")
    ArrayList<Task> selectTaskByAccUserId(int accUserId);

    @Delete("DELETE FROM task WHERE taskId = #{taskId}")
    int deleteTask(int taskId);

}
