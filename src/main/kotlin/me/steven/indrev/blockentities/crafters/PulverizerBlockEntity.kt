package me.steven.indrev.blockentities.crafters

import me.steven.indrev.components.InventoryComponent
import me.steven.indrev.components.TemperatureComponent
import me.steven.indrev.inventories.IRInventory
import me.steven.indrev.items.misc.IRCoolerItem
import me.steven.indrev.items.upgrade.IRUpgradeItem
import me.steven.indrev.items.upgrade.Upgrade
import me.steven.indrev.recipes.machines.PulverizerRecipe
import me.steven.indrev.registry.MachineRegistry
import me.steven.indrev.utils.Tier
import net.minecraft.recipe.RecipeType
import team.reborn.energy.Energy

class PulverizerBlockEntity(tier: Tier) :
    CraftingMachineBlockEntity<PulverizerRecipe>(tier, MachineRegistry.PULVERIZER_REGISTRY) {

    init {
        this.inventoryComponent = InventoryComponent({ this }) {
            IRInventory(9, intArrayOf(2), intArrayOf(3, 4)) { slot, stack ->
                val item = stack?.item
                when {
                    item is IRUpgradeItem -> getUpgradeSlots().contains(slot)
                    Energy.valid(stack) && Energy.of(stack).maxOutput > 0 -> slot == 0
                    item is IRCoolerItem -> slot == 1
                    slot == 2 -> true
                    else -> false
                }
            }
        }
        this.temperatureComponent = TemperatureComponent({ this }, 0.06, 700..1100, 1400.0)
    }

    override val type: RecipeType<PulverizerRecipe> = PulverizerRecipe.TYPE

    override fun getUpgradeSlots(): IntArray = intArrayOf(5, 6, 7, 8)

    override fun getAvailableUpgrades(): Array<Upgrade> = Upgrade.DEFAULT
}