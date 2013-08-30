package minesweeperMod.common;

import java.util.List;

import minesweeperMod.client.MinesweeperDrawBlockHighlightHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
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
                sendChatToNearbyPlayers("\u00a7B[Tutorial] Welcome to the Advanced Minesweeper Mod tutorial. This tutorial is about tips & tricks you might not have thought about when you were sweeping mines. Let's dive into it!(...)");
                step++;
                timer = 0;
                break;
            case 1:
                timer++;
                if(timer > 120) {
                    sendChatToNearbyPlayers("\u00a7B[Tutorial] Open one of the \u00a72selected tiles.");
                    startRenderingTile(baseX + 2, baseY, baseZ + 3, 0.0F, 1.0F, 0.0F);
                    startRenderingTile(baseX + 2, baseY, baseZ + 4, 0.0F, 1.0F, 0.0F);
                    step++;
                }
                break;
            case 2:
                if(!MinesweeperUtils.isTileClosed(world.getBlockMetadata(baseX + 2, baseY, baseZ + 3))) {
                    stopRenderingTile(baseX + 2, baseY, baseZ + 3);
                    stopRenderingTile(baseX + 2, baseY, baseZ + 4);
                    sendChatToNearbyPlayers("\u00a7B[Tutorial] Good. Now, with only the basics you wouldn't be able to solve this field. However, you can solve this field with some tricks in mind. Let's go over them. (...)");
                    step++;
                    timer = 0;
                }
                break;
            case 3:
                timer++;
                if(timer > 140) {
                    sendChatToNearbyPlayers("\u00a7B[Tutorial] If you look at \u00a71this tile\u00a7B, it says there is \u00a7nexactly\u00a7r\u00a7B one mine in \u00a7Ethis area\u00a7B. (...)");
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
                    sendChatToNearbyPlayers("\u00a7B[Tutorial] Now if you look at \u00a71this tile\u00a7B, it says there is \u00a7nexactly\u00a7r\u00a7B one mine in \u00a7Ethis area\u00a7B. (...)");
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
                    sendChatToNearbyPlayers("\u00a7B[Tutorial] Because you know the mine has to be in \u00a7Ethis area\u00a7B because of \u00a71this tile\u00a7B, you can exclude \u00a72this tile\u00a7B from being a mine! \u00a72Open this tile.");
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

                    sendChatToNearbyPlayers("\u00a7B[Tutorial] Nice! We can use this same principal on the other side of the field. Try if you can say something about \u00a72this tile.");
                    startRenderingTile(baseX + 0, baseY, baseZ + 2, 0.0F, 1.0F, 0.0F);
                    step++;
                }
                break;
            case 7:
                if(MinesweeperUtils.isTileFlagged(world.getBlockMetadata(baseX, baseY, baseZ + 2))) {
                    stopRenderingTile(baseX + 0, baseY, baseZ + 2);
                    sendChatToNearbyPlayers("\u00a7B[Tutorial] Well done! So basically, when you have a number against a wall or in a corner, you can most of the times progress from this. (...)");
                    step++;
                    timer = 0;
                }
                break;
            case 8:
                timer++;
                if(timer > 140) {
                    sendChatToNearbyPlayers("\u00a7B[Tutorial] Another trick: Did you know that you can open tiles by left-clicking on a number which has the same amount of flags around it? Try for example to (left-)click on \u00a72this tile.");
                    startRenderingTile(baseX + 1, baseY, baseZ + 2, 0.0F, 1.0F, 0.0F);
                    step++;
                }
                break;
            case 9:
                if(!MinesweeperUtils.isTileClosed(world.getBlockMetadata(baseX, baseY, baseZ + 1)) && !MinesweeperUtils.isTileClosed(world.getBlockMetadata(baseX, baseY, baseZ + 3))) {
                    stopRenderingTile(baseX + 1, baseY, baseZ + 2);

                    sendChatToNearbyPlayers("\u00a7B[Tutorial] Well done! This is a very handy trick! It by the way also exists in the real minesweeper game.");
                    step++;
                    timer = 0;
                }
                break;

            case 10:
                timer++;
                if(timer > 120) {
                    sendChatToNearbyPlayers("\u00a7B[Tutorial] Now let's get back to the other side... You know from \u00a71this tile\u00a7B that there is \u00a7nexactly\u00a7r\u00a7B one mine in one of \u00a7Ethese tiles\u00a7B. (...)");
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
                    sendChatToNearbyPlayers("\u00a7B[Tutorial] Because know this, you know that the one of \u00a71this tile\u00a7B has to be caused by \u00a7Ethis mine\u00a7B. The exact location of \u00a7Ethe mine\u00a7B doesn't really matter. (...)");
                    startRenderingTile(baseX + 4, baseY, baseZ + 2, 0.0F, 0.0F, 1.0F);
                    stopRenderingTile(baseX + 3, baseY, baseZ + 2);
                    step++;
                    timer = 0;
                }
                break;
            case 12:
                timer++;
                if(timer > 180) {
                    sendChatToNearbyPlayers("\u00a7B[Tutorial] Therefore, you can exclude these three tiles from being a mine. Open these tiles. (...)");
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

                    sendChatToNearbyPlayers("\u00a7B[Tutorial] Good! Now try to solve the remainder of the field! There are no guesses needed, only the tricks learned in this tutorial!");
                    step++;
                }
                break;
            case 16:
                sendChatToNearbyPlayers("\u00a7B[Tutorial] Well done! You've finished this tutorial!");
                return false;
        }
        return true;
    }

    private boolean handleLevel1(){
        switch(step){
            case 0:
                sendChatToNearbyPlayers("\u00a7B[Tutorial] Welcome to the Minesweeper Mod tutorial. These tiles you've generated here form a minefield. The goal of the game is to find all the mines in this field. (...)");
                step++;
                timer = 0;
                break;
            case 1:
                timer++;
                if(timer > 120) step++;
                break;
            case 2:
                sendChatToNearbyPlayers("\u00a7B[Tutorial] To do this you will first have to guess. Click on one of the \u00a72green highlighed tiles.");
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
                    sendChatToNearbyPlayers("\u00a7B[Tutorial] Good job! You haven't blown up! What you see now here are open tiles, and some are numbered. These numbers tell you how many mines there are around the tile. (...)");
                    step++;
                    timer = 0;
                }
                break;
            case 4:
                timer++;
                if(timer > 120) step++;
                break;
            case 5:
                sendChatToNearbyPlayers("\u00a7B[Tutorial] For instance, look at \u00a71the number marked blue. \u00a7BIt is a one, which means there is exactly one mine in \u00a7Ethe area around this mine.\u00a7B (...)");
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
                sendChatToNearbyPlayers("\u00a7B[Tutorial] As \u00a7Ethis area\u00a7B only contains one closed tile, this tile has to contain the mine. Right click on this tile to flag it as a mine.");
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
                    sendChatToNearbyPlayers("\u00a7B[Tutorial] Well done! Now you've marked this mine, you now can use \u00a71the number highlighted here.\u00a7B There is one mine around this number... You've already found this mine. Therefore, you know \u00a72the green highlighed tile\u00a7B isn't a mine. left-click on this tile to verify this.");
                    step++;
                }
                break;
            case 9:
                if(!MinesweeperUtils.isTileClosed(world.getBlockMetadata(baseX, baseY, baseZ + 6))) {
                    stopRenderingTile(baseX, baseY, baseZ + 6);
                    stopRenderingTile(baseX + 1, baseY, baseZ + 6);
                    sendChatToNearbyPlayers("\u00a7B[Tutorial] Amazing! With this in mind, try to solve the rest of the minefield, will ya?");
                    step++;
                }
                timer = 0;
                break;
            case 16:
                timer++;
                switch(timer){
                    case 1:
                        sendChatToNearbyPlayers("\u00a7B[Tutorial] \u00a72Well done! You've succesfully completed this minefield!\u00a7B As you can see, every busted mine in the field drops some experience. (...)");
                        break;
                    case 120:
                        sendChatToNearbyPlayers("\u00a7B[Tutorial] Also normally, when you clear a minefield, you'll get a reward for doing so. (...)");
                        break;
                    case 240:
                        sendChatToNearbyPlayers("\u00a7B[Tutorial] The value and amount of the reward depends on the difficulty of the minefield and the amount of tiles the minefield existed out of. (...)");
                        break;
                    case 360:
                        sendChatToNearbyPlayers("\u00a7B[Tutorial] Minefields can be found in the world. You can also craft them by yourself by taking three pieces of wool (\u00a7Eyellow/\u00a76orange/\u00a7Cred/\u00a78brown\u00a7B), three pieces of sticks and crafting them together in a flag pattern. (...)");
                        break;
                    case 480:
                        sendChatToNearbyPlayers("\u00a7B[Tutorial] These generators can be increased in size by placing four of the same type in a crafting grid. (...)");
                        break;
                    case 600:
                        sendChatToNearbyPlayers("\u00a7B[Tutorial] This is the end of the tutorial! Good luck sweeping! To read this information back, go to the main forum post of the Minesweeper Mod (http://www.minecraftforum.net/topic/1708118-151forgesspsmp-minesweeper-mod/).");
                        return false;
                }
        }
        return true;
    }

    private boolean checkForDone(){
        for(int i = 0; i < 6; i++) {
            for(int j = 0; j < 6; j++) {
                if(world.getBlockId(baseX + i, baseY, baseZ + j) != MinesweeperMod.blockMinesweeper.blockID) {
                    sendChatToNearbyPlayers("\u00a7C[Tutorial] Whoops, you've blown it! This is what happens when you find a mine.");
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
        AxisAlignedBB bbBox = AxisAlignedBB.getAABBPool().getAABB(baseX - 5, baseY - 5, baseZ - 5, baseX + 13, baseY + 8, baseZ + 13);
        List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, bbBox);
        for(int i = 0; i < players.size(); i++) {
            players.get(i).addChatMessage(chatMessage);
        }
    }

}
