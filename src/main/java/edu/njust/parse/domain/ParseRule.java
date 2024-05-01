package edu.njust.parse.domain;

import edu.njust.common.Constant;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文法规则
 */
@Getter
public class ParseRule {

    private final List<String> ruleList = new ArrayList<>();

    private final Map<String, Set<String>> types;

    /**
     * 解析文法规则
     * @param file 规则文件
     */
    public ParseRule(String file, String outFile) throws IOException {
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
        types = readTypeInfo(outFile);
    }

    private Map<String, Set<String>> readTypeInfo(String outFile) throws IOException {
        URL url = this.getClass().getClassLoader().getResource(outFile);

        assert url != null;
        BufferedReader reader = new BufferedReader(new FileReader(url.getFile()));

        String rule;
        Map<String, Set<String>> types = new HashMap<>();
        boolean flag = false;
        while ((rule = reader.readLine()) != null) {
            if (rule.startsWith("----")) {
                flag = true;
            } else if (flag) {
                String type = rule.substring(0, rule.indexOf(':'));
                String[] infos = rule.substring(rule.indexOf('[') + 1, rule.length() - 1).split(", ");
                Set<String> set = new HashSet<>(Arrays.asList(infos));
                types.put(type, set);
            }
        }
        return types;
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

    /**
     * 生成 非终结符集合
     * @return
     */
    public Set<Vn> generateVn() {
        Map<String, Vn> map = getAllVn();
        Set<Vn> vns = new HashSet<>(map.values());

        for (String rule : ruleList) {
            String[] split = rule.split(" -> ");

            // 左部一定是非终结符
            String left = split[0];
            String symbol = split[0].substring(1, left.length() - 1);

            // 获取已有值
            Vn vn = map.get(symbol);
            vn.getFirst().addAll(generateNextFirst(split[1], types.keySet()));
        }

        generateFirst(vns, map);

        return vns;
    }


    /**
     * 获取 First 集
     * @param vns
     * @param map
     */
    private void generateFirst(Set<Vn> vns, Map<String, Vn> map) {
        // 获取First集
        boolean hasChange;
        do {
            hasChange = false;
            for (Vn vn : vns) {
                // 获取终结符集合
                if (!types.containsKey(vn.getSymbol())) {
                    int oldLen = vn.getFirst().size();

                    Set<String> need = new HashSet<>();
                    for (String first : vn.getFirst()) {
                        if (first.length() > 1) {
                            need.addAll(map.get(first).getFirst());
                        }

                    }
                    vn.getFirst().addAll(need);
                    if (oldLen != vn.getFirst().size()) {
                        hasChange = true;
                    }
                }
            }
        } while (hasChange);

        // 移除占位符
        for (Vn vn : vns) {
            for (String type: map.keySet()) {
                vn.getFirst().remove(type);
            }
        }
    }
    private Set<String> generateNextFirst(String right, Set<String> types) {
        Set<String> set = new HashSet<>();
        for (int i = 0; i < right.length();) {
            if (right.charAt(i) == '<' && right.indexOf('>', i) != -1) {
                int index = right.indexOf('>', i);
                String symbol = right.substring(i + 1, index);
                set.add(symbol);
                // 终结符集合
                if (types.contains(symbol)) {
                    break;
                }
                i = index + 1;
            } else {
                set.add(String.valueOf(right.charAt(i)));
                break;
            }
        }
        return set;
    }

    /**
     * 获取 非终结符 的集合
     * @return
     */
    private Map<String, Vn> getAllVn() {
        Map<String, Vn> map = new HashMap<>();
        for (String rule : ruleList) {
            String[] split = rule.split(" -> ");

            // 左部一定是非终结符
            String left = split[0];
            String symbol = split[0].substring(1, left.length() - 1);

            Vn vn = new Vn(symbol);
            if (split[1].equals(Constant.EPSILON)) {
                vn = new Vn(symbol, true);
            }
            map.put(symbol, vn);
        }

        // 固有类型
        for (String type : types.keySet()) {
            Vn vn = new Vn(type);

            List<String> list = types.get(type).stream().map((val) -> val.substring(0, 1))
                    .collect(Collectors.toList());

            vn.getFirst().addAll(list);
            map.put(type, vn);
        }

        return map;
    }

}