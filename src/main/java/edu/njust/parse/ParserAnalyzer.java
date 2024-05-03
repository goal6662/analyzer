package edu.njust.parse;

import edu.njust.parse.domain.AnalyzerResult;
import edu.njust.parse.domain.Project;
import edu.njust.word.domain.token.TokenInfo;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Stack;

@AllArgsConstructor
public class ParserAnalyzer {

    private Project project;

    private List<TokenInfo> tokens;

    public AnalyzerResult analyzer() {
        AnalyzerResult result = new AnalyzerResult();

        // 当前符号栈
        Stack<String> curStack = new Stack<>();
        curStack.push("#");
        // 输入符号串
        TokenInfo inputToken = tokens.get(0);



        return result;
    }
}