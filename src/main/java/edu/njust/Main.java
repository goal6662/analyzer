package edu.njust;

import edu.njust.common.TokenType;
import edu.njust.parse.ParserAnalyzer;
import edu.njust.parse.domain.AnalyzerResult;
import edu.njust.parse.domain.Project;
import edu.njust.word.domain.dfa.DFA;
import edu.njust.word.domain.grammar.Grammar;
import edu.njust.word.domain.nfa.NFA;
import edu.njust.word.domain.token.TokenInfo;
import edu.njust.word.util.DFAHandler;
import edu.njust.word.util.Matcher;
import edu.njust.word.util.NFAHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static edu.njust.config.BaseConfig.*;

public class Main {

    public static void main(String[] args) throws IOException {
        // 1. 测试并获取Token序列
        List<TokenInfo> infos = testWord();

        Map<String, Set<String>> tokenMap = Matcher.readTypeInfo(WORD_OUT_FILE);
        // 2. 进行语法分析
        Project project = new Project(PARSE_GRAMMAR_FILE, tokenMap);
        project.getTable().writeToCSV(PREDICT_CSV_FILE);

        ParserAnalyzer analyzer = new ParserAnalyzer(project, infos);

        // 3. 获取分析结果
        AnalyzerResult result = analyzer.analyzer();
        // 4. 写入分析信息
        if (result.isSuccess()) {
            result.writeProcessToFile(PARSE_OUT_FILE);
        } else {
            result.writeProcessToFile(PARSE_OUT_FILE, "\"" + result + "\"");
        }
        // 5. 控制台打印分析结果
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

            if (key.equals(TokenType.KEY_WORD)) {
                // 按需打印信息
                NFAHandler.printNFA(nfaT);
                DFAHandler.printDFA(dfaT);
            }

            dfa.put(key, dfaT);
        }
        // 3. 获取Token序列
        Matcher matcher = new Matcher(FILE);
        List<TokenInfo> infos = matcher.match(dfa);

        // 4. 获取结果并写入文件
        matcher.writeToFile(WORD_OUT_FILE, matcher.match(dfa));

        return infos;
    }
}