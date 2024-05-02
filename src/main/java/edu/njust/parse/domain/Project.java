package edu.njust.parse.domain;

import lombok.Getter;
import lombok.ToString;

import java.io.IOException;
import java.util.Set;

@Getter
@ToString
public class Project {

    /**
     * 文法开始符号
     */
    public static final String START = "<start>";

    /**
     * 非终结符的集合
     */
    private final Set<Vn> vns;

    /**
     * 终结符的集合
     */
    private final Set<String> vts;

    private final PredictTable table;

    /**
     * 根据规则构造工程
     */
    public Project(String ruleFile, String wordOutFile) throws IOException {
        ParseRule rule = new ParseRule(ruleFile, wordOutFile, START);
        vns = rule.getVns();
        vts = rule.getVts();
        table = new PredictTable(rule);
    }
}
