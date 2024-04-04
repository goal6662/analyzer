package edu.njust;

import cn.hutool.json.JSONUtil;
import edu.njust.word.domain.Grammar;

import java.io.IOException;

public class MainTest {



    public static void main(String[] args) throws IOException {
        Grammar grammar = new Grammar("word/gra.txt");

        System.out.println(JSONUtil.parse(grammar));
    }
}
