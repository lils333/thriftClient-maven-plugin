package com.lee;

import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MethodGenerator extends BaseGenerator implements Opcodes {

    private FiledGenerator filed = new FiledGenerator();

    public void createConstructorMethod(ClassWriter cw, String innerClass) {

        String outClass = "L" + StringUtils.split(innerClass, '$')[0] + ";";

        StringBuilder sb = new StringBuilder("(ZLjava/lang/String;III)V");
        sb.insert(1, outClass);

        System.out.println(sb.toString());

        MethodVisitor methodVisitor = cw.visitMethod(0, "<init>", sb.toString(), null, null);
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitVarInsn(ALOAD, 1);
        methodVisitor.visitFieldInsn(PUTFIELD, innerClass, "this$0", outClass);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "org/apache/commons/pool2/BasePooledObjectFactory", "<init>", "()V", false);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitVarInsn(ILOAD, 2);
        methodVisitor.visitFieldInsn(PUTFIELD, innerClass, "isFramed", "Z");
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitVarInsn(ALOAD, 3);
        methodVisitor.visitFieldInsn(PUTFIELD, innerClass, "address", "Ljava/lang/String;");
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitVarInsn(ILOAD, 4);
        methodVisitor.visitFieldInsn(PUTFIELD, innerClass, "port", "I");
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitVarInsn(ILOAD, 5);
        methodVisitor.visitFieldInsn(PUTFIELD, innerClass, "connectionTimeout", "I");
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitVarInsn(ILOAD, 6);
        methodVisitor.visitFieldInsn(PUTFIELD, innerClass, "socketTimeout", "I");
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitTypeInsn(NEW, "org/apache/commons/pool2/impl/GenericObjectPool");
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, innerClass, "createConfig", "()Lorg/apache/commons/pool2/impl/GenericObjectPoolConfig;", false);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "org/apache/commons/pool2/impl/GenericObjectPool", "<init>", "(Lorg/apache/commons/pool2/PooledObjectFactory;Lorg/apache/commons/pool2/impl/GenericObjectPoolConfig;)V", false);
        methodVisitor.visitFieldInsn(PUTFIELD, innerClass, "objectPool", "Lorg/apache/commons/pool2/impl/GenericObjectPool;");
        methodVisitor.visitTypeInsn(NEW, "net/jodah/failsafe/RetryPolicy");
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "net/jodah/failsafe/RetryPolicy", "<init>", "()V", false);
        methodVisitor.visitInsn(ICONST_3);
        methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Class");
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitInsn(ICONST_0);
        methodVisitor.visitLdcInsn(Type.getType("Lorg/apache/thrift/transport/TTransportException;"));
        methodVisitor.visitInsn(AASTORE);
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitInsn(ICONST_1);
        methodVisitor.visitLdcInsn(Type.getType("Ljava/net/SocketException;"));
        methodVisitor.visitInsn(AASTORE);
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitInsn(ICONST_2);
        methodVisitor.visitLdcInsn(Type.getType("Ljava/net/SocketTimeoutException;"));
        methodVisitor.visitInsn(AASTORE);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "net/jodah/failsafe/RetryPolicy", "retryOn", "([Ljava/lang/Class;)Lnet/jodah/failsafe/RetryPolicy;", false);
        methodVisitor.visitLdcInsn(new Long(2000L));
        methodVisitor.visitLdcInsn(new Long(8000L));
        methodVisitor.visitFieldInsn(GETSTATIC, "java/util/concurrent/TimeUnit", "MILLISECONDS", "Ljava/util/concurrent/TimeUnit;");
        methodVisitor.visitLdcInsn(new Double("1.25"));
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "net/jodah/failsafe/RetryPolicy", "withBackoff", "(JJLjava/util/concurrent/TimeUnit;D)Lnet/jodah/failsafe/RetryPolicy;", false);
        methodVisitor.visitIntInsn(BIPUSH, 6);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "net/jodah/failsafe/RetryPolicy", "withMaxRetries", "(I)Lnet/jodah/failsafe/RetryPolicy;", false);
        methodVisitor.visitLdcInsn(new Long(30L));
        methodVisitor.visitFieldInsn(GETSTATIC, "java/util/concurrent/TimeUnit", "SECONDS", "Ljava/util/concurrent/TimeUnit;");
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "net/jodah/failsafe/RetryPolicy", "withMaxDuration", "(JLjava/util/concurrent/TimeUnit;)Lnet/jodah/failsafe/RetryPolicy;", false);
        methodVisitor.visitVarInsn(ASTORE, 7);
        methodVisitor.visitTypeInsn(NEW, "net/jodah/failsafe/CircuitBreaker");
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "net/jodah/failsafe/CircuitBreaker", "<init>", "()V", false);
        methodVisitor.visitInsn(ICONST_3);
        methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Class");
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitInsn(ICONST_0);
        methodVisitor.visitLdcInsn(Type.getType("Lorg/apache/thrift/transport/TTransportException;"));
        methodVisitor.visitInsn(AASTORE);
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitInsn(ICONST_1);
        methodVisitor.visitLdcInsn(Type.getType("Ljava/net/SocketException;"));
        methodVisitor.visitInsn(AASTORE);
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitInsn(ICONST_2);
        methodVisitor.visitLdcInsn(Type.getType("Ljava/net/SocketTimeoutException;"));
        methodVisitor.visitInsn(AASTORE);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "net/jodah/failsafe/CircuitBreaker", "failOn", "([Ljava/lang/Class;)Lnet/jodah/failsafe/CircuitBreaker;", false);
        methodVisitor.visitIntInsn(BIPUSH, 6);
        methodVisitor.visitIntInsn(BIPUSH, 10);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "net/jodah/failsafe/CircuitBreaker", "withFailureThreshold", "(II)Lnet/jodah/failsafe/CircuitBreaker;", false);
        methodVisitor.visitInsn(ICONST_3);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "net/jodah/failsafe/CircuitBreaker", "withSuccessThreshold", "(I)Lnet/jodah/failsafe/CircuitBreaker;", false);
        methodVisitor.visitLdcInsn(new Long(5L));
        methodVisitor.visitFieldInsn(GETSTATIC, "java/util/concurrent/TimeUnit", "SECONDS", "Ljava/util/concurrent/TimeUnit;");
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "net/jodah/failsafe/CircuitBreaker", "withDelay", "(JLjava/util/concurrent/TimeUnit;)Lnet/jodah/failsafe/CircuitBreaker;", false);
        methodVisitor.visitLdcInsn(new Long(30L));
        methodVisitor.visitFieldInsn(GETSTATIC, "java/util/concurrent/TimeUnit", "SECONDS", "Ljava/util/concurrent/TimeUnit;");
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "net/jodah/failsafe/CircuitBreaker", "withTimeout", "(JLjava/util/concurrent/TimeUnit;)Lnet/jodah/failsafe/CircuitBreaker;", false);
        methodVisitor.visitVarInsn(ASTORE, 8);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitVarInsn(ALOAD, 7);
        methodVisitor.visitMethodInsn(INVOKESTATIC, "net/jodah/failsafe/Failsafe", "with", "(Lnet/jodah/failsafe/RetryPolicy;)Lnet/jodah/failsafe/SyncFailsafe;", false);
        methodVisitor.visitVarInsn(ALOAD, 8);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "net/jodah/failsafe/SyncFailsafe", "with", "(Lnet/jodah/failsafe/CircuitBreaker;)Ljava/lang/Object;", false);
        methodVisitor.visitTypeInsn(CHECKCAST, "net/jodah/failsafe/SyncFailsafe");
        methodVisitor.visitFieldInsn(PUTFIELD, innerClass, "executor", "Lnet/jodah/failsafe/SyncFailsafe;");
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(8, 9);
        methodVisitor.visitEnd();
    }

    //构造方法
    //给内部类字段赋值，因为需要访问外部类，所以需要把第一个参数设置成外部类的引用
    //记住：第0个参数不用设置，它永远都是指向当前对象本身
    private void createInnerConstructorMethod(
            ClassWriter writer, MethodNode method, String outClass, String innerClass) {
        MethodVisitor intervalMv;

        StringBuilder sb = new StringBuilder(method.desc);
        sb.insert(1, "L" + outClass + ";");
        String descriptor = StringUtils.substring(
                sb.toString(), 0, StringUtils.lastIndexOf(sb.toString(), ")") + 1) + "V";

        System.out.println("descriptor  = " + descriptor);

        if (method.signature == null) {
            intervalMv = writer.visitMethod(0, "<init>", descriptor, null, null);
        } else {
            StringBuilder sb1 = new StringBuilder(
                    StringUtils.substring(method.signature, 0, StringUtils.indexOf(method.signature, ')') + 1) + "V"
            );
            sb1.insert(1, "L" + outClass + ";");
            intervalMv = writer.visitMethod(0, "<init>", descriptor, sb1.toString(), null);
            System.out.println("signature  = " + sb1.toString());
        }

        intervalMv.visitCode();

        int index = 0;
        for (LocalVariableNode localVariable : method.localVariables) {
            //this 不用传递，永远都是第一个对象，我们这个地方只是用来设置一下指向外部类对象的引用
            if ("this".equals(localVariable.name)) {
                index++;
                intervalMv.visitVarInsn(ALOAD, 0);
                intervalMv.visitVarInsn(ALOAD, index);
                intervalMv.visitFieldInsn(PUTFIELD, innerClass, "this$1", "L" + outClass + ";");
            } else {
                intervalMv.visitVarInsn(ALOAD, 0);
                Integer value = LOADS.get(localVariable.desc);
                if (value == null) {
                    value = Opcodes.ALOAD;
                }

                if ("D".equalsIgnoreCase(localVariable.desc) || "J".equalsIgnoreCase(localVariable.desc)) {
                    index++;
                    intervalMv.visitVarInsn(value, index);
                    index++;
                    //可能在设置值的时候，需要把值设置多次
                } else {
                    index++;
                    intervalMv.visitVarInsn(value, index);
                }
                intervalMv.visitFieldInsn(PUTFIELD, innerClass, "val$" + localVariable.name, localVariable.desc);
            }
        }

        intervalMv.visitVarInsn(ALOAD, 0);
        intervalMv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        intervalMv.visitInsn(RETURN);
        intervalMv.visitMaxs(2, 4);
        intervalMv.visitEnd();
    }

    private void createFiled(ClassWriter writer, MethodNode method, String outClass) {
        for (LocalVariableNode localVariable : method.localVariables) {
            if ("this".equals(localVariable.name)) {
                //需要把外部类对象的一个引用设置给当前的内部类对象，这样内部类可以获取外部类的方法和字段
                filed.createFiled(
                        writer,
                        ACC_FINAL + ACC_SYNTHETIC,
                        "this$1",
                        "L" + outClass + ";",
                        null
                );
            } else {
                //其他的方法里面的参数也需要给内部类作为一个字段来存放
                filed.createFiled(
                        writer,
                        ACC_FINAL + ACC_SYNTHETIC,
                        "val$" + localVariable.name,
                        localVariable.desc,
                        localVariable.signature
                );
            }
        }
    }

    private void createRunMethod(
            ClassWriter writer, MethodNode method, String client, String thrift, String innerClassName) {
        MethodVisitor mv = writer.visitMethod(ACC_PUBLIC, "run", "()V", null, new String[]{"java/lang/Exception"});
        mv.visitCode();
        Label l0 = new Label();
        Label l1 = new Label();
        Label l2 = new Label();
        mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
        Label l3 = new Label();
        mv.visitTryCatchBlock(l0, l1, l3, null);
        Label l4 = new Label();
        mv.visitTryCatchBlock(l2, l4, l3, null);
        mv.visitInsn(ACONST_NULL);
        mv.visitVarInsn(ASTORE, 1);
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, innerClassName, "this$1", "L" + client + ";");
        mv.visitMethodInsn(INVOKESTATIC, client, "access$000", "(L" + client + ";)Lorg/apache/commons/pool2/impl/GenericObjectPool;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/commons/pool2/impl/GenericObjectPool", "borrowObject", "()Ljava/lang/Object;", false);
        mv.visitTypeInsn(CHECKCAST, thrift + "$Client");
        mv.visitVarInsn(ASTORE, 1);
        mv.visitVarInsn(ALOAD, 1);

        for (LocalVariableNode localVariable : method.localVariables) {
            if (!"this".equals(localVariable.name)) {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, innerClassName, "val$" + localVariable.name, localVariable.desc);
            }
        }

        mv.visitMethodInsn(INVOKEVIRTUAL, thrift + "$Client", method.name, method.desc, false);

        mv.visitLabel(l1);
        mv.visitVarInsn(ALOAD, 1);
        Label l5 = new Label();
        mv.visitJumpInsn(IFNULL, l5);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, innerClassName, "this$1", "L" + client + ";");
        mv.visitMethodInsn(INVOKESTATIC, client, "access$000", "(L" + client + ";)Lorg/apache/commons/pool2/impl/GenericObjectPool;", false);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/commons/pool2/impl/GenericObjectPool", "returnObject", "(Ljava/lang/Object;)V", false);
        mv.visitJumpInsn(GOTO, l5);
        mv.visitLabel(l2);
        mv.visitFrame(Opcodes.F_FULL, 2, new Object[]{innerClassName, thrift + "$Client"}, 1, new Object[]{"java/lang/Exception"});
        mv.visitVarInsn(ASTORE, 2);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitTypeInsn(INSTANCEOF, "org/apache/thrift/transport/TTransportException");
        Label l6 = new Label();
        mv.visitJumpInsn(IFNE, l6);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitTypeInsn(INSTANCEOF, "java/net/SocketException");
        mv.visitJumpInsn(IFNE, l6);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitTypeInsn(INSTANCEOF, "java/net/SocketTimeoutException");
        Label l7 = new Label();
        mv.visitJumpInsn(IFEQ, l7);
        mv.visitLabel(l6);
        mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{"java/lang/Exception"}, 0, null);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitJumpInsn(IFNULL, l7);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, innerClassName, "this$1", "L" + client + ";");
        mv.visitMethodInsn(INVOKESTATIC, client, "access$000", "(L" + client + ";)Lorg/apache/commons/pool2/impl/GenericObjectPool;", false);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/commons/pool2/impl/GenericObjectPool", "invalidateObject", "(Ljava/lang/Object;)V", false);
        mv.visitInsn(ACONST_NULL);
        mv.visitVarInsn(ASTORE, 1);
        mv.visitLabel(l7);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitInsn(ATHROW);
        mv.visitLabel(l3);
        mv.visitFrame(Opcodes.F_FULL, 2, new Object[]{innerClassName, thrift + "$Client"}, 1, new Object[]{"java/lang/Throwable"});
        mv.visitVarInsn(ASTORE, 3);
        mv.visitLabel(l4);
        mv.visitVarInsn(ALOAD, 1);
        Label l8 = new Label();
        mv.visitJumpInsn(IFNULL, l8);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, innerClassName, "this$1", "L" + client + ";");
        mv.visitMethodInsn(INVOKESTATIC, client, "access$000", "(L" + client + ";)Lorg/apache/commons/pool2/impl/GenericObjectPool;", false);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/commons/pool2/impl/GenericObjectPool", "returnObject", "(Ljava/lang/Object;)V", false);
        mv.visitLabel(l8);
        mv.visitFrame(Opcodes.F_APPEND, 2, new Object[]{Opcodes.TOP, "java/lang/Throwable"}, 0, null);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitInsn(ATHROW);
        mv.visitLabel(l5);
        mv.visitFrame(Opcodes.F_CHOP, 2, null, 0, null);
        mv.visitInsn(RETURN);
        mv.visitMaxs(2, 4);
        mv.visitEnd();
    }

    private void createCallMethod(
            ClassWriter writer, MethodNode method, String outClass, String thrift, String innerClass, String signature) {

        String desc = JAVATOJVM.get(Type.getReturnType(method.desc).getClassName());
        if (desc == null) {
            desc = Type.getReturnType(method.desc).getDescriptor();
        }

        System.out.println("desc = " + desc);

        MethodVisitor intervalMv = writer.visitMethod(
                ACC_PUBLIC,
                "call",
                "()" + desc,
                "()" + signature,
                new String[]{"java/lang/Exception"}
        );

        intervalMv.visitCode();
        Label l0 = new Label();
        Label l1 = new Label();
        Label l2 = new Label();
        intervalMv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
        Label l3 = new Label();
        intervalMv.visitTryCatchBlock(l0, l1, l3, null);
        Label l4 = new Label();
        intervalMv.visitTryCatchBlock(l2, l4, l3, null);
        intervalMv.visitInsn(ACONST_NULL);
        intervalMv.visitVarInsn(ASTORE, 1);
        intervalMv.visitLabel(l0);
        intervalMv.visitVarInsn(ALOAD, 0);
        intervalMv.visitFieldInsn(GETFIELD, innerClass, "this$1", "L" + outClass + ";");
        intervalMv.visitMethodInsn(INVOKESTATIC, outClass, "access$000", "(L" + outClass + ";)Lorg/apache/commons/pool2/impl/GenericObjectPool;", false);
        intervalMv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/commons/pool2/impl/GenericObjectPool", "borrowObject", "()Ljava/lang/Object;", false);
        intervalMv.visitTypeInsn(CHECKCAST, thrift + "$Client");
        intervalMv.visitVarInsn(ASTORE, 1);
        intervalMv.visitVarInsn(ALOAD, 1);

        for (LocalVariableNode localVariable : method.localVariables) {
            if (!"this".equals(localVariable.name)) {
                intervalMv.visitVarInsn(ALOAD, 0);
                intervalMv.visitFieldInsn(GETFIELD, innerClass, "val$" + localVariable.name, localVariable.desc);
            }
        }

        intervalMv.visitMethodInsn(INVOKEVIRTUAL, thrift + "$Client", method.name, method.desc, false);

        String javaType = JAVATOOBJECT.get(Type.getReturnType(method.desc).getClassName());
        if (javaType != null) {
            intervalMv.visitMethodInsn(INVOKESTATIC,
                    javaType,
                    "valueOf",
                    "(" + Type.getReturnType(method.desc).getDescriptor() + ")" + JAVATOJVM.get(Type.getReturnType(method.desc).getClassName()),
                    false
            );
        } else {
            javaType = Type.getReturnType(method.desc).getClassName().replace(".", "/");
            intervalMv.visitTypeInsn(CHECKCAST, javaType);
        }

        intervalMv.visitVarInsn(ASTORE, 2);
        intervalMv.visitLabel(l1);
        intervalMv.visitVarInsn(ALOAD, 1);
        Label l5 = new Label();
        intervalMv.visitJumpInsn(IFNULL, l5);
        intervalMv.visitVarInsn(ALOAD, 0);
        intervalMv.visitFieldInsn(GETFIELD, innerClass, "this$1", "L" + outClass + ";");
        intervalMv.visitMethodInsn(INVOKESTATIC, outClass, "access$000", "(L" + outClass + ";)Lorg/apache/commons/pool2/impl/GenericObjectPool;", false);
        intervalMv.visitVarInsn(ALOAD, 1);
        intervalMv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/commons/pool2/impl/GenericObjectPool", "returnObject", "(Ljava/lang/Object;)V", false);
        intervalMv.visitLabel(l5);
        intervalMv.visitFrame(Opcodes.F_APPEND, 2, new Object[]{thrift + "$Client", javaType}, 0, null);
        intervalMv.visitVarInsn(ALOAD, 2);
        intervalMv.visitInsn(ARETURN);
        intervalMv.visitLabel(l2);
        intervalMv.visitFrame(Opcodes.F_FULL, 2, new Object[]{innerClass, thrift + "$Client"}, 1, new Object[]{"java/lang/Exception"});
        intervalMv.visitVarInsn(ASTORE, 2);
        intervalMv.visitVarInsn(ALOAD, 2);
        intervalMv.visitTypeInsn(INSTANCEOF, "org/apache/thrift/transport/TTransportException");
        Label l6 = new Label();
        intervalMv.visitJumpInsn(IFNE, l6);
        intervalMv.visitVarInsn(ALOAD, 2);
        intervalMv.visitTypeInsn(INSTANCEOF, "java/net/SocketException");
        intervalMv.visitJumpInsn(IFNE, l6);
        intervalMv.visitVarInsn(ALOAD, 2);
        intervalMv.visitTypeInsn(INSTANCEOF, "java/net/SocketTimeoutException");
        Label l7 = new Label();
        intervalMv.visitJumpInsn(IFEQ, l7);
        intervalMv.visitLabel(l6);
        intervalMv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{"java/lang/Exception"}, 0, null);
        intervalMv.visitVarInsn(ALOAD, 1);
        intervalMv.visitJumpInsn(IFNULL, l7);
        intervalMv.visitVarInsn(ALOAD, 0);
        intervalMv.visitFieldInsn(GETFIELD, innerClass, "this$1", "L" + outClass + ";");
        intervalMv.visitMethodInsn(INVOKESTATIC, outClass, "access$000", "(L" + outClass + ";)Lorg/apache/commons/pool2/impl/GenericObjectPool;", false);
        intervalMv.visitVarInsn(ALOAD, 1);
        intervalMv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/commons/pool2/impl/GenericObjectPool", "invalidateObject", "(Ljava/lang/Object;)V", false);
        intervalMv.visitInsn(ACONST_NULL);
        intervalMv.visitVarInsn(ASTORE, 1);
        intervalMv.visitLabel(l7);
        intervalMv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        intervalMv.visitVarInsn(ALOAD, 2);
        intervalMv.visitInsn(ATHROW);
        intervalMv.visitLabel(l3);
        intervalMv.visitFrame(Opcodes.F_FULL, 2, new Object[]{innerClass, thrift + "$Client"}, 1, new Object[]{"java/lang/Throwable"});
        intervalMv.visitVarInsn(ASTORE, 3);
        intervalMv.visitLabel(l4);
        intervalMv.visitVarInsn(ALOAD, 1);
        Label l8 = new Label();
        intervalMv.visitJumpInsn(IFNULL, l8);
        intervalMv.visitVarInsn(ALOAD, 0);
        intervalMv.visitFieldInsn(GETFIELD, innerClass, "this$1", "L" + outClass + ";");
        intervalMv.visitMethodInsn(INVOKESTATIC, outClass, "access$000", "(L" + outClass + ";)Lorg/apache/commons/pool2/impl/GenericObjectPool;", false);
        intervalMv.visitVarInsn(ALOAD, 1);
        intervalMv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/commons/pool2/impl/GenericObjectPool", "returnObject", "(Ljava/lang/Object;)V", false);
        intervalMv.visitLabel(l8);
        intervalMv.visitFrame(Opcodes.F_APPEND, 2, new Object[]{Opcodes.TOP, "java/lang/Throwable"}, 0, null);
        intervalMv.visitVarInsn(ALOAD, 3);
        intervalMv.visitInsn(ATHROW);
        intervalMv.visitMaxs(3, 4);
        intervalMv.visitEnd();

        //bridge
        intervalMv = writer.visitMethod(
                ACC_PUBLIC | ACC_BRIDGE | ACC_SYNTHETIC,
                "call",
                "()Ljava/lang/Object;",
                null,
                new String[]{"java/lang/Exception"}
        );
        intervalMv.visitCode();
        intervalMv.visitVarInsn(ALOAD, 0);
        intervalMv.visitMethodInsn(
                INVOKEVIRTUAL,
                innerClass,
                "call",
                "()" + desc,
                false
        );
        intervalMv.visitInsn(ARETURN);
        intervalMv.visitMaxs(1, 1);
        intervalMv.visitEnd();
    }

    void createInvokeInnerClass(
            MethodNode method, String outClass, String thrift, String innerClass, File outputDirectory) throws IOException {

        ClassWriter writer = generateWriter(ClassWriter.COMPUTE_MAXS);

        System.out.println("@@@begin to create inner class@@@");

        if ("V".equalsIgnoreCase(Type.getReturnType(method.desc).getDescriptor())) {
            //需要制定当前类的desc和sig，sig指示当前类实现了那些接口和继承类，如果全部都是基本类，那么就设置为null就可以了
            writer.visit(
                    V1_8,
                    ACC_SUPER,
                    innerClass,
                    null,
                    "java/lang/Object",
                    new String[]{"net/jodah/failsafe/function/CheckedRunnable"}
            );

            //需要指定一个外部类
            writer.visitOuterClass(outClass, method.name, method.desc);
            writer.visitInnerClass(outClass, StringUtils.split(outClass, '$')[0], StringUtils.split(outClass, '$')[1], 0);
            writer.visitInnerClass(innerClass, null, null, 0);
            writer.visitInnerClass(thrift + "$Client", thrift, "Client", ACC_PUBLIC + ACC_STATIC);

            System.out.println("@@@bgein create field");
            //创建内部类字段
            createFiled(writer, method, outClass);
            System.out.println("@@@end create field");

            System.out.println("@@@bgein create cons field");
            //创建内部类构造方法
            createInnerConstructorMethod(writer, method, outClass, innerClass);
            System.out.println("@@@end create cons field");

            System.out.println("@@@bgein create run method");
            //创建run方法
            createRunMethod(writer, method, outClass, thrift, innerClass);
            System.out.println("@@@end create run method");
        } else {
            String signature = JAVATOJVM.get(Type.getReturnType(method.desc).getClassName());
            if (signature == null) {
                if (method.signature == null) {
                    signature = Type.getReturnType(method.desc).getDescriptor();
                } else {
                    signature = StringUtils.substring(
                            method.signature, StringUtils.lastIndexOf(method.signature, ")") + 1
                    );
                }
            }

            System.out.println("signature = " + signature);

            writer.visit(
                    V1_8,
                    ACC_SUPER,
                    innerClass,
                    "Ljava/lang/Object;Ljava/util/concurrent/Callable<" + signature + ">;",
                    "java/lang/Object",
                    new String[]{"java/util/concurrent/Callable"}
            );

            writer.visitOuterClass(outClass, method.name, method.desc);
            writer.visitInnerClass(outClass, StringUtils.split(outClass, '$')[0], StringUtils.split(outClass, '$')[1], 0);
            writer.visitInnerClass(innerClass, null, null, 0);
            writer.visitInnerClass(thrift + "$Client", thrift, "Client", ACC_PUBLIC + ACC_STATIC);

            System.out.println("@@@bgein create field");
            //定义内部类字段
            createFiled(writer, method, outClass);
            System.out.println("@@@end create field");

            System.out.println("@@@bgein create cons field");
            //定义构造方法
            createInnerConstructorMethod(writer, method, outClass, innerClass);
            System.out.println("@@@end create cons field");

            System.out.println("@@@bgein create call field");
            //call方法回调
            createCallMethod(writer, method, outClass, thrift, innerClass, signature);
            System.out.println("@@@end create call field");
        }

        System.out.println("@@@end to create inner class@@@");
        writer.visitEnd();
        innerClass = innerClass + ".class";
        Files.write(Paths.get(outputDirectory.getAbsolutePath(), innerClass.split("/")), writer.toByteArray());
    }

    void createMethod(ClassWriter classWriter, MethodNode method, String outClass, String innerClass) {
        System.out.println("@@@begin to create invoke method@@@");
        MethodVisitor methodVisitor;
        methodVisitor = classWriter.visitMethod(
                method.access,
                method.name,
                method.desc,
                method.signature,
                method.exceptions.toArray(new String[method.exceptions.size()])
        );
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitFieldInsn(GETFIELD, outClass, "executor", "Lnet/jodah/failsafe/SyncFailsafe;");

        //开始调用内部类
        if ("V".equalsIgnoreCase(Type.getReturnType(method.desc).getDescriptor())) {
            methodVisitor.visitTypeInsn(NEW, innerClass);
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitVarInsn(ALOAD, 0);

            int variableIndex = 0;
            for (LocalVariableNode localVariable : method.localVariables) {
                if (!"this".equals(localVariable.name)) {
                    Integer value = LOADS.get(localVariable.desc);
                    if (value == null) {
                        value = Opcodes.ALOAD;
                    }

                    if ("D".equalsIgnoreCase(localVariable.desc) || "J".equalsIgnoreCase(localVariable.desc)) {
                        variableIndex++;
                        methodVisitor.visitVarInsn(value, variableIndex);
                        variableIndex++;
                    } else {
                        variableIndex++;
                        methodVisitor.visitVarInsn(value, variableIndex);
                    }
                }
            }

            StringBuilder sb = new StringBuilder(method.desc);
            sb.insert(1, "L" + outClass + ";");
            String innerMethod = StringUtils.substring(
                    sb.toString(), 0, StringUtils.lastIndexOf(sb.toString(), ")") + 1) + "V";

            System.out.println("innerMethod = " + innerMethod);

            methodVisitor.visitMethodInsn(INVOKESPECIAL, innerClass, "<init>", innerMethod, false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "net/jodah/failsafe/SyncFailsafe", "run", "(Lnet/jodah/failsafe/function/CheckedRunnable;)V", false);
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(5, 2);
            methodVisitor.visitEnd();
        } else {
            methodVisitor.visitTypeInsn(NEW, innerClass);
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitVarInsn(ALOAD, 0);

            int variableIndex = 0;
            for (LocalVariableNode localVariable : method.localVariables) {
                if (!"this".equals(localVariable.name)) {
                    Integer value = LOADS.get(localVariable.desc);
                    if (value == null) {
                        value = Opcodes.ALOAD;
                    }

                    if ("D".equalsIgnoreCase(localVariable.desc) || "J".equalsIgnoreCase(localVariable.desc)) {
                        variableIndex++;
                        methodVisitor.visitVarInsn(value, variableIndex);
                        variableIndex++;
                    } else {
                        variableIndex++;
                        methodVisitor.visitVarInsn(value, variableIndex);
                    }
                }
            }

            StringBuilder sb = new StringBuilder(method.desc);
            sb.insert(1, "L" + outClass + ";");
            String innerMethod = StringUtils.substring(
                    sb.toString(), 0, StringUtils.lastIndexOf(sb.toString(), ")") + 1) + "V";
            System.out.println("innerMethod = " + innerMethod);

            methodVisitor.visitMethodInsn(INVOKESPECIAL, innerClass, "<init>", innerMethod, false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "net/jodah/failsafe/SyncFailsafe", "get", "(Ljava/util/concurrent/Callable;)Ljava/lang/Object;", false);

            String signature = JAVATOJVM.get(Type.getReturnType(method.desc).getClassName());
            if (signature != null) {
                methodVisitor.visitTypeInsn(CHECKCAST, JAVATOOBJECT.get(Type.getReturnType(method.desc).getClassName()));
                methodVisitor.visitMethodInsn(
                        INVOKEVIRTUAL,
                        JAVATOOBJECT.get(Type.getReturnType(method.desc).getClassName()),
                        Type.getReturnType(method.desc).getClassName() + "Value", "()" + Type.getReturnType(method.desc).getDescriptor(),
                        false
                );
                methodVisitor.visitInsn(RETURNS.get(Type.getReturnType(method.desc).getDescriptor()));
                methodVisitor.visitMaxs(6, 3);
                methodVisitor.visitEnd();
            } else {
                methodVisitor.visitTypeInsn(CHECKCAST, Type.getReturnType(method.desc).getClassName().replace(".", "/"));
                methodVisitor.visitInsn(ARETURN);
                methodVisitor.visitMaxs(6, 3);
                methodVisitor.visitEnd();
            }
        }
        System.out.println("@@@end to create invoke method@@@");
    }
}
