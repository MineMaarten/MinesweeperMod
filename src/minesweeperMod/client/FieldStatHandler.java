package minesweeperMod.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import minesweeperMod.common.BlockMinesweeper;
import minesweeperMod.common.MinesweeperMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

/**
 * Minesweeper Mod
 * @author MineMaarten
 * www.minemaarten.com
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

@SideOnly(Side.CLIENT)
public class FieldStatHandler{
    public static BlockPos pos;//coords of the last clicked minesweeper tile.
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
                ScaledResolution sr = new ScaledResolution(minecraft);
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
                if(pos != null && world.getBlockState(pos).getBlock() == MinesweeperMod.blockMinesweeper) {
                    minesweeperStat.openWindow();
                    if(shouldUpdate || forceUpdate) {
                        Set<BlockPos> tiles = new HashSet<BlockPos>();
                        ((BlockMinesweeper)MinesweeperMod.blockMinesweeper).getAccessoryTiles(tiles, world, pos);
                        flagCount = 0;
                        bombCount = 0;
                        int hardcoreBombCount = 0;
                        for(BlockPos tile : tiles) {
                            BlockMinesweeper.EnumState state = BlockMinesweeper.getState(world.getBlockState(tile));
                            if(state.bomb) bombCount++;
                            if(state.flagged) flagCount++;
                            if(state.hardcoreBomb) hardcoreBombCount++;

                        }
                        tileBombRatio = (double)bombCount / (double)tiles.size();
                        hardcoreBombPercentage = (double)hardcoreBombCount / (double)bombCount;
                        statHoldTimer--;
                        if(forceUpdate) statHoldTimer = MinesweeperMod.instance.configStatDuration;
                        forceUpdate = false;
                        if(statHoldTimer <= 0) {
                            pos = null;
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
                minesweeperStat.render(minecraft.fontRendererObj, textList, 0);
                GL11.glPopMatrix();
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glDepthMask(true);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }
        }
    }

}
