package minesweeperMod.common;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * Minesweeper Mod
 * @author MineMaarten
 * www.minemaarten.com
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class ForgeEventHandler{
    /**
     * Used to open a Minesweeper block in creative, when the player is holding a sword.
     * @param event
     */
    @SubscribeEvent
    public void onPlayerClick(PlayerInteractEvent event){
        if(event.action == Action.LEFT_CLICK_BLOCK && event.entityPlayer.worldObj.getBlock(event.x, event.y, event.z) == MinesweeperMod.blockMinesweeper) {
            event.entityPlayer.worldObj.getBlock(event.x, event.y, event.z).onBlockClicked(event.entityPlayer.worldObj, event.x, event.y, event.z, event.entityPlayer);
        }
    }
}
