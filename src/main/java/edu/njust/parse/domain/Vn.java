package edu.njust.parse.domain;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Vn {

    /**
     * 非终结符标识
     */
    private String symbol;

//    private boolean isEnd = false;

    /**
     * First 集
     */
    @Setter
    private Set<String> first = new HashSet<>();

    /**
     * Follow 集
     */
    private Set<String> follow = new HashSet<>();

    public Vn(String symbol) {
        this.symbol = symbol;
    }

}
