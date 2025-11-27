package com.github.dimka9910.dto;


import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DebugDTO {

    private String accountName;
    private String currency;
    private Double calculatedAmount;
    private Double reportedAmount;

}
