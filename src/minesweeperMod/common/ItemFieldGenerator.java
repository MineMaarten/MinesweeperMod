package minesweeperMod.common;

import java.util.List;
import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Minesweeper Mod
 * @author MineMaarten
 * www.minemaarten.com
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class ItemFieldGenerator extends Item{
  //  IIcon[] texture;

    public ItemFieldGenerator(){
        super();
        setHasSubtypes(true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item par1, CreativeTabs tab, List subItems){
        for(int ix = 0; ix < 12; ix++) {
            subItems.add(new ItemStack(this, 1, ix));
        }
        subItems.add(new ItemStack(this, 1, 100));
        subItems.add(new ItemStack(this, 1, 101));
    }

   /* @Override
    public IIcon getIconFromDamage(int par1){
        if(par1 < 12) return texture[par1];
        else if(par1 == 100 || par1 == 101) return texture[par1 - 88];

        return texture[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister){
        texture = new IIcon[14];
        for(int i = 0; i < 14; i++) {
            texture[i] = par1IconRegister.registerIcon("minesweeperMod:ItemFieldGenerator" + i);
        }
    }*/

    @Override
    public String getUnlocalizedName(ItemStack itemstack){
        int i = itemstack.getItemDamage();
        if(i == 100) return super.getUnlocalizedName(itemstack) + ".tutorial";
        if(i == 101) return super.getUnlocalizedName(itemstack) + ".advancedTutorial";
        String size;
        if(i < 4) {
            size = "small";
        } else if(i < 8) {
            size = "medium";
        } else {
            size = "big";
        }
        String difficulty;
        if(i % 4 == 0) {
            difficulty = "Easy";
        } else if(i % 4 == 1) {
            difficulty = "Normal";
        } else if(i % 4 == 2) {
            difficulty = "Hard";
        } else {
            difficulty = "Hardcore";
        }
        return super.getUnlocalizedName(itemstack) + "." + size + difficulty;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack iStack, World world, EntityPlayer player){
        if(!world.isRemote) {
            int d = iStack.getItemDamage();
            int size;
            if(d == 100 || d == 101) {
                size = 7; // tutorial level size
            } else if(d < 4) {
                size = 10;
            } else if(d < 8) {
                size = 20;
            } else {
                size = 40;
            }
            int baseX = (int)player.posX - size / 2;
            int baseY = (int)player.posY - 1;
            int baseZ = (int)player.posZ - size / 2;
            MinesweeperMod.tickHandler.generators.add(new FieldGeneratorHandler(world, baseX, baseY, baseZ, d, new Random()));
        }
        iStack.stackSize--;
        return iStack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer player, List list, boolean par4){
        int i = par1ItemStack.getItemDamage();
        if(i == 100 || i == 101) return;
        String size;
        if(i < 4) {
            size = "item.fieldGenerator.tooltip.small";
        } else if(i < 8) {
            size = "item.fieldGenerator.tooltip.medium";
        } else {
            size = "item.fieldGenerator.tooltip.big";
        }
        list.add(StatCollector.translateToLocal(size));
        String difficulty;
        if(i % 4 == 0) {
            difficulty = "item.fieldGenerator.tooltip.easy";
        } else if(i % 4 == 1) {
            difficulty = "item.fieldGenerator.tooltip.normal";
        } else if(i % 4 == 2) {
            difficulty = "item.fieldGenerator.tooltip.hard";
        } else {
            difficulty = "item.fieldGenerator.tooltip.hardcore2";
            list.add(StatCollector.translateToLocal("item.fieldGenerator.tooltip.hardcore"));
        }
        list.add(StatCollector.translateToLocal(difficulty));
    }
}
