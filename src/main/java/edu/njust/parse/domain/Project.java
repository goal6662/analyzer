package edu.njust.parse.domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Project {

    /**
     * 文法开始符号
     */
    public static final String START = "start";

    /**
     * 非终结符的集合
     */
    private final Set<Vn> vns;

    /**
     * 终结符的集合
     */
    private final Set<String> vts;


    /**
     * 根据规则构造工程
     */
    public Project(String ruleFile, String wordOutFile) throws IOException {
        ParseRule rule = new ParseRule(ruleFile);
        vns = rule.generateVn(readTypeInfo(wordOutFile));
        vts = rule.generateVt();
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
}
