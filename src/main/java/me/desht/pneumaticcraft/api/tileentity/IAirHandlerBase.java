package me.desht.pneumaticcraft.api.tileentity;

/**
 * Base functionality for all air handlers.
 */
public interface IAirHandlerBase {
    /**
     * Get the current pressure for this handler.
     *
     * @return the current pressure
     */
    float getPressure();

    /**
     * Returns the amount of air in this handler.  Note: amount of air = pressure * volume.
     *
     * @return the air in this air handler
     */
    int getAir();

    /**
     * Adds air to this handler.
     *
     * @param amount amount of air to add in mL, may be negative.
     */
    void addAir(int amount);

    /**
     * Gets the base volume of this handler, before any Volume Upgrades are taken into account. When the volume
     * decreases, the pressure will remain the same, meaning air will be lost. When the volume increases, the air
     * remains the same, meaning the pressure will drop.
     *
     * @return the base volume
     */
    int getBaseVolume();

    /**
     * Get the effective volume of this air handler. This may have been increased by Volume Upgrades.
     * @return the effective volume, in mL
     */
    int getVolume();

    /**
     * Get the maximum pressure this handler can tank.  Behaviour when more air is added is implementation-dependent
     * (e.g. items tend to stop accepting air, while machines tend to explode)
     *
     * @return the maximum pressure for this handler
     */
    float maxPressure();
}
