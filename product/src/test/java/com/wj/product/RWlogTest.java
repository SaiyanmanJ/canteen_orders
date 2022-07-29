package com.wj.product;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Wang Jing
 * @time 7/28/2022 10:12 PM
 */
@SpringBootTest
public class RWlogTest {

    @Test
    public void writeLog(){
        try {
            String line = "123";
            FileWriter fileWriter = new FileWriter("classpath:resources/schedule_work_logs/decrease_stock.logs", true);
            fileWriter.append(line);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
