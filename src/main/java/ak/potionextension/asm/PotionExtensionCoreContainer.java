package ak.potionextension.asm;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;

import java.util.Arrays;

/**
 * Coremod用のModContainerクラス
 * Created by A.K. on 14/07/08.
 */
public class PotionExtensionCoreContainer extends DummyModContainer {
    /**
     * コンストラクタ
     */
    public PotionExtensionCoreContainer() {
        super(new ModMetadata());

        /* mcmodinfoの設定 */
        ModMetadata meta = getMetadata();
        meta.modId = "PotionExtensionCore";
        meta.name = "PotionExtensionCore";
        meta.version = "@VERSION@";
        meta.authorList = Arrays.asList("A.K.");
        meta.description = "Potion Extension Core Mod";
        meta.url = "http://forum.minecraftuser.jp/viewtopic.php?f=13&t=6672";
        meta.credits = "";
        this.setEnabledState(true);
    }

    /**
     * CoremodをModとして登録
     * @param bus 登録用BUS
     * @param controller LoadController
     * @return 登録したかどうか
     */
    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }
}
