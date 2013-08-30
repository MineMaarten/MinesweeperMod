package minesweeperMod.common;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.PacketDispatcher;

/**
 * Minesweeper Mod
 * @author MineMaarten
 * www.minemaarten.com
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class EntityFlag extends Entity{
    private int flaggedX;
    private int flaggedY;
    private int flaggedZ;
    public int flagRotation;
    public int oldFlagRotation;
    private double rotAcc;
    private double rotSpeed;

    public EntityFlag(World par1World){
        super(par1World);
    }

    public EntityFlag(World world, int x, int y, int z){
        this(world);
        setPosition(x + 0.5D, y, z + 0.5D);
        flaggedX = x;
        flaggedY = y - 1;
        flaggedZ = z;
    }

    public void spawnSmoke(){
        if(MinesweeperMod.instance.configRenderFlag) {
            for(int i = 0; i < 20; i++) {
                double randX = posX + (rand.nextDouble() - 0.5D) / 2D;
                double randY = posY + rand.nextDouble() * 2D;
                double randZ = posZ + (rand.nextDouble() - 0.5D) / 2D;
                PacketDispatcher.sendPacketToAllPlayers(MinesweeperPacketHandler.spawnParticle("explode", randX, randY, randZ, 0, 0, 0));
            }
        }
    }

    @Override
    public void onUpdate(){
        if(!worldObj.isRemote && (worldObj.getBlockId(flaggedX, flaggedY, flaggedZ) != MinesweeperMod.blockMinesweeper.blockID || !MinesweeperUtils.isTileFlagged(worldObj.getBlockMetadata(flaggedX, flaggedY, flaggedZ)))) {
            setDead();
            spawnSmoke();
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
        flaggedX = tag.getInteger("flaggedX");
        flaggedY = tag.getInteger("flaggedY");
        flaggedZ = tag.getInteger("flaggedZ");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag){
        tag.setInteger("flaggedX", flaggedX);
        tag.setInteger("flaggedY", flaggedY);
        tag.setInteger("flaggedZ", flaggedZ);
    }

}
