package com.zgl.swsad.mapper;

import com.zgl.swsad.model.Question;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Array;
import java.util.ArrayList;

@Repository
public interface QuestionMapper {
    @Insert("INSERT INTO question (questionType, question, answer, choiceNum, choiceStr, questionareId)" +
            " Values(#{questionType}, #{question},#{answer}, #{choiceNum},#{choiceStr},#{questionareId} )")
    int insertQuestion(Question question);

    @Update("UPDATE question set questionType=#{questionType}, question=#{question},answer=#{answer}, choiceNum=#{choiceNum},choiceStr=#{choiceStr},questionareId=#{questionareId} where questionId=#{questionId}")
    int updateQuestion(Question question);

    @Select("SELECT * FROM question WHERE questionareId = #{questionareId}")
    ArrayList<Question> selectQuestionByQuestionareID(int id);//id 是questionare的id

    @Select("SELECT * FROM question WHERE questionId = #{questionId}")
    Question selectQuestion(int id);

    @Delete("DELETE FROM question WHERE questionId = #{questionId}")
    int deleteQuestion(int id);
}
