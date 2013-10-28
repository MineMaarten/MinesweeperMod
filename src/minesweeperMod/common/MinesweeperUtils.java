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
    public static boolean isTileClosed(int meta){
        return meta == 9 || meta == 10 || meta == 12 || meta == 13 || meta == 14 || meta == 15;
    }

    public static boolean isTileBomb(int meta){
        return meta == 11 || meta == 12 || meta == 13 || meta == 14 || meta == 15;
    }

    public static boolean isTileHardcoreBomb(int meta){
        return meta == 12 || meta == 13;
    }

    public static boolean isTileFlagged(int meta){
        return meta == 10 || meta == 13 || meta == 15;
    }

    /**
     * 
     * @param unlocalizedName achieve.<unlocalizedName>. this means you shouldn't include the achieve.
     * @return
     */
    public static Achievement getAchieveFromName(String unlocalizedName){
        for(Achievement achieve : (List<Achievement>)AchievementList.achievementList) {
            if(achieve.getName().equals("achievement." + unlocalizedName)) {
                return achieve;
            }
        }
        return null;
    }
}
