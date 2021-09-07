package me.steven.indrev.gui.screenhandlers.machines

import io.github.cottonmc.cotton.gui.widget.WBar
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import me.steven.indrev.blockentities.crafters.CompressorFactoryBlockEntity
import me.steven.indrev.blockentities.crafters.PulverizerFactoryBlockEntity
import me.steven.indrev.gui.PatchouliEntryShortcut
import me.steven.indrev.gui.screenhandlers.IRGuiScreenHandler
import me.steven.indrev.gui.screenhandlers.PULVERIZER_FACTORY_HANDLER
import me.steven.indrev.gui.widgets.machines.upProcessBar
import me.steven.indrev.utils.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.util.Identifier

class PulverizerFactoryScreenHandler(
    syncId: Int,
    playerInventory: PlayerInventory,
    ctx: ScreenHandlerContext
) :
    IRGuiScreenHandler(
        PULVERIZER_FACTORY_HANDLER,
        syncId,
        playerInventory,
        ctx
    ), PatchouliEntryShortcut {
    init {
        val root = WGridPanel()
        setRootPanel(root)
        configure("block.indrev.pulverizer_factory", ctx, playerInventory, blockInventory, widgetPos = 0.15)
        withBlockEntity<PulverizerFactoryBlockEntity> { blockEntity ->
            val slotsAmount = 5
            val offset = 2.2

            for ((index, slot) in blockEntity.inventoryComponent!!.inventory.inputSlots.withIndex()) {
                val inputSlot = WItemSlot.of(blockInventory, slot)
                root.add(inputSlot, offset + (index * 1.4), 0.6)
            }

            for (i in 0 until slotsAmount) {
                val processWidget = upProcessBar(blockEntity, PulverizerFactoryBlockEntity.CRAFTING_COMPONENT_START_ID + i)
                root.add(processWidget, offset + (i * 1.4), 1.7)
            }

            for ((index, slot) in blockEntity.inventoryComponent!!.inventory.outputSlots.withIndex()) {
                val outputSlot = WItemSlot.of(blockInventory, slot)
                root.add(outputSlot, offset + (index * 1.4), 2.9)
                outputSlot.addChangeListener { _, _, _, _ ->
                    val player = playerInventory.player
                    if (!player.world.isClient) {
                        blockEntity.dropExperience(player)
                    }
                }
                outputSlot.isInsertingAllowed = false
            }
        }
        root.validate(this)
    }

    override fun canUse(player: PlayerEntity?): Boolean = true

    override fun getEntry(): Identifier = identifier("machines/basic_machines")

    override fun getPage(): Int = 1

    companion object {
        val SCREEN_ID = identifier("pulverizer_factory_screen")
    }
}