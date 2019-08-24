/*
 * Copyright (c) 2018 Wantu, All rights reserved.
 */
package com.jincou.config;


import com.alibaba.druid.pool.DruidDataSource;
import com.jincou.plugin.AutoIdInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;


import javax.sql.DataSource;
import java.util.Collections;

/**
 * @author xub
 * @Description: 连接数据库信息 包括添加插件信息
 * @date 2019/8/19 下午12:31
 */
@Configuration
@ComponentScan(basePackageClasses = DalModule.class)
@MapperScan(basePackages = "com.jincou.mapper")
public class DalModule {

    /**
     * 连接数据库数据源信息
     */
    @Bean
    public DataSource dataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=UTF-8");
        druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("root");
        druidDataSource.setMaxActive(20);
        druidDataSource.setInitialSize(1);
        druidDataSource.setMaxWait(60000);
        druidDataSource.setMinIdle(1);
        druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
        druidDataSource.setMinEvictableIdleTimeMillis(300000);
        druidDataSource.setValidationQuery("SELECT 'x'");
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setTestOnReturn(false);
        druidDataSource.setPoolPreparedStatements(true);
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
        druidDataSource.setConnectionInitSqls(Collections.singletonList("SET NAMES utf8mb4"));
        return druidDataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    /**
     * 插件实体
     */
    @Bean
    AutoIdInterceptor autoIdInterceptor() {
        return new AutoIdInterceptor();
    }

    /**
     * SqlSessionFactory 实体
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setFailFast(true);
        sessionFactory.setMapperLocations(resolver.getResources("classpath:/mapper/*Mapper.xml"));
        /**
         * 添加插件信息(因为插件采用责任链模式所有可以有多个，所以采用数组
         */
        Interceptor[] interceptors = new Interceptor[1];
        interceptors[0] = autoIdInterceptor();
        sessionFactory.setPlugins(interceptors);
        return sessionFactory.getObject();
    }

}
