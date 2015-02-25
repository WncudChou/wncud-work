package com.wncud.thread;

import org.junit.runner.RunWith;

import java.util.Date;

/**
 * Created by yajunz on 2014/12/8.
 */
public abstract class Task implements RunWith{

    private Date generateTime = null;
    private Date submitTime = null;
    private Date beginExecuteTime = null;
    private Date finishTime = null;
    private long taskId;

    public Task(){
        this.generateTime = new Date();
    }

    public void run(){

    }

    public abstract Task[] taskCore() throws Exception;

    protected abstract boolean userDb();

    protected abstract boolean needExecuteImmediate();

    protected abstract String info();

    public Date getGenerateTime() {
        return generateTime;
    }

    public void setGenerateTime(Date generateTime) {
        this.generateTime = generateTime;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public Date getBeginExecuteTime() {
        return beginExecuteTime;
    }

    public void setBeginExecuteTime(Date beginExecuteTime) {
        this.beginExecuteTime = beginExecuteTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }
}
