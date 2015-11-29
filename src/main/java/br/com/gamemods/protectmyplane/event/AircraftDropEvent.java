package br.com.gamemods.protectmyplane.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.EntityEvent;

@Cancelable
public class AircraftDropEvent extends EntityEvent
{
    public final Item item;
    public final int amount;
    public final float offset;

    public AircraftDropEvent(Entity entity, Item item, int amount, float offset)
    {
        super(entity);
        this.item = item;
        this.amount = amount;
        this.offset = offset;
    }
}
