package edu.njust.parse;

import edu.njust.parse.domain.Project;

import java.io.IOException;

public class Parser {
    public static void main(String[] args) throws IOException {
        Project project = new Project("parse/parse_grammar.txt", "word/out10.txt");

        System.out.println(project);

    }
}
