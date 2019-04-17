package al132.alchemistry.recipes

import al132.alchemistry.Alchemistry
import al132.alchemistry.Reference
import al132.alchemistry.utils.extensions.toOre
import al132.alchemistry.utils.extensions.toStack
import al132.alib.utils.extensions.areItemStacksEqual
import al132.alib.utils.extensions.areItemsEqual
import al132.alib.utils.extensions.toIngredient
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.oredict.OreDictionary
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File

/**
 * Created by al132 on 5/10/2018.
 */

fun Element.getFirst(name: String) = this.getElementsByTagName(name).item(0) as? Element

fun Element.getNth(name: String, nth: Int) = this.getElementsByTagName(name).item(nth) as? Element

fun NodeList.getNth(nth: Int) = this.item(nth) as? Element

fun Element?.tagToStack(): ItemStack {
    val meta = this?.getAttribute("meta")?.toIntOrNull() ?: 0
    val quantity = this?.getAttribute("quantity")?.toIntOrNull() ?: 1
    return this?.textContent?.toStack(quantity = quantity, meta = meta) ?: ItemStack.EMPTY
}

class XMLRecipeParser {

    fun init(path: String) {
        val docBuilderFactory = javax.xml.parsers.DocumentBuilderFactory.newInstance()
        val docBuilder = docBuilderFactory.newDocumentBuilder()
        try {
            val doc = docBuilder.parse(File(Reference.configDir, path))

            doc.documentElement.normalize()

            val nodes: NodeList = doc.getElementsByTagName("recipe")
            (0 until nodes.length).forEach { index ->
                val element = nodes.item(index) as Element
                val recipeType = element.getAttribute("type").toLowerCase()
                when (recipeType) {
                    "dissolver"    -> parseDissolverRecipe(element)
                    "combiner"     -> parseCombinerRecipe(element)
                    "evaporator"   -> parseEvaporatorRecipe(element)
                    "electrolyzer" -> parseElectrolyzerRecipe(element)
                    "atomizer"     -> parseAtomizerRecipe(element)
                    "liquifier"    -> parseLiquifierRecipe(element)
                }
            }
        } catch (e: org.xml.sax.SAXParseException) {
            Alchemistry.logger.info(e.message)
        }
    }


    fun parseElectrolyzerRecipe(element: Element) {
        val inputFluid: Fluid? = FluidRegistry.getFluid(element.getFirst("input")?.textContent ?: "")
        val inputQuantity: Int = element.getFirst("input")?.getAttribute("quantity")?.toIntOrNull() ?: 100
        val actionType: String? = element.getAttribute("action")
        val electrolyteConsumptionChance = element.getFirst("electrolyte")?.getAttribute("probability")?.toIntOrNull()
                ?: 50
        val electrolytesXML = element.getFirst("electrolyte")
        val electrolyteString = electrolytesXML?.textContent ?: ""
        val electrolyteStack = electrolytesXML.tagToStack()

        if (actionType != "remove") {
            val outputs: ArrayList<ItemStack> = arrayListOf()
            val outputXMLElement = element.getFirst("output")
            (0 until 2).forEach { index ->
                val outputQuantity: Int = outputXMLElement?.getNth("item", index)?.getAttribute("quantity")?.toIntOrNull()
                        ?: 1
                val tempStack = (outputXMLElement?.getNth("item", index)?.textContent
                        ?: "").toStack(quantity = outputQuantity)
                if (!tempStack.isEmpty) outputs.add(tempStack)
            }
            if (inputFluid != null && outputs.count() > 0) {
                if (OreDictionary.doesOreNameExist(electrolyteString)) {
                    ModRecipes.electrolyzerRecipes.add(ElectrolyzerRecipe(
                            fluid = inputFluid,
                            fluidQuantity = inputQuantity,
                            electrolyte = electrolyteString,
                            elecConsumption = electrolyteConsumptionChance,
                            outputOne = outputs[0],
                            outputTwo = outputs[1]))
                    Alchemistry.logger.info("Added Electrolyzer recipe for [${inputFluid.name},$inputQuantity,$electrolyteString]")

                } else if (!electrolyteStack.isEmpty) {
                    ModRecipes.electrolyzerRecipes.add(ElectrolyzerRecipe(
                            fluid = inputFluid,
                            fluidQuantity = inputQuantity,
                            electrolyte = electrolyteStack,
                            elecConsumption = electrolyteConsumptionChance,
                            outputOne = outputs[0],
                            outputTwo = outputs[1]))
                    Alchemistry.logger.info("Added Electrolyzer recipe for [${inputFluid.name},$inputQuantity,$electrolyteStack]")
                }
            }
        } else if (actionType == "remove") {
            ModRecipes.electrolyzerRecipes
                    .filter {
                        it.input.fluid == inputFluid
                                && it.input.amount == inputQuantity
                                && (it.matchesElectrolyte(electrolyteStack) || it.matchesElectrolyte(electrolyteString))
                    }
                    .forEach {
                        ModRecipes.electrolyzerRecipes.remove(it)
                        Alchemistry.logger.info("Removed Electrolyzer recipe: $it")
                    }
        }
    }


