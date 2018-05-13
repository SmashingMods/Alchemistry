package al132.alchemistry

import al132.alchemistry.chemistry.ElementRegistry
import al132.alchemistry.recipes.DissolverRecipe
import al132.alchemistry.recipes.ModRecipes
import al132.alchemistry.recipes.ProbabilityGroup
import al132.alchemistry.recipes.ProbabilitySet
import al132.alib.utils.extensions.areItemStacksEqual
import al132.alib.utils.extensions.toStack
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.oredict.OreDictionary
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File

/**
 * Created by al132 on 5/10/2018.
 */
class XMLRecipeParser {

    fun init(path: String) {
        val docBuilderFactory = javax.xml.parsers.DocumentBuilderFactory.newInstance()
        val docBuilder = docBuilderFactory.newDocumentBuilder()
        val doc = docBuilder.parse(File(Reference.configDir, path))
        doc.documentElement.normalize()

        val nodes: NodeList = doc.getElementsByTagName("recipe")
        for (index in 0 until nodes.length) {
            val element = nodes.item(index) as Element
            val recipeType = element.getAttribute("type").toLowerCase()
            when (recipeType) {
                "dissolver" -> parseDissolverRecipe(element)
            }
        }
    }

    fun parseDissolverRecipe(element: Element) {
        val inputStr: String = element.getElementsByTagName("input").item(0).textContent ?: ""
        val actionType: String? = element.getAttribute("action")
        val inputItem: Item? = Item.REGISTRY.getObject(ResourceLocation(inputStr))

        if (actionType != "remove") {
            val output: Element? = element.getElementsByTagName("output").item(0) as? Element
            val outputType: String? = output?.getAttribute("type")
            val outputRolls: Int = output?.getAttribute("rolls")?.toIntOrNull() ?: 1
            val groupsList: ArrayList<ProbabilityGroup> = arrayListOf()

            val xmlGroups = output?.getElementsByTagName("group")
            for (groupIndex in 0 until (xmlGroups?.length ?: 0)) {
                val currentElement = xmlGroups?.item(groupIndex) as? Element
                val probability: Int = currentElement?.getAttribute("probability")?.toIntOrNull() ?: 100
                val xmlItems = currentElement?.getElementsByTagName("item")
                val itemStacks: ArrayList<ItemStack> = arrayListOf()

                for (itemIndex in 0 until (xmlItems?.length ?: 0)) {
                    val name = xmlItems?.item(itemIndex)?.textContent
                    val contents = ElementRegistry[name ?: ""] ?: return
                    val quantity: Int = (xmlItems?.item(itemIndex) as Element).getAttribute("quantity").toIntOrNull() ?: 1
                    itemStacks.add(contents.toItemStack(quantity))
                }
                groupsList.add(ProbabilityGroup(_output = itemStacks, probability = probability))
            }

            val outputSet = ProbabilitySet(_set = groupsList, relativeProbability = outputType != "absolute", rolls = outputRolls)
            if (inputItem == null) {
                if (OreDictionary.doesOreNameExist(inputStr)) {
                    ModRecipes.dissolverRecipes.add(DissolverRecipe(dictName = inputStr, _outputs = outputSet))
                }
            } else {
                ModRecipes.dissolverRecipes.add(DissolverRecipe(stack = inputItem.toStack(), _outputs = outputSet))
            }
        } else if (actionType == "remove") {
            if (inputItem != null) {
                ModRecipes.dissolverRecipes
                        .filter { it.inputs.count() == 1 && it.inputs[0].areItemStacksEqual(inputItem.toStack()) }
                        .forEach { ModRecipes.dissolverRecipes.remove(it) }
            } else {
                if (OreDictionary.doesOreNameExist(inputStr)) {
                    ModRecipes.dissolverRecipes
                            .filter { it.dictName == inputStr }
                            .forEach { ModRecipes.dissolverRecipes.remove(it) }
                }
            }
        }
    }
}