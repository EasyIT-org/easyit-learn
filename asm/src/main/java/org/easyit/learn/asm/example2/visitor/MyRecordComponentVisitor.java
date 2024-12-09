package org.easyit.learn.asm.example2.visitor;

import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.TypeReference;

public class MyRecordComponentVisitor extends RecordComponentVisitor {
    private final String prefix;
    private final Map<String, Integer> methodCount = new HashMap<>();

    private static final String[] METHOD_NAMES = new String[] {
        "visitAnnotation",
        "visitTypeAnnotation",
        "visitAttribute",
        "visitEnd",
        };

    protected MyRecordComponentVisitor(final int api, String prefix) {
        super(api);
        this.api = api;
        this.prefix = prefix;
        for (String methodName : METHOD_NAMES) {
            methodCount.put(methodName, 0);
        }

    }

    private void record(final String methodName, final Object[] params) {
        System.out.println(prefix + "RecordComponentVisitor: " + methodName);
        methodCount.put(methodName, methodCount.get(methodName) + 1);
    }

    /**
     * The ASM API version implemented by this visitor. The value of this field must be one of {@link Opcodes#ASM8} or
     * {@link Opcodes#ASM9}.
     */
    protected int api;

    /**
     * Visits an annotation of the record component.
     *
     * @param descriptor the class descriptor of the annotation class.
     * @param visible    {@literal true} if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not interested in
     * visiting this annotation.
     */
    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        record("visitAnnotation", new Object[] {
            descriptor,
            visible
        });
        return new MyAnnotationVisitor(api, prefix + "\t");
    }

    /**
     * Visits an annotation on a type in the record component signature.
     *
     * @param typeRef    a reference to the annotated type. The sort of this type reference must be
     *                   {@link TypeReference#CLASS_TYPE_PARAMETER}, {@link TypeReference#CLASS_TYPE_PARAMETER_BOUND} or
     *                   {@link TypeReference#CLASS_EXTENDS}. See {@link TypeReference}.
     * @param typePath   the path to the annotated type argument, wildcard bound, array element type, or static inner
     *                   type within 'typeRef'. May be {@literal null} if the annotation targets 'typeRef' as a whole.
     * @param descriptor the class descriptor of the annotation class.
     * @param visible    {@literal true} if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not interested in
     * visiting this annotation.
     */
    public AnnotationVisitor visitTypeAnnotation(
        final int typeRef, final TypePath typePath, final String descriptor, final boolean visible) {
        record("visitTypeAnnotation", new Object[] {
            typeRef,
            typePath,
            descriptor,
            visible
        });
        return new MyAnnotationVisitor(api, prefix + "\t");
    }

    /**
     * Visits a non standard attribute of the record component.
     *
     * @param attribute an attribute.
     */
    public void visitAttribute(final Attribute attribute) {
        record("visitAttribute", new Object[] {attribute});
    }

    /**
     * Visits the end of the record component. This method, which is the last one to be called, is used to inform the
     * visitor that everything have been visited.
     */
    public void visitEnd() {
        record("visitEnd", new Object[] {});
    }

}
