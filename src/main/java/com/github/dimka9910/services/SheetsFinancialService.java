package com.github.dimka9910.services;

import com.github.dimka9910.dto.RecordDTO;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
public class SheetsFinancialService {

    private Sheets sheetsService;
    private String sheetID = "15tFk4EHb9NAhg9JJS-tj4IywLzd2DfVn_5hbnMitfCM";
    private String LAST_ROW_CELL = "B1";
    private final String EX_SHEET_NAME;

    private final String TRANSFER_SHEET_NAME = "TRANSFERS";
    private final String HOME_PAGE = "DASHBOARD_DIMA";

    private MapToRowList mapToExpenses = new MapToRowList();

    public SheetsFinancialService(Sheets sheetsService) {
        this.sheetsService = sheetsService;
        try {
            String range = HOME_PAGE + '!' + "F1";
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(sheetID, range)
                    .execute();
            EX_SHEET_NAME = response.getValues().get(0).get(0).toString().trim();
        } catch (Exception e) {
            throw new RuntimeException("CAN'T GET SHEET NAME: " + e.getMessage(), e);
        }
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

    private String getRangeForExpenses(Integer rowToPlace) {
        return EX_SHEET_NAME + "!B" + rowToPlace + ":I" + rowToPlace;
    }


    private String getRangeForTransfer(Integer rowToPlace) {
        return TRANSFER_SHEET_NAME + "!B" + rowToPlace + ":J" + rowToPlace;
    }

    public void handleExpenses(RecordDTO recordDTO) throws IOException {
        Integer rowToPlace = getLastRowNum(EX_SHEET_NAME);
        List<List<Object>> values = mapToExpenses.mapToExpensesRecordList(recordDTO);
        ValueRange body = new ValueRange().setValues(values);
        String range = getRangeForExpenses(rowToPlace);

        Sheets.Spreadsheets.Values.Update request = sheetsService.spreadsheets().values().update(sheetID, range, body);
        request.setValueInputOption("USER_ENTERED");
        log.info(String.valueOf(request.execute()));
    }

    public void handleTransferOperation(RecordDTO recordDTO) throws IOException {
        Integer rowToPlace = getLastRowNum(TRANSFER_SHEET_NAME);
        List<List<Object>> values = mapToExpenses.mapToTranferObject(recordDTO);

        ValueRange body = new ValueRange().setValues(values);
        String range = getRangeForTransfer(rowToPlace);

        Sheets.Spreadsheets.Values.Update request = sheetsService.spreadsheets().values().update(sheetID, range, body);
        request.setValueInputOption("USER_ENTERED");
        log.info(String.valueOf(request.execute()));
    }

}
