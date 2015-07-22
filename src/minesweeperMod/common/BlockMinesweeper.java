package minesweeperMod.common;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import minesweeperMod.client.FieldStatHandler;
import minesweeperMod.common.network.NetworkHandler;
import minesweeperMod.common.network.PacketSpawnParticle;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * Minesweeper Mod
 * @author MineMaarten
 * www.minemaarten.com
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class BlockMinesweeper extends Block{
    public static final float EXPLOSION_RADIUS = 4.0F;
    public static final PropertyEnum STATE = PropertyEnum.create("state",EnumState.class);
    
    public enum EnumState implements IStringSerializable{   	
    	B0, B1, B2, B3,B4,B5,B6,B7,B8,CLOSED(false, false, false),CLOSED_FLAGGED(false, true, false), OPENED_BOMB(true, false, true),CLOSED_BOMB_HARDCORE(false, false, true, true),CLOSED_BOMB_HARDCORE_FLAGGED(false, true,true,true),CLOSED_BOMB(false, false, true),CLOSED_BOMB_FLAGGED(false, true, true);
    	
    	public boolean opened = true;
    	public boolean flagged;
    	public boolean bomb;
    	public boolean hardcoreBomb;
    	
    	private EnumState(){
    		
    	}
    	
    	private EnumState(boolean opened, boolean flagged, boolean bomb){
    		this.opened = opened;
    		this.flagged = flagged;
    		this.bomb = bomb;
    	}
    	
    	private EnumState(boolean opened, boolean flagged, boolean bomb, boolean hardcoreBomb){
    		this(opened, flagged, bomb);
    		this.hardcoreBomb = hardcoreBomb;
    	}
    	
    	public EnumState toggleFlag(){
    		for(EnumState otherState : values()){
    			if(!otherState.opened && otherState.bomb == bomb && otherState.hardcoreBomb == hardcoreBomb && otherState.flagged != flagged){
    				return otherState;
    			}
    		}
    		throw new IllegalStateException("Can't switch flags for an opened tile");
    	}

		@Override
		public String getName() {
			return name();
		}
    }
  //  private IIcon[] texture;

    public BlockMinesweeper(Material par3Material){
        super(par3Material);
    }
    
    protected BlockState createBlockState()
    {
        return new BlockState(this, STATE);
    }
    
    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(STATE, EnumState.values()[meta]);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((EnumState)state.getValue(STATE)).ordinal();
    }
    

   /* @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister){
        texture = new IIcon[12];
        for(int i = 0; i < 12; i++) {
            texture[i] = par1IconRegister.registerIcon("minesweeperMod:BlockMinesweeper" + i);
        }
    }

    @Override
    public IIcon getIcon(int side, int meta){
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
         *
    }*/
    
    public static EnumState getState(IBlockState state){
    	return (EnumState)state.getValue(STATE);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ){
        if(world.isRemote) {
            FieldStatHandler.pos = pos;
            FieldStatHandler.forceUpdate = true;
        } else {
        	EnumState s = getState(state);
            if(!s.opened && (player.getCurrentEquippedItem() == null || player.getCurrentEquippedItem().getItem() != MinesweeperMod.itemMineDetector)) {
                world.setBlockState(pos, state.withProperty(STATE, s.toggleFlag()));
                if(!s.flagged) {//if we just flagged a tile
                    EntityFlag flag = new EntityFlag(world, pos);
                    flag.spawnSmoke();
                    world.spawnEntityInWorld(flag);
                }
            }
        }
        return false;
    }

    @Override
    public void onBlockClicked(World world, BlockPos pos, EntityPlayer player){
        if(player.posY + player.getEyeHeight() <= pos.getY() + 1D) return; // don't let the player clear the minefield from the bottom.
        if(world.isRemote) {
            FieldStatHandler.pos = pos;
            FieldStatHandler.forceUpdate = true;
        } else {
            EnumState state = getState(world.getBlockState(pos));
            if(!state.opened) {
                openTile(world, pos,state, player);
            } else {
                if(getSurroundingFlags(world, pos) == state.ordinal()) {// when the amount of flags is the same as the number of bombs around the clicked block, we should be able to open every other tile surrounding this one.
                    openSurroundingNonFlags(world, pos, player);
                }
            }
            if(isGameDoneAndReward(world, pos, player)) {
                eraseField(world, pos, false);
            }
        }
    }

    private void openTile(World world, BlockPos pos, EnumState state, EntityPlayer player){
        if(state.bomb) {
            eraseField(world, pos, true);
            world.createExplosion(null, (double)pos.getX() + 0.5F, (double)pos.getY() + 1.5F, (double)pos.getZ() + 0.5F, EXPLOSION_RADIUS, true);
        } else {
            int bombCount = getSurroundingBombs(world, pos);
            if(bombCount == 7) {
                player.triggerAchievement(MinesweeperUtils.getAchieveFromName("achieve7"));
            } else if(bombCount == 8) {
                player.triggerAchievement(MinesweeperUtils.getAchieveFromName("achieve8"));
            }
            world.setBlockState(pos, world.getBlockState(pos).withProperty(STATE, EnumState.values()[bombCount]));
            if(bombCount == 0) {
                openSurroundingNonFlags(world, pos, player);
            }
        }
    }

    private int getSurroundingBombs(World world, BlockPos pos){
    	int x = pos.getX();
    	int y = pos.getY();
    	int z = pos.getZ();
        int bombCount = 0;
        for(int i = x - 1; i <= x + 1; i++) {
            for(int j = z - 1; j <= z + 1; j++) {
            	IBlockState state = world.getBlockState(new BlockPos(i, y,j));
                if(state.getBlock() == this && getState(state).bomb) {
                    bombCount++;
                }
            }
        }
        return bombCount;
    }

    private int getSurroundingFlags(World world, BlockPos pos){
    	int x = pos.getX();
    	int y = pos.getY();
    	int z = pos.getZ();
        int flagCount = 0;
        for(int i = x - 1; i <= x + 1; i++) {
            for(int j = z - 1; j <= z + 1; j++) {
            	IBlockState state = world.getBlockState(new BlockPos(i, y,j));
                if(state.getBlock() == this && getState(state).flagged) {
                    flagCount++;
                }
            }
        }
        return flagCount;
    }

    private void openSurroundingNonFlags(World world, BlockPos pos, EntityPlayer player){
    	int x = pos.getX();
    	int y = pos.getY();
    	int z = pos.getZ();
        for(int i = x - 1; i <= x + 1; i++) {
            for(int j = z - 1; j <= z + 1; j++) {
            	BlockPos localPos = new BlockPos(i, y,j);
            	IBlockState state = world.getBlockState(localPos);
                if(state.getBlock() == this && !getState(state).flagged && !getState(state).opened) {
                    openTile(world, localPos, getState(state), player);
                }
            }
        }
    }

    public boolean isGameDoneAndReward(World world, BlockPos pos, EntityPlayer player){
        Set<BlockPos> positions = new HashSet<BlockPos>();
        getAccessoryTiles(positions, world,pos);
        int tileCount = positions.size();
        int bombCount = 0;
        int hardcoreBombCount = 0;

        for(BlockPos coord : positions) {
            EnumState state = getState(world.getBlockState(coord));
            if(!state.opened && !state.bomb) {
                return false;
            }
            if(state.bomb) bombCount++;
            if(state.hardcoreBomb) hardcoreBombCount++;
        }

        if(tileCount > 50) player.triggerAchievement(MinesweeperUtils.getAchieveFromName("achieveCleared1"));
        if(tileCount > 100) player.triggerAchievement(MinesweeperUtils.getAchieveFromName("achieveCleared2"));
        if(tileCount > 200) player.triggerAchievement(MinesweeperUtils.getAchieveFromName("achieveCleared3"));
        if(tileCount > 500) player.triggerAchievement(MinesweeperUtils.getAchieveFromName("achieveCleared4"));
        if(tileCount > 1000) player.triggerAchievement(MinesweeperUtils.getAchieveFromName("achieveCleared5"));

        // reward the player depending on how many tiles have been cleared.
        double tileBombRatio = (double)bombCount / (double)tileCount;
        double hardcoreBombPercentage = (double)hardcoreBombCount / (double)bombCount;
        ItemStack[] iStack = null;
        if(tileBombRatio > 1D / 6D && hardcoreBombPercentage > 0.5D) {
            // hardcore rewards:
            player.triggerAchievement(MinesweeperUtils.getAchieveFromName("achieveDifficulty4"));
            iStack = getReward(Constants.LOOT_TABLE_HARDCORE_CATEGORY, tileCount);
        } else if(tileBombRatio > 1D / 6D) {
            // hard rewards:
            player.triggerAchievement(MinesweeperUtils.getAchieveFromName("achieveDifficulty3"));
            iStack = getReward(Constants.LOOT_TABLE_HARD_CATEGORY, tileCount);
        } else if(tileBombRatio > 1D / 8D) {
            // normal rewards:
            player.triggerAchievement(MinesweeperUtils.getAchieveFromName("achieveDifficulty2"));
            iStack = getReward(Constants.LOOT_TABLE_NORMAL_CATEGORY, tileCount);
        } else {
            // easy rewards:
            player.triggerAchievement(MinesweeperUtils.getAchieveFromName("achieveDifficulty1"));
            iStack = getReward(Constants.LOOT_TABLE_EASY_CATEGORY, tileCount);
        }

        if (iStack == null) {
            return true;
        }
        
        for (int i = 0; i < iStack.length; i++) {
	        float var6 = 0.7F;
	        double var7 = world.rand.nextFloat() * var6 + (1.0F - var6) * 0.5D;
	        double var9 = world.rand.nextFloat() * var6 + (1.0F - var6) * 0.5D;
	        double var11 = world.rand.nextFloat() * var6 + (1.0F - var6) * 0.5D;
	        EntityItem var13 = new EntityItem(world, pos.getX() + var7, pos.getY() + 1D + var9, pos.getZ() + var11, iStack[i]);
	        var13.setDefaultPickupDelay();
	        world.spawnEntityInWorld(var13);
        }
        return true;
    }

    private ItemStack[] getReward(String difficulty, int tileCount) {
        Configuration config = new Configuration(MinesweeperMod.configFile);
        ConfigCategory category = config.getCategory(difficulty);
        TreeMap<Integer, Property> difficultyRewards = new TreeMap<Integer, Property>();
        for (Entry<String, Property> entry : category.getValues().entrySet()) {
            difficultyRewards.put(Integer.parseInt(entry.getKey()), entry.getValue());
        }

        Entry<Integer, Property> rewardEntry = difficultyRewards.floorEntry(tileCount);
        if (rewardEntry == null) {
            return null;
        }

        String[] rewardConfig = rewardEntry.getValue().getStringList();
        if (rewardConfig.length == 0) {
        	return null;
        }
        Reward[] rewards = new Reward[rewardConfig.length];
        for (int i = 0; i < rewardConfig.length; i++) {
            rewards[i] = new Reward(rewardConfig[i]);
        }
        
        int totalWeight = 0;
        for (Reward reward : rewards) {
            totalWeight += reward.getWeight();
        }
        
        int randomIndex = -1;
        double random = Math.random() * totalWeight;
        for (int i = 0; i < rewards.length; i++) {
            random -= rewards[i].getWeight();
            if (random <= 0.0d) {
                randomIndex = i;
                break;
            }
        }
        if (randomIndex == -1) {
            return null;
        }
        
        Reward reward = rewards[randomIndex];

        return reward.getReward();
    }

    public void eraseField(World world, BlockPos pos, boolean explodeOnHardcore){
        Set<BlockPos> positions = new HashSet<BlockPos>();
        Random rand = new Random();
        getAccessoryTiles(positions, world, pos);
        for(BlockPos p : positions) {
            world.scheduleUpdate(p, this, rand.nextInt(100) + 5);
            EnumState state = getState(world.getBlockState(p));
            if(state.bomb && (!state.hardcoreBomb || !explodeOnHardcore)) {
                world.setBlockState(p, world.getBlockState(p).withProperty(STATE, EnumState.OPENED_BOMB));// show that this tile was a bomb.
                if(!explodeOnHardcore) {
                    for(int k = 0; k < 1; k++) {
                        //When the player cleared, the field, give XP for each bomb cleared.
                        EntityXPOrb xpOrb = new EntityXPOrb(world, (double)pos.getX() + 0.5F, (double)pos.getY()+ 1.0F, (double)pos.getZ() + 0.5F, 2);
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
     * All Minesweeper blocks connecting this Minesweeper blocks will be added to the given set of coordinates. This method
     * will be recursively called until the whole minefield is on the list.
     * @param world
     * @param x
     * @param y
     * @param z
     */
    public void getAccessoryTiles(Set<BlockPos> positions, World world, BlockPos startPos){
    	int x = startPos.getX();
    	int y = startPos.getY();
    	int z = startPos.getZ();
        for(int i = x - 1; i <= x + 1; i++) {
            for(int j = z - 1; j <= z + 1; j++) {
            	BlockPos pos = new BlockPos(i,y,j);
                if(world.getBlockState(pos).getBlock() == this && positions.add(pos)) {
                    getAccessoryTiles(positions, world, pos);
                }
            }
        }
    }

    // when the minefield game is done the blocks have to disappear, and they're doing this with this method.
    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand){
        if(getState(state).hardcoreBomb) {
            world.createExplosion(null, (double)pos.getX() + 0.5F, (double)pos.getY() + 1.5F, (double)pos.getZ() + 0.5F, EXPLOSION_RADIUS, true);
        }
        world.setBlockToAir(pos);
        for(int i = 0; i < 5; i++) {
            double randX = pos.getX() + rand.nextDouble();
            double randY = pos.getY() + rand.nextDouble();
            double randZ = pos.getZ() + rand.nextDouble();
            NetworkHandler.sendToAllAround(new PacketSpawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, randX, randY, randZ, 0, 0, 0), world);
        }
    }

    @Override
    public int quantityDropped(Random rand){
        return 0;
    }
}
