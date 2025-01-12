package me.desht.pneumaticcraft.api.misc;

import me.desht.pneumaticcraft.api.PneumaticRegistry;
import net.minecraft.world.damagesource.DamageSource;

/**
 * Get an instance of this via {@link PneumaticRegistry.IPneumaticCraftInterface#getDamageSources()}
 */
public interface DamageSources {
    /**
     * Damage dealt to mobs and players trapped in a Pressure Chamber when > 2 bar
     *
     * @param damageSource damage source to check
     * @return true if pressure chamber damage
     */
    boolean isPressureDamage(DamageSource damageSource);

    /**
     * Damage dealt to players who fail to hack a Security Station
     *
     * @param damageSource damage source to check
     * @return true if security station damage
     */
    boolean isSecurityStationDamage(DamageSource damageSource);

    /**
     * Damage dealt to mobs and players standing in Etching Acid
     *
     * @param damageSource damage source to check
     * @return true if etching acid damage
     */
    boolean isEtchingAcidDamage(DamageSource damageSource);

    /**
     * Damage dealt to mobs (and players without footwear) standing on Plastic Construction Bricks (tm)
     *
     * @param damageSource damage source to check
     * @return true if plastic block damage
     */
    boolean isPlasticBlockDamage(DamageSource damageSource);

    /**
     * Damage dealt to Drones when they are wrenched, execute a Suicide widget, or encounter a fatal run-time program
     * error. This damage is always instantly fatal to the Drone.
     *
     * @param damageSource damage source to check
     * @return true if drone overload damage
     */
    boolean isDroneOverloadDamage(DamageSource damageSource);
}
