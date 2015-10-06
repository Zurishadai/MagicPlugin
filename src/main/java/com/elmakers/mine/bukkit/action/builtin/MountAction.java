package com.elmakers.mine.bukkit.action.builtin;

import com.elmakers.mine.bukkit.action.BaseSpellAction;
import com.elmakers.mine.bukkit.api.action.CastContext;
import com.elmakers.mine.bukkit.api.magic.Mage;
import com.elmakers.mine.bukkit.api.spell.SpellResult;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class MountAction extends BaseSpellAction {
    private boolean eject = false;

    @Override
    public void prepare(CastContext context, ConfigurationSection parameters) {
        eject = parameters.getBoolean("eject", false);
    }

	@Override
    public SpellResult perform(CastContext context) {
		LivingEntity source = context.getLivingEntity();
		if (source == null) {
			return SpellResult.LIVING_ENTITY_REQUIRED;
		}

		// Make it so this spell can be used to get someone off of you
        if (eject) {
            source.eject();
            return SpellResult.CAST;
        }

        Entity current = source.getVehicle();
        if (current != null) {
            current.eject();
        }
		Entity targetEntity = context.getTargetEntity();
		if (targetEntity == null) {
			return SpellResult.NO_TARGET;
		}
		
		targetEntity.setPassenger(source);
		
		return SpellResult.CAST;
	}
}
