package com.zgl.swsad.mapper;

import com.zgl.swsad.model.Questionare;
import org.apache.ibatis.annotations.*;

public interface QuestionareMapper {
    @Insert("INSERT INTO questionare (taskId, questionNum)" +
            " Values(#{taskId}, #{questionNum} )")
    @Options(useGeneratedKeys = true, keyProperty = "questionareId")
    int insertQuestionare(Questionare questionare);

    @Update("UPDATE questionare set  questionNum=#{questionNum} where taskId=#{taskId}")
    int updateQuestionare(Questionare questionare);

    @Select("SELECT * FROM questionare WHERE taskId = #{taskId}")
    Questionare selectQuestionareByTaskID(int id);//这里的id是taskid

    @Select("SELECT * FROM questionare WHERE questionId = #{questionId}")
    Questionare selectQuestionare(int id);

    @Delete("DELETE FROM questionare WHERE questionareId = #{questionareId}")
    int deleteQuestionare(int id);
}
