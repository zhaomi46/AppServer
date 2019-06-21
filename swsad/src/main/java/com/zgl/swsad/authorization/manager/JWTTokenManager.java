package com.zgl.swsad.authorization.manager;

import com.zgl.swsad.authorization.model.TokenModel;
import com.zgl.swsad.config.Constants;
import com.zgl.swsad.util.HMACSHA256Util;
//import jdk.nashorn.internal.parser.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.stereotype.Component;
import java.util.Base64;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import com.alibaba.fastjson.JSON;


@Component
public class JWTTokenManager implements TokenManager {

    final Base64.Decoder decoder = Base64.getDecoder();
    final Base64.Encoder encoder = Base64.getEncoder();

    public String createToken(Long userId, Boolean valid) throws Exception {
        Date createDate = new Date();
        String text0 = encoder.encodeToString(userId.toString().getBytes());
        String text1 = encoder.encodeToString(createDate.toString().getBytes());
        String text2 = encoder.encodeToString(valid.toString().getBytes());
        String signature = HMACSHA256Util.HMACSHA256(text0 + text1+text2, Constants.TOKEN_KEY);
        TokenModel token = new TokenModel(userId, createDate, valid, signature);
        String tokenString = encoder.encodeToString(JSON.toJSONString(token).getBytes());
        return tokenString;
    }

    public TokenModel getToken(String authentication) throws IOException {
        if (authentication == null || authentication.length() == 0) {
            return null;
        }

        byte[] bytes = decoder.decode(authentication);
        String decode = new String(bytes);
        TokenModel token = JSON.parseObject(decode, TokenModel.class);
        return token;
    }

    public boolean checkToken(TokenModel model) throws Exception {
        if (model == null) {
            return false;
        }
        Long userId = model.getUserId();
        Date createDate = model.getCreateDate();
        Boolean valid = model.getValid();
        String real_signature = model.getSignature();
        String text0 = encoder.encodeToString(userId.toString().getBytes());
        String text1 = encoder.encodeToString(createDate.toString().getBytes());
        String text2 = encoder.encodeToString(valid.toString().getBytes());
        String signature = HMACSHA256Util.HMACSHA256(text0 + text1+text2, Constants.TOKEN_KEY);

        if(!real_signature.equals(signature))
            return false;

        //检测是否登出
        if(!valid)
            return false;

        Date currentDate = new Date();

        //检测有无过期
        Long diff = currentDate.getTime() - createDate.getTime();
        if (diff > Constants.TOKEN_EXPIRES_HOUR * 60 * 60 * 1000)
            return false;

        return true;
    }
}

