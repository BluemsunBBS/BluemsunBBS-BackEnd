package ink.wyy.mapper;

import ink.wyy.bean.Pager;
import ink.wyy.bean.Reply;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReplyMapper {

    @Select("select * from tb_reply where article_id=#{id}" +
            "order by reply_time limit #{p.begin},#{p.size}")
    List<Reply> selectByArticle(@Param("id") String articleId,
                                @Param("p") Pager<Reply> pager) throws Exception;

    @Select("select * from tb_reply where reply_id=#{id}" +
            "order by reply_time limit #{p.begin},#{p.size}")
    List<Reply> selectByReply(@Param("id") String replyId,
                                @Param("p") Pager<Reply> pager) throws Exception;

    @Insert("insert into tb_reply (id, text, user_id, article_id, reply_id, reply_time)values" +
            "(#{id}, #{text}, #{userId}, #{articleId}, #{replyId}, NOW())")
    int insert(Reply reply) throws Exception;

    @Delete("delete from tb_reply where id=#{id}")
    int delete(String id) throws Exception;

    @Select("select COUNT(1) from tb_reply where article_id=#{id} or reply_id=#{id}")
    int count(String id);

    @Select("select * from tb_reply where id=#{id}")
    Reply getById(String id);
}
