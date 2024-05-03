package edu.njust;

import edu.njust.parse.ParserAnalyzer;
import edu.njust.parse.domain.AnalyzerResult;
import edu.njust.parse.domain.ParseRule;
import edu.njust.parse.domain.Project;
import edu.njust.word.domain.dfa.DFA;
import edu.njust.word.domain.grammar.Grammar;
import edu.njust.word.domain.nfa.NFA;
import edu.njust.word.domain.token.TokenInfo;
import edu.njust.word.util.DFAHandler;
import edu.njust.word.util.Matcher;
import edu.njust.word.util.NFAHandler;
import org.omg.CORBA.TIMEOUT;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException {
        // 1. 测试并获取Token序列
        List<TokenInfo> infos = testWord();

        // 2. 进行语法分析
        Project project = new Project(PARSE_GRAMMAR_FILE);
        project.getTable().writeToCSV("parse/table1.csv");

        ParserAnalyzer analyzer = new ParserAnalyzer(project, infos);

        AnalyzerResult result = analyzer.analyzer();
        System.out.println(result);
    }

    public static List<TokenInfo> testWord() throws IOException {
        Grammar grammar = new Grammar(WORD_GRAMMAR_FILE);

        // 1. 获取NFA
        Map<String, NFA> nfa = NFAHandler.getNFA(grammar);
        // 2. 转为DFA
        Map<String, DFA> dfa = new HashMap<>();
        for (String key : nfa.keySet()) {
            NFA nfaT = nfa.get(key);
            DFA dfaT = DFAHandler.nfaToNFA(nfaT);
            dfa.put(key, dfaT);
        }
        // 3. 获取Token序列
        Matcher matcher = new Matcher(FILE);
        List<TokenInfo> infos = matcher.match(dfa);

        // 4. 获取结果并写入文件
        matcher.writeToFile(WORD_OUT_FILE, matcher.match(dfa));

        return infos;
    }

    /**
     * 需要分析的文件
     */
    public static final String FILE = "word/lex.txt";

    /**
     * 语法分析规则文件
     */
    public static final String PARSE_GRAMMAR_FILE = "parse/parse_grammar.txt";

    /**
     * 词法分析结果文件
     */
    public static final String WORD_OUT_FILE = "word/out10.txt";

    /**
     * 词法分析文法
     */
    public static final String WORD_GRAMMAR_FILE = "word/gra.txt";

}