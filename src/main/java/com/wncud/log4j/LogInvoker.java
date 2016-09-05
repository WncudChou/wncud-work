package com.wncud.log4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhouyajun on 2015/12/2.
 */
public class LogInvoker {
    public static void main(String[] args) {
        long index = 1;

        Logger logger = LoggerFactory.getLogger(LogInvoker.class);

        /*for(int j=0; j < 3000; j++){
            try {
                Thread.sleep(2000);
                int i=0;
                i = 10/i;
            } catch (Exception e) {
                e.printStackTrace();
                logger.error((index++) + "错误信息:" + e.getMessage(), e);
            }
            logger.info((index++) + "debug message");
        }
*/
        for(int i = 0; i < 30000; i++){
            try {
                Thread.sleep(500);
                i = 10/i;
                logger.info(String.valueOf(index));
                logger.debug(String.valueOf(index));
                logger.warn(String.valueOf(index));
                logger.trace(index + "中文！！");
                logger.error((index++) + "中文！！");
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
            }
        }
    }
}
