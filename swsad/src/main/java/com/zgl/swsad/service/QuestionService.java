package com.zgl.swsad.service;

import com.zgl.swsad.mapper.QuestionMapper;
import com.zgl.swsad.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {
    @Autowired
    QuestionMapper questionMapper;

    //新建question
    public int insertQuestion(Question question) {return questionMapper.insertQuestion(question);}

    //获取question
    public Question selectQuestion(int id) {return questionMapper.selectQuestion(id);}

    //调整
    public int updateQuestion(Question question) { return questionMapper.updateQuestion(question);}

    //删除
    public int deleteQuestion(int id) {return questionMapper.deleteQuestion(id);}
}
