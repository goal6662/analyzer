package edu.njust.parse;

import edu.njust.common.Constant;
import edu.njust.common.TokenType;
import edu.njust.parse.domain.*;
import edu.njust.word.domain.token.TokenInfo;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

@AllArgsConstructor
public class ParserAnalyzer {

    private Project project;

    private List<TokenInfo> tokens;

    public AnalyzerResult analyzer() {

        List<Point> analyzeProcess = new ArrayList<>();
        PredictTable table = project.getTable();
        // 当前符号栈
        Stack<String> curStack = new Stack<>();
        curStack.push("#");
        curStack.push(project.getSTART());
        // 结束符
        tokens.add(new TokenInfo(-1, -1, "accept", "#"));

        LinkedList<String> cur = new LinkedList<>(curStack);

        for (int i = 0; i < tokens.size(); ++i) {
            TokenInfo input = tokens.get(i);

            Point point = new Point(cur, input.getContent(), tokens.subList(i + 1, tokens.size()));
            analyzeProcess.add(point);

            // 值匹配，执行下一轮
            String curSign = curStack.peek();
            if (curSign.equals(input.getContent())) {
                cur.removeLast();
                curStack.pop();
                continue;
            }

            String rowSign = curStack.peek();

            // 遇到错误
            if (input.getType().equals(TokenType.ERROR)) {
                return new AnalyzerResult(input.getContent(), analyzeProcess);
            }

            String valueMatch = table.getRule(rowSign, input.getContent());
            if (valueMatch == null) {
                if (input.getContent().equals(";")) {
                    return new AnalyzerResult(input.getRow() + "行缺少 ; ", analyzeProcess);
                }
                return new AnalyzerResult("类型不匹配: " + input.getInfo(), analyzeProcess);
            }

            point.setRule(valueMatch);
            // 产生式匹配
            curStack.pop();
            cur.removeLast();

            Rule rule = new Rule(valueMatch);
            List<String> rightList = rule.getRightList();
            for (int j = rightList.size() - 1; j >= 0; j--) {
                String item = rightList.get(j);
                // 推出空直接退出
                if (item.equals(Constant.EPSILON)) {
                    break;
                }
                cur.addLast(item);
                curStack.add(item);
            }
            i -= 1;

        }
        return new AnalyzerResult(analyzeProcess);
    }
}