package me.steven.indrev.registry

import me.steven.indrev.api.machines.Tier
import me.steven.indrev.blockentities.cables.BasePipeBlockEntity
import me.steven.indrev.blockentities.miningrig.DrillBlockEntity
import me.steven.indrev.blockentities.farms.BiomassComposterBlockEntity
import me.steven.indrev.blockentities.generators.SteamTurbineBlockEntity
import me.steven.indrev.blockentities.generators.SteamTurbineSteamInputValveBlockEntity
import me.steven.indrev.blockentities.laser.CapsuleBlockEntity
import me.steven.indrev.blockentities.solarpowerplant.*
import me.steven.indrev.blockentities.storage.CabinetBlockEntity
import me.steven.indrev.blockentities.storage.TankBlockEntity
import me.steven.indrev.blocks.HeliostatBlock
import me.steven.indrev.blocks.machine.CapsuleBlock
import me.steven.indrev.blocks.machine.DrillBlock
import me.steven.indrev.blocks.machine.pipes.BasePipeBlock
import me.steven.indrev.blocks.machine.pipes.CableBlock
import me.steven.indrev.blocks.machine.pipes.FluidPipeBlock
import me.steven.indrev.blocks.machine.pipes.ItemPipeBlock
import me.steven.indrev.blocks.machine.solarpowerplant.*
import me.steven.indrev.blocks.misc.*
import me.steven.indrev.networks.energy.CableEnergyIo
import me.steven.indrev.utils.*
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.MapColor
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.BlockItem
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.math.Direction
import net.minecraft.util.registry.Registry
import team.reborn.energy.api.EnergyStorage

@Suppress("MemberVisibilityCanBePrivate")
object IRBlockRegistry {

