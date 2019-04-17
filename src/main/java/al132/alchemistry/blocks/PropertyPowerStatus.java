package al132.alchemistry.blocks;

import net.minecraft.util.IStringSerializable;

public enum PropertyPowerStatus implements IStringSerializable {
    OFF("off"),
    STANDBY("standby"),
    ON("on");

    private final String name;

    private PropertyPowerStatus(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
