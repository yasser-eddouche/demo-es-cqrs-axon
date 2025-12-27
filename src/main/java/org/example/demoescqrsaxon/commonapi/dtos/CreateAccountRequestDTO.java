package org.example.demoescqrsaxon.commonapi.dtos;

import lombok.Data;

@Data
public class CreateAccountRequestDTO {
    private double initialBalance;
    private String currency;
}
