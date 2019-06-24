package com.zgl.swsad.model;

public class Question {

    private Integer questionId;
    private Integer questionType;
    private String question;
    private String answer;
    private Integer choiceNum; // ?
    private String choiceStr; //这个变量是干啥的？
    private Integer questionareId;

    public Integer getQuestionType() {
        return questionType;
    }

    public void setQuestionType(Integer questionType) {
        this.questionType = questionType;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Integer getChoiceNum() {
        return choiceNum;
    }

    public void setChoiceNum(Integer choiceNum) {
        this.choiceNum = choiceNum;
    }

    public String getChoiceStr() {
        return choiceStr;
    }

    public void setChoiceStr(String choiceStr) {
        this.choiceStr = choiceStr;
    }

    public Integer getQuestionareId() {
        return questionareId;
    }

    public void setQuestionareId(Integer questionareId) {
        this.questionareId = questionareId;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }
}

