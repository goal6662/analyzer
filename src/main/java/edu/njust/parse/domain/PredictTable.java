package edu.njust.parse.domain;

import lombok.ToString;

import java.util.*;

@ToString
public class PredictTable {

    private final List<List<String>> table = new ArrayList<>();
    private final Map<String, Integer> rowMap = new HashMap<>();
    private final Map<String, Integer> columnMap = new HashMap<>();

    public PredictTable(ParseRule rule) {

        int count = 0;
        for (Vn vn : rule.getVns()) {
            table.add(new ArrayList<>(rule.getVts().size() + 1));
            rowMap.put(vn.getSymbol(), count++);
        }

        columnMap.put("#", 0);
        count = 1;
        for (String vt: rule.getVts()) {
            columnMap.put(vt, count++);
        }

        fillTable(rule);
    }

    private void fillTable(ParseRule rule) {

    }

}