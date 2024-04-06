package edu.njust.word.domain.nfa;

import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Getter
public class NFAState {

    /**
     * 状态标识 0、1、2...
     */
    int stateNum;

    /**
     * 空输入可达状态
     */
    HashSet<NFAState> epsilonClosures;

    /**
     * 输入 symbol 可达状态
     */
    HashMap<String, HashSet<NFAState>> transitions;

    public NFAState(int stateNum) {
        this.stateNum = stateNum;
        epsilonClosures = new HashSet<>();
        transitions = new HashMap<>();
    }

    /**
     * "[1, 3, 5]"
     * @param states
     * @return
     */
    public static String printStatesInfo(Set<NFAState> states) {
        StringBuilder builder = new StringBuilder("[");
        for (NFAState state : states) {
            builder.append(state.getStateNum()).append(", ");
        }

        return builder.substring(0, builder.lastIndexOf(",")) + "]";
    }
}
