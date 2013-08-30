package minesweeperMod.common;

import net.minecraft.block.Block;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

/**
 * Minesweeper Mod
 * @author MineMaarten
 * www.minemaarten.com
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class ForgeEventHandler{
    /**
     * Used to open a Minesweeper block in creative, even when the player is holding a sword.
     * @param event
     */
    @ForgeSubscribe
    public void onPlayerClick(PlayerInteractEvent event){
        if(event.action == Action.LEFT_CLICK_BLOCK && event.entityPlayer.worldObj.getBlockId(event.x, event.y, event.z) == MinesweeperMod.blockMinesweeper.blockID) {
            Block.blocksList[event.entityPlayer.worldObj.getBlockId(event.x, event.y, event.z)].onBlockClicked(event.entityPlayer.worldObj, event.x, event.y, event.z, event.entityPlayer);
        }
    }
}
