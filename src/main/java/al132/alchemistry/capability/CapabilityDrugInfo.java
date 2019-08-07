package al132.alchemistry.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class CapabilityDrugInfo {

    @CapabilityInject(AlchemistryDrugInfo.class)
    public static Capability<AlchemistryDrugInfo> DRUG_INFO = null;

    public static AlchemistryDrugInfo getPlayerDrugInfo(EntityPlayer player) {
        return player.getCapability(DRUG_INFO, null);
    }
}