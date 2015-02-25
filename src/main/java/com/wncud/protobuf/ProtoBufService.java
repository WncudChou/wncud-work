package com.wncud.protobuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            person.writeTo(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] byteArray = outputStream.toByteArray();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);

        PersonMsg.Person p = null;
        try {
            p = PersonMsg.Person.parseFrom(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(p.getName());
    }
}
