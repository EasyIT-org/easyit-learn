package org.easyit.learn.asm.example3;

public class ValueConstantEntry extends ConstantPoolEntry {

    private Object value;

    public void setValue(final Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "[Utf8ConstantEntry]" + value;
    }
}
