package edu.njust.word.domain.token;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenInfo {

    private int row;

    private int column;

    private String type;

    private String content;

    public TokenInfo(int column, String content) {
        this.content = content;
        this.column = column;
    }

    public String getInfo() {
        return "token: (行号: " + row + ", 列号: " + column + ", 类别: " + type  + ", 内容: " + content + ")";
    }

}
