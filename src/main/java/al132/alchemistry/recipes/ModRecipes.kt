package al132.alchemistry.recipes

import al132.alchemistry.chemistry.CompoundRegistry
import al132.alchemistry.chemistry.ElementRegistry
import al132.alchemistry.items.ModItems
import al132.alchemistry.utils.extensions.toCompoundStack
import al132.alchemistry.utils.extensions.toElementStack
import al132.alchemistry.utils.extensions.toOre
import al132.alchemistry.utils.extensions.toStack
import al132.alib.utils.Utils.firstOre
import al132.alib.utils.Utils.oreExists
import al132.alib.utils.extensions.toDict
import al132.alib.utils.extensions.toIngredient
import al132.alib.utils.extensions.toStack
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

    val metals: List<String> = listOf(//not all technically metals, I know
            "aluminum",
            "arsenic",
            "beryllium",
            "bismuth",
            "boron",
            "cadmium",
            "calcium",
            "cerium",
            "chromium",
            "cobalt",
            "copper",
            "dysprosium",
            "erbium",
            "gadolinium",
            "gold",
            "holmium",
            "iridium",
            "iron",
            "lanthanum",
            "lead",
            "lithium",
            "lutetium",
            "magnesium",
            "manganese",
            "neodymium",
            "nickel",
            "niobium",
            "osmium",
            "phosphorus",
            "platinum",
            "potassium",
            "praseodymium",
            "samarium",
            "scandium",
            "silicon",
            "silver",
            "sodium",
            "sulfur",
            "tantalum",
            "terbium",
            "thorium",
            "thulium",
            "tin",
            "titanium",
            "tungsten",
            "uranium",
            "ytterbium",
            "yttrium",
            "zinc")

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
    }


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
                    addGroup { addStack { "cellulose".toCompoundStack() }; probability = 25.0 }
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
                                addStack { "chromium".toElementStack(16) }
                            }
                        }
                    })
                }

        if (oreNotEmpty("blockChrome")) {
            dissolverRecipes.add(dissolverRecipe {
                input = "blockChrome".toOre()
                output {
                    addGroup {
                        addStack { "chromium".toElementStack(16 * 9) }
                    }
                }
            })
        }

        if (oreNotEmpty("oreChrome")) {
            dissolverRecipes.add(dissolverRecipe {
                input = "oreChrome".toOre()
                output {
                    addGroup {
                        addStack { "chromium".toElementStack(16 * 2) }
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
                        addStack { "potassium_carbonate".toCompoundStack(4) }
                    }
                }
            })
        }

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.COAL_ORE.toIngredient()
            output {
                addGroup { addStack { "carbon".toElementStack(quantity = 32) } }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.NETHERRACK.toIngredient()
            output {
                addGroup { addStack { ItemStack.EMPTY }; probability = 15.0 }
                addGroup { addStack { "zinc_oxide".toCompoundStack() }; probability = 2.0 }
                addGroup { addStack { "gold".toElementStack() }; probability = 1.0 }
                addGroup { addStack { "phosphorus".toElementStack() }; probability = 1.0 }
                addGroup { addStack { "sulfur".toElementStack() }; probability = 3.0 }
                addGroup { addStack { "germanium".toElementStack() }; probability = 1.0 }
                addGroup { addStack { "silicon".toElementStack() }; probability = 4.0 }

            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.NETHERBRICK.toIngredient()
            output {
                addGroup { addStack { ItemStack.EMPTY }; probability = 5.0 }
                addGroup { addStack { "zinc_oxide".toCompoundStack() }; probability = 2.0 }
                addGroup { addStack { "gold".toElementStack() }; probability = 1.0 }
                addGroup { addStack { "phosphorus".toElementStack() }; probability = 1.0 }
                addGroup { addStack { "sulfur".toElementStack() }; probability = 4.0 }
                addGroup { addStack { "germanium".toElementStack() }; probability = 1.0 }
                addGroup { addStack { "silicon".toElementStack() }; probability = 4.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.IRON_HORSE_ARMOR.toIngredient()
            output {
                addStack { "iron".toElementStack(64) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.DIAMOND_HORSE_ARMOR.toIngredient()
            output {
                addStack { "carbon".toElementStack(4 * (64 * 8)) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.ANVIL.toIngredient()
            output {
                addStack { "iron".toElementStack((144 * 3) + (16 * 4)) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.IRON_DOOR.toIngredient()
            output {
                addStack { "iron".toElementStack(32) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.IRON_TRAPDOOR.toIngredient()
            output {
                addStack { "iron".toElementStack(64) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.CHEST.toIngredient()
            output {
                addStack { "cellulose".toCompoundStack(2) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.CRAFTING_TABLE.toIngredient()
            output {
                addStack { "cellulose".toCompoundStack(1) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.WEB.toIngredient()
            output {
                addStack { "protein".toCompoundStack(2) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.GOLDEN_HORSE_ARMOR.toIngredient()
            output {
                addStack { "gold".toElementStack(64) }
            }
        })

        (0 until 16).forEach { index ->
            dissolverRecipes.add(dissolverRecipe {
                input = Blocks.WOOL.toIngredient(meta = index)
                output {
                    addStack { "protein".toCompoundStack(1) }
                    addStack { "triglyceride".toCompoundStack(1) }

                }
            })
        }

        dissolverRecipes.add(dissolverRecipe {
            input = Items.EMERALD.toIngredient()
            output {
                reversible = true
                addGroup {
                    addStack { "beryl".toCompoundStack(8) }
                    addStack { "chromium".toElementStack(8) }
                    addStack { "vanadium".toElementStack(4) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.END_STONE.toIngredient()
            output {
                addGroup { addStack { "mercury".toElementStack() }; probability = 50.0 }
                addGroup { addStack { "neodymium".toElementStack() }; probability = 5.0 }
                addGroup { addStack { "silicon_dioxide".toCompoundStack(2) }; probability = 250.0 }
                addGroup { addStack { "lithium".toElementStack() }; probability = 50.0 }
                addGroup { addStack { "thorium".toStack() }; probability = 2.0 }
            }
        })


        dissolverRecipes.add(dissolverRecipe {
            input = "record".toOre()
            output {
                addGroup {
                    addStack { "polyvinyl_chloride".toStack(64) };
                    addStack { "lead".toStack(16) }
                    addStack { "cadmium".toStack(16) }
                }
            }
        })

        listOf(Blocks.GRASS.toStack(), Blocks.DIRT.toStack(), Blocks.DIRT.toStack(meta = 1), Blocks.DIRT.toStack(meta = 2)).forEach {
            dissolverRecipes.add(dissolverRecipe {
                input = it.toIngredient()
                output {
                    addGroup { addStack { "water".toCompoundStack() }; probability = 30.0 }
                    addGroup { addStack { "silicon_dioxide".toCompoundStack() }; probability = 50.0 }
                    addGroup { addStack { "cellulose".toCompoundStack() }; probability = 10.0 }
                    addGroup { addStack { "kaolinite".toCompoundStack() }; probability = 10.0 }
                }
            })
        }

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.EMERALD_BLOCK.toIngredient()
            output {
                addGroup {
                    addStack { "beryl".toCompoundStack(8 * 9) }
                    addStack { "chromium".toElementStack(8 * 9) }
                    addStack { "vanadium".toElementStack(4 * 9) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = "blockGlass".toOre()
            output {
                addStack { "silicon_dioxide".toCompoundStack(4) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = "treeSapling".toOre()
            output {
                relativeProbability = false
                addGroup { addStack { "cellulose".toCompoundStack(1) }; probability = 25.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.DEADBUSH.toIngredient()
            output {
                relativeProbability = false
                addGroup { addStack { "cellulose".toCompoundStack() }; probability = 25.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.VINE.toIngredient()
            output {
                relativeProbability = false
                addGroup { addStack { "cellulose".toCompoundStack() }; probability = 25.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.WATERLILY.toIngredient()
            output {
                relativeProbability = false
                addGroup { addStack { "cellulose".toCompoundStack() }; probability = 25.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.PUMPKIN.toIngredient()
            output {
                relativeProbability = false
                addGroup {
                    probability = 50.0
                    addStack { "cucurbitacin".toCompoundStack() }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.QUARTZ.toIngredient()
            reversible = true
            output {
                addGroup {
                    addStack { "barium".toElementStack(8) }
                    addStack { "silicon_dioxide".toCompoundStack(16) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.QUARTZ_BLOCK.toIngredient()
            reversible = true
            output {
                addGroup {
                    addStack { "barium".toElementStack(8 * 4) }
                    addStack { "silicon_dioxide".toCompoundStack(16 * 4) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.BROWN_MUSHROOM.toIngredient()
            reversible = true
            output {
                addGroup {
                    addStack { "psilocybin".toCompoundStack() }
                    addStack { "cellulose".toCompoundStack() }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.RED_MUSHROOM.toIngredient()
            reversible = true
            output {
                addGroup {
                    addStack { "cellulose".toCompoundStack() }
                    addStack { "psilocybin".toCompoundStack() }
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
            input = Items.DYE.toIngredient(quantity = 4, meta = 4) //lapis
            output {
                reversible = true
                addGroup {
                    addStack { "sodium".toElementStack(6) }
                    addStack { "calcium".toElementStack(2) }
                    addStack { "aluminum".toElementStack(6) }
                    addStack { "silicon".toElementStack(6) }
                    addStack { "oxygen".toElementStack(24) }
                    addStack { "sulfur".toElementStack(2) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.LAPIS_BLOCK.toIngredient()
            output {
                addGroup {
                    addStack { "sodium".toElementStack(6 * 9) }
                    addStack { "calcium".toElementStack(2 * 9) }
                    addStack { "aluminum".toElementStack(6 * 9) }
                    addStack { "silicon".toElementStack(6 * 9) }
                    addStack { "oxygen".toElementStack(24 * 9) }
                    addStack { "sulfur".toElementStack(2 * 9) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.STRING.toIngredient()
            output {
                relativeProbability = false
                addGroup {
                    probability = 50.0
                    addStack { "protein".toCompoundStack() }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = ModItems.condensedMilk.toIngredient()
            output {
                relativeProbability = false
                addGroup { addStack { "calcium".toElementStack(4) }; probability = 40.0 }
                addGroup { addStack { "protein".toCompoundStack() }; probability = 20.0 }
                addGroup { addStack { "sucrose".toCompoundStack() }; probability = 20.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.WHEAT.toIngredient()
            output {
                relativeProbability = false
                addGroup { addStack { "starch".toCompoundStack() }; probability = 5.0 }
                addGroup { addStack { "cellulose".toCompoundStack() }; probability = 25.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.GRAVEL.toIngredient()
            output {
                addGroup { addStack { "silicon_dioxide".toCompoundStack() } }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.HAY_BLOCK.toIngredient()
            output {
                rolls = 9
                relativeProbability = false
                addGroup { addStack { "starch".toCompoundStack() }; probability = 5.0 }
                addGroup { addStack { "cellulose".toCompoundStack() }; probability = 25.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.POTATO.toIngredient()
            output {
                relativeProbability = false
                addGroup { addStack { "starch".toCompoundStack() }; probability = 10.0 }
                addGroup { addStack { "potassium".toElementStack(5) }; probability = 25.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.BAKED_POTATO.toIngredient()
            output {
                relativeProbability = false
                addGroup { addStack { "starch".toCompoundStack() }; probability = 10.0 }
                addGroup { addStack { "potassium".toElementStack(5) }; probability = 25.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.REDSTONE.toIngredient()
            output {
                reversible = true
                addGroup {
                    addStack { "iron_oxide".toCompoundStack() }
                    addStack { "strontium_carbonate".toCompoundStack() }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.BEEF.toIngredient()
            output {
                addGroup {
                    addStack { "protein".toCompoundStack(4) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.COOKED_PORKCHOP.toIngredient()
            output {
                addGroup { addStack { "protein".toCompoundStack(4) } }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.MUTTON.toIngredient()
            output {
                addGroup { addStack { "protein".toCompoundStack(4) } }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.COOKED_MUTTON.toIngredient()
            output {
                addGroup { addStack { "protein".toCompoundStack(4) } }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.PORKCHOP.toIngredient()
            output {
                addGroup { addStack { "protein".toCompoundStack(4) } }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.COOKED_BEEF.toIngredient()
            output {
                addGroup {
                    addStack { "protein".toCompoundStack(4) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Ingredient.fromStacks(Items.CHICKEN.toStack())
            output {
                addGroup {
                    addStack { "protein".toCompoundStack(4) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.COOKED_CHICKEN.toIngredient()
            output {
                addGroup {
                    addStack { "protein".toCompoundStack(4) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.FISH.toIngredient()
            output {
                addGroup {
                    addStack { "protein".toCompoundStack(4) }
                    addStack { "selenium".toElementStack(2) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.FISH.toIngredient(meta = 1)
            output {
                addGroup {
                    addStack { "protein".toCompoundStack(4) }
                    addStack { "selenium".toElementStack(4) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.FISH.toIngredient(meta = 2)
            output {
                addGroup {
                    addStack { "protein".toCompoundStack(4) }
                    addStack { "selenium".toElementStack(4) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.COOKED_FISH.toIngredient()
            output {
                addGroup {
                    addStack { "protein".toCompoundStack(4) }
                    addStack { "selenium".toElementStack(2) }

                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.LEATHER.toIngredient()
            output {
                addGroup {
                    addStack { "protein".toCompoundStack(3) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.ROTTEN_FLESH.toIngredient()
            output {
                addGroup {
                    addStack { "protein".toCompoundStack(3) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.RABBIT.toIngredient()
            output {
                addGroup {
                    addStack { "protein".toCompoundStack(4) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.COOKED_RABBIT.toIngredient()
            output {
                addGroup {
                    addStack { "protein".toCompoundStack(4) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.CARROT.toIngredient()
            output {
                relativeProbability = false
                addGroup { addStack { "beta_carotene".toCompoundStack(1) }; probability = 25.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = "dyeRed".toOre()
            output {
                addStack { "iron_oxide".toCompoundStack(quantity = 4) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = "dyePink".toOre()
            output {
                addStack { "arsenic_sulfide".toCompoundStack(quantity = 4) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = "dyeGreen".toOre()
            output {
                addStack { "nickel_chloride".toCompoundStack(quantity = 4) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = "dyeLime".toOre()
            output {
                addGroup {
                    addStack { "cadmium_sulfide".toCompoundStack(quantity = 2) }
                    addStack { "chromium_oxide".toCompoundStack(quantity = 2) }
                }
            }
        })



        dissolverRecipes.add(dissolverRecipe {
            input = "dyePurple".toOre()
            output {
                addStack { "potassium_permanganate".toCompoundStack(quantity = 4) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = "dyeYellow".toOre()
            output {
                addStack { "lead_iodide".toCompoundStack(quantity = 4) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = "dyeOrange".toOre()
            output {
                addStack { "potassium_dichromate".toCompoundStack(quantity = 4) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = "dyeBlack".toOre()
            output {
                addStack { "titanium_oxide".toCompoundStack(quantity = 4) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = "dyeGray".toOre()
            output {
                addStack { "barium_sulfate".toCompoundStack(quantity = 4) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = "dyeMagenta".toOre()
            output {
                addStack { "han_purple".toCompoundStack(quantity = 4) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = "dyeLightBlue".toOre()
            output {
                addGroup {
                    addStack { "cobalt_aluminate".toCompoundStack(quantity = 2) }
                    addStack { "antimony_trioxide".toCompoundStack(quantity = 2) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = "dyeLightGray".toOre()
            output {
                addStack { "magnesium_sulfate".toCompoundStack(quantity = 4) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = "dyeCyan".toOre()
            output {
                addStack { "copper_chloride".toCompoundStack(quantity = 4) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.REDSTONE_BLOCK.toIngredient()
            output {
                addGroup {
                    addStack { "iron_oxide".toCompoundStack(9) }
                    addStack { "strontium_carbonate".toCompoundStack(9) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
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


        dissolverRecipes.add(dissolverRecipe {
            input = "protein".toCompoundStack().toIngredient()
            output {
                rolls = 14
                addGroup { addStack { "oxygen".toElementStack() }; probability = 10.0 }
                addGroup { addStack { "carbon".toElementStack() }; probability = 30.0 }
                addGroup { addStack { "nitrogen".toElementStack() }; probability = 5.0 }
                addGroup { addStack { "sulfur".toElementStack() }; probability = 5.0 }
                addGroup { addStack { "hydrogen".toElementStack() }; probability = 20.0 }
            }
        })


        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.CLAY.toIngredient()
            output {
                addStack { "kaolinite".toCompoundStack(4) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.CLAY_BALL.toIngredient()
            reversible = true
            output {
                addStack { "kaolinite".toCompoundStack() }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.SUGAR.toIngredient()
            reversible = true
            output {
                addStack { "sucrose".toStack() }
            }
        })


        dissolverRecipes.add(dissolverRecipe {
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

        dissolverRecipes.add(dissolverRecipe {
            input = Items.BONE.toIngredient()
            reversible = true
            output {
                addStack { "hydroxylapatite".toStack(2) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.OBSIDIAN.toIngredient()
            output {
                addGroup {
                    addStack { "magnesium_oxide".toCompoundStack(8) }
                    addStack { "potassium_chloride".toCompoundStack(8) }
                    addStack { "aluminum_oxide".toCompoundStack(8) }
                    addStack { "silicon_dioxide".toCompoundStack(24) }

                }

            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.DYE.toIngredient(meta = 15) //bonemeal
            output {
                relativeProbability = false
                addGroup { addStack { "hydroxylapatite".toCompoundStack(1) }; probability = 50.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.BONE_BLOCK.toIngredient()
            output {
                rolls = 9
                relativeProbability = false
                addGroup { addStack { "hydroxylapatite".toCompoundStack(1) }; probability = 50.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.EGG.toIngredient()
            reversible = true
            output {
                addGroup {
                    addStack { "calcium_carbonate".toCompoundStack(8) }
                    addStack { "protein".toCompoundStack(2) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = ModItems.mineralSalt.toIngredient()
            output {
                addGroup { addStack { "sodium_chloride".toCompoundStack() }; probability = 60.0 }
                addGroup { addStack { "lithium".toElementStack() }; probability = 5.0 }
                addGroup { addStack { "potassium_chloride".toCompoundStack() }; probability = 10.0 }
                addGroup { addStack { "magnesium".toElementStack() }; probability = 10.0 }
                addGroup { addStack { "iron".toElementStack() }; probability = 5.0 }
                addGroup { addStack { "copper".toElementStack() }; probability = 4.0 }
                addGroup { addStack { "zinc".toElementStack() }; probability = 2.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.COAL.toIngredient()
            reversible = true
            output {
                addStack { "carbon".toElementStack(quantity = 8) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.COAL.toIngredient(meta = 1)
            reversible = false
            output {
                addStack { "carbon".toElementStack(quantity = 8) }
            }
        })


        dissolverRecipes.add(dissolverRecipe {
            input = "slabWood".toOre()
            output {
                relativeProbability = false
                addGroup { addStack { "cellulose".toCompoundStack() }; probability = 12.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = "slimeball".toOre()
            reversible = true
            output {
                addGroup {
                    addStack { "protein".toStack(2) }
                    addStack { "sucrose".toStack(2) }
                }
            }
        })


        if (oreNotEmpty("itemSilicon")) {
            dissolverRecipes.add(dissolverRecipe {
                input = "itemSilicon".toOre()
                reversible = true
                output {
                    addStack { "silicon".toElementStack(16) }
                }
            })
        }

        dissolverRecipes.add(dissolverRecipe {
            input = Items.ENDER_PEARL.toIngredient()
            reversible = true
            output {
                addGroup {
                    addStack { "silicon".toElementStack(16) }
                    addStack { "mercury".toElementStack(16) }
                    addStack { "neodymium".toElementStack(16) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.DIAMOND.toIngredient()
            output {
                addStack { "carbon".toElementStack(quantity = 64 * 8) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.DIAMOND_BLOCK.toIngredient()
            output {
                addStack { "carbon".toElementStack(quantity = 64 * 8 * 9) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = "plankWood".toOre()
            output {
                relativeProbability = false
                addGroup { addStack { "cellulose".toCompoundStack() }; probability = 25.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = "cobblestone".toOre()
            output {
                addGroup { addStack { ItemStack.EMPTY }; probability = 700.0 }
                addGroup { addStack { "aluminum".toElementStack(1) }; probability = 2.0 }
                addGroup { addStack { "iron".toElementStack(1) }; probability = 4.0 }
                addGroup { addStack { "gold".toElementStack(1) }; probability = 1.5 }
                addGroup { addStack { "silicon_dioxide".toCompoundStack(1) }; probability = 10.0 }
                addGroup { addStack { "dysprosium".toElementStack(1) }; probability = 0.5 }
                addGroup { addStack { "zirconium".toElementStack(1) }; probability = 1.25 }
                addGroup { addStack { "nickel".toElementStack(1) }; probability = 0.5 }
                addGroup { addStack { "gallium".toElementStack(1) }; probability = 0.5 }
                addGroup { addStack { "tungsten".toElementStack(1) }; probability = 0.5 }

            }
        })

        listOf("stoneGranite", "stoneGranitePolished").forEach {
            dissolverRecipes.add(dissolverRecipe {
                input = it.toOre()
                output {
                    addGroup { addStack { ItemStack.EMPTY }; probability = 80.0 }
                    addGroup { addStack { "aluminum_oxide".toCompoundStack(1) }; probability = 5.0 }
                    addGroup { addStack { "iron".toElementStack(1) }; probability = 2.0 }
                    addGroup { addStack { "potassium_chloride".toCompoundStack(1) }; probability = 2.0 }
                    addGroup { addStack { "silicon_dioxide".toCompoundStack(1) }; probability = 10.0 }
                    addGroup { addStack { "technetium".toElementStack(1) }; probability = 1.0 }
                    addGroup { addStack { "manganese".toElementStack(1) }; probability = 1.5 }
                    addGroup { addStack { "radium".toElementStack(1) }; probability = 1.5 }

                }
            })
        }

        listOf("stoneDiorite", "stoneDioritePolished").forEach {
            dissolverRecipes.add(dissolverRecipe {
                input = it.toOre()
                output {
                    addGroup { addStack { ItemStack.EMPTY }; probability = 80.0 }
                    addGroup { addStack { "aluminum_oxide".toCompoundStack(1) }; probability = 4.0 }
                    addGroup { addStack { "iron".toElementStack(1) }; probability = 2.0 }
                    addGroup { addStack { "potassium_chloride".toCompoundStack(1) }; probability = 4.0 }
                    addGroup { addStack { "silicon_dioxide".toCompoundStack(1) }; probability = 10.0 }
                    addGroup { addStack { "indium".toElementStack(1) }; probability = 1.5 }
                    addGroup { addStack { "manganese".toElementStack(1) }; probability = 2.0 }
                    addGroup { addStack { "osmium".toElementStack(1) }; probability = 2.0 }
                }
            })
        }

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.MAGMA.toIngredient()
            output {
                rolls = 2
                addGroup { addStack { "manganese".toElementStack(2) }; probability = 10.0 }
                addGroup { addStack { "aluminum_oxide".toCompoundStack(1) }; probability = 5.0 }
                addGroup { addStack { "magnesium_oxide".toCompoundStack(1) }; probability = 20.0 }
                addGroup { addStack { "potassium_chloride".toCompoundStack(1) }; probability = 2.0 }
                addGroup { addStack { "silicon_dioxide".toCompoundStack(2) }; probability = 10.0 }
                addGroup { addStack { "sulfur".toElementStack(2) }; probability = 20.0 }
                addGroup { addStack { "iron_oxide".toCompoundStack() }; probability = 10.0 }
                addGroup { addStack { "lead".toElementStack(2) }; probability = 8.0 }
                addGroup { addStack { "fluorine".toElementStack() }; probability = 4.0 }
                addGroup { addStack { "bromine".toElementStack() }; probability = 4.0 }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = "treeLeaves".toOre()
            output {
                relativeProbability = false
                addGroup { addStack { "cellulose".toCompoundStack() }; probability = 5.0 }
            }
        })

        listOf("stoneAndesite", "stoneAndesitePolished").forEach {
            dissolverRecipes.add(dissolverRecipe {
                input = it.toOre()
                output {
                    addGroup { addStack { ItemStack.EMPTY }; probability = 80.0 }
                    addGroup { addStack { "aluminum_oxide".toCompoundStack(1) }; probability = 4.0 }
                    addGroup { addStack { "iron".toElementStack(1) }; probability = 3.0 }
                    addGroup { addStack { "potassium_chloride".toCompoundStack(1) }; probability = 4.0 }
                    addGroup { addStack { "silicon_dioxide".toCompoundStack(1) }; probability = 10.0 }
                    addGroup { addStack { "platinum".toElementStack() }; probability = 2.0 }
                    addGroup { addStack { "calcium".toElementStack() }; probability = 4.0 }
                }
            })
        }

        dissolverRecipes.add(dissolverRecipe {
            input = "stone".toOre()
            output {
                addGroup { addStack { ItemStack.EMPTY }; probability = 200.0 }
                addGroup { addStack { "aluminum".toElementStack(1) }; probability = 2.0 }
                addGroup { addStack { "iron".toElementStack(1) }; probability = 4.0 }
                addGroup { addStack { "gold".toElementStack(1) }; probability = 1.5 }
                addGroup { addStack { "silicon_dioxide".toCompoundStack(1) }; probability = 10.0 }
                addGroup { addStack { "dysprosium".toElementStack(1) }; probability = 0.5 }
                addGroup { addStack { "zirconium".toElementStack(1) }; probability = 1.25 }
                addGroup { addStack { "tungsten".toElementStack(1) }; probability = 0.5 }
                addGroup { addStack { "nickel".toElementStack(1) }; probability = 0.5 }
                addGroup { addStack { "gallium".toElementStack(1) }; probability = 0.5 }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.SAND.toIngredient()
            output {
                relativeProbability = false
                addGroup { addStack { "silicon_dioxide".toCompoundStack(quantity = 4) }; probability = 100.0 }
                addGroup { addStack { "gold".toElementStack() } }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.SAND.toIngredient(meta = 1) //red sand
            output {
                relativeProbability = false
                addGroup { addStack { "silicon_dioxide".toCompoundStack(quantity = 4) }; probability = 100.0 }
                addGroup { addStack { "iron_oxide".toCompoundStack() }; probability = 10.0 }
            }
        })



        dissolverRecipes.add(dissolverRecipe {
            input = Items.GUNPOWDER.toIngredient()
            reversible = true
            output {
                addGroup {
                    addStack { "potassium_nitrate".toCompoundStack(2) }
                    addStack { "sulfur".toElementStack(8) }
                    addStack { "carbon".toElementStack(8) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = "logWood".toOre()
            output {
                addStack { "cellulose".toCompoundStack() }
            }
        })

        metalOreData.forEach { list ->
            (0 until list.size).forEach { index ->
                val elementName = list.strs[index]
                val oreName = list.toDictName(index)
                if (OreDictionary.doesOreNameExist(oreName) && OreDictionary.getOres(oreName).isNotEmpty()) {
                    dissolverRecipes.add(dissolverRecipe {
                        input = oreName.toOre()
                        output {
                            addStack {
                                ModItems.elements.toStack(quantity = list.quantity, meta = ElementRegistry.getMeta(elementName))
                            }
                        }
                    })
                }
            }
        }

        dissolverRecipes.add(dissolverRecipe {
            input = Items.GLOWSTONE_DUST.toIngredient()
            reversible = true
            output {
                addStack { "phosphorus".toElementStack(quantity = 4) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.IRON_BARS.toIngredient()
            output {
                addStack { "iron".toElementStack(quantity = 6) }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.BLAZE_POWDER.toIngredient()
            reversible = true
            output {
                addGroup {
                    addStack { "germanium".toElementStack(quantity = 8) }
                    addStack { "carbon".toElementStack(quantity = 8) }
                    addStack { "sulfur".toElementStack(quantity = 8) }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Items.NETHER_WART.toIngredient()
            output {
                addGroup {
                    addStack { "cellulose".toCompoundStack() }
                    addStack { "germanium".toElementStack(quantity = 4) }
                    addStack { "selenium".toElementStack(quantity = 4) }
                }
            }
        })

        if (oreNotEmpty("dropHoney")) {
            dissolverRecipes.add(dissolverRecipe {
                input = "dropHoney".toOre()
                output {
                    addStack { "sucrose".toCompoundStack(quantity = 4) }
                }
            })
        }

        if (oreNotEmpty("gemPrismarine")) {
            dissolverRecipes.add(dissolverRecipe {
                input = "gemPrismarine".toOre()
                reversible = true
                output {
                    addGroup {
                        addStack { "beryl".toCompoundStack(quantity = 2) }
                        addStack { "cobalt_aluminate".toCompoundStack(quantity = 4) }
                    }
                }
            })
        }

        addDissolverRecipesForAlloy("Bronze", "copper", 3, "tin", 1, conservationOfMass = true)
        addDissolverRecipesForAlloy("Electrum", "gold", 1, "silver", 1, conservationOfMass = true)
        addDissolverRecipesForAlloy("ElectricalSteel", "iron", 1, "carbon", 1, "silicon", 1, conservationOfMass = false)
        //addDissolverRecipesForAlloy("Invar", "iron", 2, "nickel", 1, conservationOfMass = true)


        listOf("gemRuby", "dustRuby", "plateRuby")
                .filter { oreNotEmpty(it) }
                .forEach { ore ->
                    dissolverRecipes.add(dissolverRecipe {
                        input = ore.toOre()
                        output {
                            addGroup {
                                addStack { "aluminum_oxide".toCompoundStack(quantity = 16) }
                                addStack { "chromium".toElementStack(quantity = 8) }
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
                                addStack { "aluminum_oxide".toCompoundStack(quantity = 16) }
                                addStack { "iron".toElementStack(quantity = 4) }
                                addStack { "titanium".toElementStack(quantity = 4) }

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
                    addStack { "cucurbitacin".toCompoundStack(); }
                }
                addGroup {
                    probability = 1.0
                    addStack { "water".toCompoundStack(quantity = 4) }
                    addStack { "sucrose".toCompoundStack(quantity = 2) }
                }
            }
        })

        if (oreNotEmpty("itemSalt")) {
            dissolverRecipes.add(dissolverRecipe {
                input = "itemSalt".toOre()
                reversible = true
                output {
                    addStack { "sodium_chloride".toCompoundStack(quantity = 16) }
                }
            })
        }
        dissolverRecipes.add(dissolverRecipe {
            input = "blockCactus".toOre()
            reversible = true
            output {
                addGroup {
                    addStack { "cellulose".toCompoundStack() }
                    addStack { "mescaline".toCompoundStack() }
                }
            }
        })

        dissolverRecipes.add(dissolverRecipe {
            input = Blocks.HARDENED_CLAY.toIngredient()
            reversible = true
            output {
                addStack { "mullite".toCompoundStack(quantity = 2) }
            }
        })

        (0 until 16).forEach {
            dissolverRecipes.add(dissolverRecipe {
                input = Blocks.STAINED_HARDENED_CLAY.toIngredient(meta = it)
                reversible = false
                output {
                    addStack { "mullite".toCompoundStack(quantity = 2) }
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
                    addStack { "mullite".toCompoundStack(quantity = 2) }
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
                        addStack { "starch".toCompoundStack(); }
                    }
                }
            })
        }
    }


    fun initElectrolyzerRecipes() {
        electrolyzerRecipes.add(ElectrolyzerRecipe(
                inputFluid = FluidRegistry.WATER.toStack(quantity = 125),
                electrolyteInternal = "calcium_carbonate".toCompoundStack(),
                electrolyteConsumptionChanceInternal = 20,
                outputOne = "hydrogen".toElementStack(4),
                outputTwo = "oxygen".toElementStack(2)))

        electrolyzerRecipes.add(ElectrolyzerRecipe(
                inputFluid = FluidRegistry.WATER.toStack(125),
                electrolyteInternal = "sodium_chloride".toCompoundStack(),
                electrolyteConsumptionChanceInternal = 20,
                outputOne = "hydrogen".toElementStack(2),
                outputTwo = "oxygen".toElementStack(1),
                outputThree = "chlorine".toElementStack(2), output3Probability = 10))
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
        combinerRecipes.add(CombinerRecipe(Items.COAL.toStack(meta = 1),
                listOf(ItemStack.EMPTY, "carbon".toElementStack(8))))


        metals.forEach { entry ->
            val dustOutput: ItemStack? = firstOre(entry.toDict("dust"))
            if (dustOutput != null && !dustOutput.isEmpty) {
                combinerRecipes.add(CombinerRecipe(dustOutput,
                        listOf(ItemStack.EMPTY, entry.toElementStack(16))))
            }

            val ingotOutput: ItemStack? = firstOre(entry.toDict("ingot"))
            if (ingotOutput != null && !ingotOutput.isEmpty) {
                combinerRecipes.add(CombinerRecipe(ingotOutput,
                        listOf(entry.toElementStack(16))))
            }
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


        val carbon = "carbon".toElementStack(quantity = 64)
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
                listOf("silicon_dioxide".toCompoundStack())))

        combinerRecipes.add(CombinerRecipe(Blocks.STONE.toStack(),
                listOf(ItemStack.EMPTY, "silicon_dioxide".toCompoundStack())))

        combinerRecipes.add(CombinerRecipe(Blocks.OBSIDIAN.toStack(),
                listOf("magnesium_oxide".toCompoundStack(8), "potassium_chloride".toCompoundStack(8), "aluminum_oxide".toCompoundStack(8),
                        "silicon_dioxide".toCompoundStack(24))))

        combinerRecipes.add(CombinerRecipe(Blocks.CLAY.toStack(),
                listOf(ItemStack.EMPTY, "kaolinite".toCompoundStack(4))))

        combinerRecipes.add(CombinerRecipe(Blocks.DIRT.toStack(4),
                listOf("water".toCompoundStack(), "cellulose".toCompoundStack(), "kaolinite".toCompoundStack())))

        combinerRecipes.add(CombinerRecipe(Blocks.GRASS.toStack(4),
                listOf(null, null, null,
                        "water".toCompoundStack(), "cellulose".toCompoundStack(), "kaolinite".toCompoundStack())))

        combinerRecipes.add(CombinerRecipe(Blocks.GRAVEL.toStack(),
                listOf(null, null, "silicon_dioxide".toCompoundStack(1))))

        combinerRecipes.add(CombinerRecipe(Items.WATER_BUCKET.toStack(),
                listOf(null, null, null,
                        null, "water".toCompoundStack(16), null,
                        null, Items.BUCKET, null)))


        combinerRecipes.add(CombinerRecipe(Items.MILK_BUCKET.toStack(),
                listOf(null, null, null,
                        "protein".toStack(2), "water".toStack(16), "sucrose".toStack(),
                        null, Items.BUCKET, null)))

        combinerRecipes.add(CombinerRecipe(Items.POTIONITEM.toStack()
                .apply { this.setTagInfo("Potion", net.minecraft.nbt.NBTTagString("water")) },
                listOf(null, null, null,
                        null, "water".toCompoundStack(16), null,
                        null, Items.GLASS_BOTTLE, null)))

        combinerRecipes.add(CombinerRecipe(Blocks.REDSTONE_BLOCK.toStack(),
                listOf(null, null, null,
                        "iron_oxide".toCompoundStack(9), "strontium_carbonate".toCompoundStack(9))))

        combinerRecipes.add(CombinerRecipe(Items.STRING.toStack(4),
                listOf(null, "protein".toCompoundStack(2))))

        combinerRecipes.add(CombinerRecipe(Blocks.WOOL.toStack(),
                listOf(null, null, null,
                        null, null, null,
                        "protein".toCompoundStack(2))))

        combinerRecipes.add(CombinerRecipe(Items.CARROT.toStack(),
                listOf(null, null, null,
                        "cellulose".toCompoundStack(), "beta_carotene".toCompoundStack())))

        combinerRecipes.add(CombinerRecipe(Items.REEDS.toStack(),
                listOf(null, null, null,
                        "cellulose".toCompoundStack(), "sucrose".toCompoundStack())))


        combinerRecipes.add(CombinerRecipe(Blocks.STONE.toStack(meta = 1), //granite
                listOf(null, null, null,
                        "silicon_dioxide".toCompoundStack(1))))

        combinerRecipes.add(CombinerRecipe(Blocks.STONE.toStack(meta = 3), //diorite
                listOf(null, null, null,
                        null, "silicon_dioxide".toCompoundStack(1))))

        combinerRecipes.add(CombinerRecipe(Blocks.STONE.toStack(meta = 5), //andesite
                listOf(null, null, null,
                        null, null, "silicon_dioxide".toCompoundStack(1))))

        combinerRecipes.add(CombinerRecipe(Items.FLINT.toStack(),
                listOf(null, null, null,
                        null, null, null,
                        null, "silicon_dioxide".toCompoundStack(4), null)))

        combinerRecipes.add(CombinerRecipe(Items.POTATO.toStack(),
                listOf("starch".toCompoundStack(), "potassium".toCompoundStack(4))))

        combinerRecipes.add(CombinerRecipe(Items.APPLE.toStack(),
                listOf(null, "cellulose".toCompoundStack(), null,
                        null, "sucrose".toCompoundStack(1), null)))

        combinerRecipes.add(CombinerRecipe(ModItems.fertilizer.toStack(8),
                listOf("urea".toCompoundStack(1),
                        "diammonium_phosphate".toCompoundStack(1),
                        "potassium_chloride".toCompoundStack(1))))

        if (oreNotEmpty("gemRuby")) {
            val rubyStack = firstOre("gemRuby")
            combinerRecipes.add(CombinerRecipe(rubyStack,
                    listOf("aluminum_oxide".toCompoundStack(16), "chromium".toElementStack(8))))
        }

        if (oreNotEmpty("gemSapphire")) {
            combinerRecipes.add(CombinerRecipe(firstOre("gemSapphire"),
                    listOf("aluminum_oxide".toCompoundStack(16),
                            "iron".toElementStack(4),
                            "titanium".toElementStack(4))))
        }

        val seeds = listOf(Items.WHEAT_SEEDS.toStack(),
                Items.PUMPKIN_SEEDS.toStack(),
                Items.MELON_SEEDS.toStack(),
                Items.BEETROOT_SEEDS.toStack())

        seeds.withIndex().forEach { (index: Int, stack: ItemStack) ->
            val inputs = mutableListOf(null, "triglyceride".toCompoundStack(), null)
            (0 until index).forEach { inputs.add(null) }
            inputs.add("sucrose".toCompoundStack())
            if (stack.item == Items.BEETROOT_SEEDS) inputs.add("iron_oxide".toStack())
            combinerRecipes.add(CombinerRecipe(stack, inputs))
        }

        combinerRecipes.add(CombinerRecipe(Items.BEETROOT.toStack(), listOf(
                null, "sucrose".toStack(), "iron_oxide".toStack())))

        Item.REGISTRY.getObject(ResourceLocation("forestry", "iodine_capsule"))?.let {
            combinerRecipes.add(CombinerRecipe(it.toStack(),
                    listOf(null, null, null,
                            "iodine".toElementStack(8), "iodine".toElementStack(8))))
        }


        (0..5).forEach { i ->
            val input = (0 until i).mapTo(ArrayList<ItemStack>(), { ItemStack.EMPTY })
            input.add("oxygen".toStack())
            input.add("cellulose".toCompoundStack(2))
            combinerRecipes.add(CombinerRecipe(Blocks.SAPLING.toStack(quantity = 4, meta = i), input))
        }

        (0 until 6).forEach { i ->
            val input = (0 until i).mapTo(ArrayList<ItemStack>()) { ItemStack.EMPTY }
            input.add("cellulose".toCompoundStack())

            //y u gotta do dis mojang
            if (i < 4) combinerRecipes.add(CombinerRecipe(ItemStack(Blocks.LOG, 1, i), input))
            else combinerRecipes.add(CombinerRecipe(ItemStack(Blocks.LOG2, 1, i - 4), input))
        }

        combinerRecipes.add(CombinerRecipe(Items.DYE.toStack(meta = 11), listOf("lead_iodide".toCompoundStack(4))))
        combinerRecipes.add(CombinerRecipe(Items.DYE.toStack(meta = 14), listOf("potassium_dichromate".toCompoundStack(4))))
        combinerRecipes.add(CombinerRecipe(Items.DYE.toStack(meta = 2), listOf("nickel_chloride".toCompoundStack(4))))
        combinerRecipes.add(CombinerRecipe(Items.DYE.toStack(meta = 4), listOf("potassium_permanganate".toCompoundStack(4))))
        combinerRecipes.add(CombinerRecipe(Items.DYE.toStack(meta = 7), listOf("magnesium_sulfate".toCompoundStack(4))))
        combinerRecipes.add(CombinerRecipe(Items.DYE.toStack(meta = 6), listOf("copper_chloride".toCompoundStack(4))))
        combinerRecipes.add(CombinerRecipe(Items.DYE.toStack(meta = 0), listOf("titanium_oxide".toCompoundStack(4))))
        combinerRecipes.add(CombinerRecipe(Items.DYE.toStack(meta = 13), listOf("han_purple".toCompoundStack(4))))
        combinerRecipes.add(CombinerRecipe(Items.DYE.toStack(meta = 9), listOf("arsenic_sulfide".toCompoundStack(4))))
        combinerRecipes.add(CombinerRecipe(Items.DYE.toStack(meta = 8), listOf("barium_sulfate".toCompoundStack(4))))

        combinerRecipes.add(CombinerRecipe(Items.DYE.toStack(meta = 10),
                listOf("cadmium_sulfide".toCompoundStack(2), "chromium_oxide".toCompoundStack(2))))
        combinerRecipes.add(CombinerRecipe(Items.DYE.toStack(meta = 12),
                listOf("cobalt_aluminate".toCompoundStack(2), "antimony_trioxide".toCompoundStack(2))))


        combinerRecipes.add(CombinerRecipe(Items.SNOWBALL.toStack(),
                listOf(null, null, null,
                        null, null, null,
                        "water".toCompoundStack(4))))

        combinerRecipes.add(CombinerRecipe(Items.LEATHER.toStack(),
                listOf(null, null, null,
                        null, "protein".toCompoundStack(3))))


        combinerRecipes.add(CombinerRecipe(Items.ROTTEN_FLESH.toStack(),
                listOf(null, null, null,
                        null, null, null,
                        "protein".toCompoundStack(3))))

        combinerRecipes.add(CombinerRecipe(Items.NETHER_STAR.toStack(),
                listOf("lutetium".toStack(64), "hydrogen".toStack(64), "titanium".toStack(64),
                        "hydrogen".toStack(64), "hydrogen".toStack(64), "hydrogen".toStack(64),
                        "dysprosium".toStack(64), "hydrogen".toStack(64), "mendelevium".toStack(64))))
    }

    fun initAtomizerRecipes() {
        atomizerRecipes.add(AtomizerRecipe(FluidStack(FluidRegistry.WATER, 500), "water".toCompoundStack(8)))

        if (fluidExists("canolaoil")) {
            atomizerRecipes.add(AtomizerRecipe(
                    FluidRegistry.getFluidStack("canolaoil", 500)!!, "triglyceride".toCompoundStack(1)))
        }

        ElementRegistry.getAllElements().forEach {
            if (fluidExists(it.name)) {
                atomizerRecipes.add(AtomizerRecipe(
                        FluidRegistry.getFluidStack(it.name, 144)!!, it.name.toElementStack(16)))
            }
        }
    }

    fun initLiquifierRecipes() {
        liquifierRecipes.add(LiquifierRecipe("water".toCompoundStack(8), FluidStack(FluidRegistry.WATER, 500)))

        ElementRegistry.getAllElements().forEach {
            if (fluidExists(it.name)) {
                liquifierRecipes.add(LiquifierRecipe(
                        it.name.toElementStack(16), FluidRegistry.getFluidStack(it.name, 144)!!
                ))
            }
        }
    }

    fun initFissionRecipes() {
        for (i in ElementRegistry.keys().filterNot { it == 1 }) {
            val output1 = if (i % 2 == 0) i / 2 else (i / 2) + 1
            val output2 = if (i % 2 == 0) 0 else i / 2
            if (ElementRegistry[output1] != null && (output2 == 0 || ElementRegistry[output2] != null)) {
                this.fissionRecipes.add(FissionRecipe(i, output1, output2))
            }
        }
    }
}

fun fluidExists(name: String): Boolean = FluidRegistry.isFluidRegistered(name)

fun oreNotEmpty(ore: String) = oreExists(ore) && OreDictionary.getOres(ore).isNotEmpty()