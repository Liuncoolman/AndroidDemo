package com.liun.demo;

import com.google.gson.Gson;

import org.junit.Test;

import java.security.PublicKey;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Description:
 * Author：Liun
 * Date:2018/05/24 10:11
 */
public class MainActivityTest {

    @Test
    public void testFormart() {
        String json = "{\n" +
                "    \"A\": [\n" +
                "        [\n" +
                "            \"C1\",\n" +
                "            \"C2\",\n" +
                "            \"C3\"\n" +
                "        ],\n" +
                "        [\n" +
                "            \"C4\",\n" +
                "            \"C5\",\n" +
                "            \"C6\"\n" +
                "        ],\n" +
                "        [\n" +
                "            \"C7\",\n" +
                "            \"C8\",\n" +
                "            \"C9\",\n" +
                "            \"C10\"\n" +
                "        ]\n" +
                "    ]\n" +
                "}";
        Gson gson = new Gson();
        TestA testA = gson.fromJson(json, TestA.class);
        for (int i = 0; i < testA.A.size(); i++) {
            System.out.println(i);
            ArrayList<String> bList = testA.A.get(i);
            for (int j = 0; j < bList.size(); j++) {
                System.out.println(bList.get(j));
            }

        }
    }

    private class TestA {
        public ArrayList<ArrayList<String>> A;


    }
}