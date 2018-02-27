package com.imooc.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * @author gusuchen
 * Created in 2018-01-10 15:16
 * Description: 后端框架搭建: JPA 配置类
 * Modified by:
 */
@Configuration
@EnableJpaRepositories(basePackages = {"com.imooc.repository"})
@EnableTransactionManagement
public class JPAConfig {

    /**
     * @author gusuchen
     * Created in 2018/1/10 15:25
     * Description: 创建数据源
     * Modified by:
     * @return Datasource
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * @author gusuchen
     * Created in 2018/1/10 15:27
     * Description: 创建实体映射关系管理类
     * Modified by:
     * @return LocalContainerEntityManagerFactoryBean
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        // 创建 hibernate jpa 适配器
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        // 是否自动生成sql
        jpaVendorAdapter.setGenerateDdl(false);

        // 创建实体映射关系管理类
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        // 设置数据源
        entityManagerFactory.setDataSource(dataSource());
        // 设置JPA适配器
        entityManagerFactory.setJpaVendorAdapter(jpaVendorAdapter);
        // 设置需要寻找的实体类的包名
        entityManagerFactory.setPackagesToScan("com.imooc.entity");
        return entityManagerFactory;
    }

    /**
     * @author gusuchen
     * Created in 2018/1/10 15:37
     * Description: 创建事务管理类
     * Modified by:
     * @return PlatformTransactionManager
     */
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        // 创建 jpa 事务管理
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        // 绑定实体映射管理类
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}
