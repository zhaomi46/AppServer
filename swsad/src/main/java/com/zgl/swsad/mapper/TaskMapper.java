package com.zgl.swsad.mapper;

import  com.zgl.swsad.model.Task;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskMapper {

    @Insert("INSERT INTO task (taskType, taskStatus, finishTime, pubUserId, missionId, accUserId)" +
            " Values(#{taskType}, #{taskStatus}, #{finishTime}, #{pubUserId}, #{missionId}, #{accUserId})")
    int insertTask(Task task);

    @Update("UPDATE task set taskType=#{taskType}, taskStatus=#{taskStatus}, finishTime=#{finishTime}, pubUserId=#{pubUserId}, missionId=#{missionId}, " +
            "accUserId=#{accUserId} WHERE taskId=#{taskId}")
    int updateTask(Task task);

    @Select("SELECT * FROM task WHERE taskId = #{taskId}")
    Task selectTask(int id);

    @Delete("DELETE FROM task WHERE taskId = #{taskId}")
    int deleteTask(int id);

}
