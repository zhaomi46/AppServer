package com.zgl.swsad.mapper;

import com.zgl.swsad.model.Questionare;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface QuestionareMapper {
    @Insert("INSERT INTO questionare (taskId, questionNum)" +
            " Values(#{taskId}, #{questionNum} )")
    int insertQuestionare(Questionare questionare);

    @Update("UPDATE questionare set taskId=#{taskId}, questionNum=#{questionNum} where questionareId=#{questionareId}")
    int updateQuestionare(Questionare questionare);

    @Select("SELECT * FROM question WHERE questionareId = #{questionareId}")
    Questionare selectQuestionare(int id);

    @Delete("DELETE FROM questionare WHERE questionareId = #{questionareId}")
    int deleteQuestionare(int id);
}