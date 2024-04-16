package edu.njust.word.domain.grammar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class Rule {

    /**
     * 规则左部
     * 一定是一个非终结符
     */
    private final String left;

    /**
     * 规则右部
     */
//    private final String right;

    /**
     * 转移条件
     */
    private final String symbol;

    /**
     * 该规则导出的下一个状态
     */
    private final String next;


    public Rule(String rule) {
        String[] res = splitRule(rule);

        left = res[0].substring(1, res[0].length() - 1);
//        right = res[1];
        if (res[1].length() == 1) {
            next = null;
            symbol = res[1];
        } else {
            int index = res[1].lastIndexOf("<");
            if (index != -1) {
                String s = res[1].substring(0, index);
                symbol = s.isEmpty() ? null : s;
                next = res[1].substring(index + 1, res[1].length() - 1);
            } else {
                symbol = res[1];
                next = null;
            }
        }
    }

    private String[] splitRule(String rule) {
        return rule.split(" -> ");
    }
}
