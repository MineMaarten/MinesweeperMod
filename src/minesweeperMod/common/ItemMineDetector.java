package minesweeperMod.common;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Minesweeper Mod
 * @author MineMaarten
 * www.minemaarten.com
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class ItemMineDetector extends Item{
    @Override
    public void registerIcons(IIconRegister par1IconRegister){
        itemIcon = par1IconRegister.registerIcon("minesweeperMod:ItemMineDetector");
    }

    @Override
    public boolean onItemUse(ItemStack IStack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10){
        if(world.getBlock(x, y, z) == MinesweeperMod.blockMinesweeper) {
            if(world.isRemote) {
                player.addStat(MinesweeperUtils.getAchieveFromName("achieveUseDetector"), 1);
            }
            int meta = world.getBlockMetadata(x, y, z);
            if(MinesweeperUtils.isTileBomb(meta)) {
                world.playSoundEffect(x, y, z, "minesweepermod:minebeep", 1.0F, 1.0F);
            }
            IStack.stackSize--;
            return true;
        }
        return false; // we didn't use the item.
    }
}
