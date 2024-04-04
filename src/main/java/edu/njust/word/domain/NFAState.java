package edu.njust.word.domain;

import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;

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
     * 输入 char 可达状态
     */
    HashMap<String, HashSet<NFAState>> transitions;

    public NFAState(int stateNum) {
        this.stateNum = stateNum;
        epsilonClosures = new HashSet<>();
        transitions = new HashMap<>();
    }
}
