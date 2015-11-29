package br.com.gamemods.protectmyplane.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.EntityEvent;

@Cancelable
public class AircraftAttackEvent extends EntityEvent
{
    public final DamageSource source;
    public final float amount;

    public AircraftAttackEvent(Entity aircraft, DamageSource source, float amount)
    {
        super(aircraft);
        this.source = source;
        this.amount = amount;
    }
}
