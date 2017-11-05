package cn.jijiajian.redisdemo;


import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * @author J
 * @time 2017/10/28 22:12
 * @description
 **/
@Component
public class RedisUtil<T> {

    /*@Autowired
    //fixme Autowired 当键值设为String时默认会注入StringRedisTemplate ??????
    private RedisTemplate<String,T> redisTemplate;*/

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, T> redisTemplate;




    public void set(String key,T t){
        Object getT = this.get(key);
        //若取出第一个数据有效且类型不一致
        if (getT != null && !getT.getClass().isInstance(t)) {
            throw new ClassCastException("原来存放的数据类型的不一致," + t.getClass().getName() + " => " + getT.getClass().getName());
        }
        redisTemplate.opsForValue().set(key,t);
    }

    public T get(String key){
        return redisTemplate.opsForValue().get(key);
    }


    /**
     * 新建一个列表,若原来的key被占用  则覆盖
     *
     * @param key
     * @param tList
     */
    public void buildList(String key, List<T> tList) {

        if (redisTemplate.hasKey(key)) {
            //若该key的类型为非List类型
            if (!redisTemplate.type(key).equals(DataType.LIST)) {
                throw new RuntimeException("该key已被非List类型占用");
            }
            redisTemplate.delete(key);
        }
        redisTemplate.opsForList().rightPushAll(key, tList);
    }

    public List<T> getList(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    public Long addList(String key, T t) {

        if (this.isOccupied(key, DataType.LIST)) {
            throw new RuntimeException("该key已被非List类型占用");
        }

        Object getT = redisTemplate.opsForList().index(key, 0);
        //若取出第一个数据有效且类型不一致
        if (getT != null && !getT.getClass().isInstance(t)) {
            throw new ClassCastException("数据类型与列表中的不一致," + t.getClass().getName() + " => " + getT.getClass().getName());
        }


        return redisTemplate.opsForList().rightPush(key, t);
    }

    /**
     * 放入实体
     *
     * @param key key
     * @param id  id
     * @param t   实体
     */
    public void putEntity(String key, Number id, T t) {
        if (this.isOccupied(key, DataType.HASH)) {
            throw new RuntimeException("该key已被非Map类型占用");
        }

        String entityKey = this.generateKey(t.getClass(), id);
        redisTemplate.opsForHash().put(key, entityKey, t);
    }

    /**
     * 取出实体
     *
     * @param key 实体集合key
     * @param id  实体id
     * @return 实体
     */
    @SuppressWarnings("unchecked")
    public T getEntity(String key, Number id, Class clazz) throws ClassCastException {
        String entityKey = this.generateKey(clazz, id);
        return (T) redisTemplate.opsForHash().get(key, entityKey);
    }


    /**
     * 取出同一前缀的实体
     *
     * @param key    实体集合key
     * @param prefix 实体key 前缀
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<T> getEntitiesByPrefix(String key, String prefix) {
        Map<Object, Object> all = redisTemplate.opsForHash().entries(key);
        if (all.size() == 0) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>(all.size());

        for (Map.Entry<Object, Object> entry : all.entrySet()) {
            String entryKey = (String) entry.getKey();
            if (entryKey.startsWith(prefix)) {
                result.add((T) entry.getValue());
            }
        }
        return result;
    }

    /**
     * 删除同一前缀的实体
     *
     * @param key    实体集合key
     * @param prefix 实体key 前缀
     * @return 删除条数
     */
    public int deleteEntitiesByPrefix(String key, String prefix) {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        Map<Object, Object> all = hashOperations.entries(key);
        if (all.size() == 0) {
            return 0;
        }

        int count = 0;
        for (Map.Entry<Object, Object> entry : all.entrySet()) {
            String entryKey = (String) entry.getKey();
            if (entryKey.startsWith(prefix)) {
                count += hashOperations.delete(key, entryKey);
            }
        }
        return count;
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
     * @param key  key
     * @param dataType  数据类型
     * @return true 被其他类型占用  false未被其它类型占用
     */
    private boolean isOccupied(String key, DataType dataType) {
        if (redisTemplate.hasKey(key)) {
            //若该key的类型为非dataType类型
            if (!redisTemplate.type(key).equals(dataType)) {
                return true;
            }
        }
        return false;
    }

}
