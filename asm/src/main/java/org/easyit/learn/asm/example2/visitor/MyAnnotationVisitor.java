package org.easyit.learn.asm.example2.visitor;

import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

public class MyAnnotationVisitor extends AnnotationVisitor {

    private String prefix;
    private int api;

    private static final String[] METHOD_NAMES = new String[] {
        "visit",
        "visitEnum",
        "visitAnnotation",
        "visitArray",
        "visitEnd"
    };

    public MyAnnotationVisitor(final int api, String prefix) {
        super(api);
        this.api = api;
        this.prefix = prefix;
        for (final String methodName : METHOD_NAMES) {
            methodCount.put(methodName, 0);
        }
    }

    private final Map<String, Integer> methodCount = new HashMap<>();

    private void record(final String methodName, final Object[] params) {
        System.out.println(prefix + "AnnotationVisitor: " + methodName);
        methodCount.put(methodName, methodCount.get(methodName) + 1);
    }

    /**
     * Visits a primitive value of the annotation.
     *
     * @param name  the value name.
     * @param value the actual value, whose type must be {@link Byte}, {@link Boolean}, {@link Character},
     *              {@link Short}, {@link Integer} , {@link Long}, {@link Float}, {@link Double}, {@link String} or
     *              {@link Type} of {@link Type#OBJECT} or {@link Type#ARRAY} sort. This value can also be an array of
     *              byte, boolean, short, char, int, long, float or double values (this is equivalent to using
     *              {@link #visitArray} and visiting each array element in turn, but is more convenient).
     */
    public void visit(final String name, final Object value) {
        record("visit", new Object[] {
            name,
            value
        });
    }

    /**
     * Visits an enumeration value of the annotation.
     *
     * @param name       the value name.
     * @param descriptor the class descriptor of the enumeration class.
     * @param value      the actual enumeration value.
     */
    public void visitEnum(final String name, final String descriptor, final String value) {
        record("visitEnum", new Object[] {
            name,
            descriptor,
            value
        });
    }

    /**
     * Visits a nested annotation value of the annotation.
     *
     * @param name       the value name.
     * @param descriptor the class descriptor of the nested annotation class.
     * @return a visitor to visit the actual nested annotation value, or {@literal null} if this visitor is not
     * interested in visiting this nested annotation. <i>The nested annotation value must be fully visited before
     * calling other methods on this annotation visitor</i>.
     */
    public AnnotationVisitor visitAnnotation(final String name, final String descriptor) {
        record("visitAnnotation", new Object[] {
            name,
            descriptor
        });
        return new MyAnnotationVisitor(api, prefix + "\t");
    }

    /**
     * Visits an array value of the annotation. Note that arrays of primitive values (such as byte, boolean, short,
     * char, int, long, float or double) can be passed as value to {@link #visit visit}. This is what
     * {@link ClassReader} does for non empty arrays of primitive values.
     *
     * @param name the value name.
     * @return a visitor to visit the actual array value elements, or {@literal null} if this visitor is not interested
     * in visiting these values. The 'name' parameters passed to the methods of this visitor are ignored. <i>All the
     * array values must be visited before calling other methods on this annotation visitor</i>.
     */
    public AnnotationVisitor visitArray(final String name) {
        record("visitArray", new Object[] {name});
        return new MyAnnotationVisitor(api, prefix + "\t");
    }

    /**
     * Visits the end of the annotation.
     */
    public void visitEnd() {
        record("visitEnd", null);
    }

}
