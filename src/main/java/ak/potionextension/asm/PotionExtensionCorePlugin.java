package ak.potionextension.asm;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

/**
 * Created by A.K. on 14/07/07.
 */
public class PotionExtensionCorePlugin implements IFMLLoadingPlugin {
    public static final Logger LOGGER = LogManager.getLogger("PotionExtension");
    public static int maxPotionArray = 128;
    public static boolean checkPotion = true;
    public static File mcLoc;
    private static boolean debug = false;
    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"ak.potionextension.asm.PotionEffectTransformer",
                "ak.potionextension.asm.PotionArrayTransformer"/*,
                "ak.potionextension.asm.InventoryEffectRendererTransformer"*/};
    }

    @Override
    public String getModContainerClass() {
        return "ak.potionextension.asm.PotionExtensionCoreContainer";
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        if (data.containsKey("mcLocation"))
        {
            File mcLoc = (File) data.get("mcLocation");
            File configLocation = new File(mcLoc, "config");
            File configFile = new File(configLocation, "PotionExtension.cfg");
            initConfig(configFile);
        }
    }

    private void initConfig(File configFile) {
        Configuration config = new Configuration(configFile);
        config.load();
        checkPotion = config.get(Configuration.CATEGORY_GENERAL, "checkPotion", checkPotion, "check conflicting of Potion ID").getBoolean();
        config.save();
    }
    @Override
    public String getAccessTransformerClass() {
        return null;
    }
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
