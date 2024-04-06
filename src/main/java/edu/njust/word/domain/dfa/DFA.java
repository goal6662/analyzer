package edu.njust.word.domain.dfa;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

@Getter
@ToString
public class DFA {

    /**
     * 某状态的状态转移集合
     */
    private final Map<DFAState, Map<String, DFAState>> transitions;

    /**
     * 初态集
     */
    @Setter
    private DFAState startState;

    /**
     * 终态集
     */
    private Set<DFAState> acceptState;

    public DFA() {
        this.transitions = new HashMap<>();
        this.acceptState = new HashSet<>();
    }

    /**
     * 添加终结状态
     * @param state
     */
    public void addAcceptState(DFAState state) {
        acceptState.add(state);
    }

    public void addState(DFAState state) {
        transitions.put(state, new HashMap<>());
    }

    public void addTransition(DFAState from, String symbol, DFAState to) {
        transitions.get(from).put(symbol, to);
    }

}
