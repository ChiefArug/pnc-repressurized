/*
 * This file is part of pnc-repressurized.
 *
 *     pnc-repressurized is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     pnc-repressurized is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with pnc-repressurized.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.desht.pneumaticcraft.common.block;

import me.desht.pneumaticcraft.api.item.EnumUpgrade;
import me.desht.pneumaticcraft.client.ColorHandlers;
import me.desht.pneumaticcraft.client.render.fluid.IFluidItemRenderInfoProvider;
import me.desht.pneumaticcraft.client.render.fluid.RenderLiquidHopper;
import me.desht.pneumaticcraft.common.capabilities.FluidItemWrapper;
import me.desht.pneumaticcraft.common.core.ModItems;
import me.desht.pneumaticcraft.common.item.IFluidRendered;
import me.desht.pneumaticcraft.common.tileentity.TileEntityLiquidHopper;
import me.desht.pneumaticcraft.common.util.UpgradableItemUtils;
import me.desht.pneumaticcraft.lib.PneumaticValues;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;

public class BlockLiquidHopper extends BlockOmnidirectionalHopper {

    public BlockLiquidHopper() {
        super();
    }

    @Override
    protected Class<? extends TileEntity> getTileEntityClass() {
        return TileEntityLiquidHopper.class;
    }

    public static class ItemBlockLiquidHopper extends BlockItem implements ColorHandlers.ITintableItem, IFluidRendered {
        public static final String TANK_NAME = "Tank";
        RenderLiquidHopper.ItemRenderInfoProvider renderInfoProvider = null;

        public ItemBlockLiquidHopper(Block block) {
            super(block, ModItems.defaultProps());
        }

        @Override
        public boolean hasContainerItem(ItemStack stack) {
            return true;
        }

        @Override
        public ItemStack getContainerItem(ItemStack itemStack) {
            boolean creative = UpgradableItemUtils.hasCreativeUpgrade(itemStack);
            return FluidUtil.getFluidHandler(itemStack).map(handler -> {
                // TODO can (or indeed should) we support recipes which drain amounts other than 1000mB?
                handler.drain(1000, creative ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
                return handler.getContainer().copy();
            }).orElseThrow(RuntimeException::new);
        }

        @Nullable
        @Override
        public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
            return new FluidItemWrapper(stack, TANK_NAME, PneumaticValues.NORMAL_TANK_CAPACITY);
        }

        @Override
        public int getTintColor(ItemStack stack, int tintIndex) {
            int n = UpgradableItemUtils.getUpgrades(stack, EnumUpgrade.CREATIVE);
            return n > 0 ? 0xFFFF60FF : 0xFFFFFFFF;
        }

        @Override
        public IFluidItemRenderInfoProvider getFluidItemRenderer() {
            if (renderInfoProvider == null) renderInfoProvider = new RenderLiquidHopper.ItemRenderInfoProvider();
            return renderInfoProvider;
        }
    }
}
