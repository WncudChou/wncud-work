package com.wncud.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by yajunz on 2015/2/2.
 */
public class ThreadPools {
    private static ThreadPools threadPool;
    private volatile int finish_task;
    private List<Runnable> taskQueue = new ArrayList<Runnable>();
    private int thread_number = 5;
    private WorkThread[] workThreads;

    private ThreadPools(){
        WorkThread workThread;
        for(int i = 0; i < thread_number; i++){
            workThread = new WorkThread();
            workThreads[i] = workThread;
            workThread.start();
        }
    }

    private ThreadPools(int thread_number){
        this.thread_number = thread_number;
        workThreads = new WorkThread[thread_number];
        WorkThread workThread;
        for(int i = 0; i < thread_number; i++){
            workThread = new WorkThread();
            workThreads[i] = workThread;
            workThread.start();
        }
    }

    public static ThreadPools getThreadPool(){
        if(threadPool == null){
            threadPool = new ThreadPools();
        }
        return threadPool;
    }

    public static ThreadPools getThreadPool(int threadNumber){
        if(threadPool == null){
            threadPool = new ThreadPools(threadNumber);
        }
        return threadPool;
    }

    public  void execute(Runnable work){
        synchronized (taskQueue){
            taskQueue.add(work);
            taskQueue.notify();
        }
    }

    public void execute(List<Runnable> works){
        synchronized (taskQueue){
            for(Runnable work : works){
                taskQueue.add(work);
            }
            taskQueue.notifyAll();
        }
    }

    public void execute(Runnable[] works){
        synchronized (taskQueue){
            for(Runnable work : works){
                taskQueue.add(work);
            }
            taskQueue.notifyAll();
        }
    }


    public int getFinishWork(){
        return finish_task;
    }

    public int getTaskSize(){
        return taskQueue.size();
    }

    public void destroy(){
        while(!taskQueue.isEmpty()){
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for(WorkThread workThread : workThreads){
            workThread.stopWork();
            workThread = null;
        }



        taskQueue.clear();
        threadPool = null;
    }

    @Override
    public String toString() {
        return "WorkThread number:" + thread_number + "  finished task number:"
                + finish_task + "  wait task number:" + getTaskSize();
    }

    class WorkThread extends Thread{
        private boolean isRunning = true;

        @Override
        public void run() {
            Runnable task = null;
            while (isRunning){
                synchronized (taskQueue){
                    while (isRunning && taskQueue.isEmpty()){
                        try {
                            taskQueue.wait(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if(!taskQueue.isEmpty()){
                        task = taskQueue.remove(0);
                    }
                }

                if (task != null){
                    task.run();
                }

                finish_task++;
                task = null;
            }
        }

        public void stopWork(){
            isRunning = false;
        }
    }

    public static void main(String[] args) {
        ThreadPools threadPool = ThreadPools.getThreadPool(6);
        ThreadTask[] threadTasks ={ new ThreadTask(),
                    new ThreadTask(),
                new ThreadTask(),
                    new ThreadTask(),
                    new ThreadTask(),
                    new ThreadTask()
        };
        threadPool.execute(threadTasks);
        System.out.println(threadPool);
        threadPool.destroy();


        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<Future> result = new ArrayList<Future>();
        for(int i = 0;  i < 5; i++){
            result.add(executorService.submit(new ThreadTask()));
        }

        for(Future future : result){
            System.out.println(future.isDone());
        }

        executorService.shutdown();

        executorService = Executors.newCachedThreadPool();
        result.clear();
        for(int i = 0; i < 5; i++){
            result.add(executorService.submit(new CallableTask()));
        }

        for(Future future : result){
            try {
                System.out.println("任务是否完成:" + future.isDone() + ", 返回值" + future.get().toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}

class ThreadTask implements Runnable{
    private static volatile int i = 1;

    @Override
    public void run() {
        System.out.println("任务" + (i++) + "完成");
    }
}

class CallableTask implements Callable<String>{
    private static volatile int i;

    @Override
    public String call() throws Exception {
        System.out.println("callable 任务开始执行" + i++);
        Thread.currentThread().sleep(10000);
        return (i + "");
    }
}
