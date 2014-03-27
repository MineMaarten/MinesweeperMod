package minesweeperMod.common;


/**
 * Minesweeper Mod
 * @author MineMaarten
 * www.minemaarten.com
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

/*public class MinesweeperPacketHandler implements IPacketHandler{
    private static final int GIVE_ACHIEVEMENT_ID = 0;
    private static final int SPAWN_PARTICLE_ID = 1;

    @Override
    public void onPacketData(INetworkManager network, Packet250CustomPayload packet, Player player){
        ByteArrayDataInput dat = ByteStreams.newDataInput(packet.data);
        World world = MinesweeperMod.proxy.getClientWorld();
        int packetID = dat.readInt();
        switch(packetID){
            case GIVE_ACHIEVEMENT_ID:
                ((EntityPlayer)player).addStat(MinesweeperUtils.getAchieveFromName(dat.readUTF()), 1);
                break;
            case SPAWN_PARTICLE_ID:
                world.spawnParticle(dat.readUTF(), dat.readDouble(), dat.readDouble(), dat.readDouble(), dat.readDouble(), dat.readDouble(), dat.readDouble());
                break;
        }
    }

    public static Packet getAchievementPacket(String achievement){
        ByteArrayOutputStream bos = new ByteArrayOutputStream(140);
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            dos.writeInt(GIVE_ACHIEVEMENT_ID);
            dos.writeUTF(achievement);
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
}*/
