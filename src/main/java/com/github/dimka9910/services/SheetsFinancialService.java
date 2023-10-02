package com.github.dimka9910.services;

import com.github.dimka9910.dto.RecordDTO;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class SheetsFinancialService {

    private Sheets sheetsService;
    private String sheetID = "1Q2ed7pc5MSu7ME44bshG7qzd6xkpc_p0nNbrc2roTZA";
    private String LAST_ROW_CELL = "B1";
    private String SHEET_NAME = "EXPENSES";

    private MapToExpenses mapToExpenses = new MapToExpenses();

    public SheetsFinancialService(Sheets sheetsService) {
        this.sheetsService = sheetsService;
    }

    private Integer getLastRowNum(String sheetName) {
        try {
            String range = sheetName + '!' + LAST_ROW_CELL;
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(sheetID, range)
                    .execute();
            Integer cellValue = Integer.parseInt(response.getValues().get(0).get(0).toString());
            return cellValue;
        } catch (Exception e) {
            return 0;
        }
    }

    private String getRange(Integer rowToPlace) {
        return SHEET_NAME + "!B" + rowToPlace + ":I" + rowToPlace;
    }

    public void handleExpenses(RecordDTO recordDTO) throws IOException {
        Integer rowToPlace = getLastRowNum(SHEET_NAME);
        List<List<Object>> values = mapToExpenses.mapRecordDtoToList(recordDTO);
        ValueRange body = new ValueRange().setValues(values);
        String range = getRange(rowToPlace);

        Sheets.Spreadsheets.Values.Update request = sheetsService.spreadsheets().values().update(sheetID, range, body);
        request.setValueInputOption("USER_ENTERED");
        log.info(String.valueOf(request.execute()));
    }

    public void handleFundMoneyTransferOperation(RecordDTO recordDTO) throws IOException {
        Integer rowToPlace = getLastRowNum(SHEET_NAME);
        List<List<Object>> values = mapToExpenses.mapRecordDtoToList(recordDTO);
        ValueRange body = new ValueRange().setValues(values);
        String range = getRange(rowToPlace);

        Sheets.Spreadsheets.Values.Update request = sheetsService.spreadsheets().values().update(sheetID, range, body);
        request.setValueInputOption("USER_ENTERED");
        log.info(String.valueOf(request.execute()));

        values = mapToExpenses.mapSecondPerson(recordDTO);
        body = new ValueRange().setValues(values);
        range = getRange(rowToPlace+1);

        request = sheetsService.spreadsheets().values().update(sheetID, range, body);
        request.setValueInputOption("USER_ENTERED");
        System.out.println(request.execute());
    }

    public void handleInternalTransferOperation(RecordDTO recordDTO) throws IOException {
        Integer rowToPlace = getLastRowNum(SHEET_NAME);
        List<List<Object>> values = mapToExpenses.mapRecordDtoToList(recordDTO);
        ValueRange body = new ValueRange().setValues(values);
        String range = getRange(rowToPlace);

        Sheets.Spreadsheets.Values.Update request = sheetsService.spreadsheets().values().update(sheetID, range, body);
        request.setValueInputOption("USER_ENTERED");
        log.info(String.valueOf(request.execute()));

        recordDTO.setSecondPerson(recordDTO.getUserName());
        values = mapToExpenses.mapSecondPerson(recordDTO);
        body = new ValueRange().setValues(values);
        range = getRange(rowToPlace+1);

        request = sheetsService.spreadsheets().values().update(sheetID, range, body);
        request.setValueInputOption("USER_ENTERED");
        System.out.println(request.execute());
    }
}
