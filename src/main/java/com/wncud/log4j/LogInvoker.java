package com.wncud.log4j;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Created by zhouyajun on 2015/12/2.
 */
public class LogInvoker {
    public static void main(String[] args) {
        long index = 1;

        Logger logger = Logger.getLogger(LogInvoker.class);

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
                Thread.sleep(1000);
                logger.info(index);
                logger.debug(index);
                logger.warn(index);
                logger.trace(index);
                logger.error((index++));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
