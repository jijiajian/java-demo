package cn.jijiajian.redisdemo;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author J
 * @time 2017/11/1 22:07
 * @description
 **/

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisUtilTest {

    @Autowired
    private RedisUtils redisUtils;


    @Test
    public void testListOp() {
        DemoEntity demoEntity1 = new DemoEntity();
        demoEntity1.setId(1);
        demoEntity1.setName("小米");
        InnerEntity innerEntity = new InnerEntity();
        innerEntity.setInnerId(666L);
        demoEntity1.setInnerEntity(innerEntity);

        DemoEntity demoEntity2 = new DemoEntity();
        demoEntity2.setId(2);
        demoEntity2.setName("小2");

        DemoEntity demoEntity3 = new DemoEntity();
        demoEntity3.setId(3);
        demoEntity3.setName("小3");

        List<DemoEntity> entityList = new ArrayList<>();
        entityList.add(demoEntity1);
        entityList.add(demoEntity2);

        //新建一个列表
//        redisUtils.listSave("demoList", entityList);
        redisUtils.lPushX("demoList", demoEntity1);
        redisUtils.lPushX("demoList", demoEntity2);
        redisUtils.lPushX("demoList", demoEntity3);
        redisUtils.lPop("demoList");
//        List<DemoEntity> entityList1 = redisUtils.listGet("demoList");
//        System.out.println(entityList1);


//        redisUtils.set("demoEntity","eee");

        //塞 hash
        Map<Object,Object> map = new HashMap<>();
        map.put("id","666");
        map.put("name",2123);
        map.put("inner",innerEntity);
        redisUtils.hmSet("demoHash",map);

    }
}
