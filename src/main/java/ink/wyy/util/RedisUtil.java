package ink.wyy.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component("redisCache")
public class RedisUtil {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisUtil(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 添加值
     * @param key
     * @param id
     * @param value
     */
    public void set(String key, String id, String value) {
        if (key == null || key.equals("")) {
            return;
        }
        redisTemplate.opsForHash().put(key, id, value);
    }

    /**
     * 获取值
     * @param key
     * @param id
     */
    public String get(String key, String id) {
        if (key == null || key.equals("")) {
            return null;
        }
        return (String) redisTemplate.opsForHash().get(key, id);
    }

    /**
     * 判断值是否存在
     * @param key
     * @param id
     */
    public boolean exist(String key, String id) {
        if (key == null || key.equals("")) {
            return false;
        }
        return redisTemplate.opsForHash().hasKey(key, id);
    }

    /**
     * 删除
     * @param key
     * @param id
     */
    public void delete(String key, String id) {
        if (key == null || key.equals("")) {
            return;
        }
        redisTemplate.opsForHash().delete(key, id);
    }
}
