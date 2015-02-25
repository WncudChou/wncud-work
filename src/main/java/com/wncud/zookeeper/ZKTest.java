package com.wncud.zookeeper;

import com.wncud.zookeeper.core.ZKUtil;
import com.wncud.zookeeper.core.ZooKeeperWatcher;
import org.apache.zookeeper.KeeperException;
import org.junit.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by yajunz on 2014/12/15.
 */
public class ZKTest {

    @Test
    public void testList(){
        String quorumServers = "192.168.69.9";
        int sessionTimeout = 9000;
        int maxRetries = 300000;
        int retryIntervalMillis = 5;
        try {
            ZooKeeperWatcher zooKeeperWatcher = new ZooKeeperWatcher(quorumServers, sessionTimeout, maxRetries, retryIntervalMillis);
            List<String> nodes = ZKUtil.listChildrenNoWatch(zooKeeperWatcher, "/squirrel/filelog");
            for(String node : nodes){
                System.out.println(node);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
