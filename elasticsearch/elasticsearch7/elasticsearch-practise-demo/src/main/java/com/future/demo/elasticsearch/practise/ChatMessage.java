package com.future.demo.elasticsearch.practise;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatMessage {
    private long id;
    private long userId;
//    private long userIdFrom;
//    private long userIdTo;
    private long friendId;
    private long groupId;
    private String content;
    private boolean deleted;
}
