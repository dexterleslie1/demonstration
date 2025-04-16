package com.future.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Const {
    public final static String StreamName = "my-stream";
    public final static String GroupNameA = "group-a";
    public final static String GroupNameB = "group-b";
    public final static String ConsumerNameA = "consumer-a";
    public final static String ConsumerNameB = "consumer-b";

    public final static List<String> RecordIdList = Collections.synchronizedList(new ArrayList<>());

    public final static Integer StreamCount = 256;

}
