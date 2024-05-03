package edu.njust.parse;

import edu.njust.common.Constant;
import edu.njust.common.TokenType;
import edu.njust.parse.domain.AnalyzerResult;
import edu.njust.parse.domain.PredictTable;
import edu.njust.parse.domain.Project;
import edu.njust.parse.domain.Rule;
import edu.njust.word.domain.token.TokenInfo;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Stack;

@AllArgsConstructor
public class ParserAnalyzer {

    private Project project;

    private List<TokenInfo> tokens;

    public AnalyzerResult analyzer() {

        PredictTable table = project.getTable();
        // 当前符号栈
        Stack<String> curStack = new Stack<>();
        curStack.push("#");
        curStack.push(project.getSTART());

        for (int i = 0; i < tokens.size(); ++i) {
            TokenInfo input = tokens.get(i);

            // 值匹配，执行下一轮
            String curSign = curStack.peek();
            if (curSign.equals(input.getContent()) || curSign.equals(input.getMatchType())) {
                curStack.pop();
                continue;
            }

            String rowSign = curStack.peek();
            String columnSign = input.getMatchType();

            // 遇到错误
            if (input.getType().equals(TokenType.ERROR)) {
                return new AnalyzerResult(input.getContent());
            }

            String ruleInfo = table.getRule(rowSign, columnSign);
            if (ruleInfo == null) {
                return new AnalyzerResult("类型不匹配");
            }

            // 产生式匹配
            curStack.pop();
            Rule rule = new Rule(table.getRule(rowSign, columnSign));
            List<String> rightList = rule.getRightList();
            for (int j = rightList.size() - 1; j >= 0; j--) {
                // 推出空直接退出
                if (rightList.get(j).equals(Constant.EPSILON)) {
                    break;
                }
                curStack.add(rightList.get(j));
            }
            i -= 1;

        }

        return new AnalyzerResult();
    }
}