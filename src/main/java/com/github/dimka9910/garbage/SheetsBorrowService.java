//package com.github.dimka9910.services;
//
//import com.github.dimka9910.dto.RecordDTO;
//import com.google.api.services.sheets.v4.Sheets;
//import com.google.api.services.sheets.v4.model.ValueRange;
//import lombok.extern.slf4j.Slf4j;
//
//import java.io.IOException;
//import java.util.List;
//
//@Slf4j
//public class SheetsBorrowService {
//
//    private Sheets sheetsService;
//    private String sheetID = "1Q2ed7pc5MSu7ME44bshG7qzd6xkpc_p0nNbrc2roTZA";
//    private String LAST_ROW_CELL = "B1";
//    private String SHEET_NAME = "BORROW";
//
//    private MapToBorrow mapToBorrow = new MapToBorrow();
//
//    public SheetsBorrowService(Sheets sheetsService) {
//        this.sheetsService = sheetsService;
//    }
//
//    private Integer getLastRowNum(String sheetName) {
//        try {
//            String range = sheetName + '!' + LAST_ROW_CELL;
//            ValueRange response = sheetsService.spreadsheets().values()
//                    .get(sheetID, range)
//                    .execute();
//            Integer cellValue = Integer.parseInt(response.getValues().get(0).get(0).toString());
//            return cellValue;
//        } catch (Exception e) {
//            return 0;
//        }
//    }
//
//    private String getRange(Integer rowToPlace) {
//        return SHEET_NAME + "!B" + rowToPlace + ":F" + rowToPlace;
//    }
//
//    public void handleBorrow(RecordDTO recordDTO) throws IOException {
//        Integer rowToPlace = getLastRowNum(SHEET_NAME);
//        List<List<Object>> values = mapToBorrow.mapRecordDtoToList(recordDTO);
//        ValueRange body = new ValueRange().setValues(values);
//        String range = getRange(rowToPlace);
//
//        Sheets.Spreadsheets.Values.Append request = sheetsService.spreadsheets().values().append(sheetID, range, body);
//        request.setValueInputOption("USER_ENTERED");
//        log.info(String.valueOf(request.execute()));
//    }
//
//}
