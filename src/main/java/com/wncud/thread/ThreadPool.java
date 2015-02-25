package com.wncud.thread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by yajunz on 2014/12/8.
 */
public final class ThreadPool {
    public static final ThreadPool instance = ThreadPool.getInstance();

    public static final int SYSTEM_TASK_COUNT = 150;

    public static int worker_num = 5;
    private static int taskCount = 0;

    public static boolean systemIsBusy = false;

    private static List<Task> takQueue = Collections.synchronizedList(new ArrayList<Task>());
    public PoolWorker[] workers;

    private ThreadPool(){
        workers = new PoolWorker[5];
        for(int i = 0; i < workers.length; i++){
            workers[i] = new PoolWorker(i);
        }
    }

    private ThreadPool(int pool_worker_num){
        worker_num = pool_worker_num;
        workers = new PoolWorker[worker_num];
        for(int i = 0; i < workers.length; i++){
            workers[i] = new PoolWorker(i);
        }
    }

    public static synchronized ThreadPool getInstance(){
        if(instance == null){
            return new ThreadPool();
        }
        return instance;
    }



    public class PoolWorker extends Thread {
        private int index = -1;

        private boolean isRunning = true;

        private boolean isWaiting = true;

        public PoolWorker(int index){
            this.index = index;
            start();
        }

        public void stopWorker(){
            this.isRunning = false;
        }

        public boolean isWaiting(){
            return this.isWaiting;
        }

        @Override
        public void run() {
            while(isRunning){
                Task r = null;
                synchronized (takQueue){

                }
            }
        }
    }
}
