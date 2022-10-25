package ink.wyy.mapper;

import ink.wyy.bean.Board;
import ink.wyy.bean.Pager;
import ink.wyy.bean.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardMapper {

    @Select("select * from tb_board limit #{begin},#{size}")
    List<Board> selectAll(Pager<Board> pager) throws Exception;

    @Select("select * from tb_board where name like #{name} limit #{pager.begin},#{pager.size}")
    List<Board> find(@Param("name") String name, @Param("pager") Pager<Board> pager) throws Exception;

    @Select("select COUNT(1) from tb_board where name like #{name}")
    int countByName(String name) throws Exception;

    @Insert("insert into tb_board (id, name, img, description, create_time, update_time) values " +
            "(#{id}, #{name}, #{img}, #{description}, NOW(), NOW())")
    int insert(Board board) throws Exception;

    @Update("update tb_board set name=#{name}, img=#{img}, description=#{description}, " +
            "update_time=NOW() where id=#{id}")
    int update(Board board) throws Exception;

    @Delete("delete from tb_board where id=#{id}")
    int delete(String id) throws Exception;

    @Select("select * from tb_board where id=#{id}")
    Board getById(String id) throws Exception;

    @Select("select COUNT(1) from tb_board")
    int count() throws Exception;

    @Insert("insert into tb_board_host (user_id, board_id)values(#{userId}, #{boardId})")
    int addHost(@Param("userId") String userId, @Param("boardId") String boardId);

    @Select("select COUNT(1) from tb_board_host where " +
            "user_id=#{userId} and board_id=#{boardId}")
    int checkHost(@Param("userId") String userId, @Param("boardId") String boardId);

    @Delete("delete from tb_board_host where " +
            "user_id=#{userId} and board_id=#{boardId}")
    int deleteHost(@Param("userId") String userId, @Param("boardId") String boardId);

    @Select("select u.* from tb_board_host b, tb_user u " +
            "where b.user_id=u.id and b.board_id=#{boardId}")
    List<User> getHostList(@Param("boardId") String boardId);
}
