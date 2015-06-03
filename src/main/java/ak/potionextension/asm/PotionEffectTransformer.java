package ak.potionextension.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.*;
import org.objectweb.asm.util.CheckClassAdapter;

import java.io.PrintWriter;

/**
 * Created by A.K. on 14/07/07.
 */
public class PotionEffectTransformer implements IClassTransformer, Opcodes {

    private static final String TARGET_CLASS_NAME = "net.minecraft.potion.PotionEffect";
    private boolean isDeobfEnvironment;
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!transformedName.equals(TARGET_CLASS_NAME)) {return basicClass;}
        isDeobfEnvironment = name.equals(TARGET_CLASS_NAME);
        try {
            PotionExtensionCorePlugin.outputModifiedClassFile(basicClass, "PotionEffect");
            ClassReader classReader = new ClassReader(basicClass);
            ClassWriter classWriter = new ClassWriter(1);
            classReader.accept(new CustomVisitor(name, classWriter), 8);
//            CheckClassAdapter.verify(classReader, true, new PrintWriter("PotionEffect-mod"));
            return PotionExtensionCorePlugin.outputModifiedClassFile(classWriter.toByteArray(), "PotionEffect-mod");
        } catch (Exception e) {
            throw new RuntimeException("failed : PotionEffectTransformer loading", e);
        }
    }
    /*Custom ClassVisitor
    * visitMethodでメソッドを一から書き直すことが出来る。*/
    class CustomVisitor extends ClassVisitor {
        //難読化後のクラス名。FMLDeobfuscatingRemapper.INSTANCE.mapMethodNameを使う際に使用。
        String owner;
        public CustomVisitor(String owner ,ClassVisitor cv) {
            super(Opcodes.ASM4, cv);
            this.owner = owner;
        }
        static final String TARGET_METHOD_NAME1 = "func_76455_a";//onUpdate
        static final String TARGET_METHOD_NAME_DEBUG1 = "onUpdate";
        static final String TARGET_METHOD_DESC1 = "(Lnet/minecraft/entity/EntityLivingBase;)Z";//method description

        static final String TARGET_METHOD_NAME2 = "func_82722_b";//readCustomPotionEffectFromNBT
        static final String TARGET_METHOD_NAME_DEBUG2 = "readCustomPotionEffectFromNBT";
        static final String TARGET_METHOD_DESC2 = "(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/potion/PotionEffect;";//method description

        static final String TARGET_METHOD_NAME3 = "func_76456_a";//getPotionID
        static final String TARGET_METHOD_NAME_DEBUG3 = "getPotionID";
        static final String TARGET_METHOD_DESC3 = "()I";//method description

        static final String TARGET_METHOD_NAME4 = "func_76457_b";//performEffect
        static final String TARGET_METHOD_NAME_DEBUG4 = "performEffect";
        static final String TARGET_METHOD_DESC4 = "(Lnet/minecraft/entity/EntityLivingBase;)V";//method description

        static final String TARGET_METHOD_NAME5 = "func_76453_d";//getEffectName
        static final String TARGET_METHOD_NAME_DEBUG5 = "getEffectName";
        static final String TARGET_METHOD_DESC5 = "()Ljava/lang/String;";//method description

        static final String TARGET_METHOD_NAME6 = "toString";//toString
        static final String TARGET_METHOD_NAME_DEBUG6 = "toString";
        static final String TARGET_METHOD_DESC6 = "()Ljava/lang/String;";//method description

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            //onUpdateメソッドの書き換え
            if ((TARGET_METHOD_NAME1.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc))
                    || TARGET_METHOD_NAME_DEBUG1.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc)))
                    && TARGET_METHOD_DESC1.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(desc))) {
                return new InsertChangeByteMethodVisitor(this.api, super.visitMethod(access, name, desc, signature, exceptions));
//                return this.rewriteOnUpdate(super.visitMethod(access, name, desc, signature, exceptions));
            }
            //readCustomPotionEffectFromNBTメソッドの書き換え
            if ((TARGET_METHOD_NAME2.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc))
                    || TARGET_METHOD_NAME_DEBUG2.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc)))
                    && TARGET_METHOD_DESC2.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(desc))) {
                return new ReadFromNBTMethodVisitor(this.api, super.visitMethod(access, name, desc, signature, exceptions));
            }
            //getPotionIDメソッドの書き換え
            if ((TARGET_METHOD_NAME3.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc))
                    || TARGET_METHOD_NAME_DEBUG3.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc)))
                    && TARGET_METHOD_DESC3.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(desc))) {
                return new InsertChangeByteMethodVisitor(this.api, super.visitMethod(access, name, desc, signature, exceptions));
//                return new GetPotionIDMethodVisitor(this.api, super.visitMethod(access, name, desc, signature, exceptions));
            }
            //performEffectメソッドの書き換え
            if ((TARGET_METHOD_NAME4.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc))
                    || TARGET_METHOD_NAME_DEBUG4.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc)))
                    && TARGET_METHOD_DESC4.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(desc))) {
                return new InsertChangeByteMethodVisitor(this.api, super.visitMethod(access, name, desc, signature, exceptions));
            }
            //getEffectNameメソッドの書き換え
            if ((TARGET_METHOD_NAME5.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc))
                    || TARGET_METHOD_NAME_DEBUG5.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc)))
                    && TARGET_METHOD_DESC5.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(desc))) {
                return new InsertChangeByteMethodVisitor(this.api, super.visitMethod(access, name, desc, signature, exceptions));
            }
            //toStringメソッドの書き換え
            if ((TARGET_METHOD_NAME6.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc))
                    || TARGET_METHOD_NAME_DEBUG6.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc)))
                    && TARGET_METHOD_DESC6.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(desc))) {
                return new InsertChangeByteMethodVisitor(this.api, super.visitMethod(access, name, desc, signature, exceptions));
            }
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }

    /*Custom MethodVisitor
    * visit**Methodで、InsnNodeの入れ替えや、追加等出来る。*/
    class InsertChangeByteMethodVisitor extends MethodVisitor {
        public InsertChangeByteMethodVisitor(int api, MethodVisitor mv) {
            super(api, mv);
        }
        //メソッド最初に-128~127を0~255に変換する。
        @Override
        public void visitCode() {
            super.visitCode();
            Label l0 = new Label();
            super.visitLabel(l0);
            super.visitLineNumber(124, l0);
            super.visitVarInsn(ALOAD, 0);
            super.visitVarInsn(ALOAD, 0);
            super.visitFieldInsn(GETFIELD, "net/minecraft/potion/PotionEffect", (isDeobfEnvironment) ? "potionID" : "field_76462_a", "I");
            super.visitIntInsn(SIPUSH, 256);
            super.visitInsn(IADD);
            super.visitIntInsn(SIPUSH, 256);
            super.visitInsn(IREM);
            super.visitFieldInsn(PUTFIELD, "net/minecraft/potion/PotionEffect", (isDeobfEnvironment) ? "potionID" : "field_76462_a", "I");
        }

        //new
/*        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            super.visitFieldInsn(opcode, owner, name, desc);
            String srgName = FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(owner, name, desc);
            if (opcode == GETFIELD && (srgName.equals(TARGET_FIELD_DEV) || srgName.equals(TARGET_FIELD)) && desc.equals("I") && !check) {
                check = true;
                PotionExtensionCorePlugin.LOGGER.debug("onUpdate:change id in [0 - 255]");
                //256をスタック
                super.visitIntInsn(SIPUSH, 256);
                //スタックされた２つの数字を加算する
                super.visitInsn(IADD);
                //加算された数字がスタックされる。
                //もう一度256をスタック
                super.visitIntInsn(SIPUSH, 256);
                //スタックされている２つの数字のうち、あとの数字で最初の数字の剰余をとる
                super.visitInsn(IREM);
                //剰余がスタックされる。
            }
        }*/
    }

    class ReadFromNBTMethodVisitor extends MethodVisitor {
        public ReadFromNBTMethodVisitor(int api, MethodVisitor mv) {
            super(api, mv);
        }

        @Override
        public void visitLineNumber(int line, Label start) {
            super.visitLineNumber(line, start);
            if (line == 211) {
                super.visitVarInsn(ILOAD, 1);
                super.visitIntInsn(SIPUSH, 256);
                super.visitInsn(IADD);
                super.visitIntInsn(SIPUSH, 256);
                super.visitInsn(IREM);
                super.visitVarInsn(ISTORE, 1);
                Label l2 = new Label();
                super.visitLabel(l2);
                super.visitLineNumber(217, l2);
            }
        }
/*        //visitMethodInsnが複数あるため、直前のfieldNameを保存する。
        String fieldName = "";
        @Override
        public void visitLdcInsn(Object cst) {
            if (cst.equals("Id")) {
                fieldName = (String)cst;
            }
            super.visitLdcInsn(cst);
        }
        //識別用description
        static final String TARGET_DESC = "(Ljava/lang/String;)B";


        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
            //処理を割りこませる部分を判定
            if (opcode == INVOKEVIRTUAL && TARGET_DESC.equals(desc) && fieldName.equals("Id")) {
                fieldName = "";
                PotionExtensionCorePlugin.LOGGER.debug("readCustomPotionEffectFromNBT:change id in [0 - 255]");
                //256をスタック
                super.visitIntInsn(SIPUSH, 256);
                //スタックされた２つの数字を加算する
                super.visitInsn(IADD);
                //加算された数字がスタックされる。
                //もう一度256をスタック
                super.visitIntInsn(SIPUSH, 256);
                //スタックされている２つの数字のうち、あとの数字で最初の数字の剰余をとる
                super.visitInsn(IREM);
                //剰余がスタックされる。
                //代入してないが、このメソッドが呼ばれたあと、代入処理が呼ばれている。PotionEffectクラスのバイトコードを参照のこと。
            }
        }*/
    }

    class GetPotionIDMethodVisitor extends MethodVisitor {
        public GetPotionIDMethodVisitor(int api, MethodVisitor mv) {
            super(api, mv);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            //1回しか呼ばれないので、判定なしで、処理を挟み込む。
            PotionExtensionCorePlugin.LOGGER.debug("getPotionID:change id in [0 - 255]");
            super.visitFieldInsn(opcode, owner, name, desc);
            super.visitIntInsn(SIPUSH, 256);
            super.visitInsn(IADD);
            super.visitIntInsn(SIPUSH, 256);
            super.visitInsn(IREM);
            //代入してないが、このメソッドが呼ばれたあと、代入処理が呼ばれている。PotionEffectクラスのバイトコードを参照のこと。
        }
    }
}
