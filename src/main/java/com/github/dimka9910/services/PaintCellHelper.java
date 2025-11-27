package com.github.dimka9910.services;

import com.github.dimka9910.dto.ColorEnum;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaintCellHelper {

    private final String sheetID;
    private final Sheets sheetsService;

    public PaintCellHelper(String sheetID, Sheets sheetsService) {
        this.sheetID = sheetID;
        this.sheetsService = sheetsService;
    }



    public void paintRows(String stringRange, ColorEnum color) throws IOException {


        SheetRangeParser.Range range = SheetRangeParser.parse(stringRange);


        // to get current sheet id by name
        Spreadsheet spreadsheet = sheetsService.spreadsheets()
                .get(sheetID)
                .setIncludeGridData(false) // данные нам не нужны, только метаданные
                .execute();
        Integer pageId = null;
        for (Sheet sheet : spreadsheet.getSheets()) {
            if (sheet.getProperties().getTitle().equals(range.sheetName)) {
                pageId = sheet.getProperties().getSheetId();
                break;
            }
        }

        List<Request> requests = new ArrayList<>();

        GridRange gridRange = new GridRange()
                .setSheetId(pageId)       // ID листа, не имя!
                .setStartRowIndex(range.startRow - 1)       // начиная с 4-й строки (0-based)
                .setEndRowIndex(range.endRow)        // до строки 10 (не включительно)
                .setStartColumnIndex(range.startCol)    // начиная с колонки A
                .setEndColumnIndex(range.endCol);     // до колонки B (не включительно)

        CellFormat cellFormat = new CellFormat()
                .setBackgroundColor(color.getColor());

        Request formatRequest = new Request()
                .setRepeatCell(new RepeatCellRequest()
                        .setRange(gridRange)
                        .setCell(new CellData().setUserEnteredFormat(cellFormat))
                        .setFields("userEnteredFormat.backgroundColor"));

        requests.add(formatRequest);

        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
                .setRequests(requests);

        sheetsService.spreadsheets()
                .batchUpdate(sheetID, batchUpdateRequest)
                .execute();

    }
}
