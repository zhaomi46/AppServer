package com.zgl.swsad.mapper;

import com.zgl.swsad.model.Mission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface MissionMapper {
    @Insert("INSERT INTO mission (publishTime, missionStatus, title, deadLine, tags, money, userId)" +
            "VALUES (#{publishTime}, #{missionStatus}, #{title}, #{deadLine}, #{tags}, #{money}, #{userId})")
    int insertMission(Mission mission);

    @Select("SELECT * FROM mission")
    ArrayList<Mission> selectAllMission();

    @Select("SELECT * FROM mission WHERE missionId = #{missionId}")
    Mission selectMission(int missionId);

    @Update("UPDATE mission" +
            "SET publishTime = #{publishTime}, missionStatus = #{missionStatus}, title = #{title}, deadLine = #{deadLine}, tags = #{tags}, money = #{money}, userId = #{userId}" +
            "WHERE missionId = #{missionId}")
    int updateMission(Mission mission);

    @Delete("DELETE FROM mission where missionId=#{missionId}")
    int deleteMission(int missionId);
    /*
    @Select()
    ArrayList<Task> selectTaskOfMission(int missionId);

    @Select
    ArrayList<Report> selectReportOfMission(int missionId);
     */
}
