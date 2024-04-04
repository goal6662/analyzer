package edu.njust.common;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * 文法规则
 * <A> -> a<B>
 * <B> -> b
 */
@Data
@ToString
public class Grammar {

    /**
     * 终结符的集合
     */
    private Set<String> terminator;

    /**
     * 规则的集合
     */
    private Map<String, Set<Rule>> rules;

    /**
     * 文法起始符号
     */
    private String type;

    public Grammar(String filePath) throws IOException {
        terminator = new HashSet<>();
        rules = new HashMap<>();

        handleGrammar(filePath);
    }

    private void handleGrammar(String filePath) throws IOException {

        try (BufferedReader reader = loadFile(filePath)) {

            String rule;
            Set<Rule> curRules = new LinkedHashSet<>();
            while ((rule = reader.readLine()) != null) {
                // 跳过注释
                if (rule.startsWith("#") || rule.trim().isEmpty()) {
                    continue;
                }

                // 解析规则
                Rule cur = new Rule(rule);
                if (cur.getLeft().length() > 1) {
                    if (rules.containsKey(cur.getLeft())) {
                        curRules = rules.get(cur.getLeft());
                    } else {
                        HashSet<Rule> temp = new LinkedHashSet<>();
                        rules.put(cur.getLeft(), temp);
                        curRules = temp;
                    }
                }

                curRules.add(cur);
                terminator.add(cur.getSymbol());

            }
        }

    }

    private BufferedReader loadFile(String filePath) throws IOException {
        URL url = this.getClass().getClassLoader().getResource(filePath);
        assert url != null;

        return new BufferedReader(new FileReader(url.getFile()));
    }
}

@Getter
@ToString
class Rule {

    /**
     * 规则左部
     * 一定是一个非终结符
     */
    private final String left;

    /**
     * 规则右部
     */
    private final String right;

    /**
     * 该规则导出的下一个状态
     */
    private final String next;

    /**
     * 转移条件
     */
    private final String symbol;

    public Rule(String rule) {
        String[] res = splitRule(rule);

        left = res[0].substring(1, res[0].length() - 1);
        right = res[1];
        if (res[1].length() == 1) {
            next = null;
            symbol = res[1];
        } else {
            int index= res[1].indexOf("<");
            if (index != -1) {
                symbol = res[1].substring(0, index);
                next = res[1].substring(index + 1, res[1].length() - 1);
            } else {
                symbol = res[1];
                next = null;
            }
        }
    }

    private String[] splitRule(String rule) {
        return rule.split(" -> ");
    }
}