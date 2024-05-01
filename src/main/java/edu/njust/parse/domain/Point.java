package edu.njust.parse.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 分析表坐标
 */
@Getter
@AllArgsConstructor
public class Point {

    /**
     * 终结符
     */
    private String termination;

    /**
     * 非终结符
     */
    private String noTermination;

}
