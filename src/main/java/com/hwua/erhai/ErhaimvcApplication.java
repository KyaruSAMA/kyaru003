package com.hwua.erhai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hwua.erhai.mapper")
public class ErhaimvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErhaimvcApplication.class, args);
    }

}
