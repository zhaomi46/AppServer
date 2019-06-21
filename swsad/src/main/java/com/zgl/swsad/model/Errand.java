package com.zgl.swsad.model;

public class Errand {

    private Integer errandId;
    private String description;



    private String pic;
    private Integer taskId;

    public Integer getErrandId() {
        return errandId;
    }

    public void setErrandId(Integer errandId) {
        this.errandId = errandId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }


}
