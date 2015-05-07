package minesweeperMod.common;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
     * Used to open a Minesweeper block in creative, when the player is holding a (vanilla) sword.
     * @param event
     */
    @SubscribeEvent
    public void onPlayerClick(PlayerInteractEvent event){
    	EntityPlayer player = event.entityPlayer;
    	if (player.capabilities.isCreativeMode) {
    		if (isSword(player.getCurrentEquippedItem())) {
	    		Block block = player.worldObj.getBlock(event.x, event.y, event.z);
	    		if(event.action == Action.LEFT_CLICK_BLOCK && block == MinesweeperMod.blockMinesweeper) {
	        		block.onBlockClicked(player.worldObj, event.x, event.y, event.z, player);
	        		event.setCanceled(true);
	        	}
    		}
        }
    }
    
    private boolean isSword(ItemStack equipped) {
    	if (equipped == null) {
    		return false;
    	}
    	Item item = equipped.getItem();
    	if (item == Items.diamond_shovel || item == Items.golden_sword || item == Items.iron_sword || item == Items.stone_sword || item == Items.wooden_sword) {
    		return true;
    	}
    	return false;
    }
}