    fun registerAll() {

        identifier("sulfur_crystal").block(SULFUR_CRYSTAL_CLUSTER).item(IRItemRegistry.SULFUR_CRYSTAL_ITEM)

        identifier("planks").block(PLANKS).item(BlockItem(PLANKS, itemSettings()))
        identifier("plank_block").block(PLANK_BLOCK).item(BlockItem(PLANK_BLOCK, itemSettings()))
        FlammableBlockRegistry.getDefaultInstance().add(PLANKS, 10, 40)
        FlammableBlockRegistry.getDefaultInstance().add(PLANK_BLOCK, 10, 40)

        identifier("biomass_composter").block(BIOMASS_COMPOSTER_BLOCK).item(BIOMASS_COMPOSTER_ITEM).blockEntityType(BIOMASS_COMPOSTER_BLOCK_ENTITY)

        FluidStorage.SIDED.registerForBlockEntity({ be, c ->
            if (c != Direction.UP) be.fluidInv
            else null
        }, BIOMASS_COMPOSTER_BLOCK_ENTITY)

        ItemStorage.SIDED.registerForBlockEntity({ be, c ->
            if (c != Direction.UP) be.itemInv
            else null
        }, BIOMASS_COMPOSTER_BLOCK_ENTITY)

        identifier("wither_proof_obsidian").block(WITHER_PROOF_OBSIDIAN).item(BlockItem(WITHER_PROOF_OBSIDIAN, itemSettings()))

        identifier("machine_block").block(MACHINE_BLOCK).item(BlockItem(MACHINE_BLOCK, itemSettings()))

        identifier("controller").block(CONTROLLER).item(BlockItem(CONTROLLER, itemSettings()))
        identifier("frame").block(FRAME).item(BlockItem(FRAME, itemSettings()))
        identifier("duct").block(DUCT).item(BlockItem(DUCT, itemSettings()))
        identifier("silo").block(SILO).item(BlockItem(SILO, itemSettings()))
        identifier("warning_strobe").block(WARNING_STROBE).item(BlockItem(WARNING_STROBE, itemSettings()))
        identifier("intake").block(INTAKE).item(BlockItem(INTAKE, itemSettings()))
        identifier("cabinet").block(CABINET).item(BlockItem(CABINET, itemSettings())).blockEntityType(CABINET_BLOCK_ENTITY_TYPE)

        identifier("drill_top").block(DRILL_TOP)
        identifier("drill_middle").block(DRILL_MIDDLE)
        identifier("drill_bottom").block(DRILL_BOTTOM).item(BlockItem(DRILL_BOTTOM, itemSettings()))
        identifier("drill").blockEntityType(DRILL_BLOCK_ENTITY_TYPE)

        identifier("tank").block(TANK_BLOCK).item(IRItemRegistry.TANK_BLOCK_ITEM).blockEntityType(TANK_BLOCK_ENTITY)

        FluidStorage.SIDED.registerForBlockEntity({ be, _ ->
            val combinedStorage = TankBlockEntity.CombinedTankStorage()
            TankBlock.findAllTanks(be.world!!.getChunk(be.pos), be.world!!.getBlockState(be.pos), be.pos, mutableSetOf(), combinedStorage)
            combinedStorage
        }, TANK_BLOCK_ENTITY)

        identifier("capsule").block(CAPSULE_BLOCK).item(IRItemRegistry.CAPSULE_BLOCK_ITEM).blockEntityType(CAPSULE_BLOCK_ENTITY)

        identifier("fluid_pipe_mk1").block(FLUID_PIPE_MK1)
        identifier("fluid_pipe_mk2").block(FLUID_PIPE_MK2)
        identifier("fluid_pipe_mk3").block(FLUID_PIPE_MK3)
        identifier("fluid_pipe_mk4").block(FLUID_PIPE_MK4)

        identifier("item_pipe_mk1").block(ITEM_PIPE_MK1)
        identifier("item_pipe_mk2").block(ITEM_PIPE_MK2)
        identifier("item_pipe_mk3").block(ITEM_PIPE_MK3)
        identifier("item_pipe_mk4").block(ITEM_PIPE_MK4)

        identifier("cable_mk1").block(CABLE_MK1).blockEntityType(COVERABLE_BLOCK_ENTITY_TYPE_MK1)
        identifier("cable_mk2").block(CABLE_MK2).blockEntityType(COVERABLE_BLOCK_ENTITY_TYPE_MK2)
        identifier("cable_mk3").block(CABLE_MK3).blockEntityType(COVERABLE_BLOCK_ENTITY_TYPE_MK3)
        identifier("cable_mk4").block(CABLE_MK4).blockEntityType(COVERABLE_BLOCK_ENTITY_TYPE_MK4)

        EnergyStorage.SIDED.registerForBlocks({ world, pos, _, be, dir ->
            if (world is ServerWorld && be is BasePipeBlockEntity) {
                val energyNetwork = world.energyNetworkState.networksByPos[pos.asLong()]
                if (energyNetwork != null && be.connections[dir]?.isConnected() == true) {
                    return@registerForBlocks CableEnergyIo(energyNetwork, pos, dir)
                }
            }
            CableEnergyIo.NO_NETWORK
        }, CABLE_MK1, CABLE_MK2, CABLE_MK3, CABLE_MK4)

        identifier("heliostat")
            .block(HELIOSTAT_BLOCK)
            .item(HELIOSTAT_BLOCK_ITEM)
            .blockEntityType(HELIOSTAT_BLOCK_ENTITY)

        identifier("resistant_glass")
            .block(RESISTANT_GLASS_BLOCK)
            .item(RESISTANT_GLASS_BLOCK_ITEM)

        identifier("solar_receiver")
            .block(SOLAR_RECEIVER_BLOCK)
            .item(SOLAR_RECEIVER_BLOCK_ITEM)
            .blockEntityType(SOLAR_RECEIVER_BLOCK_ENTITY)

        identifier("fluid_valve").block(FLUID_VALVE).item(FLUID_VALVE_ITEM)

        FluidStorage.SIDED.registerForBlocks({ world, pos, _, _, dir ->
            val aboveBlockEntity = world.getBlockEntity(pos.up()) as? SolarPowerPlantTowerBlockEntity
            return@registerForBlocks aboveBlockEntity?.fluidComponent?.getCachedSide(dir)
        }, FLUID_VALVE)

        identifier("steam_turbine_steam_input_valve")
            .block(STEAM_TURBINE_STEAM_INPUT_VALVE_BLOCK)
            .item(STEAM_TURBINE_STEAM_INPUT_VALVE_BLOCK_ITEM)
            .blockEntityType(STEAM_TURBINE_STEAM_INPUT_VALVE_BLOCK_ENTITY)

        FluidStorage.SIDED.registerForBlockEntity({ be, _ -> be.getSteamTurbine()?.fluidComponent }, STEAM_TURBINE_STEAM_INPUT_VALVE_BLOCK_ENTITY)

        identifier("steam_turbine_energy_output").block(STEAM_TURBINE_ENERGY_OUTPUT).item(STEAM_TURBINE_ENERGY_OUTPUT_ITEM)

        EnergyStorage.SIDED.registerForBlocks({ world, pos, _, _, s ->
            val turbineBlockEntity = world.getBlockEntity(pos.up()) as? SteamTurbineBlockEntity
            if (turbineBlockEntity?.multiblockComponent?.isBuilt(world, pos.up(), turbineBlockEntity.cachedState) == true)
                turbineBlockEntity.storage.getSideStorage(s)
            else
                null
        }, STEAM_TURBINE_ENERGY_OUTPUT)

        identifier("steam_turbine_casing").block(STEAM_TURBINE_CASING_BLOCK).item(STEAM_TURBINE_CASING_BLOCK_ITEM)
        identifier("steam_turbine_rotor").block(STEAM_TURBINE_ROTOR_BLOCK).item(STEAM_TURBINE_ROTOR_BLOCK_ITEM)
        identifier("steam_turbine_pressure_valve").block(STEAM_TURBINE_PRESSURE_VALVE_BLOCK).item(STEAM_TURBINE_PRESSURE_VALVE_BLOCK_ITEM)

        identifier("solar_power_plant_tower")
            .block(SOLAR_POWER_PLANT_TOWER_BLOCK)
            .item(SOLAR_POWER_PLANT_TOWER_BLOCK_ITEM)
            .blockEntityType(SOLAR_POWER_PLANT_TOWER_BLOCK_ENTITY)
    }

