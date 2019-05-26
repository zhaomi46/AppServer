package com.zgl.swsad.model;

public class Question {

    private int questionId;
    private int questionType;
    private String question;
    private String answer;
    private int choiceNum; // ?
    private String choiceStr; //这个变量是干啥的？
    private int questionareId;

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

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

    public Integer getChoicenum() {
        return choiceNum;
    }

    public void setChoicenum(Integer choicenum) {
        this.choiceNum = choicenum;
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

}

