package com.github.dimka9910;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dimka9910.dto.RecordDTO;
import com.github.dimka9910.services.SheetsFinancialService;
import com.github.dimka9910.services.TableViewHelper;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Slf4j
public class GoogleSheetsLambdaFunction implements RequestHandler<SQSEvent, String> {


    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "/service-acc.json";
    private static final Sheets service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    SheetsFinancialService sheetsFinancialService = new SheetsFinancialService(service);

    private static Sheets getService() throws IOException, GeneralSecurityException {
        // Try environment variable first (for Lambda), fallback to file (for local dev)
        String credentialsJson = System.getenv("GOOGLE_CREDENTIALS_JSON");
        InputStream in;
        
        if (credentialsJson != null && !credentialsJson.isBlank()) {
            log.info("Loading Google credentials from GOOGLE_CREDENTIALS_JSON env var");
            in = new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8));
        } else {
            log.info("Loading Google credentials from resource file");
            in = GoogleSheetsLambdaFunction.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
            if (in == null) {
                throw new FileNotFoundException("Set GOOGLE_CREDENTIALS_JSON env var or add " + CREDENTIALS_FILE_PATH);
            }
        }

        GoogleCredentials credentials = ServiceAccountCredentials.fromStream(in)
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
        NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, requestInitializer)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    static {
        try {
            service = getService();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public String handleRequest(SQSEvent sqsEvent, Context context) {
        log.info("MessageReceived: " + sqsEvent.toString());

        for (SQSEvent.SQSMessage sqsMessage : sqsEvent.getRecords()) {
            try {
                // Parse the SQS message body into RecordDTO
                RecordDTO record = objectMapper.readValue(sqsMessage.getBody(), RecordDTO.class);

                // Here you can do any business logic with the record object
                // Example: Print out the deserialized object
                log.info("Processing record: " + record);


                switch (record.getOperationType()) {
                    case TRANSFER:
                    case INCOME:
                        sheetsFinancialService.handleTransferOperation(record);
                        return "OK_TRANSFER";
                    case EXPENSES:
                        sheetsFinancialService.handleExpenses(record);
                        return "OK_EXPENSES";
                    case CREDIT:
                        sheetsFinancialService.handleCredit(record);
                        return "OK_EXPENSES";
                    case DEBUG:
                        sheetsFinancialService.insertIntoDebug(record);
                        return "OK_EXPENSES";
                    default:
                        log.error("NO OPERATION TYPE");
                        return "ERROR";
                }


            } catch (IOException e) {
                log.error("Error processing SQS message: " + e.getMessage());
                // Return failure if there's an error parsing the message
                return "Failure";
            }
        }
        return "Failure";
    }
}
