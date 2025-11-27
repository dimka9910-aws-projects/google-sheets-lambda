package com.github.dimka9910.services;

import com.github.dimka9910.dto.ColorEnum;
import com.github.dimka9910.dto.DebugDTO;
import com.github.dimka9910.dto.OperationTypeEnum;
import com.github.dimka9910.dto.RecordDTO;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

@Slf4j
public class SheetsFinancialService {

    private final Sheets sheetsService;
    private final String sheetID = "15tFk4EHb9NAhg9JJS-tj4IywLzd2DfVn_5hbnMitfCM";
    private final String LAST_ROW_CELL = "B1";
    private final String EX_SHEET_NAME;

    private final String TRANSFER_SHEET_NAME = "TRANSFERS";
    private final String FUNDS_SHEET_NAME = "FUNDS";
    private final String HOME_PAGE = "DASHBOARD_DIMA";
    private final String DEBUG_TABLE = "DEBUG_TABLE";

    private MapToRowList mapToExpenses = new MapToRowList();
    private final TableViewHelper tableViewHelper;
    private final PaintCellHelper paintCellHelper;


    public SheetsFinancialService(Sheets sheetsService) {
        this.sheetsService = sheetsService;
        this.tableViewHelper = new TableViewHelper(sheetID, sheetsService);
        this.paintCellHelper = new PaintCellHelper(sheetID, sheetsService);
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

    private String getRangeForFunds(Integer rowToPlace) {
        return FUNDS_SHEET_NAME + "!A" + rowToPlace + ":E" + rowToPlace;
    }

    private String getRangeForRemainData() {
        return DEBUG_TABLE + "!M3" + ":V14";
    }

    private String getRangeForRemainsInsert(Integer rowToPlace) {
        return DEBUG_TABLE + "!A" + rowToPlace + ":F" + rowToPlace;
    }

    private String getRangeForRemainsInsertColor(Integer rowToPlace) {
        return DEBUG_TABLE + "!A" + rowToPlace + ":I" + rowToPlace;
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

    public void handleCredit(RecordDTO recordDTO) throws IOException {
        recordDTO.setOperationType(OperationTypeEnum.EXPENSES);
        handleExpenses(recordDTO);

        Integer rowToPlace = getLastRowNum(FUNDS_SHEET_NAME);
        List<List<Object>> values = mapToExpenses.mapToFunds(recordDTO);
        ValueRange body = new ValueRange().setValues(values);
        String range = getRangeForFunds(rowToPlace);
        Sheets.Spreadsheets.Values.Update request = sheetsService.spreadsheets().values().update(sheetID, range, body);
        request.setValueInputOption("USER_ENTERED");
        log.info(String.valueOf(request.execute()));


        recordDTO.setSecondAccount(recordDTO.getAccountName());
        recordDTO.setSecondPerson(recordDTO.getUserName());
        recordDTO.setUserName(null);
        recordDTO.setAccountName(null);
        recordDTO.setOperationType(OperationTypeEnum.INCOME);
        handleTransferOperation(recordDTO);
    }

    public void handleTransferOperation(RecordDTO recordDTO) throws IOException {
        Integer rowToPlace = getLastRowNum(TRANSFER_SHEET_NAME);
        List<List<Object>> values = mapToExpenses.mapToTransferObject(recordDTO);

        ValueRange body = new ValueRange().setValues(values);
        String range = getRangeForTransfer(rowToPlace);

        Sheets.Spreadsheets.Values.Update request = sheetsService.spreadsheets().values().update(sheetID, range, body);
        request.setValueInputOption("USER_ENTERED");
        log.info(String.valueOf(request.execute()));
    }


    public void insertIntoDebug(RecordDTO recordDTO) throws IOException {

        if (recordDTO.getAccountRemains() == null) {
            return;
        }

        Integer rowToPlace = getLastRowNum(DEBUG_TABLE);

        Map<String, Map<String, Double>> remainsMap = tableViewHelper.getTableAsMap(getRangeForRemainData());
        System.out.println(remainsMap);


        Double accountRemains = remainsMap.get(recordDTO.getAccountName()).get(recordDTO.getCurrency());
        DebugDTO debugDTO = DebugDTO.builder()
                .currency(recordDTO.getCurrency())
                .accountName(recordDTO.getAccountName())
                .calculatedAmount(accountRemains)
                .reportedAmount(recordDTO.getAccountRemains())
                .build();

        List<List<Object>> values = mapToExpenses.mapToDebugObject(debugDTO);

        ValueRange body = new ValueRange().setValues(values);
        String range = getRangeForRemainsInsert(rowToPlace);

        Sheets.Spreadsheets.Values.Update request = sheetsService.spreadsheets().values().update(sheetID, range, body);
        request.setValueInputOption("USER_ENTERED");
        log.info(String.valueOf(request.execute()));

        paintCellHelper.paintRows(getRangeForRemainsInsertColor(rowToPlace),
                ColorConfigurationService.getColorByAccountNameAndUserName(
                        recordDTO.getAccountName(),
                        recordDTO.getUserName()));

    }
}



