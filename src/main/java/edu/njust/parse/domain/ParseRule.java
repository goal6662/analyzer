package edu.njust.parse.domain;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * 文法规则
 */
@Getter
public class ParseRule {

    private final List<String> ruleList = new ArrayList<>();

    /**
     * 解析文法规则
     * @param file 规则文件
     */
    public ParseRule(String file) throws IOException {
        URL url = this.getClass().getClassLoader().getResource(file);

        assert url != null;
        BufferedReader reader = new BufferedReader(new FileReader(url.getFile()));

        String rule;
        while ((rule = reader.readLine()) != null) {
            // 跳过注释和空行
            if (rule.isEmpty() || rule.startsWith("#")) {
                continue;
            }
            ruleList.add(rule);
        }

    }

    /**
     * 终结符的集合
     */
    public Set<String> generateVt() {
        Set<String> vts = new HashSet<>();

        for (String rule : ruleList) {
            char[] right = rule.split(" -> ")[1].toCharArray();
            for (int i = 0; i < right.length;) {
                if (right[i] == '<') {
                    while (i < right.length && right[i] != '>') {
                        ++i;
                    }
                } else {
                    vts.add(String.valueOf(right[i]));
                    ++i;
                }
            }
        }

        return vts;
    }

    public Set<Vn> generateVn(Map<String, Set<String>> types) {
        Set<Vn> vns = new HashSet<>();
        Set<String> vts = generateVt();

        Map<String, Vn> map = new HashMap<>();
        for (String rule : ruleList) {
            String[] split = rule.split(" -> ");

            // 左部一定是非终结符
            String left = split[0];
            String symbol = split[0].substring(1, left.length() - 1);

            // 获取已有值
            Vn vn = map.get(symbol);
            if (vn == null) {
                vn = new Vn(symbol);
                map.put(symbol, vn);
            }
//            vn.getFirst().addAll(generateFirst(split[1], vn));
            vns.add(vn);
//            StringBuilder builder = new StringBuilder();
//            char[] right = split[1].toCharArray();
//            for (int i = 0; i < right.length;) {
//                if (right[i] == '<') {
//                    ++i;
//                    while (i < right.length && right[i] != '>') {
//                        builder.append(right[i++]);
//                    }
//                    if (i < right.length) {
//                        vns.add(new Vn(builder.toString()));
//                    }
//                    builder = new StringBuilder();
//
//                } else {
//                    ++i;
//                }
//            }
        }

        return vns;
    }

//    private Set<String> generateFirst(String right, Vn vn) {
//
//    }

}