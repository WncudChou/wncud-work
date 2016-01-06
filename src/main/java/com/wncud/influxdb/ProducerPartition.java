package com.wncud.influxdb;

/**
 * Created by zhouyajun on 2015/9/10.
 */
public class ProducerPartition {
    private String topic;

    private int pationIndex;

    private long logSize;

    private int leader;

    private int[] isr;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getPationIndex() {
        return pationIndex;
    }

    public void setPationIndex(int pationIndex) {
        this.pationIndex = pationIndex;
    }

    public long getLogSize() {
        return logSize;
    }

    public void setLogSize(long logSize) {
        this.logSize = logSize;
    }

    public int getLeader() {
        return leader;
    }

    public void setLeader(int leader) {
        this.leader = leader;
    }

    public int[] getIsr() {
        return isr;
    }

    public void setIsr(int[] isr) {
        this.isr = isr;
    }
}
