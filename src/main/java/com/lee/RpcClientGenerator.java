package com.lee;

import com.google.common.io.Resources;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RpcClientGenerator extends BaseGenerator implements Opcodes {

    private FiledGenerator field = new FiledGenerator();
    private MethodGenerator methodGenerator = new MethodGenerator();

    //innerClass = com/lee/pool/RpcClient$Client1
    //thrift = com/lee/pool/MyFirstService(thrift 自动生成的对象)
    public void generate(String innerClass, String thrift, File outputDirectory) throws Exception {

        ClassWriter cw = generateWriter(ClassWriter.COMPUTE_MAXS);
        MethodVisitor mv;

        //主要指定signature = 当前类的实现接口和类
        cw.visit(
                V1_8,
                ACC_SUPER,
                innerClass,
                "Lorg/apache/commons/pool2/BasePooledObjectFactory<L" + thrift + "$Client;>;L" + thrift + "$Iface;Ljava/io/Closeable;",
                "org/apache/commons/pool2/BasePooledObjectFactory",
                new String[]{thrift + "$Iface", "java/io/Closeable"}
        );

        //内部类
        InputStream inputStream;
        try {
            //首先在插件执行的classpath里面来获取想对应的thrift客户端，这个时候我们需要在插件的依赖里面添加上我们需要的thrift jar包
            inputStream = Resources.getResource(thrift + "$Client.class").openStream();
        } catch (Exception e) {
            //如果没有找到，那么就去当前项目里面去获取，注意一点当前项目我们必须要使用全路径
            try {
                inputStream = Files.newInputStream(
                        Paths.get(outputDirectory.getAbsolutePath()).resolve(thrift + "$Client.class")
                );
            } catch (IOException e1) {
                throw new IllegalArgumentException("Can not find thrift innerClass " + thrift + "$Client.class", e);
            }
        }

        cw.visitInnerClass(
                innerClass,
                StringUtils.split(innerClass, '$')[0],
                StringUtils.split(innerClass, '$')[1],
                0
        );

        //ClassReader在读取完了以后，会关闭inputstream
        ClassNode node = generateClassNode(inputStream);
        int innerClassIndex = 1;

        for (MethodNode method : node.methods) {
            if (!StringUtils.startsWithIgnoreCase(method.name, SEND)
                    && !StringUtils.startsWithIgnoreCase(method.name, RECV)
                    && !"<init>".equals(method.name)
                    && !"<cinit>".equals(method.name)) {
                cw.visitInnerClass(
                        innerClass + "$" + innerClassIndex++,
                        null,
                        null,
                        0
                );
            }
        }
        cw.visitInnerClass(
                thrift + "$Client",
                thrift,
                "Client",
                ACC_PUBLIC + ACC_STATIC
        );
        cw.visitInnerClass(
                thrift + "$Iface",
                thrift,
                "Iface",
                ACC_PUBLIC + ACC_STATIC + ACC_ABSTRACT + ACC_INTERFACE
        );

        //字段
        field.createFiled(cw, ACC_PRIVATE, "isFramed", "Z", null);
        field.createFiled(cw, ACC_PRIVATE, "address", "Ljava/lang/String;", null);
        field.createFiled(cw, ACC_PRIVATE, "port", "I", null);
        field.createFiled(cw, ACC_PRIVATE, "connectionTimeout", "I", null);
        field.createFiled(cw, ACC_PRIVATE, "socketTimeout", "I", null);
        field.createFiled(
                cw,
                ACC_PRIVATE,
                "objectPool",
                "Lorg/apache/commons/pool2/impl/GenericObjectPool;",
                "Lorg/apache/commons/pool2/impl/GenericObjectPool<L" + thrift + "$Client;>;"
        );
        field.createFiled(
                cw,
                ACC_PRIVATE,
                "executor",
                "Lnet/jodah/failsafe/SyncFailsafe;",
                "Lnet/jodah/failsafe/SyncFailsafe<Ljava/lang/Object;>;"
        );
        field.createFiled(
                cw,
                ACC_FINAL + ACC_SYNTHETIC,
                "this$0",
                "L" + StringUtils.split(innerClass, '$')[0] + ";",
                null
        );

        methodGenerator.createConstructorMethod(cw, innerClass);

        System.out.println("@@@@begin to generator innerClass@@@@@");

        int innerIdx = 1;
        for (MethodNode method : node.methods) {
            if (StringUtils.startsWithIgnoreCase(method.name, SEND)
                    || StringUtils.startsWithIgnoreCase(method.name, RECV)
                    || "<init>".equals(method.name)
                    || "<cinit>".equals(method.name)) {
                continue;
            }
            String innerInvokeClass = innerClass + "$" + innerIdx++;
            methodGenerator.createMethod(cw, method, innerClass, innerInvokeClass);
            methodGenerator.createInvokeInnerClass(method, innerClass, thrift, innerInvokeClass, outputDirectory);
        }

        System.out.println("@@@@End to generator innerClass@@@@@");


        //close
        createCloseMethod(cw, innerClass);

        //create
        createCreateMethod(cw, innerClass, thrift);

        //wrap
        createWrapMethod(cw, innerClass, thrift);

        //destroyObject
        createDestroyMethod(cw, innerClass, thrift);

        //validateObject
        createValidateMethod(cw, innerClass, thrift);

        //createProtocol
        createProtocolMethod(cw, innerClass, thrift);

        //createConfig
        createConfigMethod(cw, innerClass, thrift);

        //wrap
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "wrap", "(Ljava/lang/Object;)Lorg/apache/commons/pool2/PooledObject;", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, thrift + "$Client");
            mv.visitMethodInsn(INVOKEVIRTUAL, innerClass, "wrap", "(L" + thrift + "$Client;)Lorg/apache/commons/pool2/PooledObject;", false);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }

        //create
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "create", "()Ljava/lang/Object;", null, new String[]{"java/lang/Exception"});
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, innerClass, "create", "()L" + thrift + "$Client;", false);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

        //internal class access outclass filed method
        {
            mv = cw.visitMethod(ACC_STATIC + ACC_SYNTHETIC, "access$000", "(L" + innerClass + ";)Lorg/apache/commons/pool2/impl/GenericObjectPool;", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, innerClass, "objectPool", "Lorg/apache/commons/pool2/impl/GenericObjectPool;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

        cw.visitEnd();
        innerClass = innerClass + ".class";
        Files.write(Paths.get(outputDirectory.getAbsolutePath(), innerClass.split("/")), cw.toByteArray());
    }
}