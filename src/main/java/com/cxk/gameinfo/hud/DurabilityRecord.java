package com.cxk.gameinfo.hud;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class DurabilityRecord {
    ItemStack itemStack;
    int lastDurability;
    long lastChangeTime;

    public DurabilityRecord(ItemStack itemStack, int lastDurability, long lastChangeTime) {
        this.itemStack = itemStack;
        this.lastDurability = lastDurability;
        this.lastChangeTime = lastChangeTime;
    }

    public static int WAIT_TIME = 30000; // 30秒

    public static final Map<EquipmentSlot, DurabilityRecord> durabilityTracker = new HashMap<>();

    /**
     * 检查物品是否有经验修补附魔
     */
    public static boolean hasExperienceMending(ItemStack stack) {
        try {
            // 将附魔信息转换为字符串进行匹配
            String enchantmentString = stack.getEnchantments().toString().toLowerCase();
            return enchantmentString.contains("mending");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查耐久度是否在30秒内有变化
     */
    public static boolean hasDurabilityChangedRecently(EquipmentSlot slot, ItemStack stack, long currentTime) {
        int currentDurability = stack.getMaxDamage() - stack.getDamage();
        DurabilityRecord record = durabilityTracker.get(slot);
        if (record == null) {
            // 首次记录，认为是新装备，显示它
            durabilityTracker.put(slot, new DurabilityRecord(stack, currentDurability, currentTime));
            return true;
        }

        // 检查是否是同一件装备（通过比较ItemStack）
        if (!ItemStack.areEqual(record.itemStack, stack)) {
            // 装备发生了变化，更新记录
            durabilityTracker.put(slot, new DurabilityRecord(stack, currentDurability, currentTime));
            return true;
        }

        // 检查耐久度是否发生变化
        if (record.lastDurability != currentDurability) {
            // 耐久度有变化，更新记录
            record.lastDurability = currentDurability;
            record.lastChangeTime = currentTime;
            record.itemStack = stack; // 更新ItemStack引用
            return true;
        }
        // 耐久度没有变化，检查是否超过30秒
        long timeSinceLastChange = currentTime - record.lastChangeTime;
        return timeSinceLastChange < WAIT_TIME; // 30秒 = 30000毫秒
    }
}