package com.future.demo.elasticsearch.practise;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ChatGroupNGroupAssist {
    private long id;
    private String name;
    /**
     * 0、群聊 1、群发助手
     */
    private int type;

    private List<ChatGroupNGroupAssistMember> memberList;
}
