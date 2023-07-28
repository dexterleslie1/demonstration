package com.future.demo.argument;

import lombok.Data;

import java.util.List;

@Data
public class ParamMessageVO {
    private long userIdTo;
    private String receiptId;
    private List<String> contentList;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(ParamMessageVO.class.getSimpleName()+ "{");
        builder.append("userIdTo:" + userIdTo);
        builder.append(",receiptId:" + receiptId);
        builder.append(",contentList:" + contentList);
        builder.append("}");
        return builder.toString();
    }
}
