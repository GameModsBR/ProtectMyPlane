package br.com.gamemods.protectmyplane;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;

public class ProtectMyPlaneMod extends DummyModContainer
{
    public ProtectMyPlaneMod()
    {
        super(new ModMetadata());
        ModMetadata metadata = getMetadata();
        metadata.authorList.add("joserobjr");
        metadata.name="Protect My Plane";
        metadata.modId="ProtectMyPlane";
        metadata.version="1.0";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
        return true;
    }
}
