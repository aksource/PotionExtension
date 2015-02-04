package ak.potionextension.asm;

import com.google.common.collect.Sets;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.*;

import java.util.Collection;

/**
 * Created by AKIRA on 15/02/02.
 */
public class InventoryEffectRendererTransformer implements IClassTransformer, Opcodes {
    private static final String TARGET_CLASS_NAME = "net.minecraft.client.renderer.InventoryEffectRenderer";
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!transformedName.equals(TARGET_CLASS_NAME)) {return basicClass;}
        try {
            PotionExtensionCorePlugin.LOGGER.info("Start transforming InventoryEffectRenderer Class");
            ClassReader classReader = new ClassReader(basicClass);
            ClassWriter classWriter = new ClassWriter(1);
            classReader.accept(new CustomVisitor(name, classWriter), 8);
            PotionExtensionCorePlugin.LOGGER.info("Finish transforming InventoryEffectRenderer Class");
            return classWriter.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("failed : InventoryEffectRendererTransformer loading", e);
        }
    }

    public static void removeNullPotion(Collection<PotionEffect> activePotionEffects) {
        Collection<PotionEffect> removeEffects = Sets.newHashSet();
        for (PotionEffect pe : activePotionEffects) {
            int id = pe.getPotionID();
            if (Potion.potionTypes[id] == null) {
                removeEffects.add(pe);
            }
        }
        activePotionEffects.removeAll(removeEffects);
    }

    /*Custom ClassVisitor
    * visitMethodでメソッドを一から書き直すことが出来る。*/
    class CustomVisitor extends ClassVisitor {
        //難読化後のクラス名。FMLDeobfuscatingRemapper.INSTANCE.mapMethodNameを使う際に使用。
        String owner;
        public CustomVisitor(String owner ,ClassVisitor cv) {
            super(org.objectweb.asm.Opcodes.ASM4, cv);
            this.owner = owner;
        }
        static final String TARGET_METHOD_NAME1 = "func_147044_g";//func_147044_g
        static final String TARGET_METHOD_NAME_DEBUG1 = "func_147044_g";
        static final String TARGET_METHOD_DESC1 = "()V";//method description

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            //func_147044_gメソッドの書き換え
            if ((TARGET_METHOD_NAME1.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc))
                    || TARGET_METHOD_NAME_DEBUG1.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc)))
                    && TARGET_METHOD_DESC1.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(desc))) {
                PotionExtensionCorePlugin.LOGGER.info("Transforming func_147044_g method");
                return new CustomMethodVisitor1(this.api, super.visitMethod(access, name, desc, signature, exceptions));
            }
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }

    /*Custom MethodVisitor
     * visit**Methodで、InsnNodeの入れ替えや、追加等出来る。*/
    class CustomMethodVisitor1 extends MethodVisitor {
        public CustomMethodVisitor1(int api, MethodVisitor mv) {
            super(api, mv);
        }
        //visitFieldInsnメソッドの1回めの呼び出しで処理するためのフラグ
        boolean check = false;

        @Override
        public void visitVarInsn(int opcode, int var) {
            super.visitVarInsn(opcode, var);
            if (!check && opcode == ASTORE && var == 4) {
                check = true;
                PotionExtensionCorePlugin.LOGGER.info("onUpdate:check null potion.");
                super.visitVarInsn(ALOAD, var);
                super.visitMethodInsn(INVOKESTATIC, "ak/potionextension/asm/InventoryEffectRendererTransformer", "removeNullPotion", "(Ljava/util/Collection;)V", false);
            }
        }
    }
}
