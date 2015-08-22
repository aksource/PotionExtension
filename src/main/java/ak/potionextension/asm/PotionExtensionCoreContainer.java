package ak.potionextension.asm;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;

import java.util.Arrays;

/**
 * Coremod�p��ModContainer�N���X
 * Created by A.K. on 14/07/08.
 */
public class PotionExtensionCoreContainer extends DummyModContainer {
    /**
     * �R���X�g���N�^
     */
    public PotionExtensionCoreContainer() {
        super(new ModMetadata());

        /* mcmodinfo�̐ݒ� */
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
     * Coremod��Mod�Ƃ��ēo�^
     * @param bus �o�^�pBUS
     * @param controller LoadController
     * @return �o�^�������ǂ���
     */
    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }
}
