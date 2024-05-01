package edu.njust.parse.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class Vn {

    /**
     * 非终结符标识
     */
    private String symbol;

    /**
     * First 集
     */
    private Set<String> first;

    /**
     * Follow 集
     */
    private Set<String> follow;

    public Vn(String symbol) {
        this.symbol = symbol;
    }

}
