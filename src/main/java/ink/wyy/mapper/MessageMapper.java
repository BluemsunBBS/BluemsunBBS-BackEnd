package ink.wyy.mapper;

import ink.wyy.bean.Message;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageMapper {

    @Select("select * from tb_message where to_user=#{id} order by send_time desc")
    List<Message> getByToUser(String userId);

    @Insert("insert into tb_message (id, from_user, to_user, text, send_time)values" +
            "(#{id}, #{fromUser}, #{toUser}, #{text}, NOW())")
    int insert(Message message);

    @Delete("delete from tb_message where id=#{id}")
    int delete(String id);

    @Select("select * from tb_message where id=#{id}")
    Message getById(String id);
}
