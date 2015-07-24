package minesweeperMod.client;

import minesweeperMod.common.CommonProxyMinesweeper;
import minesweeperMod.common.EntityFlag;
import minesweeperMod.common.MinesweeperMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * Minesweeper Mod
 * @author MineMaarten
 * www.minemaarten.com
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class ClientProxyMinesweeper extends CommonProxyMinesweeper{

    @Override
    public void registerRenders(){
        RenderingRegistry.registerEntityRenderingHandler(EntityFlag.class, new RenderFlag());
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(MinesweeperMod.itemMineDetector, 0, new ModelResourceLocation("minesweepermod:mineDetector", "inventory"));
        String[] names = new String[14];
        for(int i = 0; i < 14; i++) {
            int damage = i < 12 ? i : i + 88;
            String name = "minesweepermod:fieldGenerator" + i;
            names[i] = name;
            System.out.println(name);
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(MinesweeperMod.itemFieldGenerator, damage, new ModelResourceLocation(name, "inventory"));
        }
        ModelBakery.addVariantName(MinesweeperMod.itemFieldGenerator, names);
    }

    @Override
    public void registerHandlers(){
        MinecraftForge.EVENT_BUS.register(new MinesweeperDrawBlockHighlightHandler());
        FMLCommonHandler.instance().bus().register(new FieldStatHandler());
    }

    @Override
    public World getClientWorld(){
        return FMLClientHandler.instance().getClient().theWorld;
    }

}
