package al132.alchemistry.recipes

import al132.alchemistry.chemistry.CompoundRegistry
import al132.alchemistry.chemistry.ElementRegistry
import al132.alchemistry.items.ItemElementIngot
import al132.alchemistry.items.ModItems
import al132.alchemistry.utils.extensions.toOre
import al132.alchemistry.utils.extensions.toStack
import al132.alib.utils.Utils.firstOre
import al132.alib.utils.Utils.oreExists
import al132.alib.utils.extensions.toDict
import al132.alib.utils.extensions.toImmutable
import al132.alib.utils.extensions.toIngredient
import al132.alib.utils.extensions.toStack
import net.minecraft.block.Block
import net.minecraft.block.BlockTallGrass
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.IFuelHandler
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.oredict.OreDictionary
import java.util.*

/**
 * Created by al132 on 1/16/2017.
 */

val heathenSpelling = "aluminium"

data class DissolverOreData(val prefix: String, val quantity: Int, val strs: List<String>) {
    fun toDictName(index: Int) = prefix + strs[index].first().toUpperCase() + strs[index].substring(1)
    val size = strs.size
}


object ModRecipes {
    val electrolyzerRecipes = ArrayList<ElectrolyzerRecipe>()
    val dissolverRecipes = ArrayList<DissolverRecipe>()
    val combinerRecipes = ArrayList<CombinerRecipe>()
    val evaporatorRecipes = ArrayList<EvaporatorRecipe>()
    val atomizerRecipes = ArrayList<AtomizerRecipe>()
    val liquifierRecipes = ArrayList<LiquifierRecipe>()
    val fissionRecipes = ArrayList<FissionRecipe>()

    val metals: List<String> = mutableListOf(heathenSpelling)
            .apply { addAll(ElementRegistry.getAllElements().map { it.name }) }.toImmutable()

    val metalOreData: List<DissolverOreData> = listOf(
            DissolverOreData("ingot", 16, metals),
            DissolverOreData("ore", 32, metals),
            DissolverOreData("dust", 16, metals),
            DissolverOreData("block", 144, metals),
            DissolverOreData("nugget", 1, metals),
            DissolverOreData("plate", 16, metals))

    fun init() {
        initElectrolyzerRecipes()
        initEvaporatorRecipes()
        initFuelHandler()
        initDissolverRecipes() //before combiner, so combiner can use reversible recipes
        initCombinerRecipes()
        initAtomizerRecipes()
        initLiquifierRecipes()
        initFissionRecipes()
    }

    fun initOredict() {
        (1 until 119).filterNot { ItemElementIngot.invalidIngots.contains(it) }.forEach { i ->
            val elementName: String = ElementRegistry[i]!!.name.capitalize()
            OreDictionary.registerOre("ingot$elementName", ModItems.ingots.toStack(meta = i))
        }
    }
/*

    fun addDissolverRecipesForAlloy(alloySuffix: String, //Should start with uppercase, i.e. "Bronze" or "ElectricalSteel"
                                    ingotOne: String,
                                    quantityOne: Int,
                                    ingotTwo: String,
                                    quantityTwo: Int,
                                    ingotThree: String = "",
                                    quantityThree: Int = 0,
                                    conservationOfMass: Boolean = true) {
        fun fitInto16(q1: Int, q2: Int, q3: Int): List<Int>? {
            val sum = q1 + q2 + q3
            val new1 = Math.round(q1.toDouble() / sum * 16.0).toInt()
            val new2 = Math.round(q2.toDouble() / sum * 16.0).toInt()
            val new3 = Math.round(q3.toDouble() / sum * 16.0).toInt()
            if ((16).rem(sum) == 0) return listOf(new1, new2, new3)
            else return null
        }

        val sum = quantityOne + quantityTwo + quantityThree


        val ores: List<String> = listOf(("ingot$alloySuffix"), ("plate$alloySuffix"), ("dust$alloySuffix"))
        val threeIngredients: Boolean = ingotThree.isNotEmpty() && quantityThree > 0
        val fractionalQuantities = fitInto16(quantityOne, quantityTwo, quantityThree)
        val isConserved = fractionalQuantities != null && conservationOfMass
        val calculatedQuantity1 = if (isConserved) fractionalQuantities!![0] else quantityOne * 16
        val calculatedQuantity2 = if (isConserved) fractionalQuantities!![1] else quantityOne * 16
        val calculatedQuantity3 = if (isConserved) fractionalQuantities!![2] else quantityOne * 16

        ores.filter { oreNotEmpty(it) }
                .forEach { ore ->
                    dissolverRecipes.add(dissolverRecipe {
                        input = ore.toOre()
                        //if (fractionalQuantities == null && conservationOfMass) inputQuantity = quantityOne + quantityTwo + quantityThree
                        output {
                            addGroup {
                                addStack { ingotOne.toStack(quantity = calculatedQuantity1) }
                                addStack { ingotTwo.toStack(quantity = calculatedQuantity2) }
                                if (threeIngredients) {
                                    addStack { ingotThree.toStack(quantity = calculatedQuantity3) }
                                }
                            }
                        }
                    })
                }

        if (oreNotEmpty("block$alloySuffix")) {
            dissolverRecipes.add(dissolverRecipe {
                input = ("block$alloySuffix").toOre()
                output {
                    addGroup {
                        if (threeIngredients) {
                            addStack { ingotOne.toStack(calculatedQuantity1 * 9) }//quantity = 144 / (quantityOne + quantityTwo + quantityThree) * quantityOne) }
                            addStack { ingotTwo.toStack(calculatedQuantity2 * 9) }//quantity = 144 / (quantityOne + quantityTwo + quantityThree) * quantityTwo) }
                            addStack { ingotThree.toStack(calculatedQuantity3 * 9) }//quantity = 144 / (quantityOne + quantityTwo + quantityThree) * quantityThree) }
                        } else {
                            addStack { ingotOne.toStack(quantity = calculatedQuantity1 * 9) }//144 / (quantityOne + quantityTwo) * quantityOne) }
                            addStack { ingotTwo.toStack(quantity = calculatedQuantity2 * 9) }//144 / (quantityOne + quantityTwo) * quantityTwo) }
                        }
                    }
                }
            })
        }
    }*/


