package cn.jijiajian.redisdemo;

/**
 * @author J
 * @time 2017/11/5 21:54
 * @description
 **/
public enum RedisKeyConstant {

    DEMO_ENTITY_KEY("demo_entity_map_key"),
    DEMO_LIST_KEY("demo_list_key");


    private String value;

    RedisKeyConstant(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


}
