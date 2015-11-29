package br.com.gamemods.protectmyplane;

import cpw.mods.fml.common.DummyModContainer;
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
}
