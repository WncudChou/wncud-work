package com.wncud.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by yajunz on 2014/10/11.
 */
public class ProtoBufService {
    public static void main(String[] args) {

        PersonMsg.Person.Builder builder = PersonMsg.Person.newBuilder();
        builder.setId(1);
        builder.setName("wncud");
        builder.addFriends("Friend A");
        builder.addFriends("Friend B");
        PersonMsg.Person person = builder.build();

        /*ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            person.writeTo(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] byteArray = outputStream.toByteArray();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);*/

        PersonMsg.Person p = null;
        try {
            //p = PersonMsg.Person.parseFrom(inputStream);
            p = PersonMsg.Person.parseFrom(person.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(p.getName());


        //AddressBookProtos.Person.Builder itemBuild = AddressBookProtos.Person.newBuilder();

        DataPacketMsg.DataPacket.Builder builder1 = DataPacketMsg.DataPacket.newBuilder();
        builder1.setTopic("topicTest");
        builder1.setHost("127.0.0.1");
        builder1.setPath("/a.log");
        builder1.setCollectTime(1L);
        builder1.setPosition(2L);
        builder1.addDataMessage("1 message");
        builder1.addDataMessage("2 message");
        DataPacketMsg.DataPacket dataPacket = builder1.build();
        DataPacketMsg.DataPacket newPacket = null;
        try {
            newPacket = DataPacketMsg.DataPacket.parseFrom(dataPacket.toByteArray());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        List<String> data = newPacket.getDataMessageList();
        for(String key : data){
            System.out.println(key);
        }
    }
}
