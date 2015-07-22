package minesweeperMod.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import minesweeperMod.common.BlockMinesweeper.EnumState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * Minesweeper Mod
 * @author MineMaarten
 * www.minemaarten.com
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class FieldGeneratorHandler{
    /**
     * Locations of all the bombs of the tutorial levels, which are predefined obviously.
     */
    private static final int[][][] tutorialBombList = new int[][][]{{{0, 0}, {0, 1}, {6, 0}, {6, 2}, {6, 3}, {6, 4}, {6, 6}, {0, 5}}, {{4, 1}, {4, 4}, {3, 6}, {0, 0}, {0, 2}, {0, 4}, {0, 5}}};

    private final World world;
    private final int baseX;
    private final int baseY;
    private final int baseZ;
    private final int itemDamage;
    private final List<int[]> toBeGenerated;
    private final List<Integer> levels;
    private final Random rand;

    public FieldGeneratorHandler(World worldObj, int x, int y, int z, int generatorItemDamage, Random random){
        world = worldObj;
        baseX = x;
        baseY = y;
        baseZ = z;
        rand = random;
        itemDamage = generatorItemDamage;

        int size;
        if(generatorItemDamage == 100 || generatorItemDamage == 101) {
            size = 7; // tutorial level size
        } else if(generatorItemDamage < 4) {
            size = 10;
        } else if(generatorItemDamage < 8) {
            size = 20;
        } else {
            size = 40;
        }
        toBeGenerated = new ArrayList<int[]>();
        levels = new ArrayList<Integer>();
        for(int distance = 0; distance < size; distance++) {
            int tilesForThisLevel = 0;
            for(int i = x; i < x + size; i++) {
                for(int j = z; j < z + size; j++) {
                    if(distanceBetween(i, j, x + size / 2, z + size / 2) == distance) {
                        toBeGenerated.add(new int[]{i, j});
                        tilesForThisLevel++;
                    }
                }
            }
            levels.add(tilesForThisLevel);
        }

    }

    private int distanceBetween(int x1, int z1, int x2, int z2){
        return (int)Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(z1 - z2, 2));
    }

    // when returning false, this object will be deleted (indicating the
    // generation is done)
    public boolean onUpdate(){
        int difficulty;
        if(itemDamage % 4 == 0) {
            difficulty = 10;
        } else if(itemDamage % 4 == 1) {
            difficulty = 7;
        } else if(itemDamage % 4 == 2) {
            difficulty = 5;
        } else {
            difficulty = 5;
        }
        for(int i = 0; i < levels.get(0); i++) {
            int index = 0;// rand.nextInt(toBeGenerated.size());
            int[] coord = toBeGenerated.get(index);
            EnumState state;
            if(itemDamage == 100 || itemDamage == 101) {// tutorial level
                state = isTutorialLevelBomb(coord[0] - baseX, coord[1] - baseZ, itemDamage - 100) ? EnumState.CLOSED_BOMB : EnumState.CLOSED;
            } else {
                if(rand.nextInt(difficulty) == 0) {
                    state = itemDamage % 4 == 3 ? EnumState.CLOSED_BOMB_HARDCORE : EnumState.CLOSED_BOMB; // generate hardcore bombs instead of normal bombs.
                } else {
                    state = EnumState.CLOSED;
                }
            }
            world.setBlockState(new BlockPos(coord[0], baseY, coord[1]), MinesweeperMod.blockMinesweeper.getDefaultState().withProperty(BlockMinesweeper.STATE, state));
            toBeGenerated.remove(index);
        }
        levels.remove(0);
        if((itemDamage == 100 || itemDamage == 101) && toBeGenerated.isEmpty()) MinesweeperMod.tickHandler.tutorials.add(new TutorialHandler(world, baseX, baseY, baseZ, itemDamage - 100));
        return !toBeGenerated.isEmpty();
    }

    private boolean isTutorialLevelBomb(int x, int z, int level){
        for(int i = 0; i < tutorialBombList[level].length; i++) {
            if(tutorialBombList[level][i][0] == x && tutorialBombList[level][i][1] == z) return true;
        }
        return false;
    }
}
