# Mybatis自定义插件生成雪花ID做为主键项目


有关Mybatis雪花ID主键插件前面写了两篇博客作为该项目落地的铺垫。

1、[Mybatis框架---Mybatis插件原理](https://www.cnblogs.com/qdhxhz/p/11390778.html)

2、[java算法---静态内部类实现雪花算法](https://www.cnblogs.com/qdhxhz/p/11372658.html)

**该插件项目可以直接运用于实际开发中，作为分布式数据库表主键ID使用。**

## 一、项目概述

#### 1、项目背景

在生成表主键ID时，我们可以考虑**主键自增** 或者 **UUID**,但它们都有很明显的缺点

`主键自增`：**1、自增ID容易被爬虫遍历数据。2、分表分库会有ID冲突。**

`UUID`: **1、太长，并且有索引碎片，索引多占用空间的问题 2、无序。**

雪花算法就很适合在分布式场景下生成唯一ID,**它既可以保证唯一又可以排序**,该插件项目的原理是

```
通过拦截器拦截Mybatis的insert语句,通过自定义注解获取到主键，并为该主键赋值雪花ID,插入数据库中。
```

#### 2、技术架构

项目总体技术选型

```
SpringBoot2.1.7 + Mybatis + Maven3.5.4 + Mysql + lombok(插件)
```

#### 3、使用方式

在你需要做为主键的属性上添加`@AutoId`注解,那么通过插件可以自动为该属性赋值主键ID。

```java
public class TabUser {
    /**
     * id(添加自定义注解)
     */
    @AutoId
    private Long id;
    /**
     * 姓名
     */
    private String name;
  //其它属性 包括get，set方法
}
```

#### 4、项目测试

配置好数据库连接信息,直接启动Springboot启动类`Application.java`,访问`localhost:8080/save-foreach-user`就可以看到数据库数据已经有雪花ID了。

如图(图片加载可能会比较慢,如果未加载成功可以点击链接）[(图片)](https://img2018.cnblogs.com/blog/1090617/201908/1090617-20190825143949356-1399243971.png)

![](https://img2018.cnblogs.com/blog/1090617/201908/1090617-20190825143949356-1399243971.png)



<br>

## 二、项目代码说明

在正式环境中只要涉及到`插入数据`的操作都被该插件拦截,并发量会很大。所以该插件代码即要保证`线程安全`又要保证`高可用`。所以在代码设计上做一些说明。

#### 1、线程安全

这里的线程安全主要是考虑产生雪花ID的时候必须是线程安全的，不能出现同一台服务器同一时刻出现了相同的雪花ID,这里是通过

```
静态内部类单例模式 + synchronized
```

来保证线程安全的，具体有关生成雪花ID的代码这里就不粘贴。

#### 2、高可用

我们去思考消耗性能比较大的地方可能出要出现在两个地方

```
1）雪花算法生成雪花ID的过程。
2）通过类的反射机制找到哪些属性带有@AutoId注解的过程。
```

`第一点`

其实在`静态内部类实现雪花算法`这篇博客已经简单测试过，生成20万条数据，大约在1.7秒能满足实际开发中我们的需要。

`第二点`

这里是有比较好的解决方案的，可以通过两点去改善它。

**1)、在插件中添加了一个Map处理器**

```java
   /**
     * key值为Class对象 value可以理解成是该类带有AutoId注解的属性，只不过对属性封装了一层。
     * 它是非常能够提高性能的处理器 它的作用就是不用每一次一个对象经来都要看下它的哪些属性带有AutoId注解
     * 毕竟类的反射在性能上并不友好。只要key包含该Class,那么下次同样的class进来，就不需要检查它哪些属性带AutoId注解。
     */
    private Map<Class, List<Handler>> handlerMap = new ConcurrentHashMap<>();
```

插件部分源码

```java
public class AutoIdInterceptor implements Interceptor {
   /**
     * Map处理器
     */
    private Map<Class, List<Handler>> handlerMap = new ConcurrentHashMap<>();
    /**
     * 某某方法
     */
    private void process(Object object) throws Throwable {
        Class handlerKey = object.getClass();
        List<Handler> handlerList = handlerMap.get(handlerKey);
        //先判断handlerMap是否已存在该class，不存在先找到该class有哪些属性带有@AutoId
        if (handlerList == null) {
                handlerMap.put(handlerKey, handlerList = new ArrayList<>());
                // 通过反射 获取带有AutoId注解的所有属性字段,并放入到handlerMap中
        }
         //为带有@AutoId赋值ID
        for (Handler handler : handlerList) {
            handler.accept(object);
        }
      }
    }
```

**2）添加break label(标签)**

这个就比较细节了,因为上面的`process方法`不是线程安全的，也就是说可能存在同一时刻有N个线程进入process方法，那么这里可以优化如下：

```java
      //添加了SYNC标签
        SYNC:
        if (handlerList == null) {
          //此时handlerList确实为null,进入这里
            synchronized (this) {
                handlerList = handlerMap.get(handlerKey);
          //但到这里发现它已经不是为null了，因为可能被其它线程往map中插入数据,那说明其实不需要在执行下面的逻辑了，
          //直接跳出if体的SYNC标签位置。那么也就不会执行 if (handlerList == null) {}里面的逻辑。
                if (handlerList != null) {
                    break SYNC;
                }
            }
        }
```

这里虽然很细节，但也是有必要的，毕竟这里并发量很大,这样设计能一定程度提升性能。

![acda64387e0896604b5932dc433c8b77](https://user-images.githubusercontent.com/37285812/142142124-5aa67609-59c7-4cde-b19d-0e9cb2218b33.gif)
