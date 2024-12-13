package org.easyit.learn.asm.example3;

import java.io.Serializable;

public class SimpleDemo implements Serializable {
    private String privateStringField;

    public String helloWorld() {
        return "Hello world!";
    }
}
