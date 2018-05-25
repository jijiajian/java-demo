package cn.jijiajian.redis;

/**
 * @Description
 * @Author J
 * @Date 2018/5/25 14:15
 **/
public class KeyUtils {

    /**
     * 生成redis存放列表的key
     *
     * @param id
     * @return
     */
    public static String handleListKey(String name, String id) {
        return IDENTIFICATION_LIST + SEPARATOR +
                name + SEPARATOR +
                id;
    }

    /**
     * 生成redis存放实体时的key
     *
     * @param id
     * @return
     */
    public static String handleEntityKey(String name, String id) {
        return IDENTIFICATION_ENTITY + SEPARATOR +
                name + SEPARATOR +
                id;
    }


    public enum EntityName {

        ;
        String name;

        EntityName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum ListName {
        /**
         * 商户发任务查词记录
         */
        OWNER_WORD_HISTORY("owner_word_history");

        String name;

        ListName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }


    private final static String IDENTIFICATION_ENTITY = "entity";

    private final static String IDENTIFICATION_LIST = "list";

    private final static String SEPARATOR = "_";
}
