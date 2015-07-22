package minesweeperMod.common;

import minesweeperMod.common.network.NetworkHandler;
import minesweeperMod.common.network.PacketSpawnParticle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

/**
 * Minesweeper Mod
 * @author MineMaarten
 * www.minemaarten.coms
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class EntityFlag extends Entity{
    private BlockPos flaggedPos;
    public int flagRotation;
    public int oldFlagRotation;
    private double rotAcc;
    private double rotSpeed;

    public EntityFlag(World par1World){
        super(par1World);
    }

    public EntityFlag(World world, BlockPos pos){
        this(world);
        setPosition(pos.getX() + 0.5D, pos.getY() + 1, pos.getZ() + 0.5D);
        flaggedPos = pos;
    }

    public void spawnSmoke(){
        if(MinesweeperMod.instance.configRenderFlag) {
            for(int i = 0; i < 20; i++) {
                double randX = posX + (rand.nextDouble() - 0.5D) / 2D;
                double randY = posY + rand.nextDouble() * 2D;
                double randZ = posZ + (rand.nextDouble() - 0.5D) / 2D;
               NetworkHandler.sendToAllAround(new PacketSpawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, randX, randY, randZ, 0, 0, 0), worldObj);
            }
        }
    }

    @Override
    public void onUpdate(){
    	
    	if(!worldObj.isRemote){
    	IBlockState blockState = worldObj.getBlockState(flaggedPos);
        if((blockState.getBlock() != MinesweeperMod.blockMinesweeper || !BlockMinesweeper.getState(blockState).flagged)) {
            setDead();
            spawnSmoke();
        }
    	}
        oldFlagRotation = flagRotation;
        if(worldObj.isRemote) {
            double maxSpeed = 1;
            if(rand.nextInt(10) == 0 || rotSpeed == maxSpeed) {
                rotAcc = (rand.nextDouble() - 0.5D) * 0.1D;
            }
            rotSpeed += rotAcc;
            rotSpeed = Math.min(rotSpeed, maxSpeed);
            rotSpeed = Math.max(rotSpeed, -maxSpeed);
            flagRotation += rotSpeed;
            if(flagRotation > 30) flagRotation = 30;
            if(flagRotation < -30) flagRotation = -30;
        }
    }

    @Override
    protected void entityInit(){}

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag){
    	flaggedPos = new BlockPos(tag.getInteger("flaggedX"),tag.getInteger("flaggedY"),tag.getInteger("flaggedZ"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag){
    	if(flaggedPos != null){
	        tag.setInteger("flaggedX", flaggedPos.getX());
	        tag.setInteger("flaggedY", flaggedPos.getY());
	        tag.setInteger("flaggedZ", flaggedPos.getZ());
        }
    }

}
