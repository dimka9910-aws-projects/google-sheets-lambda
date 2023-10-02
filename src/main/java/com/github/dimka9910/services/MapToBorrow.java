package com.github.dimka9910.services;

import com.github.dimka9910.dto.OperationTypeEnum;
import com.github.dimka9910.dto.RecordDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MapToBorrow {

    public List<List<Object>> mapRecordDtoToList(RecordDTO recordDTO) {

        List<Object> values = new ArrayList<>();
        values.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
        values.add(recordDTO.getAmount());
        values.add(recordDTO.getCurrency());
        values.add(recordDTO.getUserName());
        values.add(recordDTO.getSecondPerson());
        List<List<Object>> result = List.of(values);

        return result;
    }

}