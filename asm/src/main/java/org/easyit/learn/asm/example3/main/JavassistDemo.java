package org.easyit.learn.asm.example3.main;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import javassist.bytecode.ClassFile;
import org.easyit.learn.asm.example3.easy.EasyClassFile;

public class JavassistDemo {
    public static final String CLASSNAME = "org.easyit.learn.asm.example3.SimpleDemo";
    public static final String SIMPLE_DEMO_CLASS_PATH = "./asm/target/classes/org/easyit/learn/asm/example3/SimpleDemo.class";

    public static void main(String[] args) throws IOException {
        InputStream stream = Files.newInputStream(Paths.get(SIMPLE_DEMO_CLASS_PATH));
        BufferedInputStream fin = new BufferedInputStream(stream);
        ClassFile cf = new ClassFile(new DataInputStream(fin));
        print(cf);
    }

    private static void print(final ClassFile cf) {
        EasyClassFile easyClassFile = EasyClassFile.fromJavassist(cf);
        System.out.println(easyClassFile);
    }
}
