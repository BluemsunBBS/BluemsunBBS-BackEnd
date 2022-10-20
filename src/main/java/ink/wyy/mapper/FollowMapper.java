package ink.wyy.mapper;

import ink.wyy.bean.Pager;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FollowMapper {

    @Select("select COUNT(1) from tb_follow where board_id=#{id}")
    int countByBoardId(String id);

    @Select("select COUNT(1) from tb_follow where user_id=#{id}")
    int countByUserId(String id);

    @Select("select board_id from tb_follow where user_id=#{id} limit #{p.begin},#{p.size}")
    List<String> getListByUser(@Param("id") String id, @Param("p") Pager<String> pager);

    @Select("select user_id from tb_follow where board_id=#{id} limit #{p.begin},#{p.size}")
    List<String> getListByBoard(@Param("id") String id, @Param("p")Pager<String> pager);

    @Insert("insert into tb_follow (user_id, board_id)values" +
            "(#{userId}, #{boardId})")
    int follow(@Param("userId") String userId, @Param("boardId") String boardId);

    @Delete("delete from tb_follow where user_id=#{userId} and board_id=#{boardId}")
    int unfollow(@Param("userId") String userId, @Param("boardId") String boardId);

    @Select("select COUNT(1) from tb_follow where user_id=#{userId} and board_id=#{boardId}")
    int check(@Param("userId") String userId, @Param("boardId") String boardId);
}
