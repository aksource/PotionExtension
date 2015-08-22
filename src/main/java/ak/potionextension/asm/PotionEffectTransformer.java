package ak.potionextension.asm;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

/**
 * PotionEffectクラスを書き換えるClassTransformerクラス
 * Created by A.K. on 14/07/07.
 */
public class PotionEffectTransformer implements IClassTransformer, Opcodes {
    /** 書き換えたいクラスの完全修飾クラス名。srg名 */
    private static final String TARGET_CLASS_NAME = "net.minecraft.potion.PotionEffect";
    /** 開発環境か実環境かの判定変数 */
    private boolean isDeobfEnvironment;

    /**
     * クラスを書き換えるメソッド。<br>
     *     すべてのクラスが渡ってくるので、クラス名で判定する必要がある。<br>
     *     IClassTransformer実装
     *     {@see net.minecraft.launchwrapper.IClassTransformer}
     * @param name クラスの難読化名
     * @param transformedName クラスのsrg名
     * @param basicClass クラスのバイト配列
     * @return 書き換え後のクラスのバイト配列
     */
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        /* transformedNameが書き換えたいクラス名かどうか判定。書き換えたいクラスじゃなければそのまま返却 */
        if (!transformedName.equals(TARGET_CLASS_NAME)) {return basicClass;}
        /* 開発環境かどうか。開発環境の場合、難読化後とsrg名が一致する */
        isDeobfEnvironment = name.equals(TARGET_CLASS_NAME);
        try {
            /* デバッグ用 */
            PotionExtensionCorePlugin.outputModifiedClassFile(basicClass, "PotionEffect");
            /* クラスのバイト配列からClassReaderクラスを生成 */
            ClassReader classReader = new ClassReader(basicClass);
            /* ClassVisitor用CLassWriterクラス生成
            * 引数の意味
            * COMPUTE_MAXS(1):スタックサイズとローカル変数の最大数を計算
            * COMPUTE_FRAMES(2):Frameを計算し直す。このフラグが立つとvisitFrameメソッドやvisitMaxsメソッドが無視される
            * フラグなので、"|"で連結可能
            */
            ClassWriter classWriter = new ClassWriter(1);
            /* クラスの書き換えを実行
             *  第二引数の意味
             *  SKIP_CODE(1):書き換えたいクラスのメソッドの中身の読み込みを省略。
             *  SKIP_DEBUG(2):visitLocalVariableやvisitLineNumber等のデバッグ用メソッドを読み込まない
             *  SKIP_FRAMES(4):visitFrameメソッドを読み込まない
             *  EXPAND_FRAMES(8):visitFrameメソッドが読み込まれる
             *  フラグなので、"|"で連結可能
             */
            classReader.accept(new CustomVisitor(name, classWriter), ClassReader.EXPAND_FRAMES);
            /* デバッグ用 */
            PotionExtensionCorePlugin.outputModifiedClassFile(classWriter.toByteArray(), "PotionEffect-mod");
            /* バイト配列にして返却 */
            return classWriter.toByteArray();
        } catch (Exception e) {
            /* 書き換えの処理内でエラーが出た場合にthrowする */
            throw new RuntimeException("failed : PotionEffectTransformer loading", e);
        }
    }
    /**
     * CustomClassVisitor<br>
     *     visitMethodでメソッドを一から書き直すことが出来る。
     */
    class CustomVisitor extends ClassVisitor {
        /** 難読化後のクラス名。FMLDeobfuscatingRemapper.INSTANCE.mapMethodNameを使う際に使用。*/
        String owner;

        /**
         * コンストラクタ。クラス名をownerに設定。
         * @param owner クラス名
         * @param cv ClassVisitorインスタンス。ClassWriterが来る。
         */
        public CustomVisitor(String owner ,ClassVisitor cv) {
            super(Opcodes.ASM4, cv);
            this.owner = owner;
        }

        /** onUpdateメソッドのsrg名 */
        static final String TARGET_METHOD_NAME1 = "func_76455_a";//onUpdate
        /** onUpdateのメソッド名 */
        static final String TARGET_METHOD_NAME_DEBUG1 = "onUpdate";
        /** onUpdateメソッドのdescription文字列 */
        static final String TARGET_METHOD_DESC1 = "(Lnet/minecraft/entity/EntityLivingBase;)Z";//method description

        /** readCustomPotionEffectFromNBTのsrg名 */
        static final String TARGET_METHOD_NAME2 = "func_82722_b";//readCustomPotionEffectFromNBT
        /** readCustomPotionEffectFromNBTのメソッド名 */
        static final String TARGET_METHOD_NAME_DEBUG2 = "readCustomPotionEffectFromNBT";
        /** readCustomPotionEffectFromNBTのdescription文字列 */
        static final String TARGET_METHOD_DESC2 = "(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/potion/PotionEffect;";//method description

        /** getPotionIDのsrg名 */
        static final String TARGET_METHOD_NAME3 = "func_76456_a";//getPotionID
        /** getPotionIDのメソッド名 */
        static final String TARGET_METHOD_NAME_DEBUG3 = "getPotionID";
        /** getPotionIDのdescription */
        static final String TARGET_METHOD_DESC3 = "()I";//method description

        /** performEffectのsrg名 */
        static final String TARGET_METHOD_NAME4 = "func_76457_b";//performEffect
        /** performEffectのメソッド名 */
        static final String TARGET_METHOD_NAME_DEBUG4 = "performEffect";
        /** performEffectのdescription文字列 */
        static final String TARGET_METHOD_DESC4 = "(Lnet/minecraft/entity/EntityLivingBase;)V";//method description

        /** getEffectNameのsrg名 */
        static final String TARGET_METHOD_NAME5 = "func_76453_d";//getEffectName
        /** getEffectNameのメソッド名 */
        static final String TARGET_METHOD_NAME_DEBUG5 = "getEffectName";
        /** getEffectNameのdescription文字列 */
        static final String TARGET_METHOD_DESC5 = "()Ljava/lang/String;";//method description

        /** toStringのsrg名 */
        static final String TARGET_METHOD_NAME6 = "toString";//toString
        /** toStringのメソッド名 */
        static final String TARGET_METHOD_NAME_DEBUG6 = "toString";
        /** toStringのdescription文字列 */
        static final String TARGET_METHOD_DESC6 = "()Ljava/lang/String;";//method description

        /**
         * visitoMethodメソッド
         * クラスの全メソッドの読み込み時に呼ばれる。
         * @param access メソッドがprivateかprotectedかpublicか
         * @param name メソッド名
         * @param desc メソッドの引数及び返り値を表す文字列
         * @param signature メソッドの署名
         * @param exceptions メソッドが投げる例外
         * @return MethodVisitor
         */
        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            /*onUpdateメソッドの書き換え*/
            if ((TARGET_METHOD_NAME1.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc))
                    || TARGET_METHOD_NAME_DEBUG1.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc)))
                    && TARGET_METHOD_DESC1.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(desc))) {
                return new InsertChangeByteMethodVisitor(this.api, super.visitMethod(access, name, desc, signature, exceptions));
            }
            /*readCustomPotionEffectFromNBTメソッドの書き換え*/
            if ((TARGET_METHOD_NAME2.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc))
                    || TARGET_METHOD_NAME_DEBUG2.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc)))
                    && TARGET_METHOD_DESC2.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(desc))) {
                return new ReadFromNBTMethodVisitor(this.api, super.visitMethod(access, name, desc, signature, exceptions));
            }
            /*getPotionIDメソッドの書き換え*/
            if ((TARGET_METHOD_NAME3.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc))
                    || TARGET_METHOD_NAME_DEBUG3.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc)))
                    && TARGET_METHOD_DESC3.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(desc))) {
                return new InsertChangeByteMethodVisitor(this.api, super.visitMethod(access, name, desc, signature, exceptions));
            }
            /*performEffectメソッドの書き換え*/
            if ((TARGET_METHOD_NAME4.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc))
                    || TARGET_METHOD_NAME_DEBUG4.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc)))
                    && TARGET_METHOD_DESC4.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(desc))) {
                return new InsertChangeByteMethodVisitor(this.api, super.visitMethod(access, name, desc, signature, exceptions));
            }
            /*getEffectNameメソッドの書き換え*/
            if ((TARGET_METHOD_NAME5.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc))
                    || TARGET_METHOD_NAME_DEBUG5.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc)))
                    && TARGET_METHOD_DESC5.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(desc))) {
                return new InsertChangeByteMethodVisitor(this.api, super.visitMethod(access, name, desc, signature, exceptions));
            }
            /*toStringメソッドの書き換え*/
            if ((TARGET_METHOD_NAME6.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc))
                    || TARGET_METHOD_NAME_DEBUG6.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc)))
                    && TARGET_METHOD_DESC6.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(desc))) {
                return new InsertChangeByteMethodVisitor(this.api, super.visitMethod(access, name, desc, signature, exceptions));
            }
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }

    /**
     * InsertChangeByteMethodVisitorクラス<br>
     *     メソッド最初に-128~127を0~255に変換する。
     */
    class InsertChangeByteMethodVisitor extends MethodVisitor {
        /** コンストラクタ */
        public InsertChangeByteMethodVisitor(int api, MethodVisitor mv) {
            super(api, mv);
        }

        /**
         * visitCodeメソッド。
         * メソッド最初に-128~127を0~255に変換する。
         */
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
    }

    /**
     * readFromNBT用MethodVisitorクラス
     */
    class ReadFromNBTMethodVisitor extends MethodVisitor {
        /** コンストラクタ */
        public ReadFromNBTMethodVisitor(int api, MethodVisitor mv) {
            super(api, mv);
        }

        /**
         * visitLineNumberメソッド
         * ID読み込み時に-128~127を0~255に変換する。
         * @param line 行数
         * @param start ラベル
         */
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
    }
}
