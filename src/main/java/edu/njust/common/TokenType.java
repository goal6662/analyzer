package edu.njust.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TokenType {

    /**
     * 关键字
     */
    public static final String KEY_WORD = "key_word";

    /**
     * 标识符
     */
    public static final String IDENTIFIER = "identifier";

    /**
     * 常量
     */
    public static final String CONSTANT = "constant";


    /**
     * 界符
     */
    public static final String DELIMITER = "delimiter";

    /**
     * 运算符
     */
    public static final String OPERATOR = "operator";

    /**
     * 数据类型
     */
    public static final String TYPE = "type";
    public static final String ERROR = "error";
    public static final String MODIFIER = "modifier";

    public static final Set<String> TYPE_LIST = new HashSet<>(
            Arrays.asList(TYPE, KEY_WORD, IDENTIFIER, CONSTANT, DELIMITER, OPERATOR, ERROR, MODIFIER)
    );

}