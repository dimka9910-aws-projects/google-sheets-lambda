package com.github.dimka9910.dto;


import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RecordDTO {

    private Double amount;
    private String currency;
    private String userName;
    private String accountName;
    private String fundName;
    private OperationTypeEnum operationType;
    private String comment;

    private String secondPerson;
    private String secondAccount;
    private String secondCurrency;
}
