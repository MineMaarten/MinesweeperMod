package minesweeperMod.common;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

/**
 * Minesweeper Mod
 * @author MineMaarten
 * www.minemaarten.com
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class MinesweeperTickHandler implements ITickHandler{
    public List<TutorialHandler> tutorials = new ArrayList<TutorialHandler>();
    public List<FieldGeneratorHandler> generators = new ArrayList<FieldGeneratorHandler>();
    private int ticks = 0;

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData){}

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData){
        if(type.equals(EnumSet.of(TickType.SERVER))) {
            handleTutorials();
        }
    }

    private void handleTutorials(){
        for(TutorialHandler tutorial : tutorials) {
            if(!tutorial.onUpdate()) {
                tutorials.remove(tutorial);
                break;
            }
        }
        ticks++;
        if(ticks > 5) {
            for(FieldGeneratorHandler generator : generators) {
                if(!generator.onUpdate()) {
                    generators.remove(generator);
                    break;
                }
            }
            ticks = 0;
        }
    }

    @Override
    public EnumSet<TickType> ticks(){
        return EnumSet.of(TickType.SERVER);
    }

    @Override
    public String getLabel(){
        return "Minesweeper";
    }
}
