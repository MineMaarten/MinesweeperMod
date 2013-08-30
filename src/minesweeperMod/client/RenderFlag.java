package minesweeperMod.client;

import minesweeperMod.common.EntityFlag;
import minesweeperMod.common.MinesweeperMod;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

/**
 * Minesweeper Mod
 * @author MineMaarten
 * www.minemaarten.com
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class RenderFlag extends Render{
    private final ModelFlag model;
    private final ResourceLocation texture = new ResourceLocation("minesweepermod:textures/model/ModelFlag.png");

    public RenderFlag(){
        super();
        model = new ModelFlag();
    }

    public void renderFlag(EntityFlag flag, double par2, double par4, double par6, float par8, float par9){
        func_110777_b(flag);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)par2, (float)par4 + 1.5F, (float)par6);
        GL11.glScalef(1.0F, -1F, -1F);
        model.renderModel(1F / 16F, flag.oldFlagRotation + (flag.flagRotation - flag.oldFlagRotation) * par9);
        GL11.glPopMatrix();
    }

    protected ResourceLocation func_110779_a(EntityFlag par1EntityFlag){
        return texture;
    }

    @Override
    protected ResourceLocation func_110775_a(Entity par1Entity){
        return func_110779_a((EntityFlag)par1Entity);
    }

    @Override
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9){
        if(MinesweeperMod.instance.configRenderFlag) renderFlag((EntityFlag)par1Entity, par2, par4, par6, par8, par9);
    }

}
