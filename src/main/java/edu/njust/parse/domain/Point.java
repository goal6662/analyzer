package edu.njust.parse.domain;

import edu.njust.word.domain.token.TokenInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * 分析表坐标
 */
@Getter
public class Point {

    private static int TOTAL_STEPS = 0;

    private final int step;

    /**
     * 匹配符号栈
     */
    private final List<String> cur;

    /**
     * 输入符号
     */
    private final String input;

    /**
     * 剩余符号栈
     */
    private final List<String> surplus;

    @Setter
    private String rule;

    public Point(List<String> cur, String input, List<TokenInfo> infos) {
        this.input = String.valueOf(input);
        this.step = TOTAL_STEPS++;
        surplus = infos.stream().map(TokenInfo::getContent).collect(Collectors.toList());
        this.cur = new ArrayList<>(cur);
    }

    public Point(Stack<String> cur, String input, List<TokenInfo> infos, String rule) {
        this.input = String.valueOf(input);
        this.step = TOTAL_STEPS++;
        surplus = infos.stream().map(TokenInfo::getContent).collect(Collectors.toList());
        this.cur = new ArrayList<>(cur);
        this.rule = rule;
    }

    private String dec(String val) {
        return "\"" + val + "\"";
    }

    @Override
    public String toString() {
        return step + "," + dec(cur.toString()) + ","
                + dec(input) + "," + dec(surplus.toString()) + (rule == null ? "" : ", " + dec(rule));
    }

}
