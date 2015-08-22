package ak.potionextension.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

/**
 * Potionクラスを書き換えるClassTransformerクラス
 * Created by A.K. on 14/03/13.
 */
public class PotionArrayTransformer implements IClassTransformer, Opcodes{
    /** 書き換えたいクラスの完全修飾クラス名。srg名 */
    private static final String TARGET_CLASS_NAME = "net.minecraft.potion.Potion";//qi

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
        try {
            /* デバッグ用 */
            PotionExtensionCorePlugin.outputModifiedClassFile(basicClass, "Potion");
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
            classReader.accept(new CustomVisitor(name,classWriter), 8);
            /* デバッグ用 */
            PotionExtensionCorePlugin.outputModifiedClassFile(classWriter.toByteArray(), "Potion-mod");
            /* バイト配列にして返却 */
            return classWriter.toByteArray();
        } catch (Exception e) {
            /* 書き換えの処理内でエラーが出た場合にthrowする */
            throw new RuntimeException("failed : PotionArrayTransformer loading", e);
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
            super(Opcodes.ASM4,cv);
            this.owner = owner;
        }

        /** staticメソッドのメソッド名 */
        static final String targetMethodName = "<clinit>";//static init method
        /** staticメソッドのdescription文字列 */
        static final String targetMethodDesc = "()V";//static init method description

        /** コンストラクタのメソッド名 */
        static final String targetMethodName2 = "<init>";//init method
        /** コンストラクタのdescription文字列 */
        static final String targetMethodDesc2 = "(IZI)V";//static init method description

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
            if (targetMethodName.equals(name) && targetMethodDesc.equals(desc)) {
                //static initメソッドの時のみ、Custom MethodVisitorを生成する。
                /*通常は、メソッド頭にフックを付けたりするのに使用。
                今回はInsnNodeの入れ替えなので、CustomMethodVisitorを生成して返している。*/
                return new CustomMethodVisitor(this.api, super.visitMethod(access, name, desc, signature, exceptions));
            }

            /* コンストラクタの書き換え */
            if (targetMethodName2.equals(name) && targetMethodDesc2.equals(desc)) {
                return new CustomMethodVisitor2(this.api, super.visitMethod(access, name, desc, signature, exceptions));
            }
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }

    /**
     * CustomMethodVisitorクラス
     * Potion配列の配列数変更用
     */
    class CustomMethodVisitor extends MethodVisitor {
        /** コンストラクタ */
        public CustomMethodVisitor(int api, MethodVisitor mv) {
            super(api, mv);
        }

        /** Byteをスタックに入れるOpcode */
        static final int targetOpcode = Opcodes.BIPUSH;
        /** もともとpushされる数字（Byte制限） */
        static final int targetOperand = 32;

        /**
         * visitIntInsnメソッド
         * 整数の読み込み、書き込みで呼ばれる
         * @param opcode 指定操作
         * @param operand 指定操作時の引数
         */
        @Override
        public void visitIntInsn(int opcode, int operand) {
            if (targetOpcode == opcode && targetOperand == operand) {
                /*BIPUSH 32を、maxPotionArrayに入れ替える。*/
                /* デバッグログ */
                PotionExtensionCorePlugin.LOGGER.debug("Change BIPUSH 32 to maxPotionArray");
                /*Configから数字を持ってきて、代入したい場合、以下のように記述するとよい。
                * 第一引数：参照か、代入か。クラス変数か、インスタンス変数か。GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD.
                * 第二引数：変数持っているクラスの完全修飾名。但し,"."は"/"に変換のこと。
                * 第三引数：変数名。
                * 第四引数：変数の形名。整数型は"I"
                */
                super.visitFieldInsn(GETSTATIC, "ak/potionextension/asm/PotionExtensionCorePlugin", "maxPotionArray", "I");
            } else super.visitIntInsn(opcode, operand);
        }
    }

    /**
     * CustomMethodVisitor2クラス
     * Potionの重複登録チェック用
     */
    class CustomMethodVisitor2 extends MethodVisitor {
        /** visitFieldInsnが複数呼ばれるので、判定用 */
        boolean check = false;
        /** コンストラクタ */
        public CustomMethodVisitor2(int api, MethodVisitor mv) {
            super(api, mv);
        }

        /**
         * visitFieldInsnメソッド
         * Potionの重複チェックを挿入
         * @param opcode 指定操作
         * @param owner フィールドの所有クラス名
         * @param name フィールド名
         * @param desc フィールドの型
         */
        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            /* 差し替えたい操作でない時や、差し替え処理後はそのまま通す */
            if (check || opcode != GETSTATIC || !owner.equals("net/minecraft/potion/Potion")) {
                super.visitFieldInsn(opcode, owner, name, desc);
            } else {
                /* 差し替えフラグをtrueにする */
                check = true;
                /* デバッグログ出力 */
                PotionExtensionCorePlugin.LOGGER.debug("init:check potion id.");
                /* ラベルの生成と差し込み */
                Label l1 = new Label();
                this.visitLabel(l1);
                //this.visitLineNumber(109, l1);
                /* フィールドの読み込み */
                this.visitFieldInsn(GETSTATIC, "ak/potionextension/asm/PotionExtensionCorePlugin", "checkPotion", "Z");
                /* ラベルの生成 */
                Label l2 = new Label();
                /* checkPotionがtrueかどうかの判定の差し込み */
                this.visitJumpInsn(IFEQ, l2);
                /* 本来の処理。この場合はpotionType配列を読み込み */
                super.visitFieldInsn(opcode, owner, name, desc);
                /*
                引数の読み込み
                0:this
                1以降:引数の1番目から
                 */
                this.visitVarInsn(ILOAD, 1);
                /* 配列の要素を読み込み */
                this.visitInsn(AALOAD);
                /* nullチェック。 */
                mv.visitJumpInsn(IFNULL, l2);
                /* ラベル生成 */
                Label l3 = new Label();
                /* ラベル読み込み */
                this.visitLabel(l3);
                //this.visitLineNumber(110, l3);
                /* インスタンス生成 */
                this.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
                /* スタックの最上位を複製してpush */
                this.visitInsn(DUP);
                /* 文字列の読み込み */
                this.visitLdcInsn("ID %d is already used!!");
                /* 1をpush */
                this.visitInsn(ICONST_1);
                /* 配列の生成 */
                this.visitTypeInsn(ANEWARRAY, "java/lang/Object");
                /* スタックの最上位を複製してpush */
                this.visitInsn(DUP);
                /* 0をpush */
                this.visitInsn(ICONST_0);
                /* 第一引数をpush */
                this.visitVarInsn(ILOAD, 1);
                /* Integer#valueOfを呼び出し */
                this.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
                /* pop */
                this.visitInsn(AASTORE);
                /* String#formatを呼び出し */
                this.visitMethodInsn(INVOKESTATIC, "java/lang/String", "format", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", false);
                /* IllegalArgumentExceptionの生成 */
                this.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
                /* 例外のthrow */
                this.visitInsn(ATHROW);
                /* ラベルの呼び出し */
                this.visitLabel(l2);
                //this.visitLineNumber(112, l2);
                super.visitFieldInsn(opcode, owner, name, desc);
            }
        }
    }
}
