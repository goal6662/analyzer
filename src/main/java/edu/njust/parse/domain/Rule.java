package edu.njust.parse.domain;

import edu.njust.common.Constant;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class Rule {
    private final String left;
    private final String right;

    /**
     * 保留了<>
     */
    private final List<String> rightList;

    public Rule(String rule) {
        String[] split = rule.split(" -> ");

        this.left = split[0];
        this.right = split[1];
        this.rightList = generateRightSet();
    }

    private List<String> generateRightSet() {
        List<String> list = new ArrayList<>();

        // 1. 获取第一个符号
        for (int i = 0; i < right.length(); i++) {
            if (right.charAt(i) == '<' && right.indexOf('>', i) != -1) {
                // 截取并加入
                int index = right.indexOf('>', i);
                String symbol = right.substring(i, index + 1);
                list.add(symbol);
                i = index;
            } else {
                int index = right.indexOf('<', i + 1);
                if (index == -1) {
                    list.add(right.substring(i));
                    return list;
                } else {
                    list.add(right.substring(i, index));
                }
                i = index - 1;
            }
        }

        return list;
    }

    /**
     * <str> -> str
     *
     * @param str
     * @return 去除首尾字符
     */
    public static String removeSign(String str) {
        return str.substring(1, str.length() - 1);
    }

    public String getOrigin() {
        if (right.contains(",")) {
            return "\"" + left + " -> " + right + "\"";
        }
        return left + " -> " + right;
    }

    /**
     * 是否为终结符
     *
     * @param sign
     * @return true: 是
     */
    public static boolean isVt(String sign) {
        return !sign.startsWith("<") || !sign.endsWith(">");
    }

    public static boolean isVn(String sign) {
        return !isVt(sign);
    }

    public static boolean isEmpty(Rule rule) {
        return rule.getRightList().size() == 1 && rule.right.equals(Constant.EPSILON);
    }
}
