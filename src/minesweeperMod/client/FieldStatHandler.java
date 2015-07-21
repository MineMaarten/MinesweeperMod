package minesweeperMod.client;

import java.util.ArrayList;
import java.util.List;

import minesweeperMod.common.BlockMinesweeper;
import minesweeperMod.common.MinesweeperMod;
import minesweeperMod.common.MinesweeperUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Minesweeper Mod
 * @author MineMaarten
 * www.minemaarten.com
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

@SideOnly(Side.CLIENT)
public class FieldStatHandler{
    public static int x;//coords of the last clicked minesweeper tile.
    public static int y;
    public static int z;
    public static boolean forceUpdate = true;
    private int bombCount;
    private double tileBombRatio;
    private double hardcoreBombPercentage;
    private int flagCount;
    private int statHoldTimer; //decreased each tick.
    private boolean isTriggeredThisTick;
    private GuiAnimatedStat minesweeperStat;

    @SubscribeEvent
    public void tickEnd(TickEvent.RenderTickEvent event){
        if(MinesweeperMod.instance.configStatEnabled && event.phase == TickEvent.Phase.END) {
            Minecraft minecraft = FMLClientHandler.instance().getClient();
            EntityPlayer player = minecraft.thePlayer;
            if(player != null) {
                World world = minecraft.theWorld;
                boolean shouldUpdate = false;
                if(!isTriggeredThisTick && player.ticksExisted % 20 == 0) {
                    isTriggeredThisTick = true;
                    shouldUpdate = true;
                } else if(player.ticksExisted % 20 != 0) {
                    isTriggeredThisTick = false;
                }
                ScaledResolution sr = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight);
                GL11.glDepthMask(false);
                GL11.glDisable(GL11.GL_CULL_FACE);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glPushMatrix();
                GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
                GL11.glColor4d(0, 1, 0, 0.8D);

                if(minesweeperStat == null) {
                    minesweeperStat = new GuiAnimatedStat("Minefield Statistics:", sr.getScaledWidth() - 2, 2, 0x3000AA00, null, true);
                    minesweeperStat.setMinDimensionsAndReset(0, 0);
                }
                List<String> textList = new ArrayList<String>();
                if(world.getBlock(x, y, z) == MinesweeperMod.blockMinesweeper) {
                    minesweeperStat.openWindow();
                    if(shouldUpdate || forceUpdate) {
                        List<int[]> tiles = new ArrayList<int[]>();
                        ((BlockMinesweeper)MinesweeperMod.blockMinesweeper).getAccessoryTiles(tiles, world, x, y, z);
                        flagCount = 0;
                        bombCount = 0;
                        int hardcoreBombCount = 0;
                        for(int[] tile : tiles) {
                            if(MinesweeperUtils.isTileBomb(world.getBlockMetadata(tile[0], tile[1], tile[2]))) bombCount++;
                            if(MinesweeperUtils.isTileFlagged(world.getBlockMetadata(tile[0], tile[1], tile[2]))) flagCount++;
                            if(MinesweeperUtils.isTileHardcoreBomb(world.getBlockMetadata(tile[0], tile[1], tile[2]))) hardcoreBombCount++;

                        }
                        tileBombRatio = (double)bombCount / (double)tiles.size();
                        hardcoreBombPercentage = (double)hardcoreBombCount / (double)bombCount;
                        statHoldTimer--;
                        if(forceUpdate) statHoldTimer = MinesweeperMod.instance.configStatDuration;
                        forceUpdate = false;
                        if(statHoldTimer <= 0) {
                            x = 0;
                            y = 0;
                            z = 0;
                        }
                    }
                    textList.add("   Mines: " + bombCount);
                    textList.add("Flagged: " + flagCount);
                    textList.add("           -- -");
                    textList.add("    Left: " + (bombCount - flagCount));
                    if(tileBombRatio > 1D / 6D && hardcoreBombPercentage > 0.5D) {
                        textList.add("Difficulty: Hardcore");
                    } else if(tileBombRatio > 1D / 6D) {
                        textList.add("Difficulty: Hard");
                    } else if(tileBombRatio > 1D / 8D) {
                        textList.add("Difficulty: Medium");
                    } else {
                        textList.add("Difficulty: Easy");
                    }
                } else {
                    minesweeperStat.closeWindow();
                }

                int xPos = 0;
                int yPos = 1;

                switch(MinesweeperMod.instance.configStatXPos){
                    case 0:
                        xPos = 2;
                        minesweeperStat.leftSided = false;
                        break;
                    case 1:
                        xPos = sr.getScaledWidth() / 2 - 55;
                        minesweeperStat.leftSided = false;
                        break;
                    case 2:
                        xPos = sr.getScaledWidth() - 2;
                        minesweeperStat.leftSided = true;
                        break;
                }

                switch(MinesweeperMod.instance.configStatYPos){
                    case 0:
                        yPos = 2;
                        break;
                    case 1:
                        yPos = sr.getScaledHeight() / 2 - 40;
                        break;
                    case 2:
                        yPos = sr.getScaledHeight() - 68;
                        break;
                }

                minesweeperStat.updateResolution(xPos, yPos);
                minesweeperStat.render(minecraft.fontRenderer, textList, 0);
                GL11.glPopMatrix();
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glDepthMask(true);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }
        }
    }

}
