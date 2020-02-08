package me.desht.pneumaticcraft.common.util;

import me.desht.pneumaticcraft.api.PNCCapabilities;
import me.desht.pneumaticcraft.api.item.EnumUpgrade;
import me.desht.pneumaticcraft.common.util.upgrade.UpgradeCache;
import me.desht.pneumaticcraft.lib.NBTKeys;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Arrays;
import java.util.List;

import static me.desht.pneumaticcraft.common.util.PneumaticCraftUtils.xlate;

/**
 * Some helper methods to manage items which can store upgrades (Pneumatic Armor, Drones...)
 */
public class UpgradableItemUtils {
    public static final String NBT_CREATIVE = "CreativeUpgrade";
    public static final String NBT_UPGRADE_TAG = "UpgradeInventory";
    public static final int UPGRADE_INV_SIZE = 9;
    private static final String NBT_UPGRADE_CACHE_TAG = "UpgradeInventoryCached";

    /**
     * Add a standardized tooltip listing the installed upgrades in the given item.
     *
     * @param iStack the item
     * @param textList list of text to append tooltip too
     * @param flag tooltip flag
     */
    public static void addUpgradeInformation(ItemStack iStack, List<ITextComponent> textList, ITooltipFlag flag) {
        ItemStack[] inventoryStacks = getUpgradeStacks(iStack);
        boolean isItemEmpty = true;
        for (ItemStack stack : inventoryStacks) {
            if (!stack.isEmpty()) {
                isItemEmpty = false;
                break;
            }
        }
        if (isItemEmpty) {
            if (!(iStack.getItem() instanceof BlockItem)) {
                textList.add(xlate("gui.tooltip.upgrades.empty").applyTextStyle(TextFormatting.DARK_GREEN));
            }
        } else {
            textList.add(xlate("gui.tooltip.upgrades.not_empty").applyTextStyle(TextFormatting.GREEN));
            PneumaticCraftUtils.sortCombineItemStacksAndToString(textList, inventoryStacks);
        }
    }

    /**
     * Store a collection of upgrades into an item stack.  This should be only be used for items; don't use it
     * to manage saved upgrades on a dropped block which has serialized upgrade data.
     *
     * @param stack the stack
     * @param handler an ItemStackHandler holding upgrade items
     */
    public static void setUpgrades(ItemStack stack, ItemStackHandler handler) {
        stack.getOrCreateTag().put(NBT_UPGRADE_TAG, handler.serializeNBT());
        UpgradeCache cache = new UpgradeCache(() -> handler);
        stack.getTag().put(NBT_UPGRADE_CACHE_TAG, cache.toNBT());

        stack.getCapability(PNCCapabilities.AIR_HANDLER_ITEM_CAPABILITY).ifPresent(h -> {
            if (h.getPressure() > 10f) {
                int maxAir = (int)(h.getVolume() * h.maxPressure());
                h.addAir(maxAir - h.getAir());
            }
        });
    }

    /**
     * Retrieves the upgrades currently installed on the given itemstack.
     */
    public static ItemStack[] getUpgradeStacks(ItemStack stack) {
        CompoundNBT tag;
        if (stack.getItem() instanceof BlockItem) {
            // tag will be in the serialized BlockEntityTag
            tag = stack.getChildTag(NBTKeys.BLOCK_ENTITY_TAG);
            if (tag == null) return new ItemStack[0];
            tag = tag.getCompound(NBT_UPGRADE_TAG);
        } else {
            // TODO implement this as a capability
            tag = NBTUtil.getCompoundTag(stack, NBT_UPGRADE_TAG);
        }
        ItemStack[] inventoryStacks = new ItemStack[UPGRADE_INV_SIZE];
        Arrays.fill(inventoryStacks, ItemStack.EMPTY);
        ListNBT itemList = tag.getList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < itemList.size(); i++) {
            CompoundNBT slotEntry = itemList.getCompound(i);
            int j = slotEntry.getByte("Slot");
            if (j >= 0 && j < UPGRADE_INV_SIZE) {
                inventoryStacks[j] = ItemStack.read(slotEntry);
            }
        }
        return inventoryStacks;
    }

    public static ItemStackHandler getUpgrades(ItemStack stack) {
        ItemStackHandler handler = new ItemStackHandler(UPGRADE_INV_SIZE);
        CompoundNBT tag = stack.getChildTag(NBT_UPGRADE_TAG);
        if (tag != null) handler.deserializeNBT(tag);
        return handler;
    }

    public static int getUpgrades(ItemStack stack, EnumUpgrade upgrade) {
        if (stack.hasTag() && stack.getTag().contains(NBT_UPGRADE_TAG)) {
            int[] upgrades = stack.getTag().getIntArray(NBT_UPGRADE_CACHE_TAG);
            if (upgrades.length != EnumUpgrade.values().length) {
                fixUpgradeCache(stack);
                upgrades = stack.getTag().getIntArray(NBT_UPGRADE_CACHE_TAG);
            }
            return upgrades[upgrade.ordinal()];
        }
        return 0;
    }

    private static void fixUpgradeCache(ItemStack stack) {
        ItemStackHandler handler = new ItemStackHandler();
        handler.deserializeNBT(stack.getChildTag(NBT_UPGRADE_TAG));
        UpgradeCache cache = new UpgradeCache(() -> handler);
        stack.getTag().put(NBT_UPGRADE_CACHE_TAG, cache.toNBT());
    }
}
