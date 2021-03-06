package com.minecolonies.coremod.placementhandlers;

import com.ldtteam.structurize.api.util.ItemStackUtils;
import com.ldtteam.structurize.placementhandlers.IPlacementHandler;
import com.ldtteam.structurize.placementhandlers.PlacementHandlers;
import com.ldtteam.structurize.util.BlockUtils;
import com.ldtteam.structurize.util.PlacementSettings;
import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.compatibility.candb.ChiselAndBitsCheck;
import com.minecolonies.api.util.Log;
import com.minecolonies.coremod.blocks.BlockMinecoloniesRack;
import com.minecolonies.coremod.blocks.schematic.BlockWaypoint;
import com.minecolonies.coremod.colony.buildings.workerbuildings.BuildingWareHouse;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ldtteam.structurize.placementhandlers.PlacementHandlers.getItemsFromTileEntity;
import static com.ldtteam.structurize.placementhandlers.PlacementHandlers.handleTileEntityPlacement;
import static com.minecolonies.api.util.constant.Constants.UPDATE_FLAG;

/**
 * Contains all Minecolonies specific placement handlers.
 */
public final class MinecoloniesPlacementHandlers
{
    /**
     * Private constructor to hide implicit one.
     */
    private MinecoloniesPlacementHandlers()
    {
        /*
         * Intentionally left empty.
         */
    }

    public static void initHandlers()
    {
        PlacementHandlers.handlers.clear();
        PlacementHandlers.handlers.add(new PlacementHandlers.AirPlacementHandler());
        PlacementHandlers.handlers.add(new PlacementHandlers.FirePlacementHandler());
        PlacementHandlers.handlers.add(new PlacementHandlers.GrassPlacementHandler());
        PlacementHandlers.handlers.add(new PlacementHandlers.DoorPlacementHandler());
        PlacementHandlers.handlers.add(new PlacementHandlers.BedPlacementHandler());
        PlacementHandlers.handlers.add(new PlacementHandlers.DoublePlantPlacementHandler());
        PlacementHandlers.handlers.add(new PlacementHandlers.SpecialBlockPlacementAttemptHandler());
        PlacementHandlers.handlers.add(new PlacementHandlers.FlowerPotPlacementHandler());
        PlacementHandlers.handlers.add(new PlacementHandlers.BlockGrassPathPlacementHandler());
        PlacementHandlers.handlers.add(new PlacementHandlers.StairBlockPlacementHandler());
        PlacementHandlers.handlers.add(new PlacementHandlers.BlockSolidSubstitutionPlacementHandler());
        PlacementHandlers.handlers.add(new ChestPlacementHandler());
        PlacementHandlers.handlers.add(new WayPointBlockPlacementHandler());
        PlacementHandlers.handlers.add(new RackPlacementHandler());
        PlacementHandlers.handlers.add(new PlacementHandlers.FallingBlockPlacementHandler());
        PlacementHandlers.handlers.add(new PlacementHandlers.BannerPlacementHandler());
        PlacementHandlers.handlers.add(new BuildingSubstitutionBlock());
        PlacementHandlers.handlers.add(new GeneralBlockPlacementHandler());
    }

    public static class WayPointBlockPlacementHandler implements IPlacementHandler
    {
        @Override
        public boolean canHandle(@NotNull final World world, @NotNull final BlockPos pos, @NotNull final BlockState blockState)
        {
            return blockState.getBlock() instanceof BlockWaypoint;
        }

        @Override
        public Object handle(
          @NotNull final World world,
          @NotNull final BlockPos pos,
          @NotNull final BlockState blockState,
          @Nullable final CompoundNBT tileEntityData,
          final boolean complete,
          final BlockPos centerPos)
        {
            world.removeBlock(pos, false);
            final IColony colony = IColonyManager.getInstance().getClosestColony(world, pos);
            if (colony != null)
            {
                if (!complete)
                {
                    colony.addWayPoint(pos, Blocks.AIR.getDefaultState());
                }
                else
                {
                    world.setBlockState(pos, blockState);
                }
            }
            return blockState;
        }

        @Override
        public List<ItemStack> getRequiredItems(@NotNull final World world, @NotNull final BlockPos pos, @NotNull final BlockState blockState, @Nullable final CompoundNBT tileEntityData, final boolean complete)
        {
            return new ArrayList<>();
        }
    }

    public static class RackPlacementHandler implements IPlacementHandler
    {
        @Override
        public boolean canHandle(@NotNull final World world, @NotNull final BlockPos pos, @NotNull final BlockState blockState)
        {
            return blockState.getBlock() instanceof BlockMinecoloniesRack;
        }

        @Override
        public Object handle(
          @NotNull final World world,
          @NotNull final BlockPos pos,
          @NotNull final BlockState blockState,
          @Nullable final CompoundNBT tileEntityData,
          final boolean complete,
          final BlockPos centerPos)
        {
            if (world.getBlockState(pos).getBlock() == ModBlocks.blockRack)
            {
                return blockState;
            }
            
            if (tileEntityData != null)
            {
                handleTileEntityPlacement(tileEntityData, world, pos);
            }

            final TileEntity entity = world.getTileEntity(pos);
            if (entity instanceof ChestTileEntity)
            {
                BuildingWareHouse.handleBuildingOverChest(pos, (ChestTileEntity) entity, world);
            }
            else if (!world.setBlockState(pos, blockState, UPDATE_FLAG))
            {
                return ActionProcessingResult.DENY;
            }

            return blockState;
        }

