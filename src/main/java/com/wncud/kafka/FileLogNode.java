package com.wncud.kafka;

import java.io.Serializable;

/**
 * Created by yajunz on 2014/11/21.
 */
public class FileLogNode implements Serializable{
    private static final long serialVersionUID = 8923085201098408806L;
    private String msg;
    private long startIndex;
    private long endIndex;
    private String host;
    private String path;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(long startIndex) {
        this.startIndex = startIndex;
    }

    public long getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(long endIndex) {
        this.endIndex = endIndex;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
