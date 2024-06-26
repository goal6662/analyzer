package edu.njust.word.domain.grammar;

import lombok.Getter;
import lombok.ToString;

import java.io.*;
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

                if (cur.getSymbol() != null && cur.getSymbol().startsWith("\\\\")) {
                    List<Rule> ruleList = handlerMatcher(cur);

                    for (Rule temp : ruleList) {
                        curRules.add(temp);
                        terminator.add(temp.getSymbol());
                    }

                } else {
                    curRules.add(cur);
                    terminator.add(cur.getSymbol());
                }

//                curRules.add(cur);
//                terminator.add(cur.getSymbol());
            }
        }

    }

    private List<Rule> handlerMatcher(Rule rule) {
        List<Rule> ruleList = new ArrayList<>();

        switch (rule.getSymbol()) {
            case "\\\\s":
                for (char i = 'a'; i <= 'z'; i++) {
                    Rule temp = new Rule(rule.getLeft(), String.valueOf(i), rule.getNext());
                    ruleList.add(temp);
                }
                break;
            case "\\\\S":
                for (char i = 'A'; i <= 'Z'; i++) {
                    Rule temp = new Rule(rule.getLeft(), String.valueOf(i), rule.getNext());
                    ruleList.add(temp);
                }
                break;
            case "\\\\d":
                for (int i = 0; i <= 9; i++) {
                    Rule temp = new Rule(rule.getLeft(), String.valueOf(i), rule.getNext());
                    ruleList.add(temp);
                }
                break;
            default:
                break;
        }

        return ruleList;
    }

    private BufferedReader loadFile(String filePath) throws IOException {
        URL url = this.getClass().getClassLoader().getResource(filePath);
        String path = url != null ? url.getPath() : filePath;

        return new BufferedReader(new FileReader(path));
    }
}

