package ink.wyy.mapper;

import ink.wyy.bean.Article;
import ink.wyy.bean.Pager;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArticleMapper {

    @Select("select a.* from tb_article a, tb_user u where " +
            "a.board_id=#{id} and a.user_id=u.id and u.ban=0 " +
            "order by top desc, ${order} limit #{p.begin},#{p.size}")
    List<Article> selectByBoard(@Param("id") String boardId,
                                @Param("p") Pager<Article> pager,
                                @Param("order") String order) throws Exception;

    @Insert("insert into tb_article (id, title, text, files, board_id, user_id, top, create_time, update_time)" +
            "values(#{id}, #{title}, #{text}, #{files}, #{boardId}, #{userId}, 0, NOW(), NOW())")
    int insert(Article article) throws Exception;

    @Delete("delete from tb_article where id=#{id}")
    int delete(String id) throws Exception;

    @Update("update tb_article set title=#{title}, text=#{text}, files=#{files}, board_id=#{boardId}, " +
            "update_time=#{updateTime}, top=#{top} where id=#{id}")
    int update(Article article) throws Exception;

    @Select("select COUNT(1) from tb_article a, tb_user u where " +
            "a.board_id=#{boardId} and a.user_id=u.id and u.ban=0")
    int count(String boardId) throws Exception;

    @Select("select COUNT(1) from tb_article a, tb_user u where " +
            "a.board_id=#{boardId} and a.user_id=u.id and u.ban=0 " +
            "and a.title like #{title}")
    int countByTitle(String boardId, String title) throws Exception;

    @Select("select COUNT(1) from tb_article a, tb_user u where " +
            "a.user_id=u.id and u.ban=0 " +
            "and a.title like #{title}")
    int countByTitleAll(String title) throws Exception;

    @Select("select SUM(a.visits) from tb_article a, tb_user u where " +
            "a.board_id=#{boardId} and a.user_id=u.id and u.ban=0")
    int countVisits(String boardId) throws Exception;

    @Select("select * from tb_article a where id=#{id}")
    Article getById(String id) throws Exception;

    @Select("select a.* from tb_article a, tb_user u where " +
            "a.board_id=#{id} and a.user_id=u.id and u.ban=0 " +
            "and a.title like #{title} order by top desc, ${order} " +
            "limit #{p.begin},#{p.size}")
    List<Article> findByTitle(@Param("id") String boardId,
                              @Param("title") String title,
                              @Param("p") Pager<Article> pager,
                              @Param("order") String order) throws Exception;

    @Select("select a.* from tb_article a, tb_user u where " +
            "a.user_id=u.id and u.ban=0 and a.title " +
            "like #{title} order by top desc, ${order} " +
            "limit #{p.begin},#{p.size}")
    List<Article> findAll(@Param("title") String title,
                          @Param("p") Pager<Article> pager,
                          @Param("order") String order) throws Exception;

    @Update("update tb_article set visits=visits+1 where id=#{id}")
    int visit(String id) throws Exception;
}
