package al132.alchemistry.recipes

import al132.alchemistry.Alchemistry
import al132.alchemistry.Reference
import al132.alchemistry.utils.toStack
import al132.alchemistry.utils.toStacks
import al132.alib.utils.extensions.areItemStacksEqual
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.oredict.OreDictionary
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File

/**
 * Created by al132 on 5/10/2018.
 */

fun Element.getFirst(name: String): Element? {
    return this.getElementsByTagName(name).item(0) as? Element
}

fun Element.getNth(name: String, nth: Int): Element? {
    return this.getElementsByTagName(name).item(nth) as? Element
}

fun NodeList.getNth(nth: Int): Element? {
    return this.item(nth) as Element?
}

class XMLRecipeParser {

    fun init(path: String) {
        val docBuilderFactory = javax.xml.parsers.DocumentBuilderFactory.newInstance()
        val docBuilder = docBuilderFactory.newDocumentBuilder()
        try {
            val doc = docBuilder.parse(File(Reference.configDir, path))

            doc.documentElement.normalize()

            val nodes: NodeList = doc.getElementsByTagName("recipe")
            for (index in 0 until nodes.length) {
                val element = nodes.item(index) as Element
                val recipeType = element.getAttribute("type").toLowerCase()
                when (recipeType) {
                    "dissolver"    -> parseDissolverRecipe(element)
                    "combiner"     -> parseCombinerRecipe(element)
                    "evaporator"   -> parseEvaporatorRecipe(element)
                    "electrolyzer" -> parseElectrolyzerRecipe(element)
                }
            }
        } catch (e: org.xml.sax.SAXParseException) {
            println(e.message)
        }
    }


    fun parseElectrolyzerRecipe(element: Element) {
        val inputFluid: Fluid? = FluidRegistry.getFluid(element.getFirst("input")?.textContent ?: "")
        val inputQuantity: Int = element.getFirst("input")?.getAttribute("quantity")?.toIntOrNull() ?: 100
        val actionType: String? = element.getAttribute("action")
        if (actionType != "remove") {
            val electrolytes: ArrayList<ItemStack> = arrayListOf()
            val electrolyteConsumptionChance = element.getFirst("electrolytes")?.getAttribute("probability")?.toIntOrNull() ?: 50
            val electrolytesXML = element.getElementsByTagName("electrolytes")
            for (index in 0 until electrolytesXML.length) {
                electrolytes.addAll((element.getNth("item", index)?.textContent ?: "").toStacks())
            }
            val outputs: ArrayList<ItemStack> = arrayListOf()
            val outputXMLElement = element.getFirst("output")
            for (index in 0 until 2) {
                val outputQuantity: Int = outputXMLElement?.getNth("item", index)?.getAttribute("quantity")?.toIntOrNull() ?: 1
                val tempStack = (outputXMLElement?.getNth("item", index)?.textContent ?: "").toStack(quantity = outputQuantity)
                if (!tempStack.isEmpty) outputs.add(tempStack)
            }
            if (inputFluid != null && electrolytes.count() > 0 && outputs.count() > 0) {
                ModRecipes.electrolyzerRecipes.add(ElectrolyzerRecipe(fluid = inputFluid,
                        fluidQuantity = inputQuantity,
                        electrolytes = electrolytes,
                        elecConsumption = electrolyteConsumptionChance,
                        outputOne = outputs[0],
                        outputTwo = outputs[1]))
                Alchemistry.logger.info("Added Electrolyzer recipe for [${inputFluid.name},$inputQuantity,$electrolytes]")
            }
        } else if (actionType == "remove") {
            //TODO
        }
    }


