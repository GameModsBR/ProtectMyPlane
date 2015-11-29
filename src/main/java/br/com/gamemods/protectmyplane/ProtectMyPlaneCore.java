package br.com.gamemods.protectmyplane;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.Name("ProtectMyPlane")
@IFMLLoadingPlugin.SortingIndex(1001)
//@IFMLLoadingPlugin.TransformerExclusions("br.com.gamemods.protectmyplane")
public class ProtectMyPlaneCore implements IFMLLoadingPlugin
{
    @Override
    public String[] getASMTransformerClass()
    {
        return new String[]{
                "br.com.gamemods.protectmyplane.classtransformers.mcheli.EntityAircraft"
        };
    }

    @Override
    public String getModContainerClass()
    {
        return "br.com.gamemods.protectmyplane.ProtectMyPlaneMod";
    }

    @Override
    public String getSetupClass()
    {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {

    }

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }
}
