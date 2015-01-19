package ak.potionextension.asm;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.io.File;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by A.K. on 14/07/07.
 */
public class PotionExtensionCorePlugin implements IFMLLoadingPlugin {
    public static final Logger LOGGER = Logger.getLogger("samplecore");
    public static int maxPotionArray = 128;
    public static boolean checkPotion = true;
    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"ak.potionextension.asm.PotionEffectTransformer","ak.potionextension.asm.PotionArrayTransformer"};
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
}
