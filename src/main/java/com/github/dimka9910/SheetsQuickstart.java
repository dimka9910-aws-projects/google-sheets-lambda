package com.github.dimka9910;

import com.github.dimka9910.dto.OperationTypeEnum;
import com.github.dimka9910.dto.RecordDTO;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SheetsQuickstart {

    public static void main(String... args) throws IOException, GeneralSecurityException {
        GoogleSheetsLambdaFunction googleSheetsLambdaFunction = new GoogleSheetsLambdaFunction();

        RecordDTO recordDTO = RecordDTO.builder()
                .amount(55.0)
                .currency("RSD")
                .userName("DIMA")
                .accountName("CASH_DIMA")
                .fundName("-")
                .operationType(OperationTypeEnum.EXPENSES)
                .build();

        googleSheetsLambdaFunction.handleRequest(recordDTO, null);
    }
}