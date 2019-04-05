package al132.alchemistry.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class AlchemistryTransformer implements IClassTransformer {

    private static String className = "net.minecraft.client.renderer.RenderItem";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals(className)) {
            AlchemistryCore.LOG.info("Transforming " + className + "...");
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(basicClass);
            classReader.accept(classNode, 0);
            MethodNode renderItemOverlayNode = null;
            for (MethodNode m : classNode.methods) {
                if (m.name.equals(AlchemistryCore.renderItemOverlay)) {
                    renderItemOverlayNode = m;
                    break;
                }
            }
            if (renderItemOverlayNode != null) {
                InsnList list = new InsnList();
                list.add(new VarInsnNode(Opcodes.ALOAD, 1));
                list.add(new VarInsnNode(Opcodes.ALOAD, 2));
                list.add(new VarInsnNode(Opcodes.ILOAD, 3));
                list.add(new VarInsnNode(Opcodes.ILOAD, 4));
                list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "al132/alchemistry/core/AlchemistryCoreUtils",
                        "renderAbbreviation", "(Ljava/lang/Object;Ljava/lang/Object;II)V", false));
                renderItemOverlayNode.instructions.insertBefore(renderItemOverlayNode.instructions.getFirst(), list);
                CustomClassWriter writer = new CustomClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                classNode.accept(writer);
                AlchemistryCore.LOG.info("Successfully transformed " + className);
                return writer.toByteArray();
            }
            AlchemistryCore.LOG.info("Failed to transform " + className);
            return basicClass;
        }
        return basicClass;
    }
}
