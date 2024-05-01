package edu.njust.parse;

import edu.njust.parse.domain.ParseRule;

import java.io.IOException;

public class Parser {
    public static void main(String[] args) throws IOException {
        ParseRule parseRule = new ParseRule("parse/parse_grammar.txt");

        System.out.println(parseRule.getRuleList());

    }
}
