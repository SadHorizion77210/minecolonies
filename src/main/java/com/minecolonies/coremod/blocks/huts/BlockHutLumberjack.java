package com.minecolonies.coremod.blocks.huts;

import com.minecolonies.api.blocks.AbstractBlockHut;
import com.minecolonies.api.colony.buildings.ModBuildings;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import org.jetbrains.annotations.NotNull;

/**
 * Hut for the lumberjack.
 * No different from {@link AbstractBlockHut}
 */
public class BlockHutLumberjack extends AbstractBlockHut<BlockHutLumberjack>
{
    public BlockHutLumberjack()
    {
        //No different from Abstract parent
        super();
    }

    @NotNull
    @Override
    public String getName()
    {
        return "blockHutLumberjack";
    }

    @Override
    public BuildingEntry getBuildingEntry()
    {
        return ModBuildings.lumberjack;
    }
}
