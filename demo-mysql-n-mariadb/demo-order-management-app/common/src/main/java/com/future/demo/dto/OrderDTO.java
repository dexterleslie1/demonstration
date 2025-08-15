package com.future.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.future.demo.entity.DeleteStatus;
import com.future.demo.entity.Status;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    // long 类型
    private Long id;
    // int 类型
    /*private Integer id;*/
    // biginteger 类型
    /*private BigInteger id;*/
    // uuid string 类型
    /*private String id;*/

    private Long userId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    @JsonSerialize(using = Status.StatusSerializer.class)
    private Status status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime payTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime deliveryTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime receivedTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime cancelTime;
    @JsonSerialize(using = DeleteStatus.DeleteStatusSerializer.class)
    private DeleteStatus deleteStatus;
    private List<OrderDetailDTO> orderDetailList;
}
