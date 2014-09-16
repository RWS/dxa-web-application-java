package org.dd4t.test;

import org.dd4t.contentmodel.GenericPage;
import org.dd4t.core.serializers.impl.DefaultSerializer;

import java.io.File;
import java.nio.file.Files;

public class DeserializeTest {

    public static void main(String[] args) throws Exception {

        byte[] bytes = Files.readAllBytes(new File("C:\\Projects\\SDL\\refimpl\\dd4t-core\\src\\test\\resources\\Page.xml").toPath());
        String input = new String(bytes, "UTF-8");


        DefaultSerializer ser = new DefaultSerializer();
        Object page = ser.deserialize(input, GenericPage.class);

        System.out.println(page);
    }
}
