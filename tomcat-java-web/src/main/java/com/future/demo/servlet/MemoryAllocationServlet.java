package com.future.demo.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 用于分配内存测试
 */
public class MemoryAllocationServlet extends HttpServlet {
    private final static Random Random = new Random();

    private List<byte[]> memoryAllocationBlocks = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain;charset=utf-8");

        byte [] bytes = new byte[1024*1024*128];
        Random.nextBytes(bytes);
        memoryAllocationBlocks.add(bytes);

        long totalBytesAllocation = 0;
        for(int i=0; i<memoryAllocationBlocks.size(); i++) {
            totalBytesAllocation += memoryAllocationBlocks.get(i).length;
        }

        resp.getWriter().write("总共已分配内存: " + totalBytesAllocation/(1024*1024) + "MB" +
                "，刚才分配内存: " + bytes.length/(1024*1024) + "MB");
    }
}
