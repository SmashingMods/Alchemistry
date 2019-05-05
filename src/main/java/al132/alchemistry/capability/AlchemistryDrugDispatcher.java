package al132.alchemistry.capability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

//Peeking off https://github.com/McJtyMods/TheOneProbe/blob/1.12/src/main/java/mcjty/theoneprobe/playerdata/PropertiesDispatcher.java
public class AlchemistryDrugDispatcher implements ICapabilityProvider, INBTSerializable<NBTTagCompound> {

    private AlchemistryDrugInfo drugInfo = new AlchemistryDrugInfo();

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityDrugInfo.DRUG_INFO;
    }
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityDrugInfo.DRUG_INFO) {
            return (T) drugInfo;
        }
        return null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        drugInfo.saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        drugInfo.loadNBTData(nbt);
    }
}
