package cn.jijiajian.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @Description 将数据以字符串的形式存入redis
 *  以字符串的形式主要是为了方便排除问题,
 *  若需要用redis的计算 则不能调用此类的方法存入数据
 * @Author J
 * @Date 2018/5/25 17:30
 **/
@Component
public class RedisStringUtils {

    private static final Logger logger = LoggerFactory.getLogger(RedisStringUtils.class);

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 放入对象字符串
     *
     * @param key
     * @param t
     */
    public void saveEntity(String key, Object t) {
        String tStr = JSON.toJSONString(t);
        this.setStr(key,tStr);
    }

    /**
     * 返回对象
     *
     * @param key
     * @return
     */
    public <T> T getEntity(String key, Class<T> clazz) {
        String objStr =  this.getStr(key);
        return JSON.parseObject(objStr, clazz);
    }


    /**
     * 列表作为String存入
     *
     * @param key
     * @param list
     */
    public void saveList(String key, List list) {
        String listStr = JSONArray.toJSONString(list);
        this.setStr(key,listStr);
    }

    /**
     * 获取列表
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     * Collection.emptyList() 不支持add,会抛出UnsupportedOperationException
     */
    public <T> List<T> getList(String key, Class<T> clazz) {
        String listStr = this.getStr(key);
        if (listStr == null){
            return Collections.emptyList();
        }
        return JSONArray.parseArray(listStr, clazz);
    }


    /**
     * 指定key设值值(String)
     *
     * @param key
     * @param value
     * @return
     */
    private boolean setStr(final String key, final String value) {
        try {
            boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
                @Override
                public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                    RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                    connection.set(serializer.serialize(key), serializer.serialize(value));
                    return true;
                }
            });
            return result;
        } catch (Exception e) {
            logger.warn("[指定key设值值(String)异常]:", e);
            return false;
        }
    }


    /**
     * 获取指定key的值
     *
     * @param key
     * @return
     */
    private String getStr(final String key) {
        try {
            String result = redisTemplate.execute(new RedisCallback<String>() {
                @Override
                public String doInRedis(RedisConnection connection) throws DataAccessException {
                    RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                    byte[] value = connection.get(serializer.serialize(key));
                    return serializer.deserialize(value);
                }
            });
            return result;
        } catch (Exception e) {
            logger.warn("[获取指定key的值(list)异常]:", e);
            return null;
        }
    }
}
