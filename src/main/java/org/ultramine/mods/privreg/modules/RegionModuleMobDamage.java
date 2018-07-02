package org.ultramine.mods.privreg.modules;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class RegionModuleMobDamage extends RegionModule
{
	@SideOnly(Side.SERVER)
	public void onLivingAttacked(LivingAttackEvent e)
	{
		if(e.source instanceof EntityDamageSource || e.source.isExplosion() || e.source.isProjectile())
		{
			Entity entity = e.source.getEntity();
			if(entity != null && !entity.isEntityPlayerMP() && e.entity.isEntityPlayerMP())
				e.setCanceled(true);
		}
	}
}
