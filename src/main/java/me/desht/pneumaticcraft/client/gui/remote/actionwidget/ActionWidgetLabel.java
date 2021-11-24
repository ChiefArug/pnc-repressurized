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

import me.desht.pneumaticcraft.client.gui.GuiRemoteEditor;
import me.desht.pneumaticcraft.client.gui.remote.GuiRemoteOptionBase;
import me.desht.pneumaticcraft.common.util.NBTUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

import java.util.List;

public class ActionWidgetLabel extends ActionWidget<WidgetLabelVariable> implements IActionWidgetLabeled {

    public ActionWidgetLabel(WidgetLabelVariable widget) {
        super(widget);
    }

    public ActionWidgetLabel() {
    }

    @Override
    public CompoundNBT toNBT(int guiLeft, int guiTop) {
        CompoundNBT tag = super.toNBT(guiLeft, guiTop);
        tag.putString("text", ITextComponent.Serializer.toJson(widget.getMessage()));
        tag.putInt("x", widget.x - guiLeft);
        tag.putInt("y", widget.y - guiTop);
        tag.put("tooltip", NBTUtils.serializeTextComponents(widget.getTooltip()));
        return tag;
    }

    @Override
    public void readFromNBT(CompoundNBT tag, int guiLeft, int guiTop) {
        super.readFromNBT(tag, guiLeft, guiTop);
        widget = new WidgetLabelVariable(tag.getInt("x") + guiLeft, tag.getInt("y") + guiTop, deserializeTextComponent(tag.getString("text")));
        widget.setTooltip(NBTUtils.deserializeTextComponents(tag.getList("tooltip", Constants.NBT.TAG_STRING)));
    }

    @Override
    public String getId() {
        return "label";
    }

    @Override
    public void setText(ITextComponent text) {
        widget.setMessage(text);
    }

    @Override
    public ITextComponent getText() {
        return widget.getMessage();
    }

    @Override
    public Screen getGui(GuiRemoteEditor guiRemote) {
        return new GuiRemoteOptionBase<>(this, guiRemote);
    }

    @Override
    public void setWidgetPos(int x, int y) {
        widget.x = x;
        widget.y = y;
    }

    @Override
    public void setTooltip(List<ITextComponent> text) {
        widget.setTooltip(text);
    }

    @Override
    public List<ITextComponent> getTooltip() {
        return widget.getTooltip();
    }
}
