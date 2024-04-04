package edu.njust.word.domain;

import lombok.Getter;

import java.util.*;

@Getter
public class NFA {

    Map<String, NFAState> stateMap;

    /**
     * 状态集合
     */
    Set<NFAState> states;

    /**
     * 起始状态
     */
    NFAState startState;

    /**
     * 结束状态，验证成功
     */
    Set<NFAState> acceptStates;

    public NFA() {
        states = new LinkedHashSet<>();
        stateMap = new HashMap<>();
        acceptStates = new HashSet<>();
    }

    public void addState(NFAState state) {
        states.add(state);
    }

    public void setStartState(NFAState state) {
        startState = state;
    }

    public void addAcceptState(NFAState state) {
        acceptStates.add(state);
    }

    /**
     * 添加过渡状态
     *
     * @param from   起始状态
     * @param symbol 接收字符
     * @param to     到达状态
     */
    public void addTransition(NFAState from, String symbol, NFAState to) {
        if (!from.transitions.containsKey(symbol))
            from.transitions.put(symbol, new HashSet<>());
        from.transitions.get(symbol).add(to);
    }

    public void addEpsilonTransition(NFAState from, NFAState to) {
        if (from != null) {
            from.epsilonClosures.add(to);
        }
    }

    public NFAState linkStateWithSign(NFAState state, String sign) {
        this.stateMap.put(sign, state);
        return state;
    }

    public NFAState getStateWithSign(String sign) {
        return stateMap.get(sign);
    }
}
