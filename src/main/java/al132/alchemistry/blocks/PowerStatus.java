package al132.alchemistry.blocks;

import net.minecraft.util.IStringSerializable;

public enum PowerStatus implements IStringSerializable {
    OFF("off"),
    STANDBY("standby"),
    ON("on");

    private final String name;

    PowerStatus(String name) {
        this.name = name;
    }

    
    @Override
    public String getString() {
        return this.name;
    }
}