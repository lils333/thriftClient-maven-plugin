package com.lee;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

public class FiledGenerator extends BaseGenerator implements Opcodes {

    public void createFiled(ClassWriter classWriter,
                            int access, String name, String descriptor, String signature) {
        FieldVisitor fieldVisitor = classWriter.visitField(access, name, descriptor, signature, null);
        fieldVisitor.visitEnd();
    }
}
