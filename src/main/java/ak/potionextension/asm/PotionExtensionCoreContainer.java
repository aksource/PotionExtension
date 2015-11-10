package ak.potionextension.asm;

import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.versioning.VersionParser;

import java.util.Collections;

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
        meta.authorList = Collections.singletonList("A.K.");
        meta.description = "Potion Extension Core Mod";
        meta.url = "http://forum.minecraftuser.jp/viewtopic.php?f=13&t=6672";
        meta.credits = "";
        meta.dependencies = Collections.singletonList(VersionParser.parseVersionReference("Forge@[11.14.3.1543,)"));
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
