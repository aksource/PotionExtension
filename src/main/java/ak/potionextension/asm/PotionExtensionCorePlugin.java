package ak.potionextension.asm;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by A.K. on 14/07/07.
 */
public class PotionExtensionCorePlugin implements IFMLLoadingPlugin {
    public static final Logger LOGGER = Logger.getLogger("samplecore");
    public static int maxPotionArray = 128;
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

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
