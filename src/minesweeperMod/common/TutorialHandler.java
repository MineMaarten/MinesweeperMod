package minesweeperMod.common;

import java.util.List;

import minesweeperMod.client.MinesweeperDrawBlockHighlightHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

/**
 * Minesweeper Mod
 * @author MineMaarten
 * www.minemaarten.com
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class TutorialHandler{
    private int timer;
    private final World world;
    private int step; // determines how far the player in this tutorial is.
    private final int baseX;
    private final int baseY;
    private final int baseZ;
    private final int levelNumber;

    public TutorialHandler(World worldObj, int x, int y, int z, int level){
        world = worldObj;
        baseX = x;
        baseY = y;
        baseZ = z;
        levelNumber = level;
    }

    /**
     * update method of tutorial handlers.
     * @return false if the tutorial is done and it should be removed.
     */
    public boolean onUpdate(){
        if(step < 15 && checkForDone()) {
            return false;
        }
        switch(levelNumber){
            case 0:
                return handleLevel1();
            case 1:
                return handleLevel2();
        }
        return false;// if no levels have been handled, the handler isn't going to be useful, so delete it.
    }

    private boolean handleLevel2(){
        switch(step){
            case 0:
                sendChatToNearbyPlayers("advancedTutorial.dialog1");
                step++;
                timer = 0;
                break;
            case 1:
                timer++;
                if(timer > 120) {
                    sendChatToNearbyPlayers("advancedTutorial.dialog2");
                    startRenderingTile(baseX + 2, baseY, baseZ + 3, 0.0F, 1.0F, 0.0F);
                    startRenderingTile(baseX + 2, baseY, baseZ + 4, 0.0F, 1.0F, 0.0F);
                    step++;
                }
                break;
            case 2:
                if(!MinesweeperUtils.isTileClosed(world.getBlockMetadata(baseX + 2, baseY, baseZ + 3))) {
                    stopRenderingTile(baseX + 2, baseY, baseZ + 3);
                    stopRenderingTile(baseX + 2, baseY, baseZ + 4);
                    sendChatToNearbyPlayers("advancedTutorial.dialog3");
                    step++;
                    timer = 0;
                }
                break;
            case 3:
                timer++;
                if(timer > 140) {
                    sendChatToNearbyPlayers("advancedTutorial.dialog4");
                    startRenderingTile(baseX + 3, baseY, baseZ + 0, 0.0F, 0.0F, 1.0F);
                    startRenderingTile(baseX + 4, baseY, baseZ + 0, 1.0F, 1.0F, 0.0F);
                    startRenderingTile(baseX + 4, baseY, baseZ + 1, 1.0F, 1.0F, 0.0F);
                    step++;
                    timer = 0;
                }
                break;
            case 4:
                timer++;
                if(timer > 180) {
                    stopRenderingTile(baseX + 3, baseY, baseZ + 0);
                    sendChatToNearbyPlayers("advancedTutorial.dialog5");
                    startRenderingTile(baseX + 3, baseY, baseZ + 1, 0.0F, 0.0F, 1.0F);
                    startRenderingTile(baseX + 4, baseY, baseZ + 2, 1.0F, 1.0F, 0.0F);
                    step++;
                    timer = 0;
                }
                break;
            case 5:
                timer++;
                if(timer > 180) {
                    stopRenderingTile(baseX + 3, baseY, baseZ + 1);
                    stopRenderingTile(baseX + 4, baseY, baseZ + 2);
                    sendChatToNearbyPlayers("advancedTutorial.dialog6");
                    startRenderingTile(baseX + 3, baseY, baseZ + 0, 0.0F, 0.0F, 1.0F);
                    startRenderingTile(baseX + 4, baseY, baseZ + 2, 0.0F, 1.0F, 0.0F);
                    step++;
                }
                break;
            case 6:
                if(!MinesweeperUtils.isTileClosed(world.getBlockMetadata(baseX + 4, baseY, baseZ + 2))) {
                    stopRenderingTile(baseX + 3, baseY, baseZ + 0);
                    stopRenderingTile(baseX + 4, baseY, baseZ + 0);
                    stopRenderingTile(baseX + 4, baseY, baseZ + 1);
                    stopRenderingTile(baseX + 4, baseY, baseZ + 2);

                    sendChatToNearbyPlayers("advancedTutorial.dialog7");
                    startRenderingTile(baseX + 0, baseY, baseZ + 2, 0.0F, 1.0F, 0.0F);
                    step++;
                }
                break;
            case 7:
                if(MinesweeperUtils.isTileFlagged(world.getBlockMetadata(baseX, baseY, baseZ + 2))) {
                    stopRenderingTile(baseX + 0, baseY, baseZ + 2);
                    sendChatToNearbyPlayers("advancedTutorial.dialog8");
                    step++;
                    timer = 0;
                }
                break;
            case 8:
                timer++;
                if(timer > 140) {
                    sendChatToNearbyPlayers("advancedTutorial.dialog9");
                    startRenderingTile(baseX + 1, baseY, baseZ + 2, 0.0F, 1.0F, 0.0F);
                    step++;
                }
                break;
            case 9:
                if(!MinesweeperUtils.isTileClosed(world.getBlockMetadata(baseX, baseY, baseZ + 1)) && !MinesweeperUtils.isTileClosed(world.getBlockMetadata(baseX, baseY, baseZ + 3))) {
                    stopRenderingTile(baseX + 1, baseY, baseZ + 2);

                    sendChatToNearbyPlayers("advancedTutorial.dialog10");
                    step++;
                    timer = 0;
                }
                break;

            case 10:
                timer++;
                if(timer > 120) {
                    sendChatToNearbyPlayers("advancedTutorial.dialog11");
                    startRenderingTile(baseX + 4, baseY, baseZ + 1, 1.0F, 1.0F, 0.0F);
                    startRenderingTile(baseX + 4, baseY, baseZ + 3, 1.0F, 1.0F, 0.0F);
                    startRenderingTile(baseX + 3, baseY, baseZ + 2, 0.0F, 0.0F, 1.0F);

                    step++;
                    timer = 0;
                }
                break;
            case 11:
                timer++;
                if(timer > 180) {
                    sendChatToNearbyPlayers("advancedTutorial.dialog12");
                    startRenderingTile(baseX + 4, baseY, baseZ + 2, 0.0F, 0.0F, 1.0F);
                    stopRenderingTile(baseX + 3, baseY, baseZ + 2);
                    step++;
                    timer = 0;
                }
                break;
            case 12:
                timer++;
                if(timer > 180) {
                    sendChatToNearbyPlayers("advancedTutorial.dialog13");
                    startRenderingTile(baseX + 5, baseY, baseZ + 1, 0.0F, 1.0F, 0.0F);
                    startRenderingTile(baseX + 5, baseY, baseZ + 2, 0.0F, 1.0F, 0.0F);
                    startRenderingTile(baseX + 5, baseY, baseZ + 3, 0.0F, 1.0F, 0.0F);
                    step++;
                }
                break;
            case 13:
                if(!MinesweeperUtils.isTileClosed(world.getBlockMetadata(baseX + 5, baseY, baseZ + 1)) && !MinesweeperUtils.isTileClosed(world.getBlockMetadata(baseX + 5, baseY, baseZ + 2)) && !MinesweeperUtils.isTileClosed(world.getBlockMetadata(baseX + 5, baseY, baseZ + 3))) {
                    stopRenderingTile(baseX + 4, baseY, baseZ + 2);

                    stopRenderingTile(baseX + 4, baseY, baseZ + 1);
                    stopRenderingTile(baseX + 4, baseY, baseZ + 3);

                    stopRenderingTile(baseX + 5, baseY, baseZ + 1);
                    stopRenderingTile(baseX + 5, baseY, baseZ + 2);
                    stopRenderingTile(baseX + 5, baseY, baseZ + 3);

                    sendChatToNearbyPlayers("advancedTutorial.dialog14");
                    step++;
                }
                break;
            case 16:
                sendChatToNearbyPlayers("advancedTutorial.dialog15");
                addAchievementToNearbyPlayers("achieveAdvancedTutorial");
                return false;
        }
        return true;
    }

    private boolean handleLevel1(){
        switch(step){
            case 0:
                sendChatToNearbyPlayers("tutorial.dialog1");
                step++;
                timer = 0;
                break;
            case 1:
                timer++;
                if(timer > 120) step++;
                break;
            case 2:
                sendChatToNearbyPlayers("tutorial.dialog2");
                for(int i = baseX + 2; i <= baseX + 4; i++) {
                    for(int j = baseZ + 2; j <= baseZ + 4; j++) {
                        startRenderingTile(i, baseY, j, 0.0F, 1.0F, 0.0F);
                    }
                }
                step++;
                break;
            case 3:
                if(!MinesweeperUtils.isTileClosed(world.getBlockMetadata(baseX + 3, baseY, baseZ + 3))) {
                    for(int i = baseX + 2; i <= baseX + 4; i++) {
                        for(int j = baseZ + 2; j <= baseZ + 4; j++) {
                            stopRenderingTile(i, baseY, j);
                        }
                    }
                    sendChatToNearbyPlayers("tutorial.dialog3");
                    step++;
                    timer = 0;
                }
                break;
            case 4:
                timer++;
                if(timer > 120) step++;
                break;
            case 5:
                sendChatToNearbyPlayers("tutorial.dialog4");
                for(int i = baseX; i <= baseX + 2; i++) {
                    for(int j = baseZ + 3; j <= baseZ + 5; j++) {
                        float red = 1.0F;
                        float green = 1.0F;
                        float blue = 0.0F;
                        if(i == baseX + 1 && j == baseZ + 4) {
                            red = 0.0F;
                            green = 0.0F;
                            blue = 1.0F;
                        }
                        startRenderingTile(i, baseY, j, red, green, blue);
                    }
                }
                step++;
                timer = 0;
                break;
            case 6:
                timer++;
                if(timer > 120) step++;
                break;
            case 7:
                sendChatToNearbyPlayers("tutorial.dialog5");
                step++;
                break;
            case 8:
                if(MinesweeperUtils.isTileFlagged(world.getBlockMetadata(baseX, baseY, baseZ + 5))) {
                    for(int i = baseX; i <= baseX + 2; i++) {
                        for(int j = baseZ + 3; j <= baseZ + 5; j++) {
                            stopRenderingTile(i, baseY, j);
                        }
                    }

                    startRenderingTile(baseX, baseY, baseZ + 6, 0.0F, 1.0F, 0.0F);
                    startRenderingTile(baseX + 1, baseY, baseZ + 6, 0.0F, 0.0F, 1.0F);
                    sendChatToNearbyPlayers("tutorial.dialog6");
                    step++;
                }
                break;
            case 9:
                if(!MinesweeperUtils.isTileClosed(world.getBlockMetadata(baseX, baseY, baseZ + 6))) {
                    stopRenderingTile(baseX, baseY, baseZ + 6);
                    stopRenderingTile(baseX + 1, baseY, baseZ + 6);
                    sendChatToNearbyPlayers("tutorial.dialog7");
                    step++;
                }
                timer = 0;
                break;
            case 16:
                timer++;
                switch(timer){
                    case 1:
                        addAchievementToNearbyPlayers("achieveTutorial");
                        sendChatToNearbyPlayers("tutorial.dialog8");
                        break;
                    case 120:
                        sendChatToNearbyPlayers("tutorial.dialog9");
                        break;
                    case 240:
                        sendChatToNearbyPlayers("tutorial.dialog10");
                        break;
                    case 360:
                        sendChatToNearbyPlayers("tutorial.dialog11");
                        break;
                    case 480:
                        sendChatToNearbyPlayers("tutorial.dialog12");
                        break;
                    case 600:
                        sendChatToNearbyPlayers("tutorial.dialog13");
                        return false;
                }
        }
        return true;
    }

    private boolean checkForDone(){
        for(int i = 0; i < 6; i++) {
            for(int j = 0; j < 6; j++) {
                if(world.getBlock(baseX + i, baseY, baseZ + j) != MinesweeperMod.blockMinesweeper) {
                    sendChatToNearbyPlayers("tutorialFail");
                    stopTutorial();
                    return true;
                } else if(MinesweeperUtils.isTileClosed(world.getBlockMetadata(baseX + i, baseY, baseZ + j))) {
                    return false; // when there is at least one tile closed, we
                                  // are not done.
                }
            }
        }
        step = 16;
        timer = 0;
        stopTutorial();
        return false;
    }

    private void stopTutorial(){
        for(int i = 0; i < 6; i++) {
            for(int j = 0; j < 6; j++) {
                stopRenderingTile(baseX + i, baseY, baseZ + j);
            }
        }
    }

    private void startRenderingTile(int x, int y, int z, float red, float green, float blue){
        MinesweeperDrawBlockHighlightHandler.renderPositions.add(new int[]{x, y, z});
        MinesweeperDrawBlockHighlightHandler.renderColors.add(new float[]{red, green, blue});
    }

    private void stopRenderingTile(int x, int y, int z){
        List<int[]> list = MinesweeperDrawBlockHighlightHandler.renderPositions;
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i)[0] == x && list.get(i)[1] == y && list.get(i)[2] == z) {
                MinesweeperDrawBlockHighlightHandler.renderPositions.remove(i);
                MinesweeperDrawBlockHighlightHandler.renderColors.remove(i);
                return;
            }
        }
    }

    private void sendChatToNearbyPlayers(String chatMessage){
        AxisAlignedBB bbBox = AxisAlignedBB.getBoundingBox(baseX - 5, baseY - 5, baseZ - 5, baseX + 13, baseY + 8, baseZ + 13);
        List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, bbBox);
        for(int i = 0; i < players.size(); i++) {
            players.get(i).addChatComponentMessage(new ChatComponentTranslation(chatMessage, new Object[0]));
        }
    }

    private void addAchievementToNearbyPlayers(String achievementName){
        AxisAlignedBB bbBox = AxisAlignedBB.getBoundingBox(baseX - 5, baseY - 5, baseZ - 5, baseX + 13, baseY + 8, baseZ + 13);
        List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, bbBox);
        for(int i = 0; i < players.size(); i++) {
            players.get(i).triggerAchievement(MinesweeperUtils.getAchieveFromName(achievementName));
        }
    }
}
