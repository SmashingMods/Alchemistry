package al132.alchemistry.setup;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ServerProxy implements IProxy {


    @Override
    public void init() {
    }

    @Override
    public Level getClientWorld() {
        throw new IllegalStateException("Only run on the client");
    }

    @Override
    public Player getClientPlayer() {
        throw new IllegalStateException("Only run on the client");
    }
}
