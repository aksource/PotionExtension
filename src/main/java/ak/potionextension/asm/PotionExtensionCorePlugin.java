package ak.potionextension.asm;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

/**
 * CoreModのメインクラス。<br>
 *     ClassTransformerの指定や、ModContainerクラスの指定を行う。
 * Created by A.K. on 14/07/07.
 */
public class PotionExtensionCorePlugin implements IFMLLoadingPlugin {
    /** logger */
    public static final Logger LOGGER = LogManager.getLogger("PotionExtension");
    /** Potion配列の配列数指定。初期値は128 */
    public static int maxPotionArray = 128;
    /** ポーションの重複チェックをするかどうかのフラグ。初期値はtrue */
    public static boolean checkPotion = true;
    /** minecraftフォルダの位置 */
    public static File mcLoc;
    /** 開発用 */
    private static boolean debug = true;

    /**
     * ClassTransformerクラスの完全修飾クラス名を配列指定
     * @return ClassTransformerクラスの完全修飾クラス名の配列
     */
    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"ak.potionextension.asm.PotionEffectTransformer",
                "ak.potionextension.asm.PotionArrayTransformer"/*,
                "ak.potionextension.asm.InventoryEffectRendererTransformer"*/};
    }

    /**
     * ModContainerクラスのクラス名指定
     * @return ModContainerクラスのクラス名
     */
    @Override
    public String getModContainerClass() {
        return "ak.potionextension.asm.PotionExtensionCoreContainer";
    }

    /**
     * CallHookクラスのクラス名指定
     * @return CallHookクラスのクラス名
     */
    @Override
    public String getSetupClass() {
        return null;
    }

    /**
     * クラスロード時に呼ばれる。コンフィグの設定とか
     * @param data minecraftフォルダ等の情報の入ったマップ
     */
    @Override
    public void injectData(Map<String, Object> data) {
        if (data.containsKey("mcLocation")) {
            mcLoc = (File) data.get("mcLocation");
            File configLocation = new File(mcLoc, "config");
            File configFile = new File(configLocation, "PotionExtension.cfg");
            initConfig(configFile);
        }
    }

    /**
     * コンフィグの読み込み
     * @param configFile コンフィグファイル
     */
    private void initConfig(File configFile) {
        Configuration config = new Configuration(configFile);
        config.load();
        checkPotion = config.get(Configuration.CATEGORY_GENERAL, "checkPotion", checkPotion, "check conflicting of Potion ID").getBoolean();
        maxPotionArray = config.get(Configuration.CATEGORY_GENERAL, "maxPotionArray", maxPotionArray, "Not Recommended to set over 127").getInt();
        config.save();
    }

    /**
     * AccessTransformaerクラスのクラス名指定
     * @return AccessTransformaerクラスのクラス名
     */
    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    /**
     * デバッグ用
     * @param modified クラスのバイト配列
     * @param className クラス名
     * @return クラスのバイト配列
     */
    public static byte[] outputModifiedClassFile(byte[] modified, String className) {
        if (debug) {
            try {
                FileOutputStream fos = new FileOutputStream(className + ".class");
                fos.write(modified);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return modified;
    }
}
