package al132.alchemistry.recipes

import net.minecraft.item.ItemStack

/**
 * Created by al132 on 4/22/2017.
 */

inline fun dissolverRecipe(init: DissolverRecipe.() -> Unit): DissolverRecipe {
    val recipe = DissolverRecipe()
    recipe.init()
    return recipe
}


class ProbabilitySetDSL(var components: ArrayList<ProbabilityGroup> = ArrayList<ProbabilityGroup>(),
                        var rolls: Int = 1,
                        var relativeProbability: Boolean = true) {

    inline fun addGroup(crossinline init: ProbabilityGroupDSL.() -> Unit) = components.add(ProbabilityGroupDSL().apply { init() }.build())

    inline fun addStack(init: ProbabilitySetDSL.() -> ItemStack) = components.add(ProbabilityGroup(listOf(init())))

    fun build() = ProbabilitySet(this.components, this.relativeProbability, this.rolls)
}

class ProbabilityGroupDSL(var stacks: ArrayList<ItemStack> = ArrayList<ItemStack>(),
                          var probability: Double = 1.0) {

    inline fun addStack(init: ProbabilityGroupDSL.() -> ItemStack) = stacks.add(init())

    fun build() = ProbabilityGroup(this.stacks, this.probability)
}