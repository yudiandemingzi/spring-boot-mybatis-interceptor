package com.jincou.plugin;

import com.google.common.base.Predicate;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.reflections.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author xub
 * @Description: mybatis ID自增拦截器
 * @date 2019/8/19 下午12:38
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {
                MappedStatement.class, Object.class
        }),
})
public class AutoIdInterceptor implements Interceptor {

    /**
     * 处理器 MAP
     */
    private Map<Class, List<Handler>> handlerMap = new ConcurrentHashMap<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        //args数组对应对象就是上面@Signature注解中args对应的对应类型
        MappedStatement mappedStatement = (MappedStatement) args[0];
        //实体对象
        Object entity = args[1];
        if ("INSERT".equalsIgnoreCase(mappedStatement.getSqlCommandType().name())) {
            // 获取实体集合
            Set<Object> entitySet = getEntitySet(entity);
            // 批量设置id
            for (Object object : entitySet) {
                process(object);
            }
        }
        return invocation.proceed();
    }

    /**
     * object是需要插入的实体数据,它可能是对象,也可能是批量插入的对象。
     * 如果是批量插入对象，那么object就是一个map集合,key值为"list",value为ArrayList集合对象
     */
    private Set<Object> getEntitySet(Object object) {
        //
        Set<Object> set = new HashSet<>();
        if (object instanceof Map) {
            //批量插入对象
            Collection values = (Collection) ((Map) object).get("list");
            System.out.println("values = " + values);
            for (Object value : values) {
                if (value instanceof Collection) {
                    set.addAll((Collection) value);
                } else {
                    set.add(value);
                }
            }
        } else {
            //单个插入对象
            set.add(object);
        }
        return set;
    }

    private void process(Object object) throws Throwable {
        Class handlerKey = object.getClass();
        List<Handler> handlerList = handlerMap.get(handlerKey);

        SYNC:
        if (handlerList == null) {
            synchronized (this) {
                handlerList = handlerMap.get(handlerKey);
                //如果到这里map集合已经存在，则跳出到指定SYNC标签
                if (handlerList != null) {
                    break SYNC;
                }
                handlerMap.put(handlerKey, handlerList = new ArrayList<>());
                // 反射工具类 获取带有AutoId注解的所有属性字段
                Set<Field> allFields = ReflectionUtils.getAllFields(
                        object.getClass(),
                        (Predicate<Field>) input -> input != null && input.getAnnotation(AutoId.class) != null
                );
                for (Field field : allFields) {
                    AutoId annotation = field.getAnnotation(AutoId.class);
                    //1、添加UUID字符串作为主键
                    if (field.getType().isAssignableFrom(String.class)) {
                        if (annotation.value().equals(AutoId.IdType.UUID)) {
                            handlerList.add(new UUIDHandler(field));
                            //2、添加String类型雪花ID
                        } else if (annotation.value().equals(AutoId.IdType.SNOWFLAKE)) {
                            handlerList.add(new UniqueLongHexHandler(field));
                        }
                    } else if (field.getType().isAssignableFrom(Long.class)) {
                        //3、添加Long类型的雪花ID
                        if (annotation.value().equals(AutoId.IdType.SNOWFLAKE)) {
                            handlerList.add(new UniqueLongHandler(field));
                        }
                    }
                }
            }
        }
        for (Handler handler : handlerList) {
            handler.accept(object);
        }
    }

    private static abstract class Handler {
        Field field;

        Handler(Field field) {
            this.field = field;
        }

        abstract void handle(Field field, Object object) throws Throwable;


        private boolean checkField(Object object, Field field) throws IllegalAccessException {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            //如果概
            return field.get(object) == null;
        }

        public void accept(Object o) throws Throwable {
            if (checkField(o, field)) {
                handle(field, o);
            }
        }
    }

    private static class UUIDHandler extends Handler {

        UUIDHandler(Field field) {
            super(field);
        }

        @Override
        void handle(Field field, Object object) throws Throwable {
            field.set(object, UUID.randomUUID().toString().replace("-", ""));
        }

    }

    private static class UniqueLongHandler extends Handler {

        UniqueLongHandler(Field field) {
            super(field);
        }

        @Override
        void handle(Field field, Object object) throws Throwable {
            field.set(object, SnowIdUtils.uniqueLong());
        }

    }

    private static class UniqueLongHexHandler extends Handler {

        UniqueLongHexHandler(Field field) {
            super(field);
        }

        @Override
        void handle(Field field, Object object) throws Throwable {
            field.set(object, SnowIdUtils.uniqueLongHex());
        }

    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
