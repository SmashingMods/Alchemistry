package al132.alchemistry.blocks;

import al132.alchemistry.Alchemistry;
import al132.alib.blocks.ABaseBlock;

public class BaseBlock extends ABaseBlock {
    public BaseBlock(String name, Properties properties) {
        super(Alchemistry.data, name, properties.hardnessAndResistance(2.0f));
    }
}
