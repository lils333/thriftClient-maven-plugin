package com.lee;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

abstract class BaseGenerator implements Opcodes {
    static Map<String, String> JAVATOJVM = new HashMap<>();
    static Map<String, String> JAVATOOBJECT = new HashMap<>();
    static Map<String, Integer> LOADS = new HashMap<>();
    static Map<String, Integer> RETURNS = new HashMap<>();

    String SEND = "send_";
    String RECV = "recv_";

    static {
        JAVATOJVM.put("int", "Ljava/lang/Integer;");
        JAVATOJVM.put("long", "Ljava/lang/Long;");
//        JAVATOJVM.put("java.lang.String", "Ljava/lang/String;");
        JAVATOJVM.put("short", "Ljava/lang/Short;");
        JAVATOJVM.put("float", "Ljava/lang/Float;");
        JAVATOJVM.put("double", "Ljava/lang/Double;");
        JAVATOJVM.put("char", "Ljava/lang/Character;");
        JAVATOJVM.put("boolean", "Ljava/lang/Boolean;");
        JAVATOJVM.put("byte", "Ljava/lang/Byte;");

        JAVATOOBJECT.put("int", "java/lang/Integer");
        JAVATOOBJECT.put("long", "java/lang/Long");
//        JAVATOOBJECT.put("java.lang.String", "java/lang/String");
        JAVATOOBJECT.put("short", "java/lang/Short");
        JAVATOOBJECT.put("float", "java/lang/Float");
        JAVATOOBJECT.put("double", "java/lang/Double");
        JAVATOOBJECT.put("char", "java/lang/Character");
        JAVATOOBJECT.put("boolean", "java/lang/Boolean");
        JAVATOOBJECT.put("byte", "java/lang/Byte");

        LOADS.put("I", Opcodes.ILOAD);
        LOADS.put("B", Opcodes.ILOAD);
        LOADS.put("S", Opcodes.ILOAD);
        LOADS.put("C", Opcodes.ILOAD);
        LOADS.put("Z", Opcodes.ILOAD);
        LOADS.put("F", Opcodes.FLOAD);
        LOADS.put("D", Opcodes.DLOAD);
        LOADS.put("J", Opcodes.LLOAD);

        RETURNS.put("I", Opcodes.IRETURN);
        RETURNS.put("B", Opcodes.IRETURN);
        RETURNS.put("S", Opcodes.IRETURN);
        RETURNS.put("C", Opcodes.IRETURN);
        RETURNS.put("Z", Opcodes.IRETURN);
        RETURNS.put("F", Opcodes.FRETURN);
        RETURNS.put("D", Opcodes.DRETURN);
        RETURNS.put("J", Opcodes.LRETURN);
    }

    //interface does not have localvariable in the method
    //只有实现类里面的方法 localvariables变量才不会为空
    ClassNode generateClassNode(InputStream thriftClient) throws IOException {
        ClassReader reader = new ClassReader(thriftClient);
        ClassNode node = new ClassNode(ASM7);
        reader.accept(node, 0);
        return node;
    }

    //0, ClassWriter.COMPUTE_MAXS , ClassWriter.COMPUTE_FRAMES
    ClassWriter generateWriter(int flags) {
        return new ClassWriter(flags);
    }

