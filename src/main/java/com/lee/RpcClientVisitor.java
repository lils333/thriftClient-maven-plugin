package com.lee;

import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.io.File;

public class RpcClientVisitor extends ClassVisitor implements Opcodes {

    private String client;
    private File outputDirectory;
    private int innerIndex;

    public RpcClientVisitor(ClassVisitor classVisitor, String client, File outputDirectory, int index) {
        super(ASM7, classVisitor);
        this.client = client.replace('.', '/');
        this.outputDirectory = outputDirectory;
        this.innerIndex = index;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if ("<init>".equals(name)) {
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        } else {
            MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
            try {
                addThriftClientToRpcClient(methodVisitor, Type.getReturnType(descriptor).getDescriptor());
            } catch (Exception e) {
                throw new IllegalArgumentException("Can not write messag to client", e);
            }
            return null;
        }
    }

    private void addThriftClientToRpcClient(MethodVisitor methodVisitor, String descriptor) throws Exception {
        addPooledClient(descriptor);
        addClientMethod(methodVisitor);
    }

    private void addPooledClient(String thrift) throws Exception {
//        RpcClientGenerator generator = new RpcClientGenerator();
//        generator.generate(
//                client, thrift.replace(".", "/").substring(1, thrift.length() - 7), innerIndex, outputDirectory
//        );
    }

    private void addClientMethod(MethodVisitor methodVisitor) {
        String dest = StringUtils.substring(client, 0, StringUtils.lastIndexOf(client, "/"));
        methodVisitor.visitCode();
        methodVisitor.visitTypeInsn(NEW, dest + "/Client" + innerIndex);
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitVarInsn(ILOAD, 1);
        methodVisitor.visitVarInsn(ALOAD, 2);
        methodVisitor.visitVarInsn(ILOAD, 3);
        methodVisitor.visitVarInsn(ILOAD, 4);
        methodVisitor.visitVarInsn(ILOAD, 5);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, dest + "/Client" + innerIndex, "<init>", "(ZLjava/lang/String;III)V", false);
        methodVisitor.visitInsn(ARETURN);
        methodVisitor.visitMaxs(7, 6);
        methodVisitor.visitEnd();
    }
}
