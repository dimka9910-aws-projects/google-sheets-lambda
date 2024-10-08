package com.github.dimka9910;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dimka9910.dto.OperationTypeEnum;
import com.github.dimka9910.dto.RecordDTO;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class SheetsQuickstart {

    public static void main(String... args) throws IOException, GeneralSecurityException {
        GoogleSheetsLambdaFunction googleSheetsLambdaFunction = new GoogleSheetsLambdaFunction();

//        RecordDTO recordDTO = RecordDTO.builder()
//                .amount(55.25)
//                .currency("RSD")
//                .userName("DIMA")
//                .accountName("CASH_DIMA")
//                .fundName("TRAVEL")
//                .operationType(OperationTypeEnum.EXPENSES)
//                .build();

        RecordDTO recordDTO = RecordDTO.builder()
                .amount(55.25)
                .currency("RSD")
                .userName("DIMA")
                .secondPerson("KIKI")
                .accountName("CASH_DIMA")
                .secondAccount("CASH_KIKI")
                .operationType(OperationTypeEnum.TRANSFER)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        SQSEvent.SQSMessage sqsMessage = new SQSEvent.SQSMessage();
        sqsMessage.setBody(objectMapper.writeValueAsString(recordDTO));
        SQSEvent sqsEvent = new SQSEvent();
        sqsEvent.setRecords(List.of(sqsMessage));

        googleSheetsLambdaFunction.handleRequest(sqsEvent, null);
    }
}