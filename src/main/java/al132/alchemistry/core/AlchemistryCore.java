package al132.alchemistry.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Map;

@Name("Alchemistry Plugin")
@MCVersion("1.12.2")
@SortingIndex(1001)
public class AlchemistryCore implements IFMLLoadingPlugin {

    public static final Logger LOG = LogManager.getLogger("Alchemistry Core");

    protected static String renderItemOverlay;

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"al132.alchemistry.core.AlchemistryTransformer"};
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> map) {

        boolean dev = !(Boolean) map.get("runtimeDeobfuscationEnabled");
        renderItemOverlay = dev ? "renderItemOverlayIntoGUI" : "func_180453_a";
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
