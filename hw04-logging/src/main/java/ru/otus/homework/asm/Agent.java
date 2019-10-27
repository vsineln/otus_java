package ru.otus.homework.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class Agent {
    private static final String OUTPUT_FILE = "proxyASM.class";

    public static void premain(String agentArgs, Instrumentation inst) {

        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className,
                                    Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain,
                                    byte[] classfileBuffer) {
                if (className.equals(agentArgs)) {
                    return scanLogAnnotation(classfileBuffer);
                }
                return classfileBuffer;
            }
        });
    }

    private static byte[] scanLogAnnotation(byte[] classfileBuffer) {
        ClassReader reader = new ClassReader(classfileBuffer);
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
        ClassVisitor visitor = new LogClassVisitor(writer);
        reader.accept(visitor, Opcodes.ASM5);

        byte[] finalClass = writer.toByteArray();

        try (OutputStream fos = new FileOutputStream(OUTPUT_FILE)) {
            fos.write(finalClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return finalClass;
    }
}
