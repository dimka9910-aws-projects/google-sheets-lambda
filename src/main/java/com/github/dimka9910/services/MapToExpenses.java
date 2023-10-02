package com.github.dimka9910.services;

import com.github.dimka9910.dto.OperationTypeEnum;
import com.github.dimka9910.dto.RecordDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MapToExpenses {

    public List<List<Object>> mapRecordDtoToList(RecordDTO recordDTO){

        List<Object> values = new ArrayList<>();
        values.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
        values.add(recordDTO.getAmount());
        values.add(recordDTO.getCurrency());
        values.add(recordDTO.getUserName());
        values.add(Optional.ofNullable(recordDTO.getFundName()).orElse(""));
        values.add(recordDTO.getAccountName());
        values.add(recordDTO.getOperationType().toString());
        values.add(Optional.ofNullable(recordDTO.getComment()).orElse(""));
        List<List<Object>> result = List.of(values);

        return result;
    }

    public List<List<Object>> mapSecondPerson(RecordDTO recordDTO){

        List<Object> values = new ArrayList<>();
        values.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
        values.add(-recordDTO.getAmount());
        values.add(recordDTO.getCurrency());
        values.add(recordDTO.getSecondPerson());
        values.add(Optional.ofNullable(recordDTO.getFundName()).orElse(""));
        values.add(recordDTO.getSecondAccount());
        values.add(recordDTO.getOperationType().toString());
        values.add(Optional.ofNullable(recordDTO.getComment()).orElse(""));
        List<List<Object>> result = List.of(values);
        return result;
    }


}
