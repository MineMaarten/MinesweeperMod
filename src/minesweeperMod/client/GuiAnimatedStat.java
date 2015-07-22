package minesweeperMod.client;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

/**
 * Minesweeper Mod
 * @author MineMaarten
 * www.minemaarten.com
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 * 
 * This class is imported from PneumaticCraft, and modified
 */

@SideOnly(Side.CLIENT)
public class GuiAnimatedStat{

    public static final int ANIMATED_STAT_SPEED = 5;
    public GuiAnimatedStat affectingStat;
    public int baseX;
    private int baseY;
    public int affectedY;
    public int width;
    public int height;
    private boolean isClicked = false;
    public int minWidth = 17;
    public int minHeight = 17;
    private final int backGroundColor;
    public String title;
    public boolean leftSided; // this boolean determines if the stat is going to expand to the left or right.
    public boolean doneExpanding;

    public GuiAnimatedStat(String title, int xPos, int yPos, int backGroundColor, GuiAnimatedStat affectingStat,
            boolean leftSided){
        baseX = xPos;
        baseY = yPos;
        this.affectingStat = affectingStat;
        width = minWidth;
        height = minHeight;
        this.backGroundColor = backGroundColor;
        this.title = title;
        this.leftSided = leftSided;
    }

    public void updateResolution(int xPos, int yPos){
        baseX = xPos;
        baseY = yPos;
    }

    public void setMinDimensionsAndReset(int minWidth, int minHeight){
        this.minWidth = minWidth;
        this.minHeight = minHeight;
        width = minWidth;
        height = minHeight;
    }

    public void render(FontRenderer fontRenderer, List<String> textList, float zLevel){
        doneExpanding = true;
        if(isClicked) {
            // calculate the width and height needed for the box to fit the
            // strings.
            int maxWidth = fontRenderer.getStringWidth(title);
            int maxHeight = 12;
            if(textList.size() > 0) {
                maxHeight += 4 + textList.size() * 10;
            }
            for(String line : textList) {
                if(fontRenderer.getStringWidth(line) > maxWidth) maxWidth = fontRenderer.getStringWidth(line);
            }
            maxWidth += 20;

            // expand the box

            for(int i = 0; i < ANIMATED_STAT_SPEED; i++) {
                if(width < maxWidth) {
                    width++;
                    doneExpanding = false;
                }
                if(height < maxHeight) {
                    height++;
                    doneExpanding = false;
                }
                if(width > maxWidth) width--;
                if(height > maxHeight) height--;
            }

        } else {
            for(int i = 0; i < ANIMATED_STAT_SPEED; i++) {
                if(width > minWidth) width--;
                if(height > minHeight) height--;
            }
            doneExpanding = false;
        }

        affectedY = baseY;
        if(affectingStat != null) {
            affectedY += affectingStat.affectedY + affectingStat.height;
        }
        if(leftSided) width *= -1;
        Gui.drawRect(baseX, affectedY, baseX + width, affectedY + height, backGroundColor);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth(3.0F);
        GL11.glColor4d(0, 0, 0, 1);
        WorldRenderer tess = Tessellator.getInstance().getWorldRenderer();
        tess.startDrawing(GL11.GL_LINE_LOOP);
        tess.addVertex(baseX, affectedY, zLevel);
        tess.addVertex(baseX + width, affectedY, zLevel);
        tess.addVertex(baseX + width, affectedY + height, zLevel);
        tess.addVertex(baseX, affectedY + height, zLevel);
        //tess.draw();
        Tessellator.getInstance().draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        if(leftSided) width *= -1;
        // if done expanding, draw the information
        if(doneExpanding) {
            fontRenderer.drawStringWithShadow(title, baseX + (leftSided ? -width + 2 : 18), affectedY + 2, 0xFFFF00);
            for(int i = 0; i < textList.size(); i++) {
                if(textList.get(i).contains("\u00a70")) {
                    fontRenderer.drawString(textList.get(i), baseX + (leftSided ? -width + 2 : 18), affectedY + i * 10 + 12, 0xFFFFFF);
                } else {
                    fontRenderer.drawStringWithShadow(textList.get(i), baseX + (leftSided ? -width + 2 : 18), affectedY + i * 10 + 12, 0xFFFFFF);
                }
            }

        }
    }

    /*
     * button: 0 = left 1 = right 2 = middle
     */
    public boolean mouseClicked(int x, int y, int button){
        if(button == 0 && mouseIsHoveringOverStat(x, y)) {
            isClicked = !isClicked;
        }
        return isClicked;
    }

    public void closeWindow(){
        isClicked = false;
    }

    public void openWindow(){
        isClicked = true;
    }

    public boolean isClicked(){
        return isClicked;
    }

    private boolean mouseIsHoveringOverStat(int x, int y){
        if(leftSided) {
            return x <= baseX && x >= baseX - width && y >= affectedY && y <= affectedY + height;
        } else {
            return x >= baseX && x <= baseX + width && y >= affectedY && y <= affectedY + height;
        }
    }
}
