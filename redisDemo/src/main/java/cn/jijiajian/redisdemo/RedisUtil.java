package cn.jijiajian.redisdemo;


import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;


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


    public void set(String key, T t) {
        Object getT = this.get(key);
        //若取出第一个数据有效且类型不一致
        if (getT != null && !getT.getClass().isInstance(t)) {
            throw new ClassCastException("原来存放的数据类型的不一致," + t.getClass().getName() + " => " + getT.getClass().getName());
        }
        redisTemplate.opsForValue().set(key, t);
    }

    public T get(String key) {
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

    public Long addItemToList(String key, T t) {

        if (this.isOccupiedByOtherType(key, DataType.LIST)) {
            throw new RuntimeException("该key已被非List类型占用");
        }

        Object getT = redisTemplate.opsForList().index(key, 0);
        //若取出第一个数据有效且类型不一致
        if (getT != null && !getT.getClass().isInstance(t)) {
            throw new ClassCastException("数据类型与列表中的不一致," + t.getClass().getName() + " => " + getT.getClass().getName());
        }


        return redisTemplate.opsForList().rightPush(key, t);
    }


    public void putMap(String mapKey, Map map) {
        if (this.isOccupiedByOtherType(mapKey, DataType.HASH)) {
            throw new RuntimeException("该key已被非Map类型占用");
        }
        redisTemplate.opsForHash().putAll(mapKey, map);
    }

    /**
     * 放入Map
     *
     * @param key   key
     * @param value value
     */
    public void putMapValue(String mapKey, String key, Object value) {
        if (this.isOccupiedByOtherType(mapKey, DataType.HASH)) {
            throw new RuntimeException("该key已被非Map类型占用");
        }
        redisTemplate.opsForHash().put(mapKey, key, value);
    }

    /**
     * 取出Map的某个属性
     *
     * @param mapKey mapKey
     * @param key    属性key
     * @return 属性value
     */
    public Object getMapValue(String mapKey, String key) {
        return redisTemplate.opsForHash().get(mapKey, key);
    }

    public Map getMap(String mapKey) {
        return redisTemplate.opsForHash().entries(mapKey);
    }


    /**
     * 根据前缀取map
     * @param prefix 前缀
     * @return map列表
     */
    public List<Map> getMapByPrefix(String prefix) {
        Set<String> keySet = redisTemplate.keys(prefix + "*");
        int size = keySet.size();
        if (size == 0) {
            return Collections.emptyList();
        }

        List<Map> result = new ArrayList<>(size);
        for (String item : keySet) {
            if (this.isOccupiedByOtherType(item, DataType.HASH)) {
                continue;
            }

            Map getMap = this.getMap(item);
            if (getMap != null) {
                result.add(getMap);
            }

        }
        return result;
    }


    /**
     * 取出同一前缀的实体
     *
     * @param key    实体集合key
     * @param prefix 实体key 前缀
     * @return
     */
    /*@SuppressWarnings("unchecked")
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
    }*/

    /**
     * 删除同一前缀的实体
     *
     * @param key    实体集合key
     * @param prefix 实体key 前缀
     * @return 删除条数
     */
    /*public int deleteEntitiesByPrefix(String key, String prefix) {
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
    }*/

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
