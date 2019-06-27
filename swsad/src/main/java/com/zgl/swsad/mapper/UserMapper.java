package com.zgl.swsad.mapper;

import com.zgl.swsad.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;


@Repository
public interface UserMapper {
    @Select("SELECT * FROM user WHERE userId = #{userId}")
    User selectUser(int id);

    @Select("SELECT * FROM user WHERE name = #{name}")
    User selectUserByName(String name);

    @Select("select * from user order by userId")
    ArrayList<User> selectAllUser();//返回一个person对象

    /**
     * 添加新用户
     * @param user 实例
     * @return 成功操作的记录数目
     */
    @Insert("INSERT INTO user (userType,stuId, name, avator, nickName, age, sex, grade, major, mailAddr, phoneNum, creditVal, " +
            "balance, tags, password) Values(#{userType},#{stuId}, #{name}, #{avator}, #{nickName}, #{age}, #{sex}, #{grade}, #{major}, " +
            "#{mailAddr}, #{phoneNum}, 0, 0, #{tags}, #{password})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int insertUser(User user);

    @Update("UPDATE user set userType=#{userType}, name=#{name}, avator=#{avator}, nickname=#{nickName}, age=#{age}," +
            "sex=#{sex}, grade=#{grade}, major=#{major}, mailAddr=#{mailAddr}, phoneNum=#{phoneNum}, " +
            " tags=#{tags}, password=#{password} where userId=#{userId}")
    int updateUser(User user);

    @Update("update user set balance=#{balance},creditVal = #{creditVal} where userId=#{userId}")
    int updateBnC(int userId,double balance,int creditVal);

    @Delete("DELETE FROM user where userId=#{userId}")
    int deleteUser(int id);
}
