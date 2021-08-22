package com.future.demo.argument;

public class ParamMessageVO {
    private long userIdTo;
    private String receiptId;
    private String content;

    public long getUserIdTo() {
        return userIdTo;
    }

    public void setUserIdTo(long userIdTo) {
        this.userIdTo = userIdTo;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(ParamMessageVO.class.getSimpleName()+ "{");
        builder.append("userIdTo:" + userIdTo);
        builder.append(",receiptId:" + receiptId);
        builder.append(",content:" + content);
        builder.append("}");
        return builder.toString();
    }
}
