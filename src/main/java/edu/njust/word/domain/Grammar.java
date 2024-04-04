package edu.njust.word.domain;

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
@Getter
@ToString
public class Grammar {

    /**
     * 终结符的集合
     */
    private final Set<String> terminator;

    /**
     * 规则的集合
     * 起始符号：规则集合
     */
    private final Map<String, Set<Rule>> rules;

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

