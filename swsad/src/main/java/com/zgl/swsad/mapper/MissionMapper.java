package com.zgl.swsad.mapper;

import com.zgl.swsad.model.Mission;
import com.zgl.swsad.model.Task;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface MissionMapper {
    @Insert("INSERT INTO mission (publishTime, missionStatus, title, deadLine, tags, money, userId) " +
            "VALUES (#{publishTime}, #{missionStatus}, #{title}, #{deadLine}, #{tags}, #{money}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "missionId")
    int insertMission(Mission mission);

    @Select("SELECT * FROM mission")
    ArrayList<Mission> selectAllMissions();

    @Select("SELECT * FROM mission WHERE missionId = #{missionId}")
    Mission selectMission(int missionId);

    @Select("SELECT * FROM mission WHERE userId = #{userId}")
    ArrayList<Mission> selectMissionByUserId(int userId);

    @Update("UPDATE mission " +
            "SET publishTime = #{publishTime}, missionStatus = #{missionStatus}, title = #{title}, deadLine = #{deadLine}, tags = #{tags}, money = #{money}, userId = #{userId} " +
            "WHERE missionId = #{missionId}")
    int updateMission(Mission mission);

    @Delete("DELETE FROM mission where missionId=#{missionId}")
    int deleteMission(int missionId);

    @Select("SELECT * FROM task " +
            "WHERE missionId = #{missionId}")
    ArrayList<Task> selectTasksByMissionId(int missionId);
    /*
    @Select
    ArrayList<Report> selectReportOfMission(int missionId);
     */
}
