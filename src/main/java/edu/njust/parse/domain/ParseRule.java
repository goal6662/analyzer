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

    private final String start;
    /**
     * 解析文法规则
     * @param file 规则文件
     */
    public ParseRule(String file, String outFile, String start) throws IOException {
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
        this.types = readTypeInfo(outFile);
        this.start = start;
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
        // TODO: 2024/5/1 存在BUG：引用类型的终结符还未加入
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
        generateFollow(vns, map);
        return vns;
    }

    /**
     * 生成 终结符的 Follow 集
     * @param vns 终结符集合
     * @param map 终结符 symbol 的映射关系
     */
    private void generateFollow(Set<Vn> vns, Map<String, Vn> map) {
        // 起始字符
        map.get(start).getFollow().add("#");

        for (String rule : ruleList) {
            generateNextFollow(rule, map);
        }

        // 获取Follow集
        boolean hasChange;
        do {
            hasChange = false;
            for (Vn vn : vns) {
                // 获取终结符集合
                int oldLen = vn.getFollow().size();

                Set<String> need = new HashSet<>();
                for (String follow : vn.getFollow()) {
                    if (follow.length() > 1) {
                        need.addAll(map.get(follow).getFollow());
                    }

                }
                vn.getFollow().addAll(need);
                if (oldLen != vn.getFollow().size()) {
                    hasChange = true;
                }
            }
        } while (hasChange);

        // 移除占位符
        for (Vn vn : vns) {
            vn.getFollow().removeIf(type -> type.length() > 1);
        }

    }
    private void generateNextFollow(String rule, Map<String, Vn> map) {
        String[] split = rule.split(" -> ");
        String left = split[0].substring(1, split[0].length() - 1);
        String right = split[1];

        if (right.length() <= 2) {
            return;
        }

        List<String> symbols = new ArrayList<>();
        // 获取右部所有非终结符
        for (int i = right.indexOf("<"); i < right.length() && i != -1; ++i) {

            if (right.charAt(i) == '<' && right.indexOf(">", i) != -1) {
                int index = right.indexOf(">", i);
                symbols.add(right.substring(i, index + 1));
                i = index;
            } else {

                symbols.add(String.valueOf(right.charAt(i)));

                int index = right.indexOf('<', i + 1);
                if (index == -1) {
                    break;
                }
                i = index - 1;
            }

        }

        for (int i = symbols.size() - 1; i >= 0; i--) {

            // 这是一个非终结符
            if (symbols.get(i).startsWith("<") && symbols.get(i).endsWith(">")) {
                String sign = symbols.get(i).substring(1, symbols.get(i).length() - 1);

                Vn cur = map.get(sign);

                boolean canEmpty = true;
                for (int j = i + 1; j < symbols.size(); j++) {
                    String sym = symbols.get(j);
                    if (sym.length() <= 1) {
                        cur.getFollow().add(sym);
                        canEmpty = false;
                        break;
                    } else {
                        // 去掉标识符
                        sym = sym.substring(1, symbols.get(j).length() - 1);
                        Vn next = map.get(sym);
                        // 一定会添加First集
                        cur.getFollow().addAll(next.getFirst());
                        // 无法推出空串：结束
                        if (!next.getFirst().contains(Constant.EPSILON)) {
                            canEmpty = false;
                            break;
                        } else {
                            // 可以推出空串：去除空字符，继续
                            cur.getFollow().remove(Constant.EPSILON);
                        }
                    }
                }

                // 可以推出空串
                if (canEmpty) {
                    map.get(sign).getFollow().add(left);
                }

            }
        }

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