    fun parseCombinerRecipe(element: Element) {
        //TODO
        /*  val actionType: String? = element.getAttribute("action")
          if (actionType != "remove") {
              val output: ItemStack = ItemStack.EMPTY
              val ingredientMap: HashMap<String, ItemStack> = hashMapOf()
              val itemsXML = element.getFirst("input")?.getElementsByTagName("item")
              for (index in 0 until (itemsXML?.length ?: 0)) {

                  //ingredientMap.put(itemsXML?.getNth(index)?.getAttribute("key"))
              }
              val rowsXML = element.getFirst("input")?.getElementsByTagName("row")

              ModRecipes.combinerRecipes.add(CombinerRecipe(output = output, objsIn = listOf(ItemStack.EMPTY)))
          } else {
              for (recipe in ModRecipes.combinerRecipes)
                  if (false) {
                      ModRecipes.combinerRecipes.remove(recipe)
                  }
          }*/
    }


    fun parseEvaporatorRecipe(element: Element) {
        val inputFluid: Fluid? = FluidRegistry.getFluid(element.getFirst("input")?.textContent ?: "")
        val actionType: String? = element.getAttribute("action")

        if (actionType != "remove") {
            val inputQuantity: Int = element.getFirst("input")?.getAttribute("quantity")?.toIntOrNull() ?: 100
            val outputXMLElement: Element? = element.getFirst("output")
            val outputStr: String = outputXMLElement?.getFirst("item")?.textContent ?: ""
            val outputQuantity: Int = outputXMLElement?.getFirst("item")?.getAttribute("quantity")?.toIntOrNull() ?: 1
            val outputMeta: Int = outputXMLElement?.getFirst("item")?.getAttribute("meta")?.toIntOrNull() ?: 0
            val outputStack = outputStr.toStack(quantity = outputQuantity, meta = outputMeta)

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
        val itemMeta: Int = element.getFirst("input")?.getAttribute("meta")?.toIntOrNull() ?: 0
        val actionType: String? = element.getAttribute("action")
        val inputStack: ItemStack = inputStr.toStack(meta = itemMeta)

        if (actionType != "remove") {
            val outputXMLElement: Element? = element.getFirst("output")
            val outputType: String? = outputXMLElement?.getAttribute("type")
            val outputRolls: Int = outputXMLElement?.getAttribute("rolls")?.toIntOrNull() ?: 1
            val groupsList: ArrayList<ProbabilityGroup> = arrayListOf()

            val xmlGroups = outputXMLElement?.getElementsByTagName("group")
            for (groupIndex in 0 until (xmlGroups?.length ?: 0)) {
                val currentXMLElement = xmlGroups?.item(groupIndex) as? Element
                val probability: Int = currentXMLElement?.getAttribute("probability")?.toIntOrNull() ?: 100
                val xmlItems = currentXMLElement?.getElementsByTagName("item")
                val itemStacks: ArrayList<ItemStack> = arrayListOf()

                for (itemIndex in 0 until (xmlItems?.length ?: 0)) {
                    val itemName = xmlItems?.item(itemIndex)?.textContent ?: ""
                    val itemQuantity: Int = (xmlItems?.item(itemIndex) as Element).getAttribute("quantity").toIntOrNull() ?: 1
                    val itemMeta: Int = (xmlItems?.item(itemIndex) as Element).getAttribute("meta").toIntOrNull() ?: 0
                    itemStacks.add(itemName.toStack(meta = itemMeta, quantity = itemQuantity))
                }
                groupsList.add(ProbabilityGroup(_output = itemStacks, probability = probability))
            }

            val outputSet = ProbabilitySet(_set = groupsList, relativeProbability = outputType != "absolute", rolls = outputRolls)
            if (inputStack.isEmpty) {
                if (OreDictionary.doesOreNameExist(inputStr)) {
                    ModRecipes.dissolverRecipes.add(DissolverRecipe(dictName = inputStr, _outputs = outputSet))
                    Alchemistry.logger.info("Added Chemical Dissolver recipe for $inputStr")

                } else {
                    Alchemistry.logger.info("Failed to add Chemical Dissolver recipe for $inputStr")
                }
            } else {
                ModRecipes.dissolverRecipes.add(DissolverRecipe(stack = inputStack, _outputs = outputSet))
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
                            .filter { it.dictName == inputStr }
                            .forEach {
                                ModRecipes.dissolverRecipes.remove(it)
                                Alchemistry.logger.info("Removed Chemical Dissolver recipe: $it")
                            }
                }
            }
        }
    }
}