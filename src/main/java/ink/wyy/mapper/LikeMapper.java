package ink.wyy.mapper;

import ink.wyy.bean.Pager;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LikeMapper {

    @Select("select COUNT(1) from tb_like where article_id=#{id}")
    int countByArticleId(String id);

    @Select("select COUNT(1) from tb_like where user_id=#{id}")
    int countByUserId(String id);

    @Select("select article_id from tb_like where user_id=#{id} limit #{p.begin},#{p.size}")
    List<String> getListByUser(@Param("id") String id, @Param("p")Pager<String> pager);

    @Select("select user_id from tb_like where article_id=#{id} limit #{p.begin},#{p.size}")
    List<String> getListByArticle(@Param("id") String id, @Param("p")Pager<String> pager);

    @Insert("insert into tb_like (user_id, article_id)values" +
            "(#{userId}, #{articleId})")
    int like(@Param("userId") String userId, @Param("articleId") String articleId);

    @Delete("delete from tb_like where user_id=#{userId} and article_id=#{articleId}")
    int unlike(@Param("userId") String userId, @Param("articleId") String articleId);

    @Select("select COUNT(1) from tb_like where user_id=#{userId} and article_id=#{articleId}")
    int check(@Param("userId") String userId, @Param("articleId") String articleId);
}
