package com.future.demo.doris;

/**
 * Doris Stream Load 记录，支持 UPSERT 与物理删除（对齐 finance DorisStreamLoadWriter）。
 */
public final class StreamLoadRecord<T> {

    private final T row;
    private final boolean delete;

    private StreamLoadRecord(T row, boolean delete) {
        this.row = row;
        this.delete = delete;
    }

    public static <T> StreamLoadRecord<T> upsert(T row) {
        return new StreamLoadRecord<>(row, false);
    }

    public static <T> StreamLoadRecord<T> delete(T row) {
        return new StreamLoadRecord<>(row, true);
    }

    public T getRow() {
        return row;
    }

    public boolean isDelete() {
        return delete;
    }
}
