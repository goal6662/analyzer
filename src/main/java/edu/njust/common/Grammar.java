package edu.njust.common;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

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
    private Set<Rule> rules;

    /**
     * 文法起始符号
     */
    private String type;

    public Grammar(String filePath) throws IOException {
        terminator = new LinkedHashSet<>();
        rules = new HashSet<>();

        handleGrammar(filePath);
    }

    private void handleGrammar(String filePath) throws IOException {

        try (BufferedReader reader = loadFile(filePath)) {

            String rule;
            while ((rule = reader.readLine()) != null) {
                // 跳过注释
                if (rule.startsWith("#")) {
                    continue;
                }

                // 解析规则
                Rule cur = new Rule(rule);
                rules.add(cur);
                terminator.add(cur.getSymbol());

                if (cur.getLeft().length() > 1) {
                    type = cur.getLeft();
                }

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
            symbol = res[1].substring(0, 1);
            next = res[1].substring(res[1].indexOf('<', res[1].length() - 1));
        }
    }

    private String[] splitRule(String rule) {
        return rule.split(" -> ");
    }
}