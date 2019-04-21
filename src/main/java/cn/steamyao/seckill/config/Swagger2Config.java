package cn.steamyao.seckill.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author steamyao
 * @Package cn.steamyao.seckill.config
 * @date 2019/4/12 18:40
 * @description
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {

        @Bean
        public Docket api() {
            return new Docket(DocumentationType.SWAGGER_2)
                    .apiInfo(apiInfo())
                    .select()
                    // 自行修改为自己的包路径
                    .apis(RequestHandlerSelectors.basePackage("cn.steamyao.seckill.web"))
                    .paths(PathSelectors.any())
                    .build();
        }

        private ApiInfo apiInfo() {
            return new ApiInfoBuilder()
                    .title("swagger-api文档")
                    .description("秒杀案例")
                    //服务条款网址
                   // .termsOfServiceUrl("https://blog.csdn.net/ysk_xh_521")
                    .version("1.0")
                    //.contact(new Contact("Y.S.K", "http://ysk521.cn", "1176971130@qq.com"))
                    .build();
        }


}
