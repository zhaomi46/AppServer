package com.zgl.swsad.model;

import java.util.ArrayList;

/*
id	integer($int64)
userType	integer($int64)
name	string
avator	string
nickName	string
age	integer($int64)
sex	integer
grade	integer
major	string
mailAddr	string
phoneNum	string
creditValue	integer
balance	integer
tags	[string]
passwords	string
 */
public class User {
    private Integer userId;
    private Integer userType;
    private String name;
    private String avator;
    private String nickName;
    private Integer age;
    private Integer sex;
    private Integer grade;
    private String major;
    private String mailAddr;
    private String phoneNum;
    private Integer creditVal;
    private Integer balance;
    private String tags;
    private String password;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvator() {
        return avator;
    }

    public void setAvator(String avator) {
        this.avator = avator;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMailAddr() {
        return mailAddr;
    }

    public void setMailAddr(String mailAddr) {
        this.mailAddr = mailAddr;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public Integer getCreditVal() {
        return creditVal;
    }

    public void setCreditVal(Integer creditVal) {
        this.creditVal = creditVal;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
