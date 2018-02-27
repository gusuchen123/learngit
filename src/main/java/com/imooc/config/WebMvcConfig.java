package com.imooc.config;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

/**
 * @author gusuchen
 * Created in 2018-01-11 14:35
 * Description: 前端框架集成
 * Modified by:
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter implements ApplicationContextAware {
    @Value("${spring.thymeleaf.cache}")
    private boolean thymeleafCacheEnable = true;

    // spring应用上下文
    private ApplicationContext applicationContext;

    /**
     * @author gusuchen
     * Created in 2018/1/11 14:36
     * Description: 获得spring应用上下文，设值注入加载bean
     * Modified by:
     * @param applicationContext
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * @author gusuchen
     * Created in 2018/1/11 15:51
     * Description: 静态资源加载配置
     * Modified by: 
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

    /**
     * @author gusuchen
     * Created in 2018/1/11 14:40
     * Description: 模板资源解析器
     * Modified by:
     * @return SpringResourceTemplateResolver
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.thymeleaf")
    public SpringResourceTemplateResolver templateResolver() {
        // 创建模板资源解析器
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(this.applicationContext);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(thymeleafCacheEnable);
        return templateResolver;
    }

    /**
     * @author gusuchen
     * Created in 2018/1/11 14:43
     * Description: thymeleaf标准方言解析器
     * Modified by:
     * @return SpringTemplateEngine
     */
    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        // 支持Spring EL 表达式
        templateEngine.setEnableSpringELCompiler(true);

        // 支持 spring security 方言
        SpringSecurityDialect securityDialect = new SpringSecurityDialect();
        templateEngine.addDialect(securityDialect);
        return templateEngine;
    }

    /**
     * @author gusuchen
     * Created in 2018/1/11 14:47
     * Description: thymeleaf 视图解析器
     * Modified by:
     * @return ThymeleafViewResolver
     */
    @Bean
    public ThymeleafViewResolver viewResolver() {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        return viewResolver;
    }

    /**
     * @author gusuchen
     * Created in 2018/1/15 10:58
     * Description: 注入一个modelMapper工具类
     * Modified by:
     * @return ModelMapper
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    /**
     * @author gusuchen
     * Created in 2018/1/15 13:11
     * Description: 注入一个Gson工具类, Bean Util
     * Modified by: 
     * @param 
     * @return 
     */
    @Bean
    public Gson gson() {
        return new Gson();
    }
}
