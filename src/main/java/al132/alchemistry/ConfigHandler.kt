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
    var liquifierEnergyPerTick: Int? = null
    var liquifierProcessingTicks: Int? = null

    var combinerEnergyCapacity: Int? = null
    var dissolverEnergyCapacity: Int? = null
    var electrolyzerEnergyCapacity: Int? = null
    var atomizerEnergyCapacity: Int? = null
    var liquifierEnergyCapacity: Int? = null

    var fissionEnergyPerTick: Int? = null
    var fissionProcessingTicks: Int? = null
    var fissionEnergyCapacity: Int? = null

    var fusionEnergyPerTick: Int? = null
    var fusionProcessingTicks: Int? = null
    var fusionEnergyCapacity: Int? = null

    var dissolverSpeed: Int? = null

    fun init(configFile: File) {
        if (config == null) {
            config = Configuration(configFile)
            load()
        }
    }

    fun load() {
        fissionEnergyCapacity = config?.getInt("fissionEnergyCapacity","Fission",50000,1,Integer.MAX_VALUE,
                "Max energy capacity of the Fission Multiblock")
        fissionEnergyPerTick = config?.getInt("fissionEnergyperTick","Fission",300,1,Integer.MAX_VALUE,
                "Max energy capacity of the Fission Multiblock")
        fissionProcessingTicks= config?.getInt("fissionProcessingTicks","Fission",40,1,Integer.MAX_VALUE,
                "Max energy capacity of the Fission Multiblock")

        fusionEnergyCapacity = config?.getInt("fusionEnergyCapacity","fusion",50000,1,Integer.MAX_VALUE,
                "Max energy capacity of the fusion Multiblock")
        fusionEnergyPerTick = config?.getInt("fusionEnergyperTick","fusion",300,1,Integer.MAX_VALUE,
                "Max energy capacity of the fusion Multiblock")
        fusionProcessingTicks= config?.getInt("fusionProcessingTicks","fusion",40,1,Integer.MAX_VALUE,
                "Max energy capacity of the fusion Multiblock")

        
        combinerEnergyPerTick = config?.getInt("combinerEnergyPerTick", "Machines", 200, 0, Integer.MAX_VALUE,
                "Set the energy consumption rate per tick for the Chemical Combiner")
        combinerProcessingTicks = config?.getInt("combinerProcessingTicks", "Machines", 10, 1, Integer.MAX_VALUE,
                "Set the number of ticks per operation for the Chemical Combiner")
        dissolverEnergyPerTick = config?.getInt("dissolverEnergyPerTick", "Machines", 100, 0, Integer.MAX_VALUE,
                "Set the energy consumption rate per tick for the Chemical Dissolver")
        electrolyzerEnergyPerTick = config?.getInt("electrolyzerEnergyPerTick", "Machines", 100, 0, Integer.MAX_VALUE,
                "Set the energy consumption rate per tick for the Electrolyzer")
        electrolyzerProcessingTicks = config?.getInt("elctrolyzerProcessingTicks", "Machines", 10, 1, Integer.MAX_VALUE,
                "Number of ticks per electrolyzer operation")
        evaporatorProcessingTicks = config?.getInt("evaporatorProcessingTicks", "Machines", 160, 1, Integer.MAX_VALUE,
                "The best possible processing time for the evaporator. In practice it will be increased by biome, time of day, etc")
        atomizerEnergyPerTick = config?.getInt("atomizerEnergyPerTick", "Machines", 50, 0, Integer.MAX_VALUE,
                "Set the energy consumption rate per tick for the Atomizer")
        atomizerProcessingTicks = config?.getInt("atomizerProcessingTicks", "Machines", 100, 1, Integer.MAX_VALUE,
                "Number of ticks per Atomizer operation")
        liquifierEnergyPerTick = config?.getInt("liquifierEnergyPerTick", "Machines", 50, 0, Integer.MAX_VALUE,
                "Set the energy consumption rate per tick for the Liquifier")
        liquifierProcessingTicks = config?.getInt("liquifierProcessingTicks", "Machines", 100, 1, Integer.MAX_VALUE,
                "Number of ticks per Liquifier operation")
        combinerEnergyCapacity = config?.getInt("combinerEnergyCapacity", "Machines", 10000, 1, Integer.MAX_VALUE,
                "Max energy capacity of the Combiner")
        dissolverEnergyCapacity = config?.getInt("dissolverEnergyCapacity", "Machines", 10000, 1, Integer.MAX_VALUE,
                "Max energy capacity of the Dissolver")
        electrolyzerEnergyCapacity = config?.getInt("electrolyzerEnergyCapacity", "Machines", 10000, 1, Integer.MAX_VALUE,
                "Max energy capacity of the Electrolyzer")
        atomizerEnergyCapacity = config?.getInt("atomizerEnergyCapacity", "Machines", 10000, 1, Integer.MAX_VALUE,
                "Max energy capacity of the Atomizer")
        liquifierEnergyCapacity = config?.getInt("liquifierEnergyCapacity", "Machines", 10000, 1, Integer.MAX_VALUE,
                "Max energy capacity of the Liquifier")
        dissolverSpeed = config?.getInt("dissolverSpeed", "Machines", 2, 1, 64,
                "The max amount of items that the Dissolver will output each tick. Please note: only one element will be outputted per tick," +
                        " \nand only the elements from one input are eligible at a time. For example: Cellulose (C6 H10 O5) with speed 4 would be outputted like so," +
                        " \nwith each comma-seperated value representing 1 tick [4xC,2xC,4xH,4xH,2xH,4xO,1xO]")
        if (config?.hasChanged() == true) config!!.save()
    }
}