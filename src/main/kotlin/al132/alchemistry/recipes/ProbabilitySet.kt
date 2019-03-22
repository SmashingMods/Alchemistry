package al132.alchemistry.recipes

import al132.alib.utils.extensions.toImmutable
import com.google.common.collect.ImmutableList
import net.minecraft.item.ItemStack
import java.util.*


data class ProbabilityGroup(private val _output: List<ItemStack>,
                            val probability: Double = 1.0) {

    val output: List<ItemStack>
        get(): List<ItemStack> = _output.toImmutable()
}


data class ProbabilitySet(private var _set: List<ProbabilityGroup>? = ArrayList<ProbabilityGroup>(),
                          val relativeProbability: Boolean = true,
                          val rolls: Int = 1) {

    val set: List<ProbabilityGroup>
        get(): List<ProbabilityGroup> = ImmutableList.copyOf(_set)


    fun toStackList(): List<ItemStack> {
        val temp = ImmutableList.Builder<ImmutableList<ItemStack>>()
        set.forEach { temp.add(ImmutableList.copyOf(it.output)) }
        return temp.build().flatten().toImmutable()
    }

    fun probabilityAtIndex(index: Int): Double {
        if (relativeProbability) return (set[index].probability.toDouble() / set.sumByDouble { it.probability })
        else return set[index].probability.toDouble()
    }


    fun calculateOutput(): List<ItemStack> {
        val temp = ArrayList<ItemStack>()
        val rando = Random()
        (1..rolls).forEach { _ ->
            if (relativeProbability) {
                val totalProbability = set.sumByDouble { it.probability }
                val targetProbability = rando.nextDouble()
                var trackingProbability = 0.0

                for (component in set) {
                    trackingProbability += (component.probability.toDouble() / totalProbability)
                    if (trackingProbability >= targetProbability) {
                        component.output.filterNot { it.isEmpty }.forEach { temp.add(it.copy()) }
                        break
                    }
                }
            } else { //absolute probability
                for (component in set) {
                    if (component.probability >= rando.nextInt(101)) {
                        component.output.filterNot { it.isEmpty }.forEach { temp.add(it.copy()) }
                    }
                }
            }
        }
        return temp
    }
}