package al132.alchemistry

import net.minecraftforge.common.config.Configuration
import java.io.File

/**
 * Created by al132 on 4/28/2017.
 */


object ConfigHandler {
    var config: Configuration? = null

    var combinerEnergyPerTick: Int? = null
    var combinerProcessingTicks: Int? = null
    var dissolverEnergyPerTick: Int? = null
    var electrolyzerEnergyPerTick: Int? = null
    var electrolyzerProcessingTicks: Int? = null
    var evaporatorProcessingTicks: Int? = null
    var atomizerEnergyPerTick: Int? = null
    var atomizerProcessingTicks: Int? = null


    fun init(configFile: File) {
        if (config == null) {
            config = Configuration(configFile)
            load()
        }
    }

    fun load() {
        combinerEnergyPerTick = config?.getInt("combinerEnergyPerTick", "Machines", 250, 0, Integer.MAX_VALUE,
                "Set the energy consumption rate per tick for the Chemical Combiner")
        combinerProcessingTicks = config?.getInt("combinerProcessingTicks", "Machines", 20, 1, Integer.MAX_VALUE,
                "Set the number of ticks per operation for the Chemical Combiner")
        dissolverEnergyPerTick = config?.getInt("dissolverEnergyPerTick", "Machines", 100, 0, Integer.MAX_VALUE,
                "Set the energy consumption rate per tick for the Chemical Dissolver")
        electrolyzerEnergyPerTick = config?.getInt("electrolyzerEnergyPerTick", "Machines", 100, 0, Integer.MAX_VALUE,
                "Set the energy consumption rate per tick for the Electrolyzer")
        electrolyzerProcessingTicks = config?.getInt("elctrolyzerProcessingTicks", "Machines", 10, 1, Integer.MAX_VALUE,
                "Number of ticks per electrolyzer operation")
        evaporatorProcessingTicks = config?.getInt("evaporatorProcessingticks", "Machines", 160, 1, Integer.MAX_VALUE,
                "The best possible processing time for the evaporator. In practice it will be increased by biome, time of day, etc")
        atomizerEnergyPerTick = config?.getInt("atomizerProcessingTicks","Machines",50,0,Integer.MAX_VALUE,
                "Set the energy consumption rate per tick for the Atomizer")
        atomizerProcessingTicks = config?.getInt("atomizerProcessingticks", "Machines", 100,1,Integer.MAX_VALUE,
                "Number of ticks per Atomizer operation")
        if (config?.hasChanged() ?: false) config!!.save()
    }
}