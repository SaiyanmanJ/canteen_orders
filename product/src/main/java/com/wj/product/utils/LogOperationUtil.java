package com.wj.product.utils;

import com.wj.product.config.RabbitTemplateConfig;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

/**
 * @author Wang Jing
 * @time 7/29/2022 9:01 AM
 * 产生log所需的path
 */

public class LogOperationUtil {

    public static boolean decreaseStockLog(String line){

        try {
            String path = Objects.requireNonNull(LogOperationUtil.class.getClassLoader().getResource("schedule_work_logs")).getPath();
            FileWriter fileWriter = new FileWriter(path + "/decrease_stock.txt", true);
            fileWriter.write(line + "\n");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
}
