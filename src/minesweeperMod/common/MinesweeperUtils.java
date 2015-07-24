package minesweeperMod.common;

import java.util.List;

import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;

/**
 * Minesweeper Mod
 * @author MineMaarten
 * www.minemaarten.com
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class MinesweeperUtils{

    /**
     * 
     * @param name achieve.<unlocalizedName>. this means you shouldn't include the achieve.
     * @return
     */
    public static Achievement getAchieveFromName(String name){
        for(Achievement achieve : (List<Achievement>)AchievementList.achievementList) {
            if(achieve.statId.equals(name)) {
                return achieve;
            }
        }
        throw new IllegalArgumentException("[Minesweeper Mod] Achievement not found! id: " + name);
    }
}
