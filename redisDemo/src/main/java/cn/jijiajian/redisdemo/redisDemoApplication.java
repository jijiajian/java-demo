package cn.jijiajian.redisdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;



/**
 * @author J
 * @time 2017/10/17 22:23
 * @description 程序入口
 **/
@SpringBootApplication
@ImportResource("classpath:config/spring-*.xml") //加载自定义spring 配置文件
public class redisDemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(redisDemoApplication.class, args);
	}
}
