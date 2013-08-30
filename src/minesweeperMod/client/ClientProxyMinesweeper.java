package minesweeperMod.client;

import minesweeperMod.common.CommonProxyMinesweeper;
import minesweeperMod.common.EntityFlag;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

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
    }

    @Override
    public void registerHandlers(){
        MinecraftForge.EVENT_BUS.register(new MinesweeperSoundHandler());
        MinecraftForge.EVENT_BUS.register(new MinesweeperDrawBlockHighlightHandler());
        TickRegistry.registerTickHandler(new FieldStatHandler(), Side.CLIENT);
    }

    @Override
    public World getClientWorld(){
        return FMLClientHandler.instance().getClient().theWorld;
    }

}
