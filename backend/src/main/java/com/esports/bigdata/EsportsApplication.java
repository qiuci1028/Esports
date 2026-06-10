package com.esports.bigdata;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Riot 电竞大数据分析平台 - 启动入口
 */
@SpringBootApplication
@MapperScan("com.esports.bigdata.module.**.mapper")
@EnableScheduling
public class EsportsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EsportsApplication.class, args);
        System.out.println("\n" +
                "  ____  _____ ____   ___  __  __ ____  ____  ____  \n" +
                " |  _ \\| ____/ ___| / _ \\|  \\/  / __ )|  _ \\/ ___| \n" +
                " | |_) |  _| \\___ \\| | | | |\\/| /  _ \\| |_) \\___ \\  \n" +
                " |  _ <| |___ ___) | |_| | |  | | (_) |  _ < ___) | \n" +
                " |_| \\_\\_____|____/ \\___/|_|  |_|\\___/|_| \\_\\____/  \n" +
                "                                                     \n" +
                "  Riot Esports Big Data Analysis Platform started.  \n" +
                "  API doc: http://localhost:8080/api/doc.html\n");
    }
}
