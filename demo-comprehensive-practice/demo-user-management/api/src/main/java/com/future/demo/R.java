package com.future.demo;

import lombok.Data;

/**
 * Api响应类
 */
@Data
public class R {
    private int errorCode;
    private String errorMessage;
    private Object data;
    private Integer totalPages;
    private Integer totalRecords;
    private Integer pageNum;
    private Integer pageSize;

    public static R success(Object data,
                            Integer totalPages,
                            Integer totalRecords,
                            Integer pageNum,
                            Integer pageSize) {
        R r = new R();
        r.setData(data);
        r.setTotalPages(totalPages);
        r.setTotalRecords(totalRecords);
        r.setPageNum(pageNum);
        r.setPageSize(pageSize);
        return r;
    }

    public static R success(Object data) {
        R r = new R();
        r.setData(data);
        return r;
    }

    public static R success() {
        return success(null);
    }
}
