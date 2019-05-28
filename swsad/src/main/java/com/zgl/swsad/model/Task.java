package com.zgl.swsad.model;

public class Task {
    private Integer taskId;
    private Integer missionId;
    private Integer pubUserId;
    private Integer accUserId;
    private Integer taskStatus; // 有哪些status，分别对应哪些数字
    private String finishTime;
    private Integer taskType;

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getMissionId() {
        return missionId;
    }

    public void setMissionId(Integer missionId) {
        this.missionId = missionId;
    }

    public Integer getPubUserId() {
        return pubUserId;
    }

    public void setPubUserId(Integer pubUserId) {
        this.pubUserId = pubUserId;
    }

    public Integer getAccUserId() {
        return accUserId;
    }

    public void setAccUserId(Integer accUserId) {
        this.accUserId = accUserId;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }




}
