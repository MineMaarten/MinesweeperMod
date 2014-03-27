package minesweeperMod.client;

import net.minecraftforge.client.event.sound.SoundLoadEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * Minesweeper Mod
 * @author MineMaarten
 * www.minemaarten.com
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class MinesweeperSoundHandler{
    @SubscribeEvent
    public void onSound(SoundLoadEvent event){
        try {
            //        event.manager.soundPoolSounds.addSound("minesweepermod:minebeep.ogg");
        } catch(Exception e) {
            System.err.println("Failed to register one or more sounds.");
        }
    }
}
