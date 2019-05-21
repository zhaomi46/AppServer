package com.zgl.swsad.mapper;

import com.zgl.swsad.model.Errand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrandMapper {
    @Insert("INSERT INTO errand (description, taskId)" +
            " Values(#{description}, #{taskId})")
    int insertErrand(Errand errand);

    @Update("UPDATE errand set description=#{description}, taskId=#{taskId} where errandId=#{errandId}")
    int updateErrand(Errand errand);

    @Select("SELECT * FROM errand WHERE errnadId = #{errandId}")
    Errand selectErrand(int id);

    @Delete("DELETE FROM errand WHERE errandId = #{errandId}")
    int deleteErrand(int id);
}
