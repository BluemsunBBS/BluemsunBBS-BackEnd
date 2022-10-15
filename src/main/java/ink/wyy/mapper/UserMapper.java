package ink.wyy.mapper;

import ink.wyy.bean.Pager;
import ink.wyy.bean.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from tb_user where ban=0 limit #{begin},#{size}")
    List<User> selectAll(Pager<User> pager) throws Exception;

    @Select("select COUNT(1) from tb_user where ban=0")
    int count() throws Exception;

    @Insert("insert into tb_user (id, username, nickname, password, role, level, phone, gender, realname, create_time)" +
            "values(#{id}, #{username}, #{nickname}, #{password}," +
            " 1, 1, #{phone}, #{gender}, #{realname}, NOW())")
    int insert(User user) throws Exception;

    @Update("update tb_user set username=#{username}, nickname=#{nickname}," +
            "password=#{password}, role=#{role}, level=#{level}, phone=#{phone}," +
            "gender=#{gender}, realname=#{realname} " +
            "where id=#{id} and ban=0")
    int updateById(User user) throws Exception;

    @Update("update tb_user set ban=0 where id=#{id} and ban=1")
    int cancelBan(String id) throws Exception;

    @Update("update tb_user set ban=1 where id=#{id} and ban=0")
    int ban(String id) throws Exception;

    @Select("select * from tb_user where ban=1 limit #{begin},#{size}")
    List<User> selectBan(Pager<User> pager) throws Exception;

    @Select("select COUNT(1) from tb_user where ban=1")
    int countBan() throws Exception;

    @Select("select * from tb_user where id=#{id} and ban=0")
    User findById(String id) throws Exception;

    @Select("select * from tb_user where username=#{username} and ban=0")
    User findByUsername(String username) throws Exception;
}
