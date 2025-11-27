package com.github.dimka9910.services;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TableViewHelper {

    private final MapToRowList mapToExpenses;
    private final String sheetID;
    private final Sheets sheetsService;

    public TableViewHelper(String sheetID, Sheets sheetsService) {
        this.mapToExpenses = new MapToRowList();
        this.sheetID = sheetID;
        this.sheetsService = sheetsService;
    }

    public Map<String, Map<String, Double>> getTableAsMap(String range
    ) throws IOException {


        Map<String, Map<String, Double>> resultMap = new HashMap<>();

        ValueRange response = sheetsService.spreadsheets().values()
                .get(sheetID, range)
                .execute();

        for (int row = 1; row < response.getValues().size(); row++) {
            for (int column = 1; column < response.getValues().get(row).size(); column++) {
                String columnName = Optional.ofNullable(response.getValues().get(0).get(column))
                        .map(String::valueOf)
                        .orElse(null);
                if (columnName == null || columnName.isBlank()) {
                    break;
                }

                String rowName = Optional.ofNullable(response.getValues().get(row).get(0))
                        .map(String::valueOf)
                        .orElse(null);
                if (rowName == null || rowName.isBlank()) {
                    break;
                }

                Double amount = Optional.ofNullable(response.getValues().get(row).get(column))
                        .map(String::valueOf)
                        .map(input -> Double.parseDouble(input.replace(",", ".")))
                        .orElse(null);
                if (amount == null) {
                    break;
                }

                resultMap.computeIfAbsent(rowName, k -> new HashMap<>());
                resultMap.get(rowName).put(columnName, amount);

            }
        }

        return resultMap;
    }

}