    fun parseCombinerRecipe(element: Element) {
        val actionType: String? = element.getAttribute("action")
        val inputs: ArrayList<ItemStack> = arrayListOf()
        val ingredientMap: HashMap<String, ItemStack> = hashMapOf()
        val itemsXML = element.getFirst("input")?.getElementsByTagName("item")
        (0 until (itemsXML?.length ?: 0)).forEach { index ->
            val key = itemsXML?.getNth(index)?.getAttribute("key") ?: ""
            val stack = itemsXML?.getNth(index).tagToStack()
            if (key.length == 1) {
                ingredientMap.put(key, stack)
            }
        }
        val rowsXML = element.getFirst("input")?.getElementsByTagName("row")
        (0 until 3).forEach { i ->
            val rowText = (rowsXML?.item(i)?.textContent ?: "").padEnd(3)
            for (c in rowText) {
                inputs.add(ingredientMap[c.toString()] ?: ItemStack.EMPTY)
            }
        }
        if (actionType != "remove") {
            val outputXML = element.getFirst("output")?.getFirst("item")
            val output = outputXML.tagToStack()
            Alchemistry.logger.info("Added Combiner recipe: for $inputs")
            ModRecipes.combinerRecipes.add(CombinerRecipe(_output = output, objsIn = inputs))
        } else if (actionType == "remove") {
            recipeCheck@ for (recipe in ModRecipes.combinerRecipes) {
                for (i in recipe.inputs.indices) {
                    if (!recipe.inputs[i].areItemStacksEqual(inputs[i])) {
                        continue@recipeCheck
                    }
                }
                ModRecipes.combinerRecipes.remove(recipe)
                Alchemistry.logger.info("Removed Combiner recipe: $recipe")
                break@recipeCheck
            }
        }
    }


    fun parseEvaporatorRecipe(element: Element) {
        val inputFluid: Fluid? = FluidRegistry.getFluid(element.getFirst("input")?.textContent ?: "")
        val actionType: String? = element.getAttribute("action")

        if (actionType != "remove") {
            val inputQuantity: Int = element.getFirst("input")?.getAttribute("quantity")?.toIntOrNull() ?: 100
            val outputStack = element.getFirst("output").tagToStack()

            if (inputFluid != null && !outputStack.isEmpty) {
                ModRecipes.evaporatorRecipes.add(EvaporatorRecipe(fluid = inputFluid, fluidQuantity = inputQuantity, output = outputStack))
                Alchemistry.logger.info("Added Evaporator recipe for [${inputFluid.name},$inputQuantity]")

            }
        } else if (actionType == "remove") {
            ModRecipes.evaporatorRecipes
                    .filter { it.input.fluid == inputFluid }
                    .forEach {
                        ModRecipes.evaporatorRecipes.remove(it)
                        Alchemistry.logger.info("Removed Evaporator recipe: $it")
                    }
        }
    }

