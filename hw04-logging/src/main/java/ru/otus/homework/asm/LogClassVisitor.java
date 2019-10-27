package ru.otus.homework.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LogClassVisitor extends ClassVisitor implements Opcodes {

    public LogClassVisitor(ClassVisitor writer) {
        super(ASM5, writer);
    }

    @Override
    public MethodVisitor visitMethod(int access, String methodName, String desc,
                                     String signature, String[] exceptions) {

        MethodVisitor methodVisitor = cv.visitMethod(access, methodName, desc, signature,
                exceptions);
        return new LogMethodVisitor(methodVisitor, methodName, desc, access);
    }
}
