package minesweeperMod.common;

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
        // if you update this method, also update the duplicate method in the
        // ItemMineDetector class.
        return meta == 11 || meta == 12 || meta == 13 || meta == 14 || meta == 15;
    }

    public static boolean isTileHardcoreBomb(int meta){
        return meta == 12 || meta == 13;
    }

    public static boolean isTileFlagged(int meta){
        return meta == 10 || meta == 13 || meta == 15;
    }
}
