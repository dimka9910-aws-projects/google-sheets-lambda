package com.github.dimka9910.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
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
    private Double accountRemains;
    
    // From AI Parser - indicates undo operation
    private Boolean undo;
}
