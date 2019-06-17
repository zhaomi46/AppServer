package com.zgl.swsad.service;

import com.zgl.swsad.config.Constants;
import com.zgl.swsad.mapper.QuestionareMapper;
import com.zgl.swsad.model.Questionare;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionareService {
    @Autowired
    QuestionareMapper questionareMapper;

    //新建
    public int insertQuestionare(Questionare questionare) {
        int reNum = questionareMapper.insertQuestionare(questionare);
        return reNum == 0 ? Constants.INSERT_FAIL : questionare.getQuestionareId();
    }

    //获取
    public Questionare selectQuestionare(int id) { return questionareMapper.selectQuestionare(id);}
    public Questionare selectQuestionareByTaskID(int id){return questionareMapper.selectQuestionareByTaskID(id);}

    //调整
    public int updateQuestionare(Questionare questionare) { return questionareMapper.updateQuestionare(questionare);}

    //删除
    public int deleteQuestionare(int id) {return questionareMapper.deleteQuestionare(id);}
}
