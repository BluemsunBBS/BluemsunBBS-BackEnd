package ink.wyy.mapper;

import ink.wyy.bean.Notification;
import ink.wyy.bean.Pager;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotificationMapper {

    @Select("select * from tb_notification where user_id=#{userId} " +
            "and category=#{category} order by notice_time desc limit #{p.begin},#{p.size}")
    List<Notification> selectByUser(@Param("userId") String userId,
                                    @Param("category") String category,
                                    @Param("p") Pager<Notification> pager) throws Exception;

    @Insert("insert into tb_notification (id, category, text, user_id, is_read, notice_time) values " +
            "(#{id}, #{category}, #{text}, #{userId}, #{read}, NOW())")
    int insert(Notification n) throws Exception;

    @Update("update tb_notification set is_read=1 where user_id=#{userId} " +
            "and category=#{category}")
    int readByUser(@Param("userId") String userId, @Param("category") String category) throws Exception;

    @Update("update tb_notification set is_read=#{state} where id=#{id}")
    int readById(String id, boolean state) throws Exception;

    @Delete("delete from tb_notification where id=#{id}")
    int delete(String id) throws Exception;

    @Select("select COUNT(1) from tb_notification where user_id=#{userId}")
    int count(String userId);

    @Select("select COUNT(1) from tb_notification where user_id=#{userId} and is_read=#{read}")
    int countByRead(String userId, boolean read);
}
