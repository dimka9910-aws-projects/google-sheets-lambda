package com.github.dimka9910.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SheetRangeParser {

    public static class Range {
        public String sheetName;
        public int startCol;
        public int startRow;
        public int endCol;
        public int endRow;

        @Override
        public String toString() {
            return "Range{" +
                    "sheetName='" + sheetName + '\'' +
                    ", startCol=" + startCol +
                    ", startRow=" + startRow +
                    ", endCol=" + endCol +
                    ", endRow=" + endRow +
                    '}';
        }
    }

    public static Range parse(String input) {
        // Разделяем имя листа и диапазон
        String[] parts = input.split("!");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid range format: " + input);
        }
        String sheetName = parts[0];
        String rangePart = parts[1].replace("$", ""); // убираем $ если есть

        // Регулярка для разбора A1:B10
        Pattern pattern = Pattern.compile("([A-Z]+)(\\d+):([A-Z]+)(\\d+)");
        Matcher matcher = pattern.matcher(rangePart);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid range format: " + input);
        }

        Range range = new Range();
        range.sheetName = sheetName;
        range.startCol = colToNumber(matcher.group(1)) - 1;
        range.startRow = Integer.parseInt(matcher.group(2));
        range.endCol = colToNumber(matcher.group(3));
        range.endRow = Integer.parseInt(matcher.group(4));

        return range;
    }

    // Преобразуем буквы колонки в номер (A=1, B=2, Z=26, AA=27 и т.д.)
    private static int colToNumber(String col) {
        int number = 0;
        for (int i = 0; i < col.length(); i++) {
            number *= 26;
            number += col.charAt(i) - 'A' + 1;
        }
        return number;
    }
}
