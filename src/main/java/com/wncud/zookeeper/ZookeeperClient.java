package com.wncud.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.List;

/**
 * Created by huanggang on 2015/9/10.
 */
public class ZookeeperClient {
    public static void main(String[] args) {
        //String connectString = "119.254.97.32:2181";
        String connectString = "192.168.8.236:2181";
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(connectString, retryPolicy);

        client.start();
        client = client.usingNamespace("dlc_transfer");

        try {
            List<String> nodes = client.getChildren().forPath("/consumers");
            System.out.println(nodes.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            client.close();
        }
    }
}
