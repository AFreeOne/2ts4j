package org.freeone.test;

import java.util.Arrays;
import java.util.List;

public class Test01 {
    public static void main(String[] args) {
        List<String> list = Arrays.asList("qwer","qwe");
        System.err.println(list.contains("qwer"));
    }
}