    void createCloseMethod(ClassWriter cw, String client) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "close", "()V", null, new String[]{"java/io/IOException"});
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, client, "objectPool", "Lorg/apache/commons/pool2/impl/GenericObjectPool;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/commons/pool2/impl/GenericObjectPool", "close", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    void createCreateMethod(ClassWriter cw, String client, String thrift) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "create", "()L" + thrift + "$Client;", null, new String[]{"java/lang/Exception"});
        mv.visitCode();
        mv.visitTypeInsn(NEW, thrift + "$Client");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, client, "isFramed", "Z");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, client, "address", "Ljava/lang/String;");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, client, "port", "I");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, client, "connectionTimeout", "I");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, client, "socketTimeout", "I");
        mv.visitMethodInsn(INVOKESPECIAL, client, "createProtocol", "(ZLjava/lang/String;III)Lorg/apache/thrift/protocol/TProtocol;", false);
        mv.visitMethodInsn(INVOKESPECIAL, thrift + "$Client", "<init>", "(Lorg/apache/thrift/protocol/TProtocol;)V", false);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(8, 1);
        mv.visitEnd();
    }

    void createWrapMethod(ClassWriter cw, String client, String thrift) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "wrap", "(L" + thrift + "$Client;)Lorg/apache/commons/pool2/PooledObject;",
                "(L" + thrift + "$Client;)Lorg/apache/commons/pool2/PooledObject<L" + thrift + "$Client;>;", null);
        mv.visitCode();
        mv.visitTypeInsn(NEW, "org/apache/commons/pool2/impl/DefaultPooledObject");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL,
                "org/apache/commons/pool2/impl/DefaultPooledObject", "<init>", "(Ljava/lang/Object;)V", false);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(3, 2);
        mv.visitEnd();
    }

    void createDestroyMethod(ClassWriter cw, String client, String thrift) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "destroyObject", "(Lorg/apache/commons/pool2/PooledObject;)V",
                "(Lorg/apache/commons/pool2/PooledObject<L" + thrift + "$Client;>;)V",
                new String[]{"java/lang/Exception"});
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEINTERFACE,
                "org/apache/commons/pool2/PooledObject", "getObject", "()Ljava/lang/Object;", true);
        mv.visitTypeInsn(CHECKCAST, thrift + "$Client");
        mv.visitVarInsn(ASTORE, 2);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKEVIRTUAL, thrift + "$Client", "getInputProtocol", "()Lorg/apache/thrift/protocol/TProtocol;", false);
        mv.visitVarInsn(ASTORE, 3);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/thrift/protocol/TProtocol",
                "getTransport", "()Lorg/apache/thrift/transport/TTransport;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/thrift/transport/TTransport", "close", "()V", false);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKEVIRTUAL, thrift + "$Client",
                "getOutputProtocol", "()Lorg/apache/thrift/protocol/TProtocol;", false);
        mv.visitVarInsn(ASTORE, 4);
        mv.visitVarInsn(ALOAD, 4);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/thrift/protocol/TProtocol",
                "getTransport", "()Lorg/apache/thrift/transport/TTransport;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/thrift/transport/TTransport", "close", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 5);
        mv.visitEnd();
    }

    void createValidateMethod(ClassWriter cw, String client, String thrift) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "validateObject", "(Lorg/apache/commons/pool2/PooledObject;)Z",
                "(Lorg/apache/commons/pool2/PooledObject<L" + thrift + "$Client;>;)Z", null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL, "org/apache/commons/pool2/BasePooledObjectFactory",
                "validateObject", "(Lorg/apache/commons/pool2/PooledObject;)Z", false);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }

    void createProtocolMethod(ClassWriter cw, String client, String thrift) {
        MethodVisitor mv = cw.visitMethod(ACC_PRIVATE, "createProtocol", "(ZLjava/lang/String;III)Lorg/apache/thrift/protocol/TProtocol;",
                null, new String[]{"org/apache/thrift/transport/TTransportException"});
        mv.visitCode();
        mv.visitVarInsn(ILOAD, 1);
        Label l0 = new Label();
        mv.visitJumpInsn(IFEQ, l0);
        mv.visitTypeInsn(NEW, "org/apache/thrift/transport/TFastFramedTransport");
        mv.visitInsn(DUP);
        mv.visitTypeInsn(NEW, "org/apache/thrift/transport/TSocket");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitVarInsn(ILOAD, 3);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitVarInsn(ILOAD, 5);
        mv.visitMethodInsn(INVOKESPECIAL, "org/apache/thrift/transport/TSocket", "<init>", "(Ljava/lang/String;III)V", false);
        mv.visitMethodInsn(INVOKESPECIAL, "org/apache/thrift/transport/TFastFramedTransport",
                "<init>", "(Lorg/apache/thrift/transport/TTransport;)V", false);
        mv.visitVarInsn(ASTORE, 6);
        Label l1 = new Label();
        mv.visitJumpInsn(GOTO, l1);
        mv.visitLabel(l0);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitTypeInsn(NEW, "org/apache/thrift/transport/TSocket");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitVarInsn(ILOAD, 3);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitVarInsn(ILOAD, 5);
        mv.visitMethodInsn(INVOKESPECIAL, "org/apache/thrift/transport/TSocket", "<init>", "(Ljava/lang/String;III)V", false);
        mv.visitVarInsn(ASTORE, 6);
        mv.visitLabel(l1);
        mv.visitFrame(F_APPEND, 1, new Object[]{"org/apache/thrift/transport/TTransport"}, 0, null);
        mv.visitVarInsn(ALOAD, 6);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/thrift/transport/TTransport", "open", "()V", false);
        mv.visitTypeInsn(NEW, "org/apache/thrift/protocol/TBinaryProtocol");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 6);
        mv.visitMethodInsn(INVOKESPECIAL, "org/apache/thrift/protocol/TBinaryProtocol",
                "<init>", "(Lorg/apache/thrift/transport/TTransport;)V", false);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(8, 7);
        mv.visitEnd();
    }

    void createConfigMethod(ClassWriter cw, String client, String thrift) {
        MethodVisitor mv = cw.visitMethod(ACC_PRIVATE, "createConfig", "()Lorg/apache/commons/pool2/impl/GenericObjectPoolConfig;",
                "()Lorg/apache/commons/pool2/impl/GenericObjectPoolConfig<L" + thrift + "$Client;>;", null);
        mv.visitCode();
        mv.visitTypeInsn(NEW, "org/apache/commons/pool2/impl/GenericObjectPoolConfig");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "org/apache/commons/pool2/impl/GenericObjectPoolConfig", "<init>", "()V", false);
        mv.visitVarInsn(ASTORE, 1);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitIntInsn(BIPUSH, 10);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/commons/pool2/impl/GenericObjectPoolConfig", "setMaxTotal", "(I)V", false);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ICONST_3);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/commons/pool2/impl/GenericObjectPoolConfig", "setMaxIdle", "(I)V", false);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ICONST_3);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/commons/pool2/impl/GenericObjectPoolConfig", "setMinIdle", "(I)V", false);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ICONST_1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/commons/pool2/impl/GenericObjectPoolConfig", "setBlockWhenExhausted", "(Z)V", false);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitLdcInsn(new Long(5000L));
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/commons/pool2/impl/GenericObjectPoolConfig", "setEvictorShutdownTimeoutMillis", "(J)V", false);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ICONST_0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/commons/pool2/impl/GenericObjectPoolConfig", "setFairness", "(Z)V", false);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ICONST_0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/commons/pool2/impl/GenericObjectPoolConfig", "setJmxEnabled", "(Z)V", false);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ICONST_0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/commons/pool2/impl/GenericObjectPoolConfig", "setLifo", "(Z)V", false);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitLdcInsn(new Long(15000L));
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/commons/pool2/impl/GenericObjectPoolConfig", "setTimeBetweenEvictionRunsMillis", "(J)V", false);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ICONST_1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/commons/pool2/impl/GenericObjectPoolConfig", "setTestWhileIdle", "(Z)V", false);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ICONST_3);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/commons/pool2/impl/GenericObjectPoolConfig", "setNumTestsPerEvictionRun", "(I)V", false);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitLdcInsn(new Long(15000L));
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/commons/pool2/impl/GenericObjectPoolConfig", "setMaxWaitMillis", "(J)V", false);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitLdcInsn(new Long(1800000L));
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/apache/commons/pool2/impl/GenericObjectPoolConfig", "setMinEvictableIdleTimeMillis", "(J)V", false);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(3, 2);
        mv.visitEnd();
    }
}
