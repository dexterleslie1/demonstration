package com.future.demo.vo;

import lombok.Data;

import java.util.List;

@Data
public class SliceVO {
    // 整个文件md5
    private String fileMd5;
    private String filename;
    private List<Slice> slices;

    @Data
    public static class Slice {
        // 分片顺序
        int order;
        // 分片md5
        private String md5;
        private boolean exists;
    }
}
