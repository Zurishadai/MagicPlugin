package com.elmakers.mine.bukkit.action.builtin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import com.elmakers.mine.bukkit.action.BaseSpellAction;
import com.elmakers.mine.bukkit.api.action.CastContext;
import com.elmakers.mine.bukkit.api.magic.MageController;
import com.elmakers.mine.bukkit.api.spell.SpellResult;
import com.elmakers.mine.bukkit.utility.CompatibilityUtils;

public class ShrinkBlockAction extends BaseSpellAction
{
    @Override
    public SpellResult perform(CastContext context)
    {
        Block targetBlock = context.getTargetBlock();
        if (targetBlock == null) {
            return SpellResult.NO_TARGET;
        }
        MageController controller = context.getController();
        String blockSkin = controller.getBlockSkin(targetBlock.getType());
        if (blockSkin == null) return SpellResult.NO_TARGET;

        if (!context.hasBreakPermission(targetBlock))
        {
            return SpellResult.INSUFFICIENT_PERMISSION;
        }
        if (!context.isDestructible(targetBlock))
        {
            return SpellResult.NO_TARGET;
        }

        context.registerForUndo(targetBlock);

        dropHead(controller, targetBlock.getLocation(), blockSkin, targetBlock.getType().name());
        targetBlock.setType(Material.AIR);
        return SpellResult.CAST;
    }

    protected void dropHead(MageController controller, Location location, String ownerName, String itemName) {
        ItemStack shrunkenHead = controller.getSkull(ownerName, itemName);
        if (CompatibilityUtils.isEmpty(shrunkenHead)) return;
        location.setX(location.getX() + 0.5);
        location.setY(location.getY() + 0.5);
        location.setZ(location.getZ() + 0.5);
        location.getWorld().dropItemNaturally(location, shrunkenHead);
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    public boolean requiresTarget() {
        return true;
    }
}
