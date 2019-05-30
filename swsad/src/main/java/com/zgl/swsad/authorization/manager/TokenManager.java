package com.zgl.swsad.authorization.manager;

import com.zgl.swsad.authorization.model.TokenModel;

import java.io.IOException;

public interface TokenManager {

    /**
     * 创建一个token关联上指定用户
     * @param userId 指定用户的id
     * @return 生成的token
     */
    public String createToken(Long userId, Boolean valid) throws Exception;

    /**
     * 检查token是否有效
     * @param model token
     * @return 是否有效
     */
    public boolean checkToken(TokenModel model) throws Exception;

    /**
     * 从字符串中解析token
     * @param authentication 加密后的字符串
     * @return
     */
    public TokenModel getToken(String authentication) throws IOException;

}