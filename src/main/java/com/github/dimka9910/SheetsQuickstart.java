package com.github.dimka9910;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dimka9910.dto.OperationTypeEnum;
import com.github.dimka9910.dto.RecordDTO;
import com.github.dimka9910.services.SheetsFinancialService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class SheetsQuickstart {

    public static void main(String... args) throws IOException, GeneralSecurityException {

        test1();
    }

    public static void test2() throws IOException, GeneralSecurityException {
        GoogleSheetsLambdaFunction googleSheetsLambdaFunction = new GoogleSheetsLambdaFunction();

        RecordDTO recordDTO = RecordDTO.builder()
                .amount(55.25)
                .currency("RSD")
                .userName("DIMA")
                .accountName("CARD_DIMA_CREDIT")
                .fundName("TRAVEL")
                .operationType(OperationTypeEnum.CREDIT)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        SQSEvent.SQSMessage sqsMessage = new SQSEvent.SQSMessage();
        sqsMessage.setBody(objectMapper.writeValueAsString(recordDTO));
        SQSEvent sqsEvent = new SQSEvent();
        sqsEvent.setRecords(List.of(sqsMessage));

        googleSheetsLambdaFunction.handleRequest(sqsEvent, null);
    }

    public static void test1() throws IOException, GeneralSecurityException {
        GoogleSheetsLambdaFunction googleSheetsLambdaFunction = new GoogleSheetsLambdaFunction();

        RecordDTO recordDTO = RecordDTO.builder()
                .amount(55.25)
                .currency("RSD")
                .userName("DIMA")
                .accountName("CASH_DIMA")
                .fundName("TRAVEL")
                .operationType(OperationTypeEnum.EXPENSES)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        SQSEvent.SQSMessage sqsMessage = new SQSEvent.SQSMessage();
        sqsMessage.setBody(objectMapper.writeValueAsString(recordDTO));
        SQSEvent sqsEvent = new SQSEvent();
        sqsEvent.setRecords(List.of(sqsMessage));

        googleSheetsLambdaFunction.handleRequest(sqsEvent, null);
    }

    public static void testDebugTable() throws IOException, GeneralSecurityException {
        GoogleSheetsLambdaFunction googleSheetsLambdaFunction = new GoogleSheetsLambdaFunction();

        RecordDTO recordDTO = RecordDTO.builder()
                .operationType(OperationTypeEnum.DEBUG)
                .currency("RSD")
                .userName("DIMA")
                .accountName("CARD_DIMA_VISA_RAIF")
                .fundName("TRAVEL")
                .accountRemains(50000.0)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        SQSEvent.SQSMessage sqsMessage = new SQSEvent.SQSMessage();
        sqsMessage.setBody(objectMapper.writeValueAsString(recordDTO));
        SQSEvent sqsEvent = new SQSEvent();
        sqsEvent.setRecords(List.of(sqsMessage));

        googleSheetsLambdaFunction.handleRequest(sqsEvent, null);
    }
}