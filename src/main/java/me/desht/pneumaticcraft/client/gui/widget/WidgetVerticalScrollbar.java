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

package me.desht.pneumaticcraft.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import me.desht.pneumaticcraft.client.render.ModRenderTypes;
import me.desht.pneumaticcraft.client.util.GuiUtils;
import me.desht.pneumaticcraft.lib.Textures;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.Validate;

import static me.desht.pneumaticcraft.client.util.RenderUtils.*;

public class WidgetVerticalScrollbar extends AbstractWidget implements ICanRender3d {
    public float currentScroll;
    private int states;
    private boolean listening;
    private boolean dragging;

    public WidgetVerticalScrollbar(int x, int y, int height) {
        super(x, y, 14, height, Component.empty());
    }

    public WidgetVerticalScrollbar setStates(int states) {
        this.states = states;
        return this;
    }

    public WidgetVerticalScrollbar setCurrentState(int state) {
        Validate.isTrue(state >= 0 && state <= states, "State " + state + " out of range! Valid range [1 - " + states + "] inclusive");
        currentScroll = (float) state / states;
        return this;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double dir) {
        if (active && listening) {
            double wheel = Mth.clamp(-dir, -1, 1);
            currentScroll = Mth.clamp(currentScroll + (float) wheel / states,0f, 1);
            return true;
        }
        return false;
    }

    @Override
    public void onClick(double x, double y) {
        currentScroll = (float) (y - 7 - this.y) / (height - 17);
        currentScroll = Mth.clamp(currentScroll, 0, 1);
        dragging = true;
    }

    @Override
    public void onRelease(double x, double y) {
        dragging = false;
    }

    @Override
    protected void onDrag(double x, double y, double dx, double dy) {
        dragging = true;
        currentScroll = (float) (y - 7 - this.y) / (height - 17);
        currentScroll = Mth.clamp(currentScroll, 0, 1);
    }

    public WidgetVerticalScrollbar setListening(boolean listening) {
        this.listening = listening;
        return this;
    }

    public int getState() {
        float scroll = currentScroll;
        scroll += 0.5F / states;
        return Mth.clamp((int) (scroll * states), 0, states);
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTick) {
        if (visible) {
            GuiUtils.bindTexture(Textures.WIDGET_VERTICAL_SCROLLBAR);
            blit(matrixStack, x, y, 12, 0, width, 1, 26, 15);
            for (int i = 0; i < height - 2; i++)
                blit(matrixStack, x, y + 1 + i, 12, 1, width, 1, 26, 15);
            blit(matrixStack, x, y + height - 1, 12, 14, width, 1, 26, 15);

            if (!active) RenderSystem.setShaderColor(0.6F, 0.6F, 0.6F, 1);
            blit(matrixStack, x + 1, y + 1 + (int) ((height - 17) * currentScroll), 0, 0, 12, 15, 26, 15);
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }
    }

    public boolean isDragging() {
        return dragging;
    }

    @Override
    public void render3d(PoseStack matrixStack, MultiBufferSource buffer, float partialTicks) {
        if (visible) {
            renderWithTypeAndFinish(matrixStack, buffer, ModRenderTypes.getTextureRenderColored(Textures.WIDGET_VERTICAL_SCROLLBAR, true), (posMat, builder)-> {
                blit3d(builder, posMat, x, y, 12, 0, width, 1, 26, 15);
                for (int i = 0; i < height - 2; i++) {
                    blit3d(builder, posMat, x, y + 1 + i, 12, 1, width, 1, 26, 15);
                }
                blit3d(builder, posMat, x, y + height - 1, 12, 14, width, 1, 26, 15);
                blit3d(builder, posMat, x + 1, y + 1 + (int) ((height - 17) * currentScroll), 0, 0, 12, 15, 26, 15);
            });
        }
    }

    private void blit3d(VertexConsumer builder, Matrix4f posMat, int x, int y, int textureX, int textureY, int width, int height, int textureWidth, int textureHeight) {
        float u1 = (float) textureX / textureWidth;
        float u2 = (float) (textureX + width) / textureWidth;
        float v1 = (float) textureY / textureHeight;
        float v2 = (float) (textureY + height) / textureHeight;

        posF(builder, posMat, x, y + height, 0)
                .color(255, 255, 255, 255)
                .uv(u1, v2)
                .uv2(FULL_BRIGHT)
                .endVertex();
        posF(builder, posMat, x + width, y + height, 0)
                .color(255, 255, 255, 255)
                .uv(u2, v2)
                .uv2(FULL_BRIGHT)
                .endVertex();
        posF(builder, posMat, x + width, y, 0)
                .color(255, 255, 255, 255)
                .uv(u2, v1)
                .uv2(FULL_BRIGHT)
                .endVertex();
        posF(builder, posMat, x, y, 0)
                .color(255, 255, 255, 255)
                .uv(u1, v1)
                .uv2(FULL_BRIGHT)
                .endVertex();
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
    }
}
