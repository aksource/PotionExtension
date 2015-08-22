package ak.potionextension.asm;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

/**
 * CoreMod�̃��C���N���X�B<br>
 *     ClassTransformer�̎w���AModContainer�N���X�̎w����s���B
 * Created by A.K. on 14/07/07.
 */
public class PotionExtensionCorePlugin implements IFMLLoadingPlugin {
    /** logger */
    public static final Logger LOGGER = LogManager.getLogger("PotionExtension");
    /** Potion�z��̔z�񐔎w��B�����l��128 */
    public static int maxPotionArray = 128;
    /** �|�[�V�����̏d���`�F�b�N�����邩�ǂ����̃t���O�B�����l��true */
    public static boolean checkPotion = true;
    /** minecraft�t�H���_�̈ʒu */
    public static File mcLoc;
    /** �J���p */
    private static boolean debug = true;

    /**
     * ClassTransformer�N���X�̊��S�C���N���X����z��w��
     * @return ClassTransformer�N���X�̊��S�C���N���X���̔z��
     */
    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"ak.potionextension.asm.PotionEffectTransformer",
                "ak.potionextension.asm.PotionArrayTransformer"/*,
                "ak.potionextension.asm.InventoryEffectRendererTransformer"*/};
    }

    /**
     * ModContainer�N���X�̃N���X���w��
     * @return ModContainer�N���X�̃N���X��
     */
    @Override
    public String getModContainerClass() {
        return "ak.potionextension.asm.PotionExtensionCoreContainer";
    }

    /**
     * CallHook�N���X�̃N���X���w��
     * @return CallHook�N���X�̃N���X��
     */
    @Override
    public String getSetupClass() {
        return null;
    }

    /**
     * �N���X���[�h���ɌĂ΂��B�R���t�B�O�̐ݒ�Ƃ�
     * @param data minecraft�t�H���_���̏��̓������}�b�v
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
     * �R���t�B�O�̓ǂݍ���
     * @param configFile �R���t�B�O�t�@�C��
     */
    private void initConfig(File configFile) {
        Configuration config = new Configuration(configFile);
        config.load();
        checkPotion = config.get(Configuration.CATEGORY_GENERAL, "checkPotion", checkPotion, "check conflicting of Potion ID").getBoolean();
        maxPotionArray = config.get(Configuration.CATEGORY_GENERAL, "maxPotionArray", maxPotionArray, "Not Recommended to set over 127").getInt();
        config.save();
    }

    /**
     * AccessTransformaer�N���X�̃N���X���w��
     * @return AccessTransformaer�N���X�̃N���X��
     */
    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    /**
     * �f�o�b�O�p
     * @param modified �N���X�̃o�C�g�z��
     * @param className �N���X��
     * @return �N���X�̃o�C�g�z��
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
