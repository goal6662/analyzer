package edu.njust.word.domain.dfa;


import edu.njust.word.domain.nfa.NFA;
import edu.njust.word.domain.nfa.NFAState;
import lombok.Getter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@ToString
public class DFAState {
    /**
     * 包含的 NFA 状态
     */
    private final Set<NFAState> nfaStates;

    /**
     * 是否为终结状态
     */
    boolean isAccept;

    /**
     * 构造时判断当前状态是否为结束状态
     * @param nfa nfa
     */
    public DFAState(NFA nfa) {
        this.nfaStates = new HashSet<>();
        isAccept = false;

        // 判断当前状态是否为终结状态
        for (NFAState state : nfaStates) {
            if (nfa.getAcceptStates().contains(state)) {
                isAccept = true;
                break;
            }
        }
    }

    public DFAState(Set<NFAState> states, Set<NFAState> acceptStates) {
        this.nfaStates = states;
        isAccept = false;

        // 判断当前状态是否为终结状态
        for (NFAState state : nfaStates) {
            if (acceptStates.contains(state)) {
                isAccept = true;
                break;
            }
        }
    }

    @Override
    public int hashCode() {
        return nfaStates.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DFAState))
            return false;
        DFAState other = (DFAState) obj;
        return nfaStates.equals(other.nfaStates);
    }

}
