package minesweeperMod.common;

import java.util.List;
import java.util.Random;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Minesweeper Mod
 * @author MineMaarten
 * www.minemaarten.com
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class ItemFieldGenerator extends Item{
    Icon[] texture;

    public ItemFieldGenerator(int par1){
        super(par1);
        setHasSubtypes(true);
    }

    public String getTextureFile(){
        return "/minesweeperMod/textures/minesweeper.png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(int par1, CreativeTabs tab, List subItems){
        for(int ix = 0; ix < 12; ix++) {
            subItems.add(new ItemStack(this, 1, ix));
        }
        subItems.add(new ItemStack(this, 1, 100));
        subItems.add(new ItemStack(this, 1, 101));
    }

    @Override
    public Icon getIconFromDamage(int par1){
        if(par1 < 12) return texture[par1];
        else if(par1 == 100 || par1 == 101) return texture[par1 - 88];

        return texture[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister){
        texture = new Icon[14];
        for(int i = 0; i < 14; i++) {
            texture[i] = par1IconRegister.registerIcon("minesweeperMod:ItemFieldGenerator" + i);
        }
    }

    @Override
    public String getItemDisplayName(ItemStack itemstack){
        int i = itemstack.getItemDamage();
        if(i == 100) return "Tutorial Generator";
        if(i == 101) return "Advanced Tutorial Generator";
        String size;
        if(i < 4) {
            size = "Small ";
        } else if(i < 8) {
            size = "Medium ";
        } else {
            size = "Big ";
        }
        String difficulty;
        if(i % 4 == 0) {
            difficulty = "Easy ";
        } else if(i % 4 == 1) {
            difficulty = "Normal ";
        } else if(i % 4 == 2) {
            difficulty = "Hard ";
        } else {
            difficulty = "Hardcore ";
        }
        return size + difficulty + "Field Generator";
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
            size = "\u00a7fSmall: 10x10";
        } else if(i < 8) {
            size = "\u00a78Medium: 20x20";
        } else {
            size = "\u00a7cBig: 40x40";
        }
        list.add(size);
        String difficulty;
        if(i % 4 == 0) {
            difficulty = "\u00a7eEasy: 1 bomb per 10 tiles average";
            list.add(difficulty);
        } else if(i % 4 == 1) {
            difficulty = "\u00a76Normal: 1 bomb per 7 tiles average";
            list.add(difficulty);
        } else if(i % 4 == 2) {
            difficulty = "\u00a7cHard: 1 bomb per 5 tiles average";
            list.add(difficulty);
        } else {
            difficulty = "\u00a74Hardcore: 1 bomb per 5 tiles average,";
            list.add(difficulty);
            list.add("\u00a74all mines explode on failure");
        }
    }
}
