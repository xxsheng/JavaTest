package com.xxq.messgaeborad;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * springboot启动类
 * Hello world!
 *
 */
@SpringBootApplication
@MapperScan("com.xxq.messgaeborad.mapper")
public class App 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(App.class, args);
        System.out.println( "Hello World!" );
    }
}
