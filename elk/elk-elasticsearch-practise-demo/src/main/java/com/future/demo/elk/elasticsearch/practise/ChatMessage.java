package com.future.demo.elk.elasticsearch.practise;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatMessage {
    private long id;
    private long userIdFrom;
    private long userIdTo;
    private long groupId;
    private String content;
}