    fun parseDissolverRecipe(element: Element) {
        val inputStr: String = element.getFirst("input")?.textContent ?: ""
        val inputMeta: Int = element.getFirst("input")?.getAttribute("meta")?.toIntOrNull() ?: 0
        val inputQuantity = element.getFirst("input")?.getAttribute("quantity")?.toIntOrNull() ?: 1
        val actionType: String? = element.getAttribute("action")
        val inputStack: ItemStack = inputStr.toStack(meta = inputMeta, quantity = inputQuantity)

        if (actionType != "remove") {
            val outputXMLElement: Element? = element.getFirst("output")
            val outputType: String? = outputXMLElement?.getAttribute("type")
            val outputRolls: Int = outputXMLElement?.getAttribute("rolls")?.toIntOrNull() ?: 1
            val groupsList: ArrayList<ProbabilityGroup> = arrayListOf()

            val xmlGroups = outputXMLElement?.getElementsByTagName("group")
            (0 until (xmlGroups?.length ?: 0)).forEach { groupIndex ->
                val currentXMLElement = xmlGroups?.getNth(groupIndex)
                val probability: Double = currentXMLElement?.getAttribute("probability")?.toDoubleOrNull() ?: 100.0
                val xmlItems = currentXMLElement?.getElementsByTagName("item")
                val itemStacks: ArrayList<ItemStack> = arrayListOf()

                (0 until (xmlItems?.length ?: 0))
                        .forEach { itemIndex -> itemStacks.add(xmlItems?.getNth(itemIndex).tagToStack()) }

                groupsList.add(ProbabilityGroup(_output = itemStacks, probability = probability))
            }

            val outputSet = ProbabilitySet(_set = groupsList, relativeProbability = outputType != "absolute", rolls = outputRolls)
            if (inputStack.isEmpty) {
                if (OreDictionary.doesOreNameExist(inputStr)) {
                    ModRecipes.dissolverRecipes.add(DissolverRecipe(input = inputStr.toOre(), _outputs = outputSet))
                    Alchemistry.logger.info("Added Chemical Dissolver recipe for $inputStr")

                } else {
                    Alchemistry.logger.info("Failed to add Chemical Dissolver recipe for $inputStr")
                }
            } else {
                ModRecipes.dissolverRecipes.add(DissolverRecipe(input = inputStack.toIngredient(), _outputs = outputSet))
                Alchemistry.logger.info("Added Chemical Dissolver recipe for $inputStack")
            }
        } else if (actionType == "remove") {
            if (!inputStack.isEmpty) {
                ModRecipes.dissolverRecipes
                        .filter { it.inputs.count() == 1 && it.inputs[0].areItemStacksEqual(inputStack) }
                        .forEach {
                            ModRecipes.dissolverRecipes.remove(it)
                            Alchemistry.logger.info("Removed Chemical Dissolver recipe: $it")

                        }
            } else {
                if (OreDictionary.doesOreNameExist(inputStr)) {
                    ModRecipes.dissolverRecipes
                            //TODO does this work properly?
                            .filter { it.input?.matchingStacks?.contentEquals(OreDictionary.getOres(inputStr).toArray())?:false }
                            .forEach {
                                ModRecipes.dissolverRecipes.remove(it)
                                Alchemistry.logger.info("Removed Chemical Dissolver recipe: $it")
                            }
                }
            }
        }
    }


    fun parseAtomizerRecipe(element: Element) {
        val inputFluid: Fluid? = FluidRegistry.getFluid(element.getFirst("input")?.textContent ?: "")
        val actionType: String? = element.getAttribute("action")

        if (actionType != "remove") {
            val inputQuantity: Int = element.getFirst("input")?.getAttribute("quantity")?.toIntOrNull() ?: 100
            val outputStack: ItemStack = element.getFirst("output").tagToStack()

            if (inputFluid != null && !outputStack.isEmpty) {
                ModRecipes.atomizerRecipes.add(AtomizerRecipe(fluid = inputFluid, fluidQuantity = inputQuantity, output = outputStack))
                Alchemistry.logger.info("Added Atomizer recipe for [${inputFluid.name},$inputQuantity]")
            }
        } else if (actionType == "remove") {
            ModRecipes.atomizerRecipes
                    .filter { it.input.fluid == inputFluid }
                    .forEach {
                        ModRecipes.atomizerRecipes.remove(it)
                        Alchemistry.logger.info("Removed Atomizer recipe: $it")
                    }
        }
    }

    fun parseLiquifierRecipe(element: Element) {
        val actionType: String? = element.getAttribute("action")
        val inputXML = element.getFirst("input")
        val inputString = inputXML?.textContent ?: ""
        val inputStack = inputXML.tagToStack()

        if (actionType != "remove") {
            val outputFluid: Fluid? = FluidRegistry.getFluid(element.getFirst("output")?.textContent ?: "")
            val outputQuantity: Int = element.getFirst("output")?.getAttribute("quantity")?.toIntOrNull() ?: 100
            if (outputFluid != null && !inputStack.isEmpty) {
                ModRecipes.liquifierRecipes.add(LiquifierRecipe(inputStack = inputStack, outputFluid = FluidStack(outputFluid, outputQuantity)))
            } else {
                Alchemistry.logger.info("Failed to add Liquifier recipe for: $inputString")
            }
        } else if (actionType == "remove") {
            ModRecipes.liquifierRecipes
                    .filter {
                        it.input.areItemsEqual(inputStack)
                    }
                    .forEach {
                        ModRecipes.liquifierRecipes.remove(it)
                        Alchemistry.logger.info("Removed Liquifier recipe: $it")
                    }
        }
    }
}