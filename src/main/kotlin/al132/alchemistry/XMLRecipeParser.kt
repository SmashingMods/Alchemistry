package al132.alchemistry

import al132.alchemistry.recipes.DissolverRecipe
import al132.alchemistry.recipes.ModRecipes
import al132.alchemistry.recipes.ProbabilityGroup
import al132.alib.utils.extensions.toStack
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
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
        val output: Element? = element.getElementsByTagName("output").item(0) as? Element
        val outputType: String? = output?.getAttribute("type")
        println("Inputstr: " + inputStr)
        println("outputType: " + outputType)
        val groupsList: ArrayList<ProbabilityGroup> = arrayListOf()
        val xmlGroups = output?.getElementsByTagName("group")
        for (groupIndex in 0 until (xmlGroups?.length ?: 0)) {
            val currentElement = xmlGroups?.item(groupIndex) as? Element
            val probability: Int = currentElement?.getAttribute("probability")?.toIntOrNull() ?: 100
            val xmlItems = currentElement?.getElementsByTagName("item")
            for (itemIndex in 0 until (xmlItems?.length ?: 0)) {
                //ElementRegistry.get("")
            }
            val itemStacks: ArrayList<ItemStack> = arrayListOf()
            groupsList.add(ProbabilityGroup(_output = itemStacks, probability = probability))
        }
        val inputItem = Item.REGISTRY.getObject(ResourceLocation(inputStr))
        if(inputItem != null) {
            //TODO Validate recipe
            ModRecipes.dissolverRecipes.add(DissolverRecipe(stack = inputItem.toStack()))
        }
        println("Dissolver recipe error")
    }

}