    val SULFUR_CRYSTAL_CLUSTER = SulfurCrystalBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.GLASS).requiresTool().strength(3f, 3f))

    val NIKOLITE_ORE = { Registry.BLOCK.get(identifier("nikolite_ore")) }
    val TIN_ORE = { Registry.BLOCK.get(identifier("tin_ore")) }
    val LEAD_ORE = { Registry.BLOCK.get(identifier("lead_ore")) }
    val SILVER_ORE = { Registry.BLOCK.get(identifier("silver_ore")) }
    val TUNGSTEN_ORE = { Registry.BLOCK.get(identifier("tungsten_ore")) }

    val DEEPSLATE_NIKOLITE_ORE = { Registry.BLOCK.get(identifier("deepslate_nikolite_ore")) }
    val DEEPSLATE_TIN_ORE = { Registry.BLOCK.get(identifier("deepslate_tin_ore")) }
    val DEEPSLATE_LEAD_ORE = { Registry.BLOCK.get(identifier("deepslate_lead_ore")) }
    val DEEPSLATE_SILVER_ORE = { Registry.BLOCK.get(identifier("deepslate_silver_ore")) }
    val DEEPSLATE_TUNGSTEN_ORE = { Registry.BLOCK.get(identifier("deepslate_tungsten_ore")) }

    val MACHINE_BLOCK = Block(
        FabricBlockSettings.of(Material.METAL).requiresTool().strength(3F, 6F)
    )
    val PLANKS = PlankBlock(
        FabricBlockSettings.of(Material.WOOD, MapColor.BROWN).strength(2.0f).sounds(BlockSoundGroup.WOOD)
    )
    val PLANK_BLOCK = Block(
        FabricBlockSettings.of(Material.WOOD, MapColor.BROWN).strength(3F, 6F).sounds(BlockSoundGroup.WOOD)
    )

    val BIOMASS_COMPOSTER_BLOCK = BiomassComposterBlock()
    val BIOMASS_COMPOSTER_ITEM = BlockItem(BIOMASS_COMPOSTER_BLOCK, itemSettings())
    val BIOMASS_COMPOSTER_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(::BiomassComposterBlockEntity, BIOMASS_COMPOSTER_BLOCK).build()

    val WITHER_PROOF_OBSIDIAN = Block(
        FabricBlockSettings.of(Material.STONE, MapColor.BLACK).requiresTool().strength(50.0F, 1200.0F).sounds(BlockSoundGroup.STONE)
    )

    val CONTROLLER = HorizontalFacingBlock(
        FabricBlockSettings.of(Material.METAL).requiresTool().strength(3F, 6F)
    )
    val DUCT = DuctBlock(
        FabricBlockSettings.of(Material.METAL).requiresTool().strength(3F, 6F)
    )
    val FRAME = Block(
        FabricBlockSettings.of(Material.METAL).requiresTool().strength(3F, 6F)
    )
    val SILO = Block(
        FabricBlockSettings.of(Material.METAL).requiresTool().strength(3F, 6F)
    )
    val WARNING_STROBE = WarningStrobeBlock(
        FabricBlockSettings.of(Material.METAL).requiresTool().luminance(15).nonOpaque().strength(3F, 6F)
    )
    val INTAKE =  HorizontalFacingBlock(
        FabricBlockSettings.of(Material.METAL).requiresTool().strength(3F, 6F)
    )
    val CABINET = CabinetBlock(
        FabricBlockSettings.of(Material.METAL).requiresTool().strength(3F, 6F)
    )
    val CABINET_BLOCK_ENTITY_TYPE: BlockEntityType<CabinetBlockEntity> = FabricBlockEntityTypeBuilder.create(::CabinetBlockEntity, CABINET).build(null)

    val DRILL_TOP = DrillBlock.TopDrillBlock(
        FabricBlockSettings.of(Material.METAL).requiresTool().nonOpaque().strength(3F, 6F)
    )
    val DRILL_MIDDLE = DrillBlock.MiddleDrillBlock(
        FabricBlockSettings.of(Material.METAL).requiresTool().nonOpaque().strength(3F, 6F)
    )
    val DRILL_BOTTOM = DrillBlock.BottomDrillBlock(
        FabricBlockSettings.of(Material.METAL).requiresTool().nonOpaque().strength(3F, 6F)
    )
    val DRILL_BLOCK_ENTITY_TYPE: BlockEntityType<DrillBlockEntity> = BlockEntityType.Builder.create(::DrillBlockEntity, DRILL_BOTTOM).build(null)

    val TANK_BLOCK = TankBlock(FabricBlockSettings.of(Material.GLASS).nonOpaque().requiresTool().strength(1f, 1f))

    val TANK_BLOCK_ENTITY: BlockEntityType<TankBlockEntity> = BlockEntityType.Builder.create(::TankBlockEntity, TANK_BLOCK).build(null)

    val CAPSULE_BLOCK = CapsuleBlock()

    val CAPSULE_BLOCK_ENTITY: BlockEntityType<CapsuleBlockEntity> = BlockEntityType.Builder.create(::CapsuleBlockEntity, CAPSULE_BLOCK).build(null)

    val FLUID_PIPE_MK1 = FluidPipeBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque().solidBlock { _, _, _ -> false }.requiresTool(), Tier.MK1)
    val FLUID_PIPE_MK2 = FluidPipeBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque().solidBlock { _, _, _ -> false }.requiresTool(), Tier.MK2)
    val FLUID_PIPE_MK3 = FluidPipeBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque().solidBlock { _, _, _ -> false }.requiresTool(), Tier.MK3)
    val FLUID_PIPE_MK4 = FluidPipeBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque().solidBlock { _, _, _ -> false }.requiresTool(), Tier.MK4)

    val ITEM_PIPE_MK1 = ItemPipeBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque().solidBlock { _, _, _ -> false }.requiresTool(), Tier.MK1)
    val ITEM_PIPE_MK2 = ItemPipeBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque().solidBlock { _, _, _ -> false }.requiresTool(), Tier.MK2)
    val ITEM_PIPE_MK3 = ItemPipeBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque().solidBlock { _, _, _ -> false }.requiresTool(), Tier.MK3)
    val ITEM_PIPE_MK4 = ItemPipeBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque().solidBlock { _, _, _ -> false }.requiresTool(), Tier.MK4)

    val CABLE_MK1 = CableBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque().solidBlock { _, _, _ -> false }.requiresTool(), Tier.MK1)
    val CABLE_MK2 = CableBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque().solidBlock { _, _, _ -> false }.requiresTool(), Tier.MK2)
    val CABLE_MK3 = CableBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque().solidBlock { _, _, _ -> false }.requiresTool(), Tier.MK3)
    val CABLE_MK4 = CableBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque().solidBlock { _, _, _ -> false }.requiresTool(), Tier.MK4)

    val COVERABLE_BLOCK_ENTITY_TYPE_MK1 = FabricBlockEntityTypeBuilder.create({ pos, state -> BasePipeBlockEntity((state.block as BasePipeBlock).type, Tier.MK1, pos, state) }, FLUID_PIPE_MK1, ITEM_PIPE_MK1, CABLE_MK1).build(null)

    val COVERABLE_BLOCK_ENTITY_TYPE_MK2 = FabricBlockEntityTypeBuilder.create({ pos, state -> BasePipeBlockEntity((state.block as BasePipeBlock).type, Tier.MK2, pos, state) }, FLUID_PIPE_MK2, ITEM_PIPE_MK2, CABLE_MK2).build(null)

    val COVERABLE_BLOCK_ENTITY_TYPE_MK3 = FabricBlockEntityTypeBuilder.create({ pos, state -> BasePipeBlockEntity((state.block as BasePipeBlock).type, Tier.MK3, pos, state) }, FLUID_PIPE_MK3, ITEM_PIPE_MK3, CABLE_MK3).build(null)

    val COVERABLE_BLOCK_ENTITY_TYPE_MK4 = FabricBlockEntityTypeBuilder.create({ pos, state -> BasePipeBlockEntity((state.block as BasePipeBlock).type, Tier.MK4, pos, state) }, FLUID_PIPE_MK4, ITEM_PIPE_MK4, CABLE_MK4).build(null)

    val HELIOSTAT_BLOCK = HeliostatBlock(
        FabricBlockSettings.of(Material.METAL).requiresTool().nonOpaque().strength(3F, 6F)
    )
    val HELIOSTAT_BLOCK_ITEM = BlockItem(HELIOSTAT_BLOCK, itemSettings())
    val HELIOSTAT_BLOCK_ENTITY = BlockEntityType.Builder.create(::HeliostatBlockEntity, HELIOSTAT_BLOCK).build(null)

    val SOLAR_RECEIVER_BLOCK = SolarReceiverBlock(
        FabricBlockSettings.of(Material.METAL).requiresTool().nonOpaque().strength(3F, 6F)
    )
    val SOLAR_RECEIVER_BLOCK_ITEM = BlockItem(SOLAR_RECEIVER_BLOCK, itemSettings())
    val SOLAR_RECEIVER_BLOCK_ENTITY = BlockEntityType.Builder.create(::SolarReceiverBlockEntity, SOLAR_RECEIVER_BLOCK).build(null)

    val STEAM_TURBINE_STEAM_INPUT_VALVE_BLOCK = SteamTurbineSteamInputValveBlock(FabricBlockSettings.of(Material.METAL).strength(3F, 6F))
    val STEAM_TURBINE_STEAM_INPUT_VALVE_BLOCK_ITEM = BlockItem(STEAM_TURBINE_STEAM_INPUT_VALVE_BLOCK, itemSettings())
    val STEAM_TURBINE_STEAM_INPUT_VALVE_BLOCK_ENTITY = BlockEntityType.Builder.create(::SteamTurbineSteamInputValveBlockEntity, STEAM_TURBINE_STEAM_INPUT_VALVE_BLOCK).build(null)

    val RESISTANT_GLASS_BLOCK = Block(
        FabricBlockSettings.of(Material.GLASS).requiresTool().nonOpaque().strength(3F, 6F)
    )
    val RESISTANT_GLASS_BLOCK_ITEM = BlockItem(RESISTANT_GLASS_BLOCK, itemSettings())

    val FLUID_VALVE = FluidValveBlock(FabricBlockSettings.of(Material.METAL).strength(3F, 6F))
    val FLUID_VALVE_ITEM = BlockItem(FLUID_VALVE, itemSettings())

    val STEAM_TURBINE_ENERGY_OUTPUT = HorizontalFacingBlock(FabricBlockSettings.of(Material.METAL).strength(3F, 6F))
    val STEAM_TURBINE_ENERGY_OUTPUT_ITEM = BlockItem(STEAM_TURBINE_ENERGY_OUTPUT, itemSettings())

    val STEAM_TURBINE_CASING_BLOCK = Block(FabricBlockSettings.of(Material.METAL).strength(3F, 6F))
    val STEAM_TURBINE_CASING_BLOCK_ITEM = BlockItem(STEAM_TURBINE_CASING_BLOCK, itemSettings())

    val STEAM_TURBINE_ROTOR_BLOCK = VerticalFacingBlock(FabricBlockSettings.of(Material.METAL).strength(3F, 6F))
    val STEAM_TURBINE_ROTOR_BLOCK_ITEM = BlockItem(STEAM_TURBINE_ROTOR_BLOCK, itemSettings())

    val STEAM_TURBINE_PRESSURE_VALVE_BLOCK = HorizontalFacingBlock(FabricBlockSettings.of(Material.METAL).strength(3F, 6F))
    val STEAM_TURBINE_PRESSURE_VALVE_BLOCK_ITEM = BlockItem(STEAM_TURBINE_PRESSURE_VALVE_BLOCK, itemSettings())

    val SOLAR_POWER_PLANT_TOWER_BLOCK = SolarPowerPlantTowerBlock(FabricBlockSettings.of(Material.METAL).strength(3F, 6F))
    val SOLAR_POWER_PLANT_TOWER_BLOCK_ITEM = BlockItem(SOLAR_POWER_PLANT_TOWER_BLOCK, itemSettings())
    val SOLAR_POWER_PLANT_TOWER_BLOCK_ENTITY = BlockEntityType.Builder.create(::SolarPowerPlantTowerBlockEntity, SOLAR_POWER_PLANT_TOWER_BLOCK).build(null)
}