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

package me.desht.pneumaticcraft.client.gui.remote.actionwidget;

import me.desht.pneumaticcraft.client.gui.RemoteEditorScreen;
import me.desht.pneumaticcraft.client.util.ClientUtils;
import me.desht.pneumaticcraft.common.variables.GlobalVariableHelper;
import me.desht.pneumaticcraft.lib.Log;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

public abstract class ActionWidget<W extends AbstractWidget> {
    protected W widget;
    private String enableVariable = "";
    private BlockPos enablingValue = BlockPos.ZERO;

    ActionWidget(W widget) {
        this.widget = widget;
    }

    ActionWidget() {
    }

    public void readFromNBT(CompoundTag tag, int guiLeft, int guiTop) {
        enableVariable = tag.getString("enableVariable");
        enablingValue = tag.contains("enablingX") ? new BlockPos(tag.getInt("enablingX"), tag.getInt("enablingY"), tag.getInt("enablingZ")) : new BlockPos(1, 0, 0);
    }

    public CompoundTag toNBT(int guiLeft, int guiTop) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", getId());
        tag.putString("enableVariable", enableVariable);
        tag.putInt("enablingX", enablingValue.getX());
        tag.putInt("enablingY", enablingValue.getY());
        tag.putInt("enablingZ", enablingValue.getZ());
        return tag;
    }

    public ActionWidget<?> copy() {
        try {
            ActionWidget<?> widget = this.getClass().getDeclaredConstructor().newInstance();
            widget.readFromNBT(this.toNBT(0, 0), 0, 0);
            return widget;
        } catch (Exception e) {
            Log.error("Error occurred when trying to copy an " + getId() + " action widget.");
            e.printStackTrace();
            return null;
        }
    }

    public W getWidget() {
        return widget;
    }

    public abstract void setWidgetPos(int x, int y);

    public abstract String getId();

    public Screen getGui(RemoteEditorScreen guiRemote) {
        return null;
    }

    public void setEnableVariable(String varName) {
        this.enableVariable = varName;
    }

    public String getEnableVariable() {
        return enableVariable;
    }

    public boolean isEnabled() {
        if (enableVariable.isEmpty()) return true;
        BlockPos pos = GlobalVariableHelper.getPos(ClientUtils.getClientPlayer().getUUID(), enableVariable, BlockPos.ZERO);
        return pos.equals(enablingValue);
    }

    public void setEnablingValue(int x, int y, int z) {
        enablingValue = new BlockPos(x, y, z);
    }

    public BlockPos getEnablingValue() {
        return enablingValue;
    }
}