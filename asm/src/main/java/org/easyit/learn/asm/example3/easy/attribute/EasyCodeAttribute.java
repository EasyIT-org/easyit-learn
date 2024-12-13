package org.easyit.learn.asm.example3.easy.attribute;

import java.util.ArrayList;
import java.util.List;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import org.easyit.learn.asm.example3.easy.EasyAttributeInfo;

import static javassist.bytecode.InstructionPrinter.instructionString;

public class EasyCodeAttribute extends EasyAttributeInfo {

    private String byteCode;

    public static EasyCodeAttribute fromJavassist(final CodeAttribute codeAttribute) {
        EasyCodeAttribute target = new EasyCodeAttribute();
        target.attribute_name = codeAttribute.getName();
        target.info = codeAttribute.getCode();
        target.byteCode = parseByteCode(codeAttribute);
        return target;
    }

    private static String parseByteCode(final CodeAttribute codeAttribute) {

        CodeIterator iterator = codeAttribute.iterator();
        List<String> result = new ArrayList<>();
        while (iterator.hasNext()) {
            int pos;
            try {
                pos = iterator.next();
            } catch (BadBytecode e) {
                throw new RuntimeException(e);
            }
            result.add(
                "\n\t\t\t\t\t\t\t" + pos + ": " + instructionString(iterator, pos, codeAttribute.getConstPool()));
        }
        return String.join("", result);
    }

    @Override
    public String toString() {
        return byteCode + "\n";
    }
}
