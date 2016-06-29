package com.progressSoft.kaue.entity;

/**
 * Created by krb on 6/29/16.
 */
public class TransactionError {

    private String file;
    private String error;
    private Long line;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Long getLine() {
        return line;
    }

    public void setLine(Long line) {
        this.line = line;
    }
}