        @Override
        public List<ItemStack> getRequiredItems(@NotNull final World world, @NotNull final BlockPos pos, @NotNull final BlockState blockState, @Nullable final CompoundNBT tileEntityData, final boolean complete)
        {
            final List<ItemStack> itemList = new ArrayList<>();
            itemList.add(BlockUtils.getItemStackFromBlockState(blockState));

            for (final ItemStack stack : getItemsFromTileEntity(tileEntityData, world))
            {
                if (!ItemStackUtils.isEmpty(stack))
                {
                    itemList.add(stack);
                }
            }
            return itemList;
        }
    }

    public static class ChestPlacementHandler implements IPlacementHandler
    {
        @Override
        public boolean canHandle(@NotNull final World world, @NotNull final BlockPos pos, @NotNull final BlockState blockState)
        {
            return blockState.getBlock() instanceof BlockMinecoloniesRack;
        }

        @Override
        public Object handle(
          @NotNull final World world,
          @NotNull final BlockPos pos,
          @NotNull final BlockState blockState,
          @Nullable final CompoundNBT tileEntityData,
          final boolean complete,
          final BlockPos centerPos,
          final PlacementSettings settings)
        {
            final TileEntity entity = world.getTileEntity(pos);
            final IColony colony = IColonyManager.getInstance().getClosestColony(world, pos);
            if (colony != null && entity instanceof ChestTileEntity)
            {
                BuildingWareHouse.handleBuildingOverChest(pos, (ChestTileEntity) entity, world);
            }
            else
            {
                if (!world.setBlockState(pos, blockState, UPDATE_FLAG))
                {
                    return ActionProcessingResult.DENY;
                }

                if (tileEntityData != null)
                {
                    handleTileEntityPlacement(tileEntityData, world, pos, settings);
                }
            }

            return blockState;
        }

        @Override
        public List<ItemStack> getRequiredItems(@NotNull final World world, @NotNull final BlockPos pos, @NotNull final BlockState blockState, @Nullable final CompoundNBT tileEntityData, final boolean complete)
        {
            final List<ItemStack> itemList = new ArrayList<>();
            itemList.add(BlockUtils.getItemStackFromBlockState(blockState));
            itemList.addAll(getItemsFromTileEntity(tileEntityData, world));

            itemList.removeIf(ItemStackUtils::isEmpty);

            return itemList;
        }
    }

    public static class BuildingSubstitutionBlock implements IPlacementHandler
    {
        @Override
        public boolean canHandle(@NotNull final World world, @NotNull final BlockPos pos, @NotNull final BlockState blockState)
        {
            return blockState.getBlock() == ModBlocks.blockBarracksTowerSubstitution;
        }

        @Override
        public Object handle(
          @NotNull final World world,
          @NotNull final BlockPos pos,
          @NotNull final BlockState blockState,
          @Nullable final CompoundNBT tileEntityData,
          final boolean complete,
          final BlockPos centerPos)
        {
            return blockState;
        }

        @Override
        public List<ItemStack> getRequiredItems(@NotNull final World world, @NotNull final BlockPos pos, @NotNull final BlockState blockState, @Nullable final CompoundNBT tileEntityData, final boolean complete)
        {
            return Collections.emptyList();
        }
    }

    public static class GeneralBlockPlacementHandler implements IPlacementHandler
    {
        private static final Direction[] DIRS = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP};

        @Override
        public boolean canHandle(@NotNull final World world, @NotNull final BlockPos pos, @NotNull final BlockState blockState)
        {
            return true;
        }

        @Override
        public Object handle(
          @NotNull final World world,
          @NotNull final BlockPos pos,
          @NotNull final BlockState blockState,
          @Nullable final CompoundNBT tileEntityData,
          final boolean complete,
          final BlockPos centerPos,
          final PlacementSettings settings)
        {
            if (world.getBlockState(pos).equals(blockState))
            {
                return ActionProcessingResult.ACCEPT;
            }

            if (!world.setBlockState(pos, blockState, com.ldtteam.structurize.api.util.constant.Constants.UPDATE_FLAG))
            {
                return ActionProcessingResult.ACCEPT;
            }

            if (tileEntityData != null)
            {
                try
                {
                    handleTileEntityPlacement(tileEntityData, world, pos, settings);
                }
                catch (final Exception ex)
                {
                    Log.getLogger().warn("Unable to place TileEntity");
                }
            }

            return blockState;
        }

        @Override
        public List<ItemStack> getRequiredItems(@NotNull final World world, @NotNull final BlockPos pos, @NotNull final BlockState blockState, @Nullable final CompoundNBT tileEntityData, final boolean complete)
        {
            final List<ItemStack> itemList = new ArrayList<>();
            if (!ChiselAndBitsCheck.isChiselAndBitsBlock(blockState))
            {
                itemList.add(BlockUtils.getItemStackFromBlockState(blockState));
            }
            if (tileEntityData != null)
            {
                itemList.addAll(ItemStackUtils.getItemStacksOfTileEntity(tileEntityData, world));
            }
            itemList.removeIf(ItemStackUtils::isEmpty);

            return itemList;
        }
    }
}
