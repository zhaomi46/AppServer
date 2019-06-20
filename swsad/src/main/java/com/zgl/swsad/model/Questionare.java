package com.zgl.swsad.model;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

public class Questionare {
    private Integer questionareId;
    private Integer questionNum;
    private Integer taskId;
    private String description;
    private String title;

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }



    public Integer getQuestionareId() {
        return questionareId;
    }

    public void setQuestionareId(Integer questionareId) {
        this.questionareId = questionareId;
    }

    public Integer getQuestionNum() {
        return questionNum;
    }

    public void setQuestionNum(Integer questionNum) {
        this.questionNum = questionNum;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    @Autowired
    private ArrayList<Question> questions;
}
