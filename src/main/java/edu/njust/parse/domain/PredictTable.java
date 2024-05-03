package edu.njust.parse.domain;

import edu.njust.common.Constant;
import lombok.ToString;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@ToString
public class PredictTable {

    private final String[][] table;
    private final Map<String, Integer> rowMap = new HashMap<>();
    private final Map<String, Integer> columnMap = new HashMap<>();

    private final Set<Vn> vns;
    private final Set<String> vts;
    private Map<String, Vn> vnMap;
    private boolean hasFillTable = false;

    public PredictTable(ParseRule rule) {

        this.vns = rule.getVns();
        this.vts = rule.getVts();
        this.table = new String[vns.size()][vts.size() + 1];
        generateVnMap();

        int count = 0;
        for (Vn vn : rule.getVns()) {
            rowMap.put(vn.getSymbol(), count++);
        }

        columnMap.put("#", 0);
        count = 1;
        for (String vt : rule.getVts()) {
            columnMap.put(vt, count++);
        }

        fillTable(rule.getRules());
    }

    private void fillTable(List<Rule> rules) {
        hasFillTable = true;
        for (Rule rule : rules) {
            int rowIndex = rowMap.get(rule.getLeft());
            String[] len = table[rowIndex];

            Set<String> first = getFirst(rule);
            for (String vt : first) {
                int columnIndex = columnMap.get(vt);

                if (len[columnIndex] != null) {
                    System.out.println("pre: " + len[columnIndex] + " \nafter: " + rule.getOrigin());
                    throw new RuntimeException("error: [" + rowIndex + ", " + columnIndex + "]");
                }
                len[columnIndex] = rule.getOrigin();

            }

            //TODO 可以推出空串
            Vn left = vnMap.get(rule.getLeft());
            if (left.getFirst().contains(Constant.EPSILON)) {
                for (String next : left.getFollow()) {
                    int columnIndex = columnMap.get(next);
//                    if (len[columnIndex] != null) {
//                        System.out.println("pre: " + len[columnIndex] + " \nafter: " + rule.getOrigin());
//                        throw new RuntimeException("error: [" + rowIndex + ", " + columnIndex + "]");
//                    }
                    len[columnIndex] = rule.getLeft() + " -> " + Constant.EPSILON;
                }
            }
        }
    }

    /**
     * 求右部的First集
     *
     * @return
     */
    private Set<String> getFirst(Rule rule) {
        Set<String> set = new HashSet<>();

        for (String sign : rule.getRightList()) {
            if (Rule.isVn(sign)) {
                Set<String> first = vnMap.get(sign).getFirst();
                set.addAll(first);
                // 不能推出空
                if (!first.contains(Constant.EPSILON)) {
                    return set;
                }
            } else {
                set.add(sign);
                return set;
            }
        }
        return set;
    }

    private void generateVnMap() {
        this.vnMap = new HashMap<>();
        for (Vn vn : vns) {
            vnMap.put(vn.getSymbol(), vn);
        }
    }

    private List<String> generateCSVInfo() {
        List<String> infos = new ArrayList<>();
        // 生成行
        StringBuilder len = new StringBuilder();
        for (String column : columnMap.keySet()) {
            if (",".equals(column)) {
                column = "\",\"";
            }
            len.append(",").append(column);
        }
        infos.add(len.toString());

        for (String row : rowMap.keySet()) {
            int rowIndex = rowMap.get(row);
            len = new StringBuilder();
            len.append(row);

            for (String column : columnMap.keySet()) {
                int columnIndex = columnMap.get(column);
                String info = table[rowIndex][columnIndex];
                len.append(",").append(info != null ? info : "");
            }
            infos.add(len.toString());
        }
        return infos;
    }

    public void writeToCSV(String filePath) throws IOException {
        List<String> infos = generateCSVInfo();

        URL url = this.getClass().getClassLoader().getResource(filePath);
        assert url != null;


        BufferedWriter writer = new BufferedWriter(new FileWriter(url.getPath()));

        for (String info : infos) {
            writer.write(info);
            writer.newLine();
        }

        writer.flush();
        writer.close();

    }
}