package al132.alchemistry.capability;

import net.minecraft.nbt.NBTTagCompound;

//Peeking off https://github.com/McJtyMods/TheOneProbe/blob/1.12/src/main/java/mcjty/theoneprobe/playerdata/PlayerGotNote.java
public class AlchemistryDrugInfo {

    public int psilocybinTicks = 0;
    public float cumulativeFOVModifier = 1.0f;

    public AlchemistryDrugInfo() {
    }

    public void saveNBTData(NBTTagCompound compound) {
        compound.setInteger("psilocybinTicks", psilocybinTicks);
        compound.setFloat("cumulativeFOVModifier", cumulativeFOVModifier);
    }

    public void loadNBTData(NBTTagCompound compound) {
        psilocybinTicks = compound.getInteger("psilocybinTicks");
        cumulativeFOVModifier = compound.getFloat("cumulativeFOVModifier");
    }
}
