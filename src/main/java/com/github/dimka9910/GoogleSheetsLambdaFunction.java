package com.github.dimka9910;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.github.dimka9910.dto.OperationTypeEnum;
import com.github.dimka9910.dto.RecordDTO;
import com.github.dimka9910.services.SheetsBorrowService;
import com.github.dimka9910.services.SheetsFinancialService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Slf4j
public class GoogleSheetsLambdaFunction implements RequestHandler<RecordDTO, String> {


    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "/service-acc.json";
    private static final Sheets service;

    private static Sheets getService() throws IOException, GeneralSecurityException {
        // Load client secrets.
        InputStream in = SheetsQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
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


    public String handleRequest(RecordDTO input, Context context) {

        SheetsFinancialService sheetsFinancialService = new SheetsFinancialService(service);
        SheetsBorrowService sheetsBorrowService = new SheetsBorrowService(service);

        try {

            switch (input.getOperationType()) {
                case FUND_MONEY_TRANSFER:
                    sheetsFinancialService.handleFundMoneyTransferOperation(input);
                    return "OK_FUND_MONEY_TRANSFER";
                case BORROW:
                    sheetsBorrowService.handleBorrow(input);
                    return "OK_BORROW";
                case INTERNAL_TRANSFER:
                    sheetsFinancialService.handleInternalTransferOperation(input);
                    return "OK_INTERNAL_TRANSFER";
                case INCOME:
                case EXPENSES:
                default:
                    sheetsFinancialService.handleExpenses(input);
                    return input.getOperationType().toString();
            }
        } catch (Exception e){
            log.error(e.getMessage());
            return e.getMessage();
        }
    }
}
