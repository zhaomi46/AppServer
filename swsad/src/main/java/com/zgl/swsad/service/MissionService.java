package com.zgl.swsad.service;

import com.zgl.swsad.mapper.MissionMapper;
import com.zgl.swsad.model.Mission;
import com.zgl.swsad.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zgl.swsad.config.Constants;
import java.util.ArrayList;

@Service
public class MissionService {
    @Autowired
    MissionMapper missionMapper;

    public int insertMission(Mission mission) {
        int count = missionMapper.insertMission(mission);
        return count == 0 ? Constants.INSERT_FAIL : mission.getMissionId();
    }

    public Mission selectMission(int missionId) {
        return missionMapper.selectMission(missionId);
    }

    public   ArrayList<Mission> selectMissionByUserId(int userId) {
        return missionMapper.selectMissionByUserId(userId);
    }

    public ArrayList<Mission> selectAllMissions() {
        return missionMapper.selectAllMissions();
    }

    public int updateMission(Mission mission) {
        return missionMapper.updateMission(mission);
    }

    public int deleteMission(int missionId) {
        return missionMapper.deleteMission(missionId);
    }

    public ArrayList<Task> selectTasksByMissionId(int missionId) {
        return missionMapper.selectTasksByMissionId(missionId);
    }

}
