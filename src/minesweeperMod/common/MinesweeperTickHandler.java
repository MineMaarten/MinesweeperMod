package minesweeperMod.common;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * Minesweeper Mod
 * @author MineMaarten
 * www.minemaarten.com
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class MinesweeperTickHandler{
    public List<TutorialHandler> tutorials = new ArrayList<TutorialHandler>();
    public List<FieldGeneratorHandler> generators = new ArrayList<FieldGeneratorHandler>();
    private int ticks = 0;

    @SubscribeEvent
    public void tickEnd(TickEvent.ServerTickEvent event){
        if(event.phase == TickEvent.Phase.START) handleTutorials();
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
}
