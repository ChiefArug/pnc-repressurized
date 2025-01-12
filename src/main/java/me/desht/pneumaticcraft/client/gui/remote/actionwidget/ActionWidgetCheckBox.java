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

import me.desht.pneumaticcraft.client.gui.widget.WidgetCheckBox;
import me.desht.pneumaticcraft.client.util.ClientUtils;
import me.desht.pneumaticcraft.common.network.NetworkHandler;
import me.desht.pneumaticcraft.common.network.PacketSetGlobalVariable;
import me.desht.pneumaticcraft.common.util.NBTUtils;
import me.desht.pneumaticcraft.common.variables.GlobalVariableHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ActionWidgetCheckBox extends ActionWidgetVariable<WidgetCheckBox> implements IActionWidgetLabeled {
    public ActionWidgetCheckBox() {
    }

    public ActionWidgetCheckBox(WidgetCheckBox widget) {
        super(widget);
    }

    @Override
    public void readFromNBT(CompoundTag tag, int guiLeft, int guiTop) {
        super.readFromNBT(tag, guiLeft, guiTop);
        widget = new WidgetCheckBox(tag.getInt("x") + guiLeft, tag.getInt("y") + guiTop, 0xFF404040, deserializeTextComponent(tag.getString("text")), b -> onActionPerformed());
        widget.setTooltip(NBTUtils.deserializeTextComponents(tag.getList("tooltip", Tag.TAG_STRING)));
    }

    @Override
    public CompoundTag toNBT(int guiLeft, int guiTop) {
        CompoundTag tag = super.toNBT(guiLeft, guiTop);
        tag.putInt("x", widget.x - guiLeft);
        tag.putInt("y", widget.y - guiTop);
        tag.putString("text", Component.Serializer.toJson(widget.getMessage()));
        tag.put("tooltip", NBTUtils.serializeTextComponents(widget.getTooltip()));
        return tag;
    }

    @Override
    public String getId() {
        return "checkbox";
    }

    @Override
    public void setText(Component text) {
        widget.setMessage(text);
    }

    @Override
    public Component getText() {
        return widget.getMessage();
    }

    @Override
    public void onActionPerformed() {
        if (!getVariableName().isEmpty()) NetworkHandler.sendToServer(new PacketSetGlobalVariable(getVariableName(), widget.checked));
    }

    @Override
    public void onVariableChange() {
        widget.checked = GlobalVariableHelper.getBool(ClientUtils.getClientPlayer().getUUID(), getVariableName());
    }

    @Override
    public void setWidgetPos(int x, int y) {
        widget.x = x;
        widget.y = y;
    }

    @Override
    public void setTooltip(List<Component> text) {
        widget.setTooltip(text);
    }

    @Override
    public List<Component> getTooltip() {
        return widget.getTooltip();
    }
}
