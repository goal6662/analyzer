package edu.njust.word.domain.token;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenInfo {

    private int len;

    private String type;

    private String content;

    public TokenInfo(String content) {
        this.content = content;
    }

    public String getInfo() {
        return "token: (行号: " + len + ", 类别: " + type  + ", 内容: " + content + ")";
    }
}
