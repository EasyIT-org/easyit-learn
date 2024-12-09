package org.easyit.learn.asm.example2.visitor;

import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.TypeReference;

public class MyFieldVisitor extends FieldVisitor {

    /**
     * The ASM API version implemented by this visitor. The value of this field must be one of the {@code ASM}<i>x</i>
     * values in {@link Opcodes}.
     */
    protected final int api;

    private String prefix;
    private final Map<String, Integer> methodCount = new HashMap<>();


    private static final String[] METHOD_NAMES = new String[] {
        "visit",
        "visitAnnotation",
        "visitTypeAnnotation",
        "visitAttribute",
        "visitEnd"
    };


    public MyFieldVisitor(final int api,String prefix) {
        super(api);
        this.api = api;
        this.prefix = prefix;

        for (final String methodName : METHOD_NAMES) {
            methodCount.put(methodName, 0);
        }
    }

    private void record(final String methodName, final Object[] params) {
        System.out.println(prefix + "FieldVisitor: " + methodName);
        methodCount.put(methodName, methodCount.get(methodName) + 1);
    }



    /**
     * Visits an annotation of the field.
     *
     * @param descriptor the class descriptor of the annotation class.
     * @param visible    {@literal true} if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not interested in
     * visiting this annotation.
     */
    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        if (fv != null) {
            return fv.visitAnnotation(descriptor, visible);
        }
        return null;
    }




    /**
     * Visits an annotation on the type of the field.
     *
     * @param typeRef    a reference to the annotated type. The sort of this type reference must be
     *                   {@link TypeReference#FIELD}. See {@link TypeReference}.
     * @param typePath   the path to the annotated type argument, wildcard bound, array element type, or static inner
     *                   type within 'typeRef'. May be {@literal null} if the annotation targets 'typeRef' as a whole.
     * @param descriptor the class descriptor of the annotation class.
     * @param visible    {@literal true} if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not interested in
     * visiting this annotation.
     */
    public AnnotationVisitor visitTypeAnnotation(
        final int typeRef, final TypePath typePath, final String descriptor, final boolean visible) {
        if (api < Opcodes.ASM5) {
            throw new UnsupportedOperationException("This feature requires ASM5");
        }
        if (fv != null) {
            return fv.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
        }
        return null;
    }

    /**
     * Visits a non standard attribute of the field.
     *
     * @param attribute an attribute.
     */
    public void visitAttribute(final Attribute attribute) {
        if (fv != null) {
            fv.visitAttribute(attribute);
        }
    }

    /**
     * Visits the end of the field. This method, which is the last one to be called, is used to inform the visitor that
     * all the annotations and attributes of the field have been visited.
     */
    public void visitEnd() {
        if (fv != null) {
            fv.visitEnd();
        }
    }

}

