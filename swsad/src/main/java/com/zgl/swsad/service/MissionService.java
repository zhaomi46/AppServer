package com.zgl.swsad.service;

import com.zgl.swsad.mapper.MissionMapper;
import com.zgl.swsad.model.Mission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class MissionService {
    @Autowired
    MissionMapper missionMapper;

    public int insertMission(Mission mission) {
        return  missionMapper.insertMission(mission);
    }

    public Mission selectMission(int missionId) {
        return missionMapper.selectMission(missionId);
    }

    public ArrayList<Mission> selectAllMission() {
        return missionMapper.selectAllMission();
    }

    public int updateMission(Mission mission) {
        return missionMapper.updateMission(mission);
    }

    public int deleteMission(int missionId) {
        return missionMapper.deleteMission(missionId);
    }

}
