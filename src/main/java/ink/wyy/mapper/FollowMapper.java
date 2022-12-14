package ink.wyy.mapper;

import ink.wyy.bean.Board;
import ink.wyy.bean.Pager;
import ink.wyy.bean.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FollowMapper {

    @Select("select COUNT(1) from tb_follow where board_id=#{id}")
    int countByBoardId(String id);

    @Select("select COUNT(1) from tb_follow where user_id=#{id}")
    int countByUserId(String id);

    @Select("select b.* from tb_follow f, tb_board b where f.user_id=#{id} " +
            "and b.id=f.board_id limit #{p.begin},#{p.size}")
    List<Board> getListByUser(@Param("id") String id, @Param("p") Pager<Board> pager);

    @Select("select u.* from tb_follow f, tb_user u where f.board_id=#{id} " +
            "and u.id=f.user_id limit #{p.begin},#{p.size}")
    List<User> getListByBoard(@Param("id") String id, @Param("p")Pager<User> pager);

    @Insert("insert into tb_follow (user_id, board_id, follow_time)values" +
            "(#{userId}, #{boardId}, NOW())")
    int follow(@Param("userId") String userId, @Param("boardId") String boardId);

    @Delete("delete from tb_follow where user_id=#{userId} and board_id=#{boardId}")
    int unfollow(@Param("userId") String userId, @Param("boardId") String boardId);

    @Select("select COUNT(1) from tb_follow where user_id=#{userId} and board_id=#{boardId}")
    int check(@Param("userId") String userId, @Param("boardId") String boardId);
}
