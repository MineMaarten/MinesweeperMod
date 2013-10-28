package minesweeperMod.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import minesweeperMod.client.FieldStatHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Minesweeper Mod
 * @author MineMaarten
 * www.minemaarten.com
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class BlockMinesweeper extends Block{
    public static final float EXPLOSION_RADIUS = 4.0F;
    private Icon[] texture;

    public BlockMinesweeper(int ID, Material par3Material){
        super(ID, par3Material);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister par1IconRegister){
        texture = new Icon[12];
        for(int i = 0; i < 12; i++) {
            texture[i] = par1IconRegister.registerIcon("minesweeperMod:BlockMinesweeper" + i);
        }
    }

    @Override
    public Icon getIcon(int side, int meta){
        switch(meta){
            case 12:
            case 14:
                return texture[9]; // closed tile texture
            case 13:
            case 15:
                return texture[10]; // flagged tile texture
            default:
                return texture[meta];
        }
        /*
         * meta mapping: 0-8 -->opened tiles, without bomb, displaying the
         * neighbour bomb count 9 -->closed tile, without bomb, not flagged. 10
         * -->closed tile, without bomb, flagged. 11 -->opened tile, with bomb,
         * non-red 12 -->closed tile, with hardcore bomb, not flagged. 13
         * -->closed tile, with hardcore bomb, flagged. 14 -->closed tile, with
         * bomb, not flagged 15 -->closed tile, with bomb, flagged.
         */
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9){
        if(world.isRemote) {
            FieldStatHandler.x = x;
            FieldStatHandler.y = y;
            FieldStatHandler.z = z;
            FieldStatHandler.forceUpdateOnPacket = true;
        }
        int meta = world.getBlockMetadata(x, y, z);
        if(MinesweeperUtils.isTileClosed(meta) && (player.getCurrentEquippedItem() == null || player.getCurrentEquippedItem().itemID != MinesweeperMod.itemMineDetector.itemID)) {
            setBlockMetadata(world, x, y, z, MinesweeperUtils.isTileFlagged(meta) ? meta - 1 : meta + 1);
            if(!MinesweeperUtils.isTileFlagged(meta) && !world.isRemote) {//if we just flagged a tile
                EntityFlag flag = new EntityFlag(world, x, y + 1, z);
                flag.spawnSmoke();
                world.spawnEntityInWorld(flag);
            }
        }
        return false;
    }

    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player){
        if(player.posY + player.getEyeHeight() <= y + 1D) return; // don't let the player clear the minefield from the bottom.
        if(world.isRemote) {
            FieldStatHandler.x = x;
            FieldStatHandler.y = y;
            FieldStatHandler.z = z;
            FieldStatHandler.forceUpdateOnPacket = true;
        }
        int meta = world.getBlockMetadata(x, y, z);
        if(MinesweeperUtils.isTileClosed(meta)) {
            openTile(world, x, y, z, player);
        } else {
            if(getSurroundingFlags(world, x, y, z) == meta) {// when the amount of flags is the same as the number of bombs around the clicked block, we should be able to open every other tile surrounding this one.
                openSurroundingNonFlags(world, x, y, z, player);
            }
        }
        if(isGameDoneAndReward(world, x, y, z, player)) {
            eraseField(world, x, y, z, false);
        }
    }

    private void openTile(World world, int x, int y, int z, EntityPlayer player){
        if(MinesweeperUtils.isTileBomb(world.getBlockMetadata(x, y, z))) {
            eraseField(world, x, y, z, true);
            if(!world.isRemote) world.createExplosion(null, (double)x + 0.5F, (double)y + 1.5F, (double)z + 0.5F, EXPLOSION_RADIUS, true);
        } else {
            int bombCount = getSurroundingBombs(world, x, y, z);
            if(bombCount == 7) {
                giveAchievement(world, player, "achieve7");
            } else if(bombCount == 8) {
                giveAchievement(world, player, "achieve8");
            }
            setBlockMetadata(world, x, y, z, bombCount);
            if(bombCount == 0 && !world.isRemote) {
                openSurroundingNonFlags(world, x, y, z, player);
            }
        }
    }

    private int getSurroundingBombs(World world, int x, int y, int z){
        int bombCount = 0;
        for(int i = x - 1; i <= x + 1; i++) {
            for(int j = z - 1; j <= z + 1; j++) {
                if(world.getBlockId(i, y, j) == blockID && MinesweeperUtils.isTileBomb(world.getBlockMetadata(i, y, j))) {
                    bombCount++;
                }
            }
        }
        return bombCount;
    }

    private int getSurroundingFlags(World world, int x, int y, int z){
        int flagCount = 0;
        for(int i = x - 1; i <= x + 1; i++) {
            for(int j = z - 1; j <= z + 1; j++) {
                if(world.getBlockId(i, y, j) == blockID && MinesweeperUtils.isTileFlagged(world.getBlockMetadata(i, y, j))) {
                    flagCount++;
                }
            }
        }
        return flagCount;
    }

    private void openSurroundingNonFlags(World world, int x, int y, int z, EntityPlayer player){
        for(int i = x - 1; i <= x + 1; i++) {
            for(int j = z - 1; j <= z + 1; j++) {
                if(world.getBlockId(i, y, j) == blockID && !MinesweeperUtils.isTileFlagged(world.getBlockMetadata(i, y, j)) && MinesweeperUtils.isTileClosed(world.getBlockMetadata(i, y, j))) {
                    openTile(world, i, y, j, player);
                }
            }
        }
    }

    public boolean isGameDoneAndReward(World world, int x, int y, int z, EntityPlayer player){
        List<int[]> list = new ArrayList<int[]>();
        Random rand = new Random();
        getAccessoryTiles(list, world, x, y, z);
        int tileCount = list.size();
        int bombCount = 0;
        int hardcoreBombCount = 0;

        for(int[] coord : list) {
            int neighMeta = world.getBlockMetadata(coord[0], coord[1], coord[2]);
            if(MinesweeperUtils.isTileClosed(neighMeta) && !MinesweeperUtils.isTileBomb(neighMeta) && world.getBlockId(coord[0], coord[1], coord[2]) == blockID) {
                return false;
            }
            if(MinesweeperUtils.isTileBomb(neighMeta)) bombCount++;
            if(MinesweeperUtils.isTileHardcoreBomb(neighMeta)) hardcoreBombCount++;
        }

        if(tileCount > 50) giveAchievement(world, player, "achieveCleared1");
        else return true;
        if(tileCount > 100) giveAchievement(world, player, "achieveCleared2");
        if(tileCount > 200) giveAchievement(world, player, "achieveCleared3");
        if(tileCount > 500) giveAchievement(world, player, "achieveCleared4");
        if(tileCount > 1000) giveAchievement(world, player, "achieveCleared5");

        // reward the player depending on how many tiles have been cleared.
        int itemID = 0;
        int itemAmount = 1; // drop default one item.
        int itemDamage = 0;
        double tileBombRatio = (double)bombCount / (double)tileCount;
        double hardcoreBombPercentage = (double)hardcoreBombCount / (double)bombCount;
        // TODO improve rewards
        if(tileBombRatio > 1D / 6D && hardcoreBombPercentage > 0.5D) {
            // hardcore rewards:
            giveAchievement(world, player, "achieveDifficulty4");
            if(tileCount > 500) {
                itemID = Item.netherStar.itemID;
            } else if(tileCount > 300) {
                itemID = Block.blockEmerald.blockID;
            } else if(tileCount > 200) {
                itemID = Block.blockDiamond.blockID;
            } else if(tileCount > 100) {
                itemID = Block.blockGold.blockID;
            } else if(tileCount > 50) {
                itemID = Block.blockIron.blockID;
            } else {
                return true;
            }
        } else if(tileBombRatio > 1D / 6D) {
            // hard rewards:
            giveAchievement(world, player, "achieveDifficulty3");
            itemAmount = rand.nextInt(3) + 3;// 3 to 5 drops
            if(tileCount > 500) {
                itemID = Item.skull.itemID;
                itemDamage = 1;
                itemAmount = 1; // one skull drop per.
            } else if(tileCount > 300) {
                itemID = Item.emerald.itemID;
            } else if(tileCount > 200) {
                itemID = Item.diamond.itemID;
            } else if(tileCount > 100) {
                itemID = Block.glowStone.blockID;
            } else if(tileCount > 50) {
                itemID = Item.glowstone.itemID;
            } else {
                return true;
            }
        } else if(tileBombRatio > 1D / 8D) {
            // normal rewards:
            giveAchievement(world, player, "achieveDifficulty2");
            itemAmount = rand.nextInt(3) + 3;// 3 to 5 drops
            if(tileCount > 500) {
                itemID = Item.emerald.itemID;
            } else if(tileCount > 300) {
                itemID = Item.diamond.itemID;
            } else if(tileCount > 200) {
                itemID = Item.glowstone.itemID;
            } else if(tileCount > 100) {
                itemID = Item.redstone.itemID;
            } else if(tileCount > 50) {
                itemID = Item.ingotGold.itemID;
            } else {
                return true;
            }
        } else {
            // easy rewards:
            giveAchievement(world, player, "achieveDifficulty1");
            itemAmount = rand.nextInt(3) + 1; // 1 to 3 drops
            if(tileCount > 500) {
                itemID = Item.diamond.itemID;
            } else if(tileCount > 300) {
                itemID = Item.glowstone.itemID;
            } else if(tileCount > 200) {
                itemID = Item.redstone.itemID;
            } else if(tileCount > 100) {
                itemID = Item.ingotGold.itemID;
            } else if(tileCount > 50) {
                itemID = Item.ingotIron.itemID;
            } else {
                return true;
            }
        }
        if(!world.isRemote) {
            ItemStack iStack = new ItemStack(itemID, itemAmount, itemDamage);

            float var6 = 0.7F;
            double var7 = world.rand.nextFloat() * var6 + (1.0F - var6) * 0.5D;
            double var9 = world.rand.nextFloat() * var6 + (1.0F - var6) * 0.5D;
            double var11 = world.rand.nextFloat() * var6 + (1.0F - var6) * 0.5D;
            EntityItem var13 = new EntityItem(world, x + var7, y + 1D + var9, z + var11, iStack);
            var13.delayBeforeCanPickup = 10;
            world.spawnEntityInWorld(var13);
        }
        return true;
    }

    public void eraseField(World world, int x, int y, int z, boolean explodeOnHardcore){
        List<int[]> list = new ArrayList<int[]>();
        Random rand = new Random();
        getAccessoryTiles(list, world, x, y, z);
        for(int i = 0; i < list.size(); i++) {
            int[] coord = list.get(i);
            world.scheduleBlockUpdate(coord[0], coord[1], coord[2], blockID, rand.nextInt(100) + 5);
            int neighMeta = world.getBlockMetadata(coord[0], coord[1], coord[2]);
            if(MinesweeperUtils.isTileBomb(neighMeta) && (!MinesweeperUtils.isTileHardcoreBomb(neighMeta) || !explodeOnHardcore)) {
                setBlockMetadata(world, coord[0], coord[1], coord[2], 11);// show that this tile was a bomb.
                if(!world.isRemote && !explodeOnHardcore) {
                    for(int k = 0; k < 1; k++) {
                        //When the player cleared, the field, give XP for each bomb cleared.
                        EntityXPOrb xpOrb = new EntityXPOrb(world, (double)coord[0] + 0.5F, (double)coord[1] + 1.0F, (double)coord[2] + 0.5F, 2);
                        xpOrb.motionX = rand.nextDouble() - 0.5D;
                        xpOrb.motionY = rand.nextDouble() - 0.5D;
                        xpOrb.motionZ = rand.nextDouble() - 0.5D;
                        world.spawnEntityInWorld(xpOrb);
                    }
                }
            }
        }
    }

    /**
     * All Minesweeper blocks connecting this Minesweeper blocks will be added to the given arraylist of coordinates. This method
     * will be recursively called until the whole minefield is on the list.
     * @param list
     * @param world
     * @param x
     * @param y
     * @param z
     */
    public void getAccessoryTiles(List<int[]> list, World world, int x, int y, int z){
        for(int i = x - 1; i <= x + 1; i++) {
            for(int j = z - 1; j <= z + 1; j++) {
                if(world.getBlockId(i, y, j) == blockID && !listContainsCoord(list, i, y, j)) {
                    int[] coord = {i, y, j};
                    list.add(coord);
                    getAccessoryTiles(list, world, i, y, j);
                }
            }
        }
    }

    private boolean listContainsCoord(List<int[]> list, int x, int y, int z){
        for(int i = 0; i < list.size(); i++) {
            int[] coord = list.get(i);
            if(coord[0] == x && coord[1] == y && coord[2] == z) return true;
        }
        return false;
    }

    // when the minefield game is done the blocks have to disappear, and they're doing this with this method.
    @Override
    public void updateTick(World world, int x, int y, int z, Random rand){
        if(MinesweeperUtils.isTileHardcoreBomb(world.getBlockMetadata(x, y, z)) && !world.isRemote) {
            world.createExplosion(null, (double)x + 0.5F, (double)y + 1.5F, (double)z + 0.5F, EXPLOSION_RADIUS, true);
        }
        world.setBlock(x, y, z, 0);// make the block disappear
        for(int i = 0; i < 5; i++) {
            double randX = x + rand.nextDouble();
            double randY = y + rand.nextDouble();
            double randZ = z + rand.nextDouble();
            PacketDispatcher.sendPacketToAllPlayers(MinesweeperPacketHandler.spawnParticle("explode", randX, randY, randZ, 0, 0, 0));
        }
    }

    @Override
    public int quantityDropped(Random rand){
        return 0;
    }

    public void setBlockMetadata(World world, int x, int y, int z, int metadata){
        world.setBlock(x, y, z, blockID, metadata, 3);
    }

    public void giveAchievement(World world, EntityPlayer player, String achievement){
        if(!world.isRemote) {
            PacketDispatcher.sendPacketToPlayer(MinesweeperPacketHandler.getAchievementPacket(achievement), (Player)player);
        }
    }

}
