package cn.jijiajian.redisdemo;


import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


/**
 * @author J
 * @time 2017/10/28 22:12
 * @description
 **/
@Component
public class RedisUtils {

    /*@Autowired
    //fixme Autowired 当键值设为String时默认会注入StringRedisTemplate ??????
    private RedisTemplate<String,T> redisTemplate;*/

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;


    public void set(String key, Object t) {
        Object getT = this.get(key);
        //若取出第一个数据有效且类型不一致
        if (getT != null && !getT.getClass().isInstance(t)) {
            throw new ClassCastException("原来存放的数据类型的不一致," + t.getClass().getName() + " => " + getT.getClass().getName());
        }
        redisTemplate.opsForValue().set(key, t);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }


    /**
     * =========== list ============================================
     */


    /**
     * 新建一个列表,若原来的key被占用  则覆盖
     *
     * @param key
     * @param tList
     */
    @SuppressWarnings("unchecked")
    public void lPush(String key, List tList) {
        if (this.isOccupiedByOtherType(key, DataType.LIST)) {
            throw new RuntimeException("该key已被非List类型占用");
        }

        redisTemplate.opsForList().leftPushAll(key, tList);
    }

    public void lPushX(String key, Object o) {
        if (this.isOccupiedByOtherType(key, DataType.LIST)) {
            throw new RuntimeException("该key已被非List类型占用");
        }

        redisTemplate.opsForList().leftPush(key, o);
    }

    public Object lPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 列表长度
     *
     * @param key
     * @return
     */
    public Long lLen(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 获取列表
     *
     * @param key
     * @return
     */
    public List lRange(String key) {
        return lRange(key, 0, -1);
    }


    /**
     * 获取列表
     *
     * @param key
     * @return
     */
    public List lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }


    /**
     *  ============== set ===================
     */

    public void hSet(String key, String field, String value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    public void hmSet(String key, Map map) {
        if (this.isOccupiedByOtherType(key, DataType.HASH)) {
            throw new RuntimeException("该key已被非Map类型占用");
        }
        redisTemplate.opsForHash().putAll(key, map);
    }


    /**
     * 取出Map的某个属性
     *
     * @param key key
     * @param field    属性field
     * @return 属性value
     */
    public Object hGet(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    public Map hVals(String key) {
        return redisTemplate.opsForHash().entries(key);
    }


    /**
     * 实体key 为实体名 + id
     *
     * @param clazz
     * @param id
     * @return
     */
    private String generateKey(Class clazz, Number id) {
        return clazz.getSimpleName() + id;
    }


    /**
     * 检查key是否被其他类型的数据占用
     *
     * @param key      key
     * @param dataType 预期的数据类型
     * @return true 被其他类型占用  false未被其它类型占用
     */
    private boolean isOccupiedByOtherType(String key, DataType dataType) {
        if (redisTemplate.hasKey(key)) {
            //若该key的类型为非dataType类型
            if (!redisTemplate.type(key).equals(dataType)) {
                return true;
            }
        }
        return false;
    }

}
