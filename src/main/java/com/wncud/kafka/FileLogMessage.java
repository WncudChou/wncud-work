package com.wncud.kafka;

import kafka.serializer.Encoder;
import org.apache.commons.lang3.SerializationUtils;

/**
 * Created by yajunz on 2014/11/21.
 */
public class FileLogMessage implements Encoder<FileLogNode>{
    @Override
    public byte[] toBytes(FileLogNode fileLogNode) {
        return SerializationUtils.serialize(fileLogNode);
    }
}
