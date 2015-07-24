package minesweeperMod.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * Minesweeper Mod
 * @author MineMaarten
 * www.minemaarten.com
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class ItemMineDetector extends Item{

    @Override
    public boolean onItemUse(ItemStack IStack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float par8, float par9, float par10){
        if(world.getBlockState(pos).getBlock() == MinesweeperMod.blockMinesweeper) {
            if(world.isRemote) {
                player.addStat(MinesweeperUtils.getAchieveFromName("achieveUseDetector"), 1);
            }
            if(BlockMinesweeper.getState(world.getBlockState(pos)).bomb) {
                world.playSoundEffect(pos.getX(), pos.getY(), pos.getZ(), "minesweepermod:minebeep", 1.0F, 1.0F);
            }
            IStack.stackSize--;
            return true;
        }
        return false; // we didn't use the item.
    }
}
