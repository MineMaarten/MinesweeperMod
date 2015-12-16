package minesweeperMod.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.lwjgl.opengl.GL11;

/**
 * Minesweeper Mod
 * @author MineMaarten
 * www.minemaarten.com
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * 
 * This class is derived from Equivalent Exchange 3's DrawBlockHighlightHandler, found at https://github.com/pahimar/Equivalent-Exchange-3/blob/master/common/com/pahimar/ee3/core/handlers/DrawBlockHighlightHandler.java
 */

public class MinesweeperDrawBlockHighlightHandler{
    public static int pulse = 0;
    private static boolean doInc = true;
    private static float pulseTransparency;
    public static List<int[]> renderPositions = new ArrayList<int[]>();
    public static List<float[]> renderColors = new ArrayList<float[]>();

    @SubscribeEvent
    public void onDrawBlockHighlightEvent(DrawBlockHighlightEvent event){
        pulseTransparency = getPulseValue() * 0.5F / 3000f;
        for(int i = 0; i < renderPositions.size(); i++) {
            try {
                highlightTile(event.player, renderPositions.get(i)[0], renderPositions.get(i)[1], renderPositions.get(i)[2], event.partialTicks, i);
            } catch(Exception e) {

            }
        }
    }

    public static void highlightTile(EntityPlayer player, double x, double y, double z, float partialTicks, int tile){
        x += 0.5D;
        y += 0.5D;
        z += 0.5D;
        double iPX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
        double iPY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
        double iPZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;

        float xScale = 1.0F;
        float yScale = 1;
        float zScale = 1.0F;
        float xShift = 0.0F;
        float yShift = 0.01F;
        float zShift = 0.0F;

        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_CULL_FACE);

        for(int i = 4; i < 5; i++) {

            int zCorrection = i == 2 ? -1 : 1;
            GL11.glPushMatrix();
            GL11.glTranslated(-iPX + x + xShift, -iPY + y + yShift, -iPZ + z + zShift);
            GL11.glScalef(1F * xScale, 1F * yScale, 1F * zScale);
            GL11.glRotatef(90, -1, 0, 0);
            GL11.glTranslated(0, 0, 0.5f * zCorrection);
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            drawQuad(-0.5F, -0.5F, 1F, 1F, 0F, tile);
            GL11.glPopMatrix();
        }

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(true);
    }

    private static int getPulseValue(){

        if(doInc) {
            pulse += 40;
        } else {
            pulse -= 40;
        }

        if(pulse >= 3000) {
            doInc = false;
        }

        if(pulse <= 1500) {
            doInc = true;
        }

        return pulse;
    }

    public static void drawQuad(float x, float y, float width, float height, double zLevel, int tile){
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth(5.0F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(renderColors.get(tile)[0], renderColors.get(tile)[1], renderColors.get(tile)[2], pulseTransparency);

        TessWrapper.startDrawingQuads();
        TessWrapper.addVertex(x + 0F, y + height, zLevel);
        TessWrapper.addVertex(x + width, y + height, zLevel);
        TessWrapper.addVertex(x + width, y + 0F, zLevel);
        TessWrapper.addVertex(x + 0F, y + 0F, zLevel);
        TessWrapper.draw();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

}
