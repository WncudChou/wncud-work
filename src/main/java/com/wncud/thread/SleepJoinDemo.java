package com.wncud.thread;

/**
 * Created by yajunz on 2015/2/2.
 */
public class SleepJoinDemo {
    public static void main(String[] args) {
        Sleeper sleeper1 = new Sleeper("sleeper1", 1500);
        Sleeper sleeper2 = new Sleeper("sleeper2", 1500);
        Joiner joiner1 = new Joiner("joiner1", sleeper1);
        Joiner joiner2 = new Joiner("joiner2", sleeper2);
        sleeper2.interrupt();
    }
}
class Sleeper extends Thread{
    private int sleepTime;

    public Sleeper(String name, int sleepTime){
        super(name);
        this.sleepTime = sleepTime;
        start();
    }

    @Override
    public void run() {
        try {
            sleep(sleepTime);
        } catch (InterruptedException e) {
            System.out.println(getName() + " was interrupted.\n"
                    + "isInterrupted():" + isInterrupted());
            return;
        }

        System.out.println(getName() + " has wakened");
    }
}

class Joiner extends Thread{
    private Sleeper sleeper;

    public Joiner(String name, Sleeper sleeper){
        super(name);
        this.sleeper = sleeper;
        start();
    }

    @Override
    public void run() {
        try {
            sleeper.join();
        } catch (InterruptedException e) {
            System.out.println("interrupted");
        }
        System.out.println(getName() + " join completed");
    }
}

