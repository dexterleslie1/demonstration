package com.future.demo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 好友关系
 */
@Data
@AllArgsConstructor
public class FriendRelation {
    private long id;
    private long userId;
    private long friendId;
    private String remark;
    private String uniqueIdentifier;
}
