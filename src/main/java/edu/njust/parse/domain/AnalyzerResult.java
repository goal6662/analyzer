package edu.njust.parse.domain;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AnalyzerResult {
    private boolean isSuccess = true;

    private String message;

    public AnalyzerResult(String message) {
        this.message = message;
        this.isSuccess = false;
    }

    @Override
    public String toString() {
        if (isSuccess) {
            return "匹配成功";
        } else {
            return "存在错误：" + message + "\n";
        }
    }
}
