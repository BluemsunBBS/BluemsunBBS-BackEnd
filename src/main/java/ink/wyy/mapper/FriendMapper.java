package ink.wyy.mapper;

import ink.wyy.bean.Pager;
import ink.wyy.bean.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FriendMapper {
    // follower 关注者 user 被关注者
    @Select("select tu.* from (select t1.user_id from (select * from tb_friend where follower_id=#{id}) as t1 " +
            "inner join tb_friend t2 on t1.user_id=t2.follower_id and t1.follower_id=t2.user_id " +
            "order by t1.follow_time limit #{p.begin},#{p.size}) userid, tb_user tu where userid.user_id=tu.id")
    List<User> getFriends(@Param("id") String userId,
                           @Param("p")Pager<User> pager);

    @Select("select tu.* from (select user_id from tb_friend where follower_id=#{id} and user_id not in " +
            "(select t1.user_id from (select * from tb_friend where follower_id=#{id}) as t1 " +
            "inner join tb_friend t2 on t1.user_id=t2.follower_id and t1.follower_id=t2.user_id) " +
            "order by tb_friend.follow_time limit #{p.begin},#{p.size}) userid, tb_user tu where userid.user_id=tu.id")
    List<User> getMyFollow(@Param("id") String userId,
                           @Param("p") Pager<User> pager);

    @Select("select tu.* from (select follower_id from tb_friend where user_id=#{id} and follower_id not in " +
            "(select t1.user_id from (select * from tb_friend where follower_id=#{id}) as t1 " +
            "inner join tb_friend t2 on t1.user_id=t2.follower_id and t1.follower_id=t2.user_id) " +
            "order by tb_friend.follow_time limit #{p.begin},#{p.size}) userid, tb_user tu where userid.follower_id=tu.id")
    List<User> getFollowMe(@Param("id") String userId,
                           @Param("p") Pager<User> pager);

    @Insert("insert into tb_friend (follower_id, user_id, follow_time)values" +
            "(#{followerId}, #{userId}, NOW())")
    int follow(@Param("followerId") String followerId,
               @Param("userId") String userId);

    @Delete("delete from tb_friend where follower_id=#{followerId} and user_id=#{userId}")
    int unfollow(@Param("followerId") String followerId,
                 @Param("userId") String userId);

    @Select("select COUNT(1) from tb_friend where follower_id=#{followerId} and user_id=#{userId}")
    int check(@Param("followerId") String followerId,
              @Param("userId") String userId);

    @Select("select COUNT(1) from (select user_id from tb_friend where follower_id=#{id} and user_id not in " +
            "(select t1.user_id from (select * from tb_friend where follower_id=#{id}) as t1 " +
            "inner join tb_friend t2 on t1.user_id=t2.follower_id and t1.follower_id=t2.user_id) " +
            "order by tb_friend.follow_time) userid, tb_user tu where userid.user_id=tu.id")
    int countMyFollow(@Param("id") String userId);

    @Select("select COUNT(1) from (select follower_id from tb_friend where user_id=#{id} and follower_id not in " +
            "(select t1.user_id from (select * from tb_friend where follower_id=#{id}) as t1 " +
            "inner join tb_friend t2 on t1.user_id=t2.follower_id and t1.follower_id=t2.user_id) " +
            "order by tb_friend.follow_time) userid, tb_user tu where userid.follower_id=tu.id")
    int countFollowMe(@Param("id") String userId);

    @Select("select COUNT(1) from (select t1.user_id from (select * from tb_friend where follower_id=#{id}) as t1 " +
            "inner join tb_friend t2 on t1.user_id=t2.follower_id and t1.follower_id=t2.user_id " +
            "order by t1.follow_time) userid, tb_user tu where userid.user_id=tu.id")
    int countFriends(@Param("id") String userId);
}
