package com.lee;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

@Mojo(name = "thriftclient", defaultPhase = LifecyclePhase.COMPILE)
public class ThriftMojo extends AbstractMojo implements Opcodes {

    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
    private File outputDirectory;

    @Parameter(required = true)
    private List<String> clients;

    private RpcClientGenerator rpcClientGenerator = new RpcClientGenerator();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            //client = com.lee.thrift.client.RpcClient
            for (String rpcClient : clients) {
                Path path = Paths.get(outputDirectory.getAbsolutePath()).resolve(rpcClient.replace(".", "/") + ".class");

                ClassReader reader = new ClassReader(Files.newInputStream(path));
                ClassNode node = new ClassNode();
                reader.accept(node, 0);

                int methodIndex = 0;
                String client = rpcClient.replace(".", "/");

                for (MethodNode methodNode : node.methods) {
                    if (!"<init>".equals(methodNode.name) && !"<clinit>".equals(methodNode.name)) {
                        methodIndex++;
                        System.err.println("**********************beg*********************");
                        InsnList instructions = methodNode.instructions;
                        ListIterator<AbstractInsnNode> iterator = instructions.iterator();
                        while (iterator.hasNext()) {
                            AbstractInsnNode next = iterator.next();
                            if (next.getOpcode() == ARETURN) {
                                iterator.remove();
                            }
                        }

                        String innerClass = client + "$Client" + methodIndex;

                        instructions.add(new TypeInsnNode(NEW, innerClass));
                        instructions.add(new InsnNode(DUP));
                        instructions.add(new VarInsnNode(ALOAD, 0));
                        instructions.add(new VarInsnNode(ILOAD, 1));
                        instructions.add(new VarInsnNode(ALOAD, 2));
                        instructions.add(new VarInsnNode(ILOAD, 3));
                        instructions.add(new VarInsnNode(ILOAD, 4));
                        instructions.add(new VarInsnNode(ILOAD, 5));
                        instructions.add(new MethodInsnNode(INVOKESPECIAL, innerClass, "<init>", "(L" + client + ";ZLjava/lang/String;III)V", false));
                        instructions.add(new InsnNode(ARETURN));
                        methodNode.maxStack = 8;
                        methodNode.maxLocals = 6;

                        node.innerClasses.add(
                                new InnerClassNode(innerClass, client, "Client" + methodIndex, 0)
                        );

                        String descriptor = Type.getReturnType(methodNode.desc).getDescriptor();

                        rpcClientGenerator.generate(
                                innerClass,
                                descriptor.replace(".", "/").substring(1, descriptor.length() - 7),
                                outputDirectory
                        );

                        System.err.println("**********************end*********************");
                    }
                }

                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                node.accept(writer);
                Files.write(path, writer.toByteArray());
            }
        } catch (Exception e) {
            throw new MojoFailureException("Can not execute thrift modify code", e);
        }
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public List<String> getClients() {
        return clients;
    }

    public void setClients(List<String> clients) {
        this.clients = clients;
    }
}
