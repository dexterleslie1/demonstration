package com.future.demo.mapdb;

import org.mapdb.DB;
import org.mapdb.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class TestDtoListService {
    public static final String LIST_NAME_PREFIX = "listTest-";

    @Autowired
    private DB db;

    public String newRequestId() {
        return UUID.randomUUID().toString();
    }

    public String toListName(String requestId) {
        return LIST_NAME_PREFIX + requestId;
    }

    @SuppressWarnings("unchecked")
    public List<TestDto> open(String requestId) {
        return (List<TestDto>) this.db.indexTreeList(toListName(requestId), Serializer.JAVA).createOrOpen();
    }

    @SuppressWarnings("unchecked")
    public void clear(String requestId) {
        String listName = toListName(requestId);
        if (!this.db.exists(listName)) {
            return;
        }
        List<TestDto> list = (List<TestDto>) this.db.indexTreeList(listName, Serializer.JAVA).createOrOpen();
        list.clear();
    }
}
