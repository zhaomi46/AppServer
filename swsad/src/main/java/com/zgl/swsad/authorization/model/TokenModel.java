package com.zgl.swsad.authorization.model;

import java.util.Date;

public class TokenModel {

    //用户id
    private long userId;

    //签名
    private String signature;

    //token创建时间
    private Date createDate;

    //是否有效，登出则无效
    private Boolean valid;

    public TokenModel(long userId, Date createDate, Boolean valid, String signature) {
        this.userId = userId;
        this.createDate = createDate;
        this.valid = valid;
        this.signature = signature;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }
}
