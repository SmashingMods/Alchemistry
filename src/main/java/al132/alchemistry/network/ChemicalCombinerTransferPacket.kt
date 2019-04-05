package al132.alchemistry.network


import al132.alchemistry.recipes.CombinerRecipe
import al132.alchemistry.tiles.TileChemicalCombiner
import io.netty.buffer.ByteBuf
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class ChemicalCombinerTransferPacket() : IMessage {

    private var blockPos: BlockPos? = null
    private var outputStack: ItemStack? = ItemStack.EMPTY

    override fun fromBytes(buf: ByteBuf) {
        this.blockPos = BlockPos(buf.readInt(), buf.readInt(), buf.readInt())
        this.outputStack = ByteBufUtils.readItemStack(buf)
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(blockPos!!.x)
        buf.writeInt(blockPos!!.y)
        buf.writeInt(blockPos!!.z)
        ByteBufUtils.writeItemStack(buf, this.outputStack)
    }

    constructor(pos: BlockPos, outputStack: ItemStack) : this() {
        this.blockPos = pos
        this.outputStack = outputStack
    }

    class Handler : IMessageHandler<ChemicalCombinerTransferPacket, IMessage> {

        override fun onMessage(message: ChemicalCombinerTransferPacket, ctx: MessageContext): IMessage? {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask { handle(message, ctx) }
            return null
        }

        private fun handle(message: ChemicalCombinerTransferPacket, ctx: MessageContext) {
            val playerEntity = ctx.serverHandler.player

            val tile = playerEntity.world.getTileEntity(message.blockPos!!)
            if (tile is TileChemicalCombiner) {
                val output: ItemStack = message.outputStack!!
                if (!output.isEmpty) {
                    tile.clientRecipeTarget.setStackInSlot(0, output)
                    tile.recipeIsLocked = true
                    tile.currentRecipe = CombinerRecipe.matchOutput(output)
                }
            }
        }
    }
}