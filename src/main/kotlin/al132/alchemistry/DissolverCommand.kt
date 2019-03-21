package al132.alchemistry

import al132.alchemistry.recipes.DissolverRecipe
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.server.MinecraftServer
import net.minecraft.util.text.TextComponentString
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection


class DissolverCommand : CommandBase() {


    override fun getName() = "dissolver"

    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String>) {
        if (server.isSinglePlayer) {
            (sender as? EntityPlayer)?.let { player ->
                val heldItem = player.heldItemMainhand
                val recipe = DissolverRecipe.match(heldItem, false)
                if (recipe != null) {
                    val selection = StringSelection(stackToCTString(heldItem, recipe))
                    Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
                    sender.send("Copied recipe to clipboard")
                } else sender.send("No recipe for held itemstack ${heldItem.toString()}")

            }
        } else sender.send("Only available in single player")
    }

    override fun getUsage(sender: ICommandSender) = "dissolver"
}

fun ICommandSender.send(message: String) {
    this.sendMessage(TextComponentString(message))
}

fun stackToCTString(held: ItemStack, recipe: DissolverRecipe): String {
    val builder = StringBuilder()
    val relativeProbability = recipe.outputs.relativeProbability
    val rolls = recipe.outputs.rolls
    builder.append("mods.alchemistry.Dissolver.removeRecipe(${formatStack(held, true)});\n")
    builder.append("mods.alchemistry.Dissolver.addRecipe(${formatStack(held, true)}, $relativeProbability, $rolls, \n[")
    recipe.outputs.set.forEachIndexed { index, group ->
        builder.append("[${group.probability}, ${group.output.joinToString(", ") { formatStack(it) }}]")
        if (index < recipe.outputs.set.count() - 1) builder.append(", \n")
    }
    builder.append("]);");
    return builder.toString()
}

fun formatStack(stack: ItemStack, ignoreQuantity: Boolean = false): String {
    if (stack.isEmpty) return "null"
    else {
        var quantityString = ""
        if (stack.count > 1 && !ignoreQuantity) quantityString = " * ${stack.count} "
        return "<" + stack.item.registryName!!.toString() + ":" + stack.metadata + ">" + quantityString
    }
}