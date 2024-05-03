package edu.njust.parse.domain;

import edu.njust.common.Constant;
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

    private final List<Rule> rules = new ArrayList<>();

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
     *
     * @param file 规则文件
     */
    public ParseRule(String file, String start) throws IOException {
        URL url = this.getClass().getClassLoader().getResource(file);

        assert url != null;
        BufferedReader reader = new BufferedReader(new FileReader(url.getFile()));

        String rule;
        while ((rule = reader.readLine()) != null) {
            // 跳过注释和空行
            if (rule.trim().isEmpty() || rule.startsWith("#")) {
                continue;
            }
            rules.add(new Rule(rule));
            ruleList.add(rule);
        }
        this.start = start;

        this.vns = generateVn();
        this.vts = generateVt();
    }

    /**
     * 终结符的集合
     */
    private Set<String> generateVt() {
        Set<String> vts = new HashSet<>();
        for (Rule rule : rules) {
            List<String> rightList = rule.getRightList();
            rightList.forEach((item) -> {
                if (Rule.isVt(item)) {
                    vts.add(item);
                }
            });
        }
        return vts;
    }

    /**
     * 生成 非终结符集合
     *
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
            vn.getFirst().addAll(generateNextFirst(rule));
        }

        generateFirst(vns, map);
        generateFollow(vns, map);
        return vns;
    }

    /**
     * 生成 终结符的 Follow 集
     *
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

        List<String> symbols = rule.getRightList();

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
     *
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
        } while (hasChange);

        // 移除占位符
        for (Vn vn : vns) {
            for (String type : map.keySet()) {
                vn.getFirst().remove(type);
            }
        }
    }

    private Set<String> generateNextFirst(Rule rule) {
        Set<String> set = new HashSet<>();
        List<String> rightList = rule.getRightList();

        for (String str : rightList) {
            set.add(str);
            // 第一个非终结符
            if (Rule.isVt(str)) {
                break;
            }
        }

        return set;
    }

    /**
     * 获取 非终结符 的集合
     *
     * @return
     */
    private Map<String, Vn> getAllVn() {
        Map<String, Vn> map = new HashMap<>();
        for (Rule rule : rules) {

            // 左部一定是非终结符
            String left = rule.getLeft();
            Vn vn = map.getOrDefault(left, new Vn(left));
            map.put(left, vn);
        }
        return map;
    }
}

