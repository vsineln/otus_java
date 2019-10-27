package ru.otus.homework.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Add logging if method has @Log annotation
 */
public class LogMethodVisitor extends MethodVisitor implements Opcodes {
    private static final String LOG_ANNOTATION = "Lru/otus/homework/annotation/Log;";

    private final String methodName;
    private final String descriptor;
    private final int access;
    private boolean isAnnotationPresent;

    public LogMethodVisitor(MethodVisitor methodVisitor, String methodName, String descriptor, int access) {
        super(ASM5, methodVisitor);
        this.methodName = methodName;
        this.descriptor = descriptor;
        this.access = access;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (LOG_ANNOTATION.equals(desc)) {
            isAnnotationPresent = true;
        }
        return super.visitAnnotation(desc, visible);
    }

    @Override
    public void visitCode() {
        if (isAnnotationPresent) {
            Type[] argTypes = Type.getArgumentTypes(descriptor);
            int i = (access | ACC_STATIC) == 0 ? 0 : 1;
            int index = argTypes.length + i;

            mv.visitIntInsn(BIPUSH, argTypes.length);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");

            int x = 0;
            for (Type t : argTypes) {
                mv.visitInsn(DUP);
                mv.visitIntInsn(BIPUSH, x);
                if (t.equals(Type.BOOLEAN_TYPE)) {
                    mv.visitVarInsn(ILOAD, i);
                    invokeStatic("java/lang/Boolean", "(Z)Ljava/lang/Boolean;");
                } else if (t.equals(Type.BYTE_TYPE)) {
                    mv.visitVarInsn(ILOAD, i);
                    invokeStatic("java/lang/Byte", "(B)Ljava/lang/Byte;");
                } else if (t.equals(Type.CHAR_TYPE)) {
                    mv.visitVarInsn(ILOAD, i);
                    invokeStatic("java/lang/Character", "(C)Ljava/lang/Character;");
                } else if (t.equals(Type.SHORT_TYPE)) {
                    mv.visitVarInsn(ILOAD, i);
                    invokeStatic("java/lang/Short", "(S)Ljava/lang/Short;");
                } else if (t.equals(Type.INT_TYPE)) {
                    mv.visitVarInsn(ILOAD, i);
                    invokeStatic("java/lang/Integer", "(I)Ljava/lang/Integer;");
                } else if (t.equals(Type.LONG_TYPE)) {
                    mv.visitVarInsn(LLOAD, i);
                    invokeStatic("java/lang/Long", "(J)Ljava/lang/Long;");
                    i++;
                } else if (t.equals(Type.FLOAT_TYPE)) {
                    mv.visitVarInsn(FLOAD, i);
                    invokeStatic("java/lang/Float", "(F)Ljava/lang/Float;");
                } else if (t.equals(Type.DOUBLE_TYPE)) {
                    mv.visitVarInsn(DLOAD, i);
                    invokeStatic("java/lang/Double", "(D)Ljava/lang/Double;");
                    i++;
                } else {
                    mv.visitVarInsn(ALOAD, i);
                }
                mv.visitInsn(AASTORE);
                i++;
                x++;
            }
            mv.visitVarInsn(ASTORE, index);
            this.visitLdcInsn(methodName);
            this.visitVarInsn(ALOAD, index);
            mv.visitMethodInsn(INVOKESTATIC, "ru/otus/homework/asm/Logger", "logMethod",
                    "(Ljava/lang/String;[Ljava/lang/Object;)V", false);
        }
        super.visitCode();
    }

    private void invokeStatic(String owner, String descriptor) {
        mv.visitMethodInsn(INVOKESTATIC, owner, "valueOf", descriptor, false);
    }
}