    fun initDissolverRecipes() {

        CompoundRegistry.compounds()
                .filter { it.autoDissolverRecipe }
                .forEach { compound ->
                    dissolverRecipes.add(dissolverRecipe {
                        input = compound.toItemStack(1).toIngredient()
                        output {
                            addGroup {
                                compound.components.forEach { component ->
                                    addStack { component.compound.toItemStack(component.quantity) }
                                }
                            }
                        }
                    })
                }

        for (meta in 0..BlockTallGrass.EnumType.values().size) {
            dissolverRecipes.add(dissolverRecipe {
                input = Blocks.TALLGRASS.toIngredient(meta = BlockTallGrass.EnumType.byMetadata(meta).ordinal)
                output {
                    relativeProbability = false
                    addGroup { addStack { "cellulose".toStack() }; probability = 25.0 }
                }
            })
        }

        listOf("ingotChrome", "plateChrome", "dustChrome")
                .filter { oreNotEmpty(it) }
                .forEach { ore ->
                    dissolverRecipes.add(dissolverRecipe {
                        input = ore.toOre()
                        output {
                            addGroup {
                                addStack { "chromium".toStack(16) }
                            }
                        }
                    })
                }

        if (oreNotEmpty("blockChrome")) {
            dissolverRecipes.add(dissolverRecipe {
                input = "blockChrome".toOre()
                output {
                    addGroup {
                        addStack { "chromium".toStack(16 * 9) }
                    }
                }
            })
        }

        if (oreNotEmpty("oreChrome")) {
            dissolverRecipes.add(dissolverRecipe {
                input = "oreChrome".toOre()
                output {
                    addGroup {
                        addStack { "chromium".toStack(16 * 2) }
                    }
                }
            })
        }

        if (oreNotEmpty("dustAsh")) {
            dissolverRecipes.add(dissolverRecipe {
                input = "dustAsh".toOre()
                reversible = true
                output {
                    addGroup {
                        addStack { "potassium_carbonate".toStack(4) }
                    }
                }
            })
        }

        dissolverRecipes.add(dissolverRecipe {
            input = Items.FLINT.toIngredient()
            output {
                addGroup { addStack { "silicon_dioxide".toStack(3) } }
            }
        })

        listOf("lumpSalt", "materialSalt", "salt", "itemSalt", "dustSalt", "foodSalt").forEach {
            if (oreNotEmpty(it)) {
                dissolverRecipes.add(dissolverRecipe {
                    input = it.toOre()
                    output {
                        addGroup { addStack { "sodium_chloride".toStack(8) } }
                    }
                })
            }
        }

        listOf("dustSaltpeter", "nitrate", "nitre").forEach {
            if (oreNotEmpty(it)) {
                dissolverRecipes.add(dissolverRecipe {
                    input = it.toOre()
                    output {
                        addGroup { addStack { "potassium_nitrate".toStack(8) } }
                    }
                })
            }
        }


        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.COAL_ORE.toIngredient()
            output {
                addGroup {
                    addStack { "carbon".toStack(quantity = 32) }
                    addStack { "sulfur".toStack(quantity = 8) }
                }
            }
        })


        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.COAL_BLOCK.toIngredient()
            output {
                addGroup { addStack { "carbon".toStack(quantity = 9 * 8) } }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.WHEAT_SEEDS.toIngredient()
            output {
                relativeProbability = false
                addGroup { addStack { "cellulose".toStack() }; probability = 10.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.NETHERRACK.toIngredient()
            output {
                addGroup { addStack { ItemStack.EMPTY }; probability = 15.0 }
                addGroup { addStack { "zinc_oxide".toStack() }; probability = 2.0 }
                addGroup { addStack { "gold".toStack() }; probability = 1.0 }
                addGroup { addStack { "phosphorus".toStack() }; probability = 1.0 }
                addGroup { addStack { "sulfur".toStack() }; probability = 3.0 }
                addGroup { addStack { "germanium".toStack() }; probability = 1.0 }
                addGroup { addStack { "silicon".toStack() }; probability = 4.0 }

            }
        })

        listOf(Items.NETHERBRICK, Blocks.NETHER_BRICK).forEach {
            dissolverRecipes.add(dissolverRecipe {
                input = if (it == Items.NETHERBRICK) (it as Item).toIngredient() else (it as Block).toIngredient()
                output {
                    rolls = if (it == Blocks.NETHER_BRICK) 4 else 1
                    addGroup { addStack { ItemStack.EMPTY }; probability = 5.0 }
                    addGroup { addStack { "zinc_oxide".toStack() }; probability = 2.0 }
                    addGroup { addStack { "gold".toStack() }; probability = 1.0 }
                    addGroup { addStack { "phosphorus".toStack() }; probability = 1.0 }
                    addGroup { addStack { "sulfur".toStack() }; probability = 4.0 }
                    addGroup { addStack { "germanium".toStack() }; probability = 1.0 }
                    addGroup { addStack { "silicon".toStack() }; probability = 4.0 }
                }
            })
        }

        dissolverRecipes.add(dissolverRecipe {
            input = Items.SPIDER_EYE.toIngredient()
            output {
                addGroup {
                    addStack { "beta_carotene".toStack(2) }
                    addStack { "protein".toStack(2) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.IRON_HORSE_ARMOR.toIngredient()
            output {
                addStack { "iron".toStack(64) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.DIAMOND_HORSE_ARMOR.toIngredient()
            output {
                addStack { "carbon".toStack(4 * (64 * 8)) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.ANVIL.toIngredient()
            output {
                addStack { "iron".toStack((144 * 3) + (16 * 4)) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.IRON_DOOR.toIngredient()
            output {
                addStack { "iron".toStack(32) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.IRON_TRAPDOOR.toIngredient()
            output {
                addStack { "iron".toStack(64) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.CHEST.toIngredient()
            output {
                addStack { "cellulose".toStack(2) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.CRAFTING_TABLE.toIngredient()
            output {
                addStack { "cellulose".toStack(1) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.WEB.toIngredient()
            output {
                addStack { "protein".toStack(2) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.GOLDEN_HORSE_ARMOR.toIngredient()
            output {
                addStack { "gold".toStack(64) }
            }
        })

        //(0 until 16).forEach { index ->
        dissolverRecipes.add(dissolverRecipe {
            input = "wool".toOre()//Blocks.WOOL.toIngredient(meta = index)
            output {
                addGroup {
                    addStack { "protein".toStack(1) }
                    addStack { "triglyceride".toStack(1) }
                }
            }
        })
        //}

        (0 until 16).forEach { index ->
            dissolverRecipes.add(dissolverRecipe {
                input = Blocks.CARPET.toIngredient(meta = index)
                output {
                    relativeProbability = false
                    addGroup {
                        addStack { "protein".toStack(1) }
                        addStack { "triglyceride".toStack(1) }
                        probability = (2.0 / 3.0) * 100
                    }
                }
            })
        }

        dissolverRecipes.add(dissolverRecipe {
            input = Items.EMERALD.toIngredient()
            output {
                reversible = true
                addGroup {
                    addStack { "beryl".toStack(8) }
                    addStack { "chromium".toStack(8) }
                    addStack { "vanadium".toStack(4) }
                }
            }
        })


        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.EMERALD_ORE.toIngredient()
            output {
                addGroup {
                    addStack { "beryl".toStack(8 * 2) }
                    addStack { "chromium".toStack(8 * 2) }
                    addStack { "vanadium".toStack(4 * 2) }
                }
            }
        })

        listOf(Blocks.END_STONE, Blocks.END_BRICKS).forEach {
            dissolverRecipes.add(dissolverRecipe {
                input = it.toIngredient()
                output {
                    addGroup { addStack { "mercury".toStack() }; probability = 50.0 }
                    addGroup { addStack { "neodymium".toStack() }; probability = 5.0 }
                    addGroup { addStack { "silicon_dioxide".toStack(2) }; probability = 250.0 }
                    addGroup { addStack { "lithium".toStack() }; probability = 50.0 }
                    addGroup { addStack { "thorium".toStack() }; probability = 2.0 }
                }
            })
        }

        listOf(Blocks.SNOW, Blocks.ICE).forEach {
            dissolverRecipes.add(dissolverRecipe {
                input = it.toIngredient()
                output {
                    addStack { "water".toStack(16) }
                }
            })
        }


        dissolverRecipes.add(dissolverRecipe {
            input = "record".toOre()
            output {
                addGroup {
                    addStack { "polyvinyl_chloride".toStack(64) }
                    addStack { "lead".toStack(16) }
                    addStack { "cadmium".toStack(16) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.JUKEBOX.toIngredient()
            output {
                addGroup {
                    addStack { "carbon".toStack(64 * 8) }
                    addStack { "cellulose".toStack(2) }
                }
            }
        })

        for (i in 0 until 16) {
            dissolverRecipes.add(dissolverRecipe {
                input = Blocks.CONCRETE_POWDER.toIngredient(quantity = 2, meta = i)
                output {
                    addGroup { addStack { "silicon_dioxide".toStack(5) } }
                }
            })
            dissolverRecipes.add(dissolverRecipe {
                input = Blocks.CONCRETE.toIngredient(quantity = 2, meta = i)
                output {
                    addGroup { addStack { "silicon_dioxide".toStack(5) } }
                }
            })
        }

        listOf(Blocks.GRASS.toStack(), Blocks.DIRT.toStack(), Blocks.DIRT.toStack(meta = 1), Blocks.DIRT.toStack(meta = 2)).forEach {
            dissolverRecipes.add(dissolverRecipe {
                input = it.toIngredient()
                output {
                    addGroup { addStack { "water".toStack() }; probability = 30.0 }
                    addGroup { addStack { "silicon_dioxide".toStack() }; probability = 50.0 }
                    addGroup { addStack { "cellulose".toStack() }; probability = 10.0 }
                    addGroup { addStack { "kaolinite".toStack() }; probability = 10.0 }
                }
            })
        }

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.EMERALD_BLOCK.toIngredient()
            output {
                addGroup {
                    addStack { "beryl".toStack(8 * 9) }
                    addStack { "chromium".toStack(8 * 9) }
                    addStack { "vanadium".toStack(4 * 9) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = "blockGlass".toOre()
            output {
                addStack { "silicon_dioxide".toStack(4) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = "treeSapling".toOre()
            output {
                relativeProbability = false
                addGroup { addStack { "cellulose".toStack(1) }; probability = 25.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.DEADBUSH.toIngredient()
            output {
                relativeProbability = false
                addGroup { addStack { "cellulose".toStack() }; probability = 25.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.VINE.toIngredient()
            output {
                relativeProbability = false
                addGroup { addStack { "cellulose".toStack() }; probability = 25.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.WATERLILY.toIngredient()
            output {
                relativeProbability = false
                addGroup { addStack { "cellulose".toStack() }; probability = 25.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.PUMPKIN.toIngredient()
            output {
                relativeProbability = false
                addGroup {
                    probability = 50.0
                    addStack { "cucurbitacin".toStack() }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.QUARTZ.toIngredient()
            reversible = true
            output {
                addGroup {
                    addStack { "barium".toStack(8) }
                    addStack { "silicon_dioxide".toStack(16) }
                }
            }
        })


        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.QUARTZ_ORE.toIngredient()
            output {
                addGroup {
                    addStack { "barium".toStack(8 * 2) }
                    addStack { "silicon_dioxide".toStack(16 * 2) }
                }
            }
        })

        listOf(0, 1, 2).forEach {
            dissolverRecipes.add(dissolverRecipe {
                input = Blocks.QUARTZ_BLOCK.toIngredient(meta = it)
                //reversible = true
                output {
                    addGroup {
                        addStack { "barium".toStack(8 * 4) }
                        addStack { "silicon_dioxide".toStack(16 * 4) }
                    }
                }
            })
        }
        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.BROWN_MUSHROOM.toIngredient()
            reversible = true
            output {
                addGroup {
                    addStack { "psilocybin".toStack() }
                    addStack { "cellulose".toStack() }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.RED_MUSHROOM.toIngredient()
            reversible = true
            output {
                addGroup {
                    addStack { "cellulose".toStack() }
                    addStack { "psilocybin".toStack() }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.SOUL_SAND.toIngredient()
            output {
                reversible = true
                addGroup {
                    addStack { "thulium".toStack() }
                    addStack { "silicon_dioxide".toStack(4) }
                }
            }
        })
        dissolverRecipes.add(dissolverRecipe {
            input = Items.REEDS.toIngredient()
            output {
                addGroup { addStack { "sucrose".toStack() } }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.DYE.toIngredient(quantity = 4, meta = 4)
            output {
                reversible = true
                addGroup {
                    addStack { "sodium".toStack(6) }
                    addStack { "calcium".toStack(2) }
                    addStack { "aluminum".toStack(6) }
                    addStack { "silicon".toStack(6) }
                    addStack { "oxygen".toStack(24) }
                    addStack { "sulfur".toStack(2) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.LAPIS_ORE.toIngredient()
            output {
                addGroup {
                    addStack { "sodium".toStack(6 * 4) }
                    addStack { "calcium".toStack(2 * 4) }
                    addStack { "aluminum".toStack(6 * 4) }
                    addStack { "silicon".toStack(6 * 4) }
                    addStack { "oxygen".toStack(24 * 4) }
                    addStack { "sulfur".toStack(2 * 4) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Blocks.LAPIS_BLOCK.toIngredient()
            output {
                addGroup {
                    addStack { "sodium".toStack(6 * 9) }
                    addStack { "calcium".toStack(2 * 9) }
                    addStack { "aluminum".toStack(6 * 9) }
                    addStack { "silicon".toStack(6 * 9) }
                    addStack { "oxygen".toStack(24 * 9) }
                    addStack { "sulfur".toStack(2 * 9) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.STRING.toIngredient()
            output {
                relativeProbability = false
                addGroup {
                    probability = 50.0
                    addStack { "protein".toStack() }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = ModItems.condensedMilk.toIngredient()
            output {
                relativeProbability = false
                addGroup { addStack { "calcium".toStack(4) }; probability = 40.0 }
                addGroup { addStack { "protein".toStack() }; probability = 20.0 }
                addGroup { addStack { "sucrose".toStack() }; probability = 20.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.WHEAT.toIngredient()
            output {
                relativeProbability = false
                addGroup { addStack { "starch".toStack() }; probability = 5.0 }
                addGroup { addStack { "cellulose".toStack() }; probability = 25.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Blocks.GRAVEL.toIngredient()
            output {
                addGroup { addStack { "silicon_dioxide".toStack() } }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Blocks.HAY_BLOCK.toIngredient()
            output {
                rolls = 9
                relativeProbability = false
                addGroup { addStack { "starch".toStack() }; probability = 5.0 }
                addGroup { addStack { "cellulose".toStack() }; probability = 25.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.POTATO.toIngredient()
            output {
                relativeProbability = false
                addGroup { addStack { "starch".toStack() }; probability = 10.0 }
                addGroup { addStack { "potassium".toStack(5) }; probability = 25.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.BAKED_POTATO.toIngredient()
            output {
                relativeProbability = false
                addGroup { addStack { "starch".toStack() }; probability = 10.0 }
                addGroup { addStack { "potassium".toStack(5) }; probability = 25.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.REDSTONE.toIngredient()
            output {
                reversible = true
                addGroup {
                    addStack { "iron_oxide".toStack() }
                    addStack { "strontium_carbonate".toStack() }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Blocks.REDSTONE_ORE.toIngredient()
            output {
                addGroup {
                    addStack { "iron_oxide".toStack(quantity = 4) }
                    addStack { "strontium_carbonate".toStack(quantity = 4) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.BEEF.toIngredient()
            output {
                addGroup {
                    addStack { "protein".toStack(4) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.COOKED_PORKCHOP.toIngredient()
            output {
                addGroup { addStack { "protein".toStack(4) } }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.MUTTON.toIngredient()
            output {
                addGroup { addStack { "protein".toStack(4) } }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.COOKED_MUTTON.toIngredient()
            output {
                addGroup { addStack { "protein".toStack(4) } }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.PORKCHOP.toIngredient()
            output {
                addGroup { addStack { "protein".toStack(4) } }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.COOKED_BEEF.toIngredient()
            output {
                addGroup {
                    addStack { "protein".toStack(4) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Ingredient.fromStacks(Items.CHICKEN.toStack())
            output {
                addGroup {
                    addStack { "protein".toStack(4) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.COOKED_CHICKEN.toIngredient()
            output {
                addGroup {
                    addStack { "protein".toStack(4) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.FISH.toIngredient()
            output {
                addGroup {
                    addStack { "protein".toStack(4) }
                    addStack { "selenium".toStack(2) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.FISH.toIngredient(meta = 3)
            output {
                addGroup {
                    addStack { "protein".toStack(4) }
                    addStack { "potassium_cyanide".toStack(4) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Blocks.SPONGE.toIngredient()
            output {
                addGroup {
                    addStack { "kaolinite".toStack(8) }
                    addStack { "calcium_carbonate".toStack(8) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.FISH.toIngredient(meta = 1)
            output {
                addGroup {
                    addStack { "protein".toStack(4) }
                    addStack { "selenium".toStack(4) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.FISH.toIngredient(meta = 2)
            output {
                addGroup {
                    addStack { "protein".toStack(4) }
                    addStack { "selenium".toStack(4) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.COOKED_FISH.toIngredient()
            output {
                addGroup {
                    addStack { "protein".toStack(4) }
                    addStack { "selenium".toStack(2) }

                }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.LEATHER.toIngredient()
            output {
                addGroup {
                    addStack { "protein".toStack(3) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.ROTTEN_FLESH.toIngredient()
            output {
                addGroup {
                    addStack { "protein".toStack(3) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.RABBIT.toIngredient()
            output {
                addGroup {
                    addStack { "protein".toStack(4) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.COOKED_RABBIT.toIngredient()
            output {
                addGroup {
                    addStack { "protein".toStack(4) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.CARROT.toIngredient()
            output {
                relativeProbability = false
                addGroup { addStack { "beta_carotene".toStack(1) }; probability = 20.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = "dyeRed".toOre()
            output {
                addStack { "mercury_sulfide".toStack(quantity = 4) }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = "dyePink".toOre()
            output {
                addStack { "arsenic_sulfide".toStack(quantity = 4) }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = "dyeGreen".toOre()
            output {
                addStack { "nickel_chloride".toStack(quantity = 4) }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = "dyeLime".toOre()
            output {
                addGroup {
                    addStack { "cadmium_sulfide".toStack(quantity = 2) }
                    addStack { "chromium_oxide".toStack(quantity = 2) }
                }
            }
        })



        dissolverRecipes.add(dissolverRecipe
        {
            input = "dyePurple".toOre()
            output {
                addStack { "potassium_permanganate".toStack(quantity = 4) }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = "dyeYellow".toOre()
            output {
                addStack { "lead_iodide".toStack(quantity = 4) }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = "dyeOrange".toOre()
            output {
                addStack { "potassium_dichromate".toStack(quantity = 4) }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = "dyeBlack".toOre()
            output {
                addStack { "titanium_oxide".toStack(quantity = 4) }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = "dyeGray".toOre()
            output {
                addStack { "barium_sulfate".toStack(quantity = 4) }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = "dyeMagenta".toOre()
            output {
                addStack { "han_purple".toStack(quantity = 4) }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = "dyeLightBlue".toOre()
            output {
                addGroup {
                    addStack { "cobalt_aluminate".toStack(quantity = 2) }
                    addStack { "antimony_trioxide".toStack(quantity = 2) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = "dyeLightGray".toOre()
            output {
                addStack { "magnesium_sulfate".toStack(quantity = 4) }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = "dyeCyan".toOre()
            output {
                addStack { "copper_chloride".toStack(quantity = 4) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.REDSTONE_BLOCK.toIngredient()
            output {
                addGroup {
                    addStack { "iron_oxide".toStack(9) }
                    addStack { "strontium_carbonate".toStack(9) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.SKULL.toIngredient(meta = 1)
            output {
                addGroup {
                    addStack { "hydroxylapatite".toStack(8) }
                    addStack { "mendelevium".toStack(32) }
                }
            }
        })

        listOf(Blocks.PURPUR_BLOCK, Blocks.PURPUR_PILLAR).forEach {
            dissolverRecipes.add(dissolverRecipe {
                input = it.toIngredient()
                output {
                    relativeProbability = false
                    addGroup {
                        addStack { "silicon_dioxide".toStack(4) }; probability = 100.0
                    }
                    addGroup {
                        addStack { "lutetium".toStack() };probability = 50.0
                    }
                }
            })
        }


        dissolverRecipes.add(dissolverRecipe
        {
            input = "protein".toStack().toIngredient()
            output {
                addGroup {
                    addStack { "carbon".toStack(3) };
                    addStack { "hydrogen".toStack(7) }
                    addStack { "nitrogen".toStack() }
                    addStack { "oxygen".toStack(2) }
                    addStack { "sulfur".toStack() }
                }
            }
        })


        dissolverRecipes.add(dissolverRecipe
        {
            input = Blocks.CLAY.toIngredient()
            output {
                addStack { "kaolinite".toStack(4) }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.CLAY_BALL.toIngredient()
            reversible = true
            output {
                addStack { "kaolinite".toStack() }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.SUGAR.toIngredient()
            reversible = true
            output {
                addStack { "sucrose".toStack() }
            }
        })


        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.BEETROOT.toIngredient()
            output {
                relativeProbability = false
                addGroup {
                    probability = 100.0
                    addStack { "sucrose".toStack() }
                }
                addGroup {
                    probability = 50.0
                    addStack { "iron_oxide".toStack() }
                }
            }

        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.BONE.toIngredient()
            reversible = true
            output {
                relativeProbability = false
                addGroup { addStack { "hydroxylapatite".toStack(3) }; probability = 50.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Blocks.OBSIDIAN.toIngredient()
            output {
                addGroup {
                    addStack { "magnesium_oxide".toStack(8) }
                    addStack { "potassium_chloride".toStack(8) }
                    addStack { "aluminum_oxide".toStack(8) }
                    addStack { "silicon_dioxide".toStack(24) }

                }

            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.FEATHER.toIngredient()
            output {
                addGroup { addStack { "protein".toStack(2) } }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.DYE.toIngredient(meta = 15) //bonemeal
            output {
                relativeProbability = false
                addGroup { addStack { "hydroxylapatite".toStack(1) }; probability = 50.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Blocks.BONE_BLOCK.toIngredient()
            output {
                rolls = 9
                relativeProbability = false
                addGroup { addStack { "hydroxylapatite".toStack(1) }; probability = 50.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.EGG.toIngredient()
            reversible = true
            output {
                addGroup {
                    addStack { "calcium_carbonate".toStack(8) }
                    addStack { "protein".toStack(2) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = ModItems.mineralSalt.toIngredient()
            output {
                addGroup { addStack { "sodium_chloride".toStack() }; probability = 60.0 }
                addGroup { addStack { "lithium".toStack() }; probability = 5.0 }
                addGroup { addStack { "potassium_chloride".toStack() }; probability = 10.0 }
                addGroup { addStack { "magnesium".toStack() }; probability = 10.0 }
                addGroup { addStack { "iron".toStack() }; probability = 5.0 }
                addGroup { addStack { "copper".toStack() }; probability = 4.0 }
                addGroup { addStack { "zinc".toStack() }; probability = 2.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.COAL.toIngredient()
            output {
                addStack { "carbon".toStack(quantity = 8) }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.COAL.toIngredient(meta = 1)
            output {
                addStack { "carbon".toStack(quantity = 8) }
            }
        })


        dissolverRecipes.add(dissolverRecipe
        {
            input = "slabWood".toOre()
            output {
                relativeProbability = false
                addGroup { addStack { "cellulose".toStack() }; probability = 12.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = "slimeball".toOre()
            reversible = true
            output {
                addGroup {
                    addStack { "protein".toStack(2) }
                    addStack { "sucrose".toStack(2) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = "blockSlime".toOre()
            reversible = false
            output {
                addGroup {
                    addStack { "protein".toStack(2 * 9) }
                    addStack { "sucrose".toStack(2 * 9) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.STICK.toIngredient()
            output {
                relativeProbability = false
                addGroup { addStack { "cellulose".toStack() }; probability = 10.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Blocks.TORCH.toIngredient()
            output {
                relativeProbability = false
                addGroup { addStack { "carbon".toStack(2) }; probability = 100.0 }
                addGroup { addStack { "cellulose".toStack() }; probability = 2.5 }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Blocks.LADDER.toIngredient()
            output {
                rolls = 7
                relativeProbability = false
                addGroup { addStack { "cellulose".toStack() }; probability = 10.0 }
            }
        })


        if (oreNotEmpty("itemSilicon")) {
            dissolverRecipes.add(dissolverRecipe {
                input = "itemSilicon".toOre()
                output {
                    addStack { "silicon".toStack(16) }
                }
            })
        }

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.ENDER_PEARL.toIngredient()
            reversible = true
            output {
                addGroup {
                    addStack { "silicon".toStack(16) }
                    addStack { "mercury".toStack(16) }
                    addStack { "neodymium".toStack(16) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.DIAMOND.toIngredient()
            output {
                addStack { "carbon".toStack(quantity = 64 * 8) }
            }
        })


        dissolverRecipes.add(dissolverRecipe
        {
            input = Blocks.DIAMOND_ORE.toIngredient()
            output {
                addStack { "carbon".toStack(quantity = 64 * 8 * 2) }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Blocks.DIAMOND_BLOCK.toIngredient()
            output {
                addStack { "carbon".toStack(quantity = 64 * 8 * 9) }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = "plankWood".toOre()
            output {
                relativeProbability = false
                addGroup { addStack { "cellulose".toStack() }; probability = 25.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = "cobblestone".toOre()
            output {
                addGroup { addStack { ItemStack.EMPTY }; probability = 700.0 }
                addGroup { addStack { "aluminum".toStack(1) }; probability = 2.0 }
                addGroup { addStack { "iron".toStack(1) }; probability = 4.0 }
                addGroup { addStack { "gold".toStack(1) }; probability = 1.5 }
                addGroup { addStack { "silicon_dioxide".toStack(1) }; probability = 10.0 }
                addGroup { addStack { "dysprosium".toStack(1) }; probability = 1.0 }
                addGroup { addStack { "zirconium".toStack(1) }; probability = 1.5 }
                addGroup { addStack { "nickel".toStack(1) }; probability = 1.0 }
                addGroup { addStack { "gallium".toStack(1) }; probability = 1.0 }
                addGroup { addStack { "tungsten".toStack(1) }; probability = 1.0 }

            }
        })

        listOf("stoneGranite", "stoneGranitePolished").forEach {
            dissolverRecipes.add(dissolverRecipe {
                input = it.toOre()
                output {
                    addGroup { addStack { "aluminum_oxide".toStack(1) }; probability = 5.0 }
                    addGroup { addStack { "iron".toStack(1) }; probability = 2.0 }
                    addGroup { addStack { "potassium_chloride".toStack(1) }; probability = 2.0 }
                    addGroup { addStack { "silicon_dioxide".toStack(1) }; probability = 10.0 }
                    addGroup { addStack { "technetium".toStack(1) }; probability = 1.0 }
                    addGroup { addStack { "manganese".toStack(1) }; probability = 1.5 }
                    addGroup { addStack { "radium".toStack(1) }; probability = 1.5 }

                }
            })
        }

        listOf("stoneDiorite", "stoneDioritePolished").forEach {
            dissolverRecipes.add(dissolverRecipe {
                input = it.toOre()
                output {
                    addGroup { addStack { "aluminum_oxide".toStack(1) }; probability = 4.0 }
                    addGroup { addStack { "iron".toStack(1) }; probability = 2.0 }
                    addGroup { addStack { "potassium_chloride".toStack(1) }; probability = 4.0 }
                    addGroup { addStack { "silicon_dioxide".toStack(1) }; probability = 10.0 }
                    addGroup { addStack { "indium".toStack(1) }; probability = 1.5 }
                    addGroup { addStack { "manganese".toStack(1) }; probability = 2.0 }
                    addGroup { addStack { "osmium".toStack(1) }; probability = 2.0 }
                    addGroup { addStack { "tin".toStack() }; probability = 3.0; }
                }
            })
        }

        dissolverRecipes.add(dissolverRecipe
        {
            input = Blocks.MAGMA.toIngredient()
            output {
                rolls = 2
                addGroup { addStack { "manganese".toStack(2) }; probability = 10.0 }
                addGroup { addStack { "aluminum_oxide".toStack(1) }; probability = 5.0 }
                addGroup { addStack { "magnesium_oxide".toStack(1) }; probability = 20.0 }
                addGroup { addStack { "potassium_chloride".toStack(1) }; probability = 2.0 }
                addGroup { addStack { "silicon_dioxide".toStack(2) }; probability = 10.0 }
                addGroup { addStack { "sulfur".toStack(2) }; probability = 20.0 }
                addGroup { addStack { "iron_oxide".toStack() }; probability = 10.0 }
                addGroup { addStack { "lead".toStack(2) }; probability = 8.0 }
                addGroup { addStack { "fluorine".toStack() }; probability = 4.0 }
                addGroup { addStack { "bromine".toStack() }; probability = 4.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = "treeLeaves".toOre()
            output {
                relativeProbability = false
                addGroup { addStack { "cellulose".toStack() }; probability = 5.0 }
            }
        })

        listOf("stoneAndesite", "stoneAndesitePolished").forEach {
            dissolverRecipes.add(dissolverRecipe {
                input = it.toOre()
                output {
                    addGroup { addStack { "aluminum_oxide".toStack(1) }; probability = 4.0 }
                    addGroup { addStack { "iron".toStack(1) }; probability = 3.0 }
                    addGroup { addStack { "potassium_chloride".toStack(1) }; probability = 4.0 }
                    addGroup { addStack { "silicon_dioxide".toStack(1) }; probability = 10.0 }
                    addGroup { addStack { "platinum".toStack() }; probability = 2.0 }
                    addGroup { addStack { "calcium".toStack() }; probability = 4.0 }
                }
            })
        }

        dissolverRecipes.add(dissolverRecipe
        {
            input = "stone".toOre()
            output {
                addGroup { addStack { ItemStack.EMPTY }; probability = 20.0 }
                addGroup { addStack { "aluminum".toStack(1) }; probability = 2.0 }
                addGroup { addStack { "iron".toStack(1) }; probability = 4.0 }
                addGroup { addStack { "gold".toStack(1) }; probability = 1.5 }
                addGroup { addStack { "silicon_dioxide".toStack(1) }; probability = 20.0 }
                addGroup { addStack { "dysprosium".toStack(1) }; probability = 0.5 }
                addGroup { addStack { "zirconium".toStack(1) }; probability = 1.25 }
                addGroup { addStack { "tungsten".toStack(1) }; probability = 1.0 }
                addGroup { addStack { "nickel".toStack(1) }; probability = 1.0 }
                addGroup { addStack { "gallium".toStack(1) }; probability = 1.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Blocks.SAND.toIngredient()
            output {
                relativeProbability = false
                addGroup { addStack { "silicon_dioxide".toStack(quantity = 4) }; probability = 100.0 }
                addGroup { addStack { "gold".toStack() } }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Blocks.SAND.toIngredient(meta = 1) //red sand
            output {
                relativeProbability = false
                addGroup { addStack { "silicon_dioxide".toStack(quantity = 4) }; probability = 100.0 }
                addGroup { addStack { "iron_oxide".toStack() }; probability = 10.0 }
            }
        })

        listOf(0, 1).forEach {
            dissolverRecipes.add(dissolverRecipe {
                input = Blocks.RED_SANDSTONE.toIngredient(meta = it)
                output {
                    rolls = 4
                    relativeProbability = false
                    addGroup { addStack { "silicon_dioxide".toStack(quantity = 4) }; probability = 100.0 }
                    addGroup { addStack { "iron_oxide".toStack() }; probability = 10.0 }
                }
            })
        }


        dissolverRecipes.add(dissolverRecipe {
            input = Items.GUNPOWDER.toIngredient()
            reversible = true
            output {
                addGroup {
                    addStack { "potassium_nitrate".toStack(2) }
                    addStack { "sulfur".toStack(8) }
                    addStack { "carbon".toStack(8) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = "logWood".toOre()
            output {
                addStack { "cellulose".toStack() }
            }
        })

        metalOreData.forEach { data ->
            (0 until data.size).forEach { index ->
                val elementName = data.strs[index]
                val oreName = data.toDictName(index)
                val meta: Int = if (elementName == heathenSpelling) ElementRegistry.getMeta("aluminum") else ElementRegistry.getMeta(elementName)
                if (OreDictionary.doesOreNameExist(oreName) && OreDictionary.getOres(oreName).isNotEmpty()) {
                    dissolverRecipes.add(dissolverRecipe {
                        input = oreName.toOre()
                        output {
                            addGroup {
                                addStack {
                                    ModItems.elements.toStack(quantity = data.quantity, meta = meta)
                                }
                                if (oreName == "oreIron") {
                                    addStack { ModItems.elements.toStack(quantity = 2, meta = ElementRegistry["tungsten"]!!.meta) }
                                    addStack { ModItems.elements.toStack(quantity = 4, meta = ElementRegistry["sulfur"]!!.meta) }
                                } else if (oreName == "oreGold") {
                                    addStack { ModItems.elements.toStack(quantity = 2, meta = ElementRegistry["copper"]!!.meta) }
                                    addStack { ModItems.elements.toStack(quantity = 2, meta = ElementRegistry["silver"]!!.meta) }
                                }
                            }
                        }
                    })
                }
            }
        }

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.GLOWSTONE_DUST.toIngredient()
            reversible = true
            output {
                addStack { "phosphorus".toStack(quantity = 4) }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Blocks.GLOWSTONE.toIngredient()
            //reversible = true
            output {
                addStack { "phosphorus".toStack(quantity = 16) }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Blocks.IRON_BARS.toIngredient()
            output {
                addStack { "iron".toStack(quantity = 6) }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.BLAZE_POWDER.toIngredient()
            reversible = true
            output {
                addGroup {
                    addStack { "germanium".toStack(quantity = 8) }
                    addStack { "carbon".toStack(quantity = 8) }
                    addStack { "sulfur".toStack(quantity = 8) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Items.NETHER_WART.toIngredient()
            reversible = true
            output {
                addGroup {
                    addStack { "cellulose".toStack() }
                    addStack { "germanium".toStack(quantity = 4) }
                    addStack { "selenium".toStack(quantity = 4) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Blocks.NETHER_WART_BLOCK.toIngredient()
            output {
                addGroup {
                    addStack { "cellulose".toStack(quantity = 9) }
                    addStack { "germanium".toStack(quantity = 4 * 9) }
                    addStack { "selenium".toStack(quantity = 4 * 9) }
                }
            }
        })

        if (oreNotEmpty("dropHoney")) {
            dissolverRecipes.add(dissolverRecipe {
                input = "dropHoney".toOre()
                output {
                    addStack { "sucrose".toStack(quantity = 4) }
                }
            })
        }

        if (oreNotEmpty("gemPrismarine")) {
            dissolverRecipes.add(dissolverRecipe {
                input = "gemPrismarine".toOre()
                reversible = true
                output {
                    addGroup {
                        addStack { "beryl".toStack(quantity = 2) }
                        addStack { "cobalt_aluminate".toStack(quantity = 4) }
                    }
                }
            })
        }

        //addDissolverRecipesForAlloy("Bronze", "copper", 3, "tin", 1, conservationOfMass = true)
        //addDissolverRecipesForAlloy("Electrum", "gold", 1, "silver", 1, conservationOfMass = true)
        //addDissolverRecipesForAlloy("ElectricalSteel", "iron", 1, "carbon", 1, "silicon", 1, conservationOfMass = false)
        //addDissolverRecipesForAlloy("Invar", "iron", 2, "nickel", 1, conservationOfMass = true)
        listOf("ingotBronze", "plateBronze", "dustBronze", "blockBronze")
                .filter { oreNotEmpty(it) }
                .forEach {
                    dissolverRecipes.add(dissolverRecipe {
                        input = it.toOre()
                        output {
                            addGroup {
                                addStack { "copper".toStack(if (it == "blockBronze") 9 * 12 else 12) }
                                addStack { "tin".toStack(if (it == "blockBronze") 9 * 4 else 4) }
                            }
                        }
                    })
                }

        listOf("ingotElectrum", "plateElectrum", "dustElectrum", "blockElectrum")
                .filter { oreNotEmpty(it) }
                .forEach {
                    dissolverRecipes.add(dissolverRecipe {
                        input = it.toOre()
                        output {
                            addGroup {
                                addStack { "gold".toStack(if (it == "blockElectrum") 9 * 8 else 8) }
                                addStack { "silver".toStack(if (it == "blockElectrum") 9 * 8 else 8) }
                            }
                        }
                    })
                }

        listOf("gemRuby", "dustRuby", "plateRuby")
                .filter { oreNotEmpty(it) }
                .forEach { ore ->
                    dissolverRecipes.add(dissolverRecipe {
                        input = ore.toOre()
                        output {
                            addGroup {
                                addStack { "aluminum_oxide".toStack(quantity = 16) }
                                addStack { "chromium".toStack(quantity = 8) }
                            }
                        }
                    })
                }

        listOf("gemSapphire", "dustSapphire", "plateSapphire")
                .filter { oreNotEmpty(it) }
                .forEach { ore ->
                    dissolverRecipes.add(dissolverRecipe {
                        input = ore.toOre()
                        output {
                            addGroup {
                                addStack { "aluminum_oxide".toStack(quantity = 16) }
                                addStack { "iron".toStack(quantity = 4) }
                                addStack { "titanium".toStack(quantity = 4) }

                            }
                        }
                    })
                }

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.MELON_BLOCK.toIngredient()
            output {
                relativeProbability = false
                addGroup {
                    probability = 50.0
                    addStack { "cucurbitacin".toStack(); }
                }
                addGroup {
                    probability = 1.0
                    addStack { "water".toStack(quantity = 4) }
                    addStack { "sucrose".toStack(quantity = 2) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = "blockCactus".toOre()
            reversible = true
            output {
                relativeProbability = false
                addGroup { addStack { "cellulose".toStack() }; probability = 100.0 }
                addGroup { addStack { "mescaline".toStack() }; probability = 50.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe
        {
            input = Blocks.HARDENED_CLAY.toIngredient()
            reversible = true
            output {
                addStack { "mullite".toStack(quantity = 2) }
            }
        })

        (0 until 16).forEach {
            dissolverRecipes.add(dissolverRecipe {
                input = Blocks.STAINED_HARDENED_CLAY.toIngredient(meta = it)
                reversible = false
                output {
                    addStack { "mullite".toStack(quantity = 2) }
                }
            })
        }

        listOf(Blocks.BLACK_GLAZED_TERRACOTTA,
                Blocks.BLUE_GLAZED_TERRACOTTA,
                Blocks.BROWN_GLAZED_TERRACOTTA,
                Blocks.CYAN_GLAZED_TERRACOTTA,
                Blocks.GRAY_GLAZED_TERRACOTTA,
                Blocks.GREEN_GLAZED_TERRACOTTA,
                Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA,
                Blocks.LIME_GLAZED_TERRACOTTA,
                Blocks.MAGENTA_GLAZED_TERRACOTTA,
                Blocks.ORANGE_GLAZED_TERRACOTTA,
                Blocks.PINK_GLAZED_TERRACOTTA,
                Blocks.PURPLE_GLAZED_TERRACOTTA,
                Blocks.RED_GLAZED_TERRACOTTA,
                Blocks.SILVER_GLAZED_TERRACOTTA,
                Blocks.WHITE_GLAZED_TERRACOTTA,
                Blocks.YELLOW_GLAZED_TERRACOTTA).forEach {
            dissolverRecipes.add(dissolverRecipe {
                input = it.toIngredient()
                reversible = false
                output {
                    addStack { "mullite".toStack(quantity = 2) }
                }
            })
        }

        if (oreNotEmpty("cropRice")) {
            dissolverRecipes.add(dissolverRecipe {
                input = "cropRice".toOre()
                output {
                    addGroup {
                        relativeProbability = false
                        probability = 10.0
                        addStack { "starch".toStack(); }
                    }
                }
            })
        }
    }


    fun initElectrolyzerRecipes() {
        electrolyzerRecipes.add(ElectrolyzerRecipe(
                input = FluidRegistry.WATER.toStack(quantity = 125),
                _electrolyte = "calcium_carbonate".toStack().toIngredient(),
                electrolyteConsumptionChance = 20,
                outputOne = "hydrogen".toStack(4),
                outputTwo = "oxygen".toStack(2)))

        electrolyzerRecipes.add(ElectrolyzerRecipe(
                input = FluidRegistry.WATER.toStack(125),
                _electrolyte = "sodium_chloride".toStack().toIngredient(),
                electrolyteConsumptionChance = 20,
                outputOne = "hydrogen".toStack(2),
                outputTwo = "oxygen".toStack(1),
                outputThree = "chlorine".toStack(2), output3Probability = 10))
    }


    fun initEvaporatorRecipes() {
        evaporatorRecipes.run {
            this.add(EvaporatorRecipe(FluidRegistry.WATER, 125, ModItems.mineralSalt.toStack()))
            this.add(EvaporatorRecipe(FluidRegistry.LAVA, 1000, Blocks.MAGMA.toStack()))
            FluidRegistry.getFluid("milk")?.let {
                this.add(EvaporatorRecipe(FluidRegistry.getFluid("milk"), 500, ModItems.condensedMilk.toStack()))
            }
        }
    }


    fun initFuelHandler() {
        val fuelHandler = object : IFuelHandler {
            override fun getBurnTime(fuel: ItemStack?): Int {
                if (fuel != null) {
                    if (fuel.item == ModItems.elements) {
                        return when (fuel.itemDamage) {
                            ElementRegistry["hydrogen"]?.meta -> 20
                            ElementRegistry["carbon"]?.meta   -> 200
                            else                              -> 0
                        }
                    } else if (fuel.item == ModItems.compounds) {
                        return when (fuel.itemDamage) {
                            CompoundRegistry["methane"]?.meta -> ((1 * 200) + (4 * 20))
                            CompoundRegistry["ethane"]?.meta  -> ((2 * 200) + (6 * 20))
                            CompoundRegistry["propane"]?.meta -> ((3 * 200) + (8 * 20))
                            CompoundRegistry["butane"]?.meta  -> ((4 * 200) + (10 * 20))
                            CompoundRegistry["pentane"]?.meta -> ((5 * 200) + (12 * 20))
                            CompoundRegistry["hexane"]?.meta  -> ((6 * 200) + (14 * 20))
                            else                              -> 0
                        }
                    }
                }
                return 0
            }
        }
        GameRegistry.registerFuelHandler(fuelHandler)
    }


    fun initCombinerRecipes() {
        combinerRecipes.add(CombinerRecipe(Items.COAL.toStack(meta = 1), listOf(null, null, "carbon".toStack(8))))

        combinerRecipes.add(CombinerRecipe(Items.COAL.toStack(), listOf(null, "carbon".toStack(8))))

        combinerRecipes.add(CombinerRecipe(Blocks.GLOWSTONE.toStack(), listOf(null, "phosphorus".toStack(16))))
        listOf(0, 1, 2).forEach {
            val input = (0 until it).mapTo(ArrayList<ItemStack>()) { ItemStack.EMPTY }.toMutableList()
            combinerRecipes.add(CombinerRecipe(Blocks.QUARTZ_BLOCK.toStack(meta = it),
                    input.apply {
                        add(0, ItemStack.EMPTY);
                        add("barium".toStack(32));
                        add("silicon_dioxide".toStack(64))
                    }))
        }

        metals.forEach { entry ->
            val dustOutput: ItemStack? = firstOre(entry.toDict("dust"))
            if (dustOutput != null && !dustOutput.isEmpty) {
                combinerRecipes.add(CombinerRecipe(dustOutput,
                        listOf(ItemStack.EMPTY, if (entry == heathenSpelling) "aluminum".toStack(16) else entry.toStack(16))))
            }

            val ingotOutput: ItemStack? = firstOre(entry.toDict("ingot"))
            if (ingotOutput != null && !ingotOutput.isEmpty) {
                combinerRecipes.add(CombinerRecipe(ingotOutput,
                        listOf(if (entry == heathenSpelling) "aluminum".toStack(16) else entry.toStack(16))))
            }
        }

        listOf("lumpSalt", "materialSalt", "salt", "itemSalt", "dustSalt", "foodSalt")
                .filter { oreNotEmpty(it) }
                .forEachIndexed { i, name ->
                    val input = (0 until i).mapTo(ArrayList<ItemStack>()) { ItemStack.EMPTY }.toMutableList()
                    combinerRecipes.add(CombinerRecipe(firstOre(name), input.apply { add("sodium_chloride".toStack(8)) }))
                }

        listOf("dustSaltpeter", "nitrate", "nitre")
                .filter { oreNotEmpty(it) }
                .forEachIndexed { i, name ->
                    val input = (0 until i).mapTo(ArrayList<ItemStack>()) { ItemStack.EMPTY }.toMutableList()
                    combinerRecipes.add(CombinerRecipe(firstOre(name), input.apply { add("potassium_nitrate".toStack(8)) }))
                }

        combinerRecipes.add(CombinerRecipe("triglyceride".toStack(),
                listOf(null, null, "oxygen".toStack(2),
                        null, "hydrogen".toStack(32), null,
                        "carbon".toStack(18))))

        combinerRecipes.add(CombinerRecipe("cucurbitacin".toStack(),
                listOf(null, null, null,
                        null, "hydrogen".toStack(44), null,
                        "carbon".toStack(32), null, "oxygen".toStack(8))))

        CompoundRegistry.compounds()
                .filter { it.autoCombinerRecipe }
                .forEach { compound ->
                    if (compound.shiftedSlots > 0) {
                        val inputList: List<Any> = compound.toItemStackList().toMutableList().also { list ->
                            (0 until compound.shiftedSlots).forEach { list.add(0, ItemStack.EMPTY) }
                        }
                        assert(inputList.size <= 9)
                        combinerRecipes.add(CombinerRecipe(compound.toItemStack(1), inputList))
                    } else combinerRecipes.add(CombinerRecipe(compound.toItemStack(1), compound.toItemStackList()))
                }

        dissolverRecipes.filter { it.reversible }.forEach { recipe ->
            if (recipe.inputs.isNotEmpty()) combinerRecipes.add(CombinerRecipe(recipe.inputs[0], recipe.outputs.toStackList()))
        }


        val carbon = "carbon".toStack(quantity = 64)
        combinerRecipes.add(CombinerRecipe(Items.DIAMOND.toStack(),
                listOf(carbon, carbon, carbon,
                        carbon, null, carbon,
                        carbon, carbon, carbon)))

        combinerRecipes.add(CombinerRecipe(Blocks.SAND.toStack(),
                listOf(null, null, null,
                        null, null, null,
                        null, null, "silicon_dioxide".toStack(4))))

        combinerRecipes.add(CombinerRecipe(Blocks.SAND.toStack(quantity = 8, meta = 1), //red sand
                listOf(null, null, null,
                        "silicon_dioxide".toStack(quantity = 32), "iron_oxide".toStack())))

        combinerRecipes.add(CombinerRecipe(Blocks.COBBLESTONE.toStack(quantity = 2),
                listOf("silicon_dioxide".toStack())))

        combinerRecipes.add(CombinerRecipe(Blocks.STONE.toStack(),
                listOf(ItemStack.EMPTY, "silicon_dioxide".toStack())))

        combinerRecipes.add(CombinerRecipe(Blocks.OBSIDIAN.toStack(),
                listOf("magnesium_oxide".toStack(8), "potassium_chloride".toStack(8), "aluminum_oxide".toStack(8),
                        "silicon_dioxide".toStack(24))))

        combinerRecipes.add(CombinerRecipe(Blocks.CLAY.toStack(),
                listOf(ItemStack.EMPTY, "kaolinite".toStack(4))))

        combinerRecipes.add(CombinerRecipe(Blocks.DIRT.toStack(4),
                listOf("water".toStack(), "cellulose".toStack(), "kaolinite".toStack())))

        combinerRecipes.add(CombinerRecipe(Blocks.MYCELIUM.toStack(4),
                listOf(null, null, null,
                        null, null, "psilocybin".toStack(),
                        "water".toStack(), "cellulose".toStack(), "kaolinite".toStack())))

        combinerRecipes.add(CombinerRecipe(Items.FEATHER.toStack(),
                listOf(null, null, null,
                        null, null, "protein".toStack(2))))

        combinerRecipes.add(CombinerRecipe(Items.SPIDER_EYE.toStack(),
                listOf(null, "beta_carotene".toStack(2), "protein".toStack(2))))

        combinerRecipes.add(CombinerRecipe(Blocks.SPONGE.toStack(),
                listOf(null, "calcium_carbonate".toStack(8), "kaolinite".toStack(8))))

        combinerRecipes.add(CombinerRecipe(Blocks.GRASS.toStack(4),
                listOf(null, null, null,
                        "water".toStack(), "cellulose".toStack(), "kaolinite".toStack())))

        combinerRecipes.add(CombinerRecipe(Blocks.GRAVEL.toStack(),
                listOf(null, null, "silicon_dioxide".toStack(1))))

        combinerRecipes.add(CombinerRecipe(Items.WATER_BUCKET.toStack(),
                listOf(null, null, null,
                        null, "water".toStack(16), null,
                        null, Items.BUCKET, null)))


        combinerRecipes.add(CombinerRecipe(Items.MILK_BUCKET.toStack(),
                listOf(null, null, null,
                        "protein".toStack(2), "water".toStack(16), "sucrose".toStack(),
                        null, Items.BUCKET, null)))

        combinerRecipes.add(CombinerRecipe(Items.POTIONITEM.toStack()
                .apply { this.setTagInfo("Potion", net.minecraft.nbt.NBTTagString("water")) },
                listOf(null, null, null,
                        null, "water".toStack(16), null,
                        null, Items.GLASS_BOTTLE, null)))

        combinerRecipes.add(CombinerRecipe(Blocks.REDSTONE_BLOCK.toStack(),
                listOf(null, null, null,
                        "iron_oxide".toStack(9), "strontium_carbonate".toStack(9))))

        combinerRecipes.add(CombinerRecipe(Items.STRING.toStack(4),
                listOf(null, "protein".toStack(2))))

        combinerRecipes.add(CombinerRecipe(Blocks.WOOL.toStack(),
                listOf(null, null, null,
                        null, null, null,
                        "protein".toStack(1), "triglyceride".toStack(1))))

        combinerRecipes.add(CombinerRecipe(Items.CARROT.toStack(),
                listOf(null, null, null,
                        "cellulose".toStack(), "beta_carotene".toStack())))

        combinerRecipes.add(CombinerRecipe(Items.REEDS.toStack(),
                listOf(null, null, null,
                        "cellulose".toStack(), "sucrose".toStack())))

        combinerRecipes.add(CombinerRecipe(Blocks.STONE.toStack(meta = 1), //granite
                listOf(null, null, null,
                        "silicon_dioxide".toStack(1))))

        combinerRecipes.add(CombinerRecipe(Blocks.STONE.toStack(meta = 3), //diorite
                listOf(null, null, null,
                        null, "silicon_dioxide".toStack(1))))

        if (oreNotEmpty("itemSilicon")) {
            val rubyStack = firstOre("itemSilicon")
            combinerRecipes.add(CombinerRecipe(rubyStack,
                    listOf(null, "silicon".toStack(16))))
        }

        combinerRecipes.add(CombinerRecipe(Blocks.STONE.toStack(meta = 5), //andesite
                listOf(null, null, null,
                        null, null, "silicon_dioxide".toStack(1))))

        combinerRecipes.add(CombinerRecipe(Items.FLINT.toStack(),
                listOf(null, null, null,
                        null, null, null,
                        null, "silicon_dioxide".toStack(3), null)))

        combinerRecipes.add(CombinerRecipe(Items.POTATO.toStack(),
                listOf("starch".toStack(), "potassium".toStack(4))))

        combinerRecipes.add(CombinerRecipe(Items.APPLE.toStack(),
                listOf(null, "cellulose".toStack(), null,
                        null, "sucrose".toStack(1), null)))

        combinerRecipes.add(CombinerRecipe(ModItems.fertilizer.toStack(8),
                listOf("urea".toStack(1),
                        "diammonium_phosphate".toStack(1),
                        "potassium_chloride".toStack(1))))

        if (oreNotEmpty("gemRuby")) {
            val rubyStack = firstOre("gemRuby")
            combinerRecipes.add(CombinerRecipe(rubyStack,
                    listOf("aluminum_oxide".toStack(16), "chromium".toStack(8))))
        }

        if (oreNotEmpty("gemSapphire")) {
            combinerRecipes.add(CombinerRecipe(firstOre("gemSapphire"),
                    listOf("aluminum_oxide".toStack(16),
                            "iron".toStack(4),
                            "titanium".toStack(4))))
        }

        val seeds = listOf(Items.WHEAT_SEEDS.toStack(),
                Items.PUMPKIN_SEEDS.toStack(),
                Items.MELON_SEEDS.toStack(),
                Items.BEETROOT_SEEDS.toStack())

        seeds.withIndex().forEach { (index: Int, stack: ItemStack) ->
            val inputs = mutableListOf(null, "triglyceride".toStack(), null)
            (0 until index).forEach { inputs.add(null) }
            inputs.add("sucrose".toStack())
            if (stack.item == Items.BEETROOT_SEEDS) inputs.add("iron_oxide".toStack())
            combinerRecipes.add(CombinerRecipe(stack, inputs))
        }

        combinerRecipes.add(CombinerRecipe(Items.BEETROOT.toStack(), listOf(
                null, "sucrose".toStack(), "iron_oxide".toStack())))



        Item.REGISTRY.getObject(ResourceLocation("forestry", "iodine_capsule"))?.let {
            combinerRecipes.add(CombinerRecipe(it.toStack(),
                    listOf(null, null, null,
                            "iodine".toStack(8), "iodine".toStack(8))))
        }


        (0..5).forEach { i ->
            val input = (0 until i).mapTo(ArrayList<ItemStack>(), { ItemStack.EMPTY })
            input.add("oxygen".toStack())
            input.add("cellulose".toStack(2))
            combinerRecipes.add(CombinerRecipe(Blocks.SAPLING.toStack(quantity = 4, meta = i), input))
        }

        (0 until 6).forEach { i ->
            val input = (0 until i).mapTo(ArrayList<ItemStack>()) { ItemStack.EMPTY }
            input.add("cellulose".toStack())

            //y u gotta do dis mojang
            if (i < 4) combinerRecipes.add(CombinerRecipe(ItemStack(Blocks.LOG, 1, i), input))
            else combinerRecipes.add(CombinerRecipe(ItemStack(Blocks.LOG2, 1, i - 4), input))
        }


        combinerRecipes.add(CombinerRecipe(Items.DYE.toStack(meta = 0), listOf("titanium_oxide".toStack(4))))
        combinerRecipes.add(CombinerRecipe(Items.DYE.toStack(meta = 1), listOf("mercury_sulfide".toStack(4))))
        combinerRecipes.add(CombinerRecipe(Items.DYE.toStack(meta = 2), listOf("nickel_chloride".toStack(4))))
        combinerRecipes.add(CombinerRecipe(Items.DYE.toStack(meta = 3), listOf("caffeine".toStack(1), "cellulose".toStack(1))))
        combinerRecipes.add(CombinerRecipe(Items.DYE.toStack(meta = 5), listOf("potassium_permanganate".toStack(4))))
        combinerRecipes.add(CombinerRecipe(Items.DYE.toStack(meta = 6), listOf("copper_chloride".toStack(4))))
        combinerRecipes.add(CombinerRecipe(Items.DYE.toStack(meta = 7), listOf("magnesium_sulfate".toStack(4))))
        combinerRecipes.add(CombinerRecipe(Items.DYE.toStack(meta = 8), listOf("barium_sulfate".toStack(4))))
        combinerRecipes.add(CombinerRecipe(Items.DYE.toStack(meta = 9), listOf("arsenic_sulfide".toStack(4))))
        combinerRecipes.add(CombinerRecipe(Items.DYE.toStack(meta = 10),
                listOf("cadmium_sulfide".toStack(2), "chromium_oxide".toStack(2))))
        combinerRecipes.add(CombinerRecipe(Items.DYE.toStack(meta = 11), listOf("lead_iodide".toStack(4))))
        combinerRecipes.add(CombinerRecipe(Items.DYE.toStack(meta = 12),
                listOf("cobalt_aluminate".toStack(2), "antimony_trioxide".toStack(2))))
        combinerRecipes.add(CombinerRecipe(Items.DYE.toStack(meta = 13), listOf("han_purple".toStack(4))))
        combinerRecipes.add(CombinerRecipe(Items.DYE.toStack(meta = 14), listOf("potassium_dichromate".toStack(4))))


        combinerRecipes.add(CombinerRecipe(Items.SNOWBALL.toStack(),
                listOf(null, null, null,
                        null, null, null,
                        "water".toStack(4))))

        combinerRecipes.add(CombinerRecipe(Blocks.SNOW.toStack(),
                listOf(null, null, null,
                        null, null, null,
                        null, "water".toStack(16))))

        combinerRecipes.add(CombinerRecipe(Blocks.ICE.toStack(),
                listOf(null, null, null,
                        null, null, null,
                        null, null, "water".toStack(16))))


        combinerRecipes.add(CombinerRecipe(Items.DYE.toStack(quantity = 3, meta = 15),
                listOf(null, null, "hydroxylapatite".toStack(2))))

        combinerRecipes.add(CombinerRecipe(Items.LEATHER.toStack(),
                listOf(null, null, null,
                        null, "protein".toStack(3))))


        combinerRecipes.add(CombinerRecipe(Items.ROTTEN_FLESH.toStack(),
                listOf(null, null, null,
                        null, null, null,
                        null, "protein".toStack(3))))

        combinerRecipes.add(CombinerRecipe(Items.NETHER_STAR.toStack(),
                listOf("lutetium".toStack(64), "hydrogen".toStack(64), "titanium".toStack(64),
                        "hydrogen".toStack(64), "hydrogen".toStack(64), "hydrogen".toStack(64),
                        "dysprosium".toStack(64), "hydrogen".toStack(64), "mendelevium".toStack(64))))
    }

    fun initAtomizerRecipes() {
        atomizerRecipes.add(AtomizerRecipe(true, FluidStack(FluidRegistry.WATER, 500), "water".toStack(8)))

        if (fluidExists("if.protein")) {
            atomizerRecipes.add(AtomizerRecipe(true,
                    FluidRegistry.getFluidStack("if.protein", 500)!!, "protein".toStack(8)))
        }
        if (fluidExists("canolaoil")) {
            atomizerRecipes.add(AtomizerRecipe(true,
                    FluidRegistry.getFluidStack("canolaoil", 500)!!, "triglyceride".toStack(4)))
        }
        if (fluidExists("cocoa_butter")) {
            atomizerRecipes.add(AtomizerRecipe(true,
                    FluidRegistry.getFluidStack("cocoa_butter", 144)!!, "triglyceride".toStack(1)))
        }
        if (fluidExists("ethanol")) {
            atomizerRecipes.add(AtomizerRecipe(true,
                    FluidRegistry.getFluidStack("ethanol", 500)!!, "ethanol".toStack(8)))
        }

        ElementRegistry.getAllElements().forEach {
            if (fluidExists(it.name)) {
                atomizerRecipes.add(AtomizerRecipe(true,
                        FluidRegistry.getFluidStack(it.name, 144)!!, it.name.toStack(16)))
            }
        }
    }

    fun initLiquifierRecipes() {

        atomizerRecipes.filter { it.reversible }.forEach {
            liquifierRecipes.add(LiquifierRecipe(it.output.copy(), it.input.copy()))
        }

        ElementRegistry.getAllElements().forEach {
            if (fluidExists(it.name)) {
                liquifierRecipes.add(LiquifierRecipe(
                        it.name.toStack(16), FluidRegistry.getFluidStack(it.name, 144)!!
                ))
            }
        }
    }

    fun initFissionRecipes() {
        for (i in ElementRegistry.keys().filterNot { it == 1 }) {
            val output1 = if (i % 2 == 0) i / 2 else (i / 2) + 1
            val output2 = if (i % 2 == 0) 0 else i / 2
            if (ElementRegistry[output1] != null && (output2 == 0 || ElementRegistry[output2] != null)) {
                fissionRecipes.add(FissionRecipe(i, output1, output2))
            }
        }
    }
}

fun fluidExists(name: String): Boolean = FluidRegistry.isFluidRegistered(name)

fun oreNotEmpty(ore: String) = oreExists(ore) && OreDictionary.getOres(ore).isNotEmpty()