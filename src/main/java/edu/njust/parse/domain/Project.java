package edu.njust.parse.domain;

import java.io.IOException;
import java.util.Set;

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
        ParseRule rule = new ParseRule(ruleFile, wordOutFile, START);
        vns = rule.generateVn();
        vts = rule.generateVt();
    }

}
