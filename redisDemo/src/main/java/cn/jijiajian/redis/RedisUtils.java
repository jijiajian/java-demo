package cn.jijiajian.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description
 * @Author J
 * @Date 2018/5/25 14:07
 **/
@Component
public class RedisUtils {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 放入对象
     *
     * @param key
     * @param t
     */
    public void objSave(String key, Object t) {
        Object getT = this.getObj(key);
        //若取出第一个数据有效且类型不一致
        if (getT != null && !getT.getClass().isInstance(t)) {
            throw new ClassCastException("原来存放的数据类型的不一致," + t.getClass().getName() + " => " + getT.getClass().getName());
        }
        redisTemplate.opsForValue().set(key, t);
    }

    /**
     * 返回对象
     *
     * @param key
     * @return
     */
    public Object getObj(String key) {
        return redisTemplate.opsForValue().get(key);
    }


    /**
     * 新建一个列表,若原来的key被占用  则覆盖
     *
     * @param key
     * @param tList
     */
    public Long listSave(String key, List<Object> tList) {

        if (redisTemplate.hasKey(key)) {
            //若该key的类型为非List类型
            if (!redisTemplate.type(key).equals(DataType.LIST)) {
                throw new RuntimeException("该key已被非List类型占用");
            }
            redisTemplate.delete(key);
        }
        return redisTemplate.opsForList().rightPushAll(key, tList);
    }

    /**
     * 往列表里添加元素
     *
     * @param key
     * @param t
     * @return
     */
    public Long listAddItem(String key, Object t) {

        Object getT = redisTemplate.opsForList().index(key, 0);
        //若取出第一个数据有效且类型不一致
        if (getT != null && !getT.getClass().isInstance(t)) {
            throw new ClassCastException("数据类型与列表中的不一致," + t.getClass().getName() + " => " + getT.getClass().getName());
        }

        return redisTemplate.opsForList().rightPush(key, t);
    }

    /**
     * 返回整个列表
     *
     * @param key
     * @return
     */
    public List getList(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

}
