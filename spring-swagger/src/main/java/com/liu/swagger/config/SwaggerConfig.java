package com.liu.swagger.config;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

@Configuration
@ComponentScan(basePackages = {"com.liu"})
@EnableSwagger2
public class SwaggerConfig {


    @Value("${docket.flag}")
    private boolean flag;

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                //分组
                .groupName("对接")
                .enable(flag)//是否启用swagger
                .select()
                //basePackage:指定要扫描的包
                //any:扫描全部
                //none:不扫描
                //.apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .apis(RequestHandlerSelectors.basePackage("com.liu.swagger"))
                //过滤:paths
                //ant：过滤路径，比如只过滤/hello/**下面的接口
                //.paths(PathSelectors.ant("/hello/**"))
                .build();
    }

    @Bean
    public Docket docketDefault() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("前端");
    }




    //配置默认信息
    private ApiInfo apiInfo() {
        Contact contact = new Contact("刘苗", "https://github.com/moodincode/com_around_project", "1695875374@qq.com");
        return new ApiInfo(
                "刘苗的Swagger构建API文档日志",
                "作者:刘苗",
                "v1.0",
                "https://github.com/moodincode/com_around_project",
                contact,
                "Apache2.0",
                "",
                new ArrayList<>()

        );
    }


}
