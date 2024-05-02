package edu.njust.parse.domain;

import edu.njust.common.Constant;
import lombok.Getter;
import lombok.ToString;

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

    private final List<Rule> rules = new ArrayList<>();

    private final Map<String, Set<String>> types;

    private final String start;

    /**
     * 非终结符的集合
     */
    private final Set<Vn> vns;

    /**
     * 终结符的集合
     */
    private final Set<String> vts;

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
            rules.add(new Rule(rule));
            ruleList.add(rule);
        }
        this.types = readTypeInfo(outFile);
        this.start = start;

        this.vns = generateVn();
        this.vts = generateVt();

        // 移除 types 的无用符号
        for (Vn vn : vns) {
            types.remove(vn.getSymbol());
        }
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
                String type = "<" + rule.substring(0, rule.indexOf(':')) + ">";
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
    private Set<String> generateVt() {
        Set<String> vts = new HashSet<>();
        types.values().forEach(vts::addAll);
        for (Rule rule : rules) {
            String right = rule.getRight();
            for (int i = 0; i < right.length(); i++) {
                if (right.charAt(i) == '<' && right.indexOf('>', i + 1) != -1) {
                    i = right.indexOf('>', i);
                } else {
                    String sign;
                    int index = right.indexOf('<', i + 1);
                    if (index == -1) {
                        vts.add(right.substring(i));
                        break;
                    } else {
                        sign = right.substring(i, index);
                        i = index - 1;
                    }
                    vts.add(sign);
                }
            }
        }
        return vts;
    }

    /**
     * 生成 非终结符集合
     * @return
     */
    private Set<Vn> generateVn() {
        Map<String, Vn> map = getAllVn();
        Set<Vn> vns = new HashSet<>(map.values());

        for (Rule rule : rules) {
            // 左部一定是非终结符
            String left = rule.getLeft();

            // 获取已有值
            Vn vn = map.get(left);
            vn.getFirst().addAll(generateNextFirst(rule, types.keySet()));
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

        for (Rule rule : rules) {
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
                    if (Rule.isVn(follow)) {
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
            vn.getFollow().removeIf(Rule::isVn);
        }

    }
    private void generateNextFollow(Rule rule, Map<String, Vn> map) {
        String left = rule.getLeft();
        String right = rule.getRight();

        if (Rule.isVt(right)) {
            return;
        }

        List<String> symbols = rule.getRightList();
//        // 获取右部所有非终结符
//        for (int i = right.indexOf("<"); i < right.length() && i != -1; ++i) {
//
//            if (right.charAt(i) == '<' && right.indexOf(">", i) != -1) {
//                int index = right.indexOf(">", i);
//                symbols.add(right.substring(i, index + 1));
//                i = index;
//            } else {
//
//                symbols.add(String.valueOf(right.charAt(i)));
//
//                int index = right.indexOf('<', i + 1);
//                if (index == -1) {
//                    break;
//                }
//                i = index - 1;
//            }
//
//        }

        for (int i = symbols.size() - 1; i >= 0; i--) {

            // 这是一个非终结符
            if (Rule.isVn(symbols.get(i))) {
//                String sign = Rule.removeSign(symbols.get(i));
                String sign = symbols.get(i);

                Vn cur = map.get(sign);
                boolean canEmpty = true;
                for (int j = i + 1; j < symbols.size(); j++) {
                    String sym = symbols.get(j);
                    if (Rule.isVn(sym)) {
                        // 去掉标识符
//                        sym = Rule.removeSign(sym);
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
                    } else {
                        cur.getFollow().add(sym);
                        canEmpty = false;
                        break;
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
                        // 非终结符
                        if (!Rule.isVt(first)) {
                            need.addAll(map.get(first).getFirst());
                        }

                    }
                    // 判断大小是否有变化
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
    private Set<String> generateNextFirst(Rule rule, Set<String> types) {
        Set<String> set = new HashSet<>();
        List<String> rightList = rule.getRightList();

        for (String str : rightList) {
            set.add(str);
            // 第一个非终结符
            if (Rule.isVt(str) || types.contains(Rule.removeSign(str))) {
                break;
            }
        }

//        for (int i = 0; i < right.length();) {
//            if (right.charAt(i) == '<' && right.indexOf('>', i) != -1) {
//                int index = right.indexOf('>', i);
//                String symbol = right.substring(i + 1, index);
//                set.add(symbol);
//                // 终结符集合
//                if (types.contains(symbol)) {
//                    break;
//                }
//                i = index + 1;
//            } else {
//                int index = right.indexOf("<");
//                if (index == -1) {
//                    set.add(right.substring(i));
//                } else {
//                    set.add(right.substring(i, index));
//                }
//                break;
//            }
//        }
        return set;
    }

    /**
     * 获取 非终结符 的集合
     * @return
     */
    private Map<String, Vn> getAllVn() {
        Map<String, Vn> map = new HashMap<>();
        for (Rule rule : rules) {

            // 左部一定是非终结符
            String left = rule.getLeft();
            Vn vn = map.getOrDefault(left, new Vn(left));
            map.put(left, vn);

            for (String sign : rule.getRightList()) {
                if (!Rule.isVt(sign)) {
                    // 加入引用符号
                    if (types.containsKey(sign)) {
                        Vn temp = map.getOrDefault(sign, new Vn(sign));
                        temp.getFirst().addAll(types.get(sign));
                        map.put(sign, temp);
                    }
                }
            }
        }

        return map;
    }

}


@Getter
@ToString
class Rule {
    private final String left;
    private final String right;

    /**
     * 保留了<>
     */
    private final List<String> rightList;

    public Rule(String rule) {
        String[] split = rule.split(" -> ");

        // 左部可以直接去掉尖括号
//        this.left = removeSign(split[0]);
        this.left = split[0];
        this.right = split[1];
        this.rightList = generateRightSet();
    }

    private List<String> generateRightSet() {
        List<String> list = new ArrayList<>();

        // 1. 获取第一个符号
        for (int i = 0; i < right.length(); i++) {
            if (right.charAt(i) == '<' && right.indexOf('>', i) != -1) {
                // 截取并加入
                int index = right.indexOf('>', i);
                String symbol = right.substring(i, index + 1);
                list.add(symbol);
                i = index;
            } else {
                int index = right.indexOf('<', i + 1);
                if (index == -1) {
                    list.add(right.substring(i));
                    return list;
                } else {
                    list.add(right.substring(i, index));
                }
                i = index - 1;
            }
        }

        return list;
    }

    /**
     * <str> -> str
     * @param str
     * @return 去除首尾字符
     */
    public static String removeSign(String str) {
        return str.substring(1, str.length() - 1);
    }

    public String getOrigin() {
        return "<" + left + "> -> " + right;
    }

    /**
     * 是否为终结符
     * @param sign
     * @return true: 是
     */
    public static boolean isVt(String sign) {
        return !sign.startsWith("<") || !sign.endsWith(">");
    }

    public static boolean isVn(String sign) {
        return !isVt(sign);
    }
}