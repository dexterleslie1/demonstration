package com.future.demo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatusEnumDTO {
    private Status name;
    private String description;
}
