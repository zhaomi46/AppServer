package com.zgl.swsad.service;

import com.zgl.swsad.mapper.ErrandMapper;
import com.zgl.swsad.model.Errand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ErrandService {
    @Autowired
    ErrandMapper errandMapper;
    //新建
    public int insertErrand(Errand errand){return errandMapper.insertErrand(errand);}

    //获取
    public  Errand selectErrand(int id){return errandMapper.selectErrand(id);}

    //调整
    public int updateErrand(Errand errand){return errandMapper.updateErrand(errand);}

    //删除
    public int deleteErrand(int id){return errandMapper.deleteErrand(id);}
}

