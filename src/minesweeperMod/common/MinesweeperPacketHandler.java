package minesweeperMod.common;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

/**
 * Minesweeper Mod
 * @author MineMaarten
 * www.minemaarten.com
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class MinesweeperPacketHandler implements IPacketHandler{
    private static final int GIVE_ACHIEVEMENT_ID = 0;
    private static final int SPAWN_PARTICLE_ID = 1;

    @Override
    public void onPacketData(INetworkManager network, Packet250CustomPayload packet, Player player){
        ByteArrayDataInput dat = ByteStreams.newDataInput(packet.data);
        World world = MinesweeperMod.proxy.getClientWorld();
        int packetID = dat.readInt();
        switch(packetID){
            case GIVE_ACHIEVEMENT_ID:
                /*
                int achievement = dat.readInt();
                EntityPlayer entityPlayer = (EntityPlayer)player;
                
                 * switch(achievement){ case 0:
                 * entityPlayer.addStat(minesweeperMod.achieveTilesCleared1, 1);
                 * break; case 1:
                 * entityPlayer.addStat(minesweeperMod.achieveTilesCleared2, 1);
                 * break; case 2:
                 * entityPlayer.addStat(minesweeperMod.achieveTilesCleared3, 1);
                 * break; case 3:
                 * entityPlayer.addStat(minesweeperMod.achieveTilesCleared4, 1);
                 * break; case 4:
                 * entityPlayer.addStat(minesweeperMod.achieveTilesCleared5, 1);
                 * break; case 5:
                 * entityPlayer.addStat(minesweeperMod.achieveDifficultyCleared1
                 * , 1); break; case 6:
                 * entityPlayer.addStat(minesweeperMod.achieveDifficultyCleared2
                 * , 1); break; case 7:
                 * entityPlayer.addStat(minesweeperMod.achieveDifficultyCleared3
                 * , 1); break; case 8:
                 * entityPlayer.addStat(minesweeperMod.achieveDifficultyCleared4
                 * , 1); break; }
                 */
                break;
            case SPAWN_PARTICLE_ID:
                world.spawnParticle(dat.readUTF(), dat.readDouble(), dat.readDouble(), dat.readDouble(), dat.readDouble(), dat.readDouble(), dat.readDouble());
                break;
        }
    }

    public static Packet getAchievementPacket(int achievement){
        ByteArrayOutputStream bos = new ByteArrayOutputStream(140);
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            dos.writeInt(GIVE_ACHIEVEMENT_ID);
            dos.writeInt(achievement);
        } catch(IOException e) {}
        Packet250CustomPayload pkt = new Packet250CustomPayload();
        pkt.channel = "minesweeper";
        pkt.data = bos.toByteArray();
        pkt.length = bos.size();
        pkt.isChunkDataPacket = true;
        return pkt;
    }

    public static Packet spawnParticle(String particleName, double spawnX, double spawnY, double spawnZ, double spawnMotX, double spawnMotY, double spawnMotZ){
        ByteArrayOutputStream bos = new ByteArrayOutputStream(140);
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            dos.writeInt(SPAWN_PARTICLE_ID);
            dos.writeUTF(particleName);
            dos.writeDouble(spawnX);
            dos.writeDouble(spawnY);
            dos.writeDouble(spawnZ);
            dos.writeDouble(spawnMotX);
            dos.writeDouble(spawnMotY);
            dos.writeDouble(spawnMotZ);
        } catch(IOException e) {}
        Packet250CustomPayload pkt = new Packet250CustomPayload();
        pkt.channel = "minesweeper";
        pkt.data = bos.toByteArray();
        pkt.length = bos.size();
        pkt.isChunkDataPacket = true;
        return pkt;
    }
}
