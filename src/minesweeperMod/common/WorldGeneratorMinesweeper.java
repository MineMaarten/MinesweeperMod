package minesweeperMod.common;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderFlat;
import cpw.mods.fml.common.IWorldGenerator;

/**
 * Minesweeper Mod
 * @author MineMaarten
 * www.minemaarten.com
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class WorldGeneratorMinesweeper implements IWorldGenerator{
    public static final int CORRIDOR_HEIGHT = 5; // height of the corridor (= 1 less than the actual minefields)

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider){
        if(!(chunkGenerator instanceof ChunkProviderFlat)) { //don't generate on flatworlds
            switch(world.provider.dimensionId){
                case 0:
                    generateSurface(world, random, chunkX * 16, chunkZ * 16);
                    break;
                case -1:
                    generateNether(world, random, chunkX * 16, chunkZ * 16);
                    break;
                case 1:
                    generateEnd(world, random, chunkX * 16, chunkZ * 16);
            }
        }
    }

    public void generateSurface(World world, Random rand, int chunkX, int chunkZ){

        if(MinesweeperMod.configHardSR != 0 && rand.nextInt(MinesweeperMod.configHardSR) == 0) {
            int baseX = chunkX + rand.nextInt(8);
            int baseY = 7 + rand.nextInt(45);
            int baseZ = chunkZ + rand.nextInt(8);
            int maxX = baseX + rand.nextInt(9) + 9;
            int maxZ = baseZ + rand.nextInt(9) + 9;
            if(collidingWithCave(world, baseX, baseY, baseZ, maxX, maxZ)) {
                generateMinefield(world, rand, baseX, baseY, baseZ, maxX, maxZ, 2);
            }
        }

        if(MinesweeperMod.configMediumSR != 0 && rand.nextInt(MinesweeperMod.configMediumSR) == 0) {
            int baseX = chunkX + rand.nextInt(8);
            int baseY = 7 + rand.nextInt(45);
            int baseZ = chunkZ + rand.nextInt(8);
            int maxX = baseX + rand.nextInt(9) + 9;
            int maxZ = baseZ + rand.nextInt(9) + 9;
            if(collidingWithCave(world, baseX, baseY, baseZ, maxX, maxZ)) {
                generateMinefield(world, rand, baseX, baseY, baseZ, maxX, maxZ, 1);
            }
        }

        if(MinesweeperMod.configEasySR != 0 && rand.nextInt(MinesweeperMod.configEasySR) == 0) {
            int baseX = chunkX + rand.nextInt(8);
            int baseZ = chunkZ + rand.nextInt(8);
            int maxX = baseX + rand.nextInt(9) + 9;
            int maxZ = baseZ + rand.nextInt(9) + 9;
            int baseY = getFlatLandLevel(world, baseX, baseZ, maxX, maxZ);
            if(baseY > 10) {
                generateMinefield(world, rand, baseX, baseY, baseZ, maxX, maxZ, 0);
            }
        }

    }

    public void generateNether(World world, Random rand, int chunkX, int chunkZ){
        if(MinesweeperMod.configHardcoreSR != 0 && rand.nextInt(MinesweeperMod.configHardcoreSR) == 0) {
            int baseX = chunkX + rand.nextInt(8);
            int baseZ = chunkZ + rand.nextInt(8);
            int maxX = baseX + rand.nextInt(9) + 9;
            int maxZ = baseZ + rand.nextInt(9) + 9;
            int baseY = getFlatLandLevel(world, baseX, baseZ, maxX, maxZ);
            if(baseY > 1) {
                generateMinefield(world, rand, baseX, baseY, baseZ, maxX, maxZ, 3);
            }
        }
    }

    public void generateEnd(World world, Random rand, int chunkX, int chunkZ){

    }

    private void generateMinefield(World world, Random rand, int baseX, int baseY, int baseZ, int maxX, int maxZ, int difficulty){
        int middleX = baseX + (maxX - baseX) / 2;
        int middleZ = baseZ + (maxZ - baseZ) / 2;
        int oddX = 1 - (maxX - baseX) % 2; // when the width is a even number, oddX = 1
        int oddZ = 1 - (maxZ - baseZ) % 2;

        generateMinefieldTiles(world, baseX + 1, baseZ + 1, baseY, maxX - 1, maxZ - 1, difficulty);
        if(difficulty == 3) // when generating above a lava lake (probably)
        fillWithMetadataBlocks(world, baseX, baseY - 1, baseZ, maxX, baseY - 1, maxZ, Block.netherrack.blockID, 0);
        fillWithMetadataBlocks(world, baseX + 1, baseY + 1, baseZ + 1, maxX - 1, baseY + 5, maxZ - 1, 0, 0);// clear some space
        fillWithMetadataBlocks(world, baseX, baseY + 6, baseZ, maxX, baseY + 6, maxZ, getDifficultyBlock(difficulty), getDifficultyMetadata(difficulty));// roof
        fillWithMetadataBlocks(world, baseX, baseY, baseZ, maxX, baseY + 6, baseZ, getDifficultyBlock(difficulty), getDifficultyMetadata(difficulty));// -Z wall
        fillWithMetadataBlocks(world, baseX, baseY, maxZ, maxX, baseY + 6, maxZ, getDifficultyBlock(difficulty), getDifficultyMetadata(difficulty));// +Z wall
        fillWithMetadataBlocks(world, baseX, baseY, baseZ, baseX, baseY + 6, maxZ, getDifficultyBlock(difficulty), getDifficultyMetadata(difficulty));// -X wall
        fillWithMetadataBlocks(world, maxX, baseY, baseZ, maxX, baseY + 6, maxZ, getDifficultyBlock(difficulty), getDifficultyMetadata(difficulty));// +X wall
        if(difficulty == 2) {
            fillWithMetadataBlocks(world, middleX - oddX - 1, baseY, baseZ, middleX + 2, baseY + 5, baseZ, Block.stoneBrick.blockID, 3);// -Z chiseled stone doorway
            fillWithMetadataBlocks(world, middleX - oddX - 1, baseY, maxZ, middleX + 2, baseY + 5, maxZ, Block.stoneBrick.blockID, 3);// +Z doorway
            fillWithMetadataBlocks(world, baseX, baseY, middleZ - oddZ - 1, baseX, baseY + 5, middleZ + 2, Block.stoneBrick.blockID, 3);// -X doorway
            fillWithMetadataBlocks(world, maxX, baseY, middleZ - oddZ - 1, maxX, baseY + 5, middleZ + 2, Block.stoneBrick.blockID, 3);// +X doorway
        }

        fillWithMetadataBlocks(world, middleX - oddX, baseY + 1, baseZ, middleX + 1, baseY + 4, baseZ, 0, 0);// -Z doorway
        fillWithMetadataBlocks(world, middleX - oddX, baseY + 1, maxZ, middleX + 1, baseY + 4, maxZ, 0, 0);// +Z doorway
        fillWithMetadataBlocks(world, baseX, baseY + 1, middleZ - oddZ, baseX, baseY + 4, middleZ + 1, 0, 0);// -X doorway
        fillWithMetadataBlocks(world, maxX, baseY + 1, middleZ - oddZ, maxX, baseY + 4, middleZ + 1, 0, 0);// +X doorway

        world.setBlock(middleX - 1 - oddX, baseY + 3, baseZ + 1, Block.torchWood.blockID, 3, 3);
        world.setBlock(middleX + 2, baseY + 3, baseZ + 1, Block.torchWood.blockID, 3, 3);

        world.setBlock(middleX - 1 - oddX, baseY + 3, maxZ - 1, Block.torchWood.blockID, 4, 3);
        world.setBlock(middleX + 2, baseY + 3, maxZ - 1, Block.torchWood.blockID, 4, 3);

        world.setBlock(baseX + 1, baseY + 3, middleZ - 1 - oddZ, Block.torchWood.blockID, 1, 3);
        world.setBlock(baseX + 1, baseY + 3, middleZ + 2, Block.torchWood.blockID, 1, 3);

        world.setBlock(maxX - 1, baseY + 3, middleZ - 1 - oddZ, Block.torchWood.blockID, 2, 3);
        world.setBlock(maxX - 1, baseY + 3, middleZ + 2, Block.torchWood.blockID, 2, 3);

        /*
         * torch metadata's: 1: X+ 2: X- 3: Z+ 4: Z-
         */

        // corridor generation
        for(int i = 0; i < MinesweeperMod.configExpansionChance; i++) {
            if(rand.nextInt(5) == 0) {
                int corridorLength = rand.nextInt(15) + 7;
                int direction = rand.nextInt(4);
                int minefieldX = rand.nextInt(9) + 9; // the size of the minefield which is going to connect with the corridor
                int minefieldZ = rand.nextInt(9) + 9;
                int minefieldMiddleX = minefieldX / 2;
                int minefieldMiddleZ = minefieldZ / 2;
                switch(direction){
                    case 0:
                        if(isCollidingWithMinefield(world, maxX, middleZ - minefieldMiddleZ, maxX + corridorLength + minefieldX, middleZ + minefieldMiddleZ, baseY)) return;
                        generateCorridor(world, rand, 0, minefieldX, minefieldZ, maxX, middleZ - 2 - oddZ, maxX + corridorLength, middleZ + 3, baseY, difficulty);
                        break;
                    case 1:
                        if(isCollidingWithMinefield(world, baseX - corridorLength - minefieldX, middleZ - minefieldMiddleZ, baseX, middleZ + minefieldMiddleZ, baseY)) return;
                        generateCorridor(world, rand, 1, minefieldX, minefieldZ, baseX - corridorLength, middleZ - 2 - oddZ, baseX, middleZ + 3, baseY, difficulty);
                        break;
                    case 2:
                        if(isCollidingWithMinefield(world, middleX - minefieldMiddleX, maxZ, middleX + minefieldMiddleX, maxZ + corridorLength + minefieldZ, baseY)) return;
                        generateCorridor(world, rand, 2, minefieldX, minefieldZ, middleX - 2 - oddX, maxZ, middleX + 3, maxZ + corridorLength, baseY, difficulty);
                        break;
                    case 3:
                        if(isCollidingWithMinefield(world, middleX - minefieldMiddleX, baseZ - corridorLength - minefieldZ, middleX + minefieldMiddleX, baseZ, baseY)) return;
                        generateCorridor(world, rand, 3, minefieldX, minefieldZ, middleX - 2 - oddX, baseZ - corridorLength, middleX + 3, baseZ, baseY, difficulty);
                }
            }
        }
    }

    public void generateCorridor(World world, Random rand, int direction, int minefieldX, int minefieldZ, int minX, int minZ, int maxX, int maxZ, int Y, int difficulty){
        // generate the connecting minefield room first
        int middleX = minefieldX / 2;
        int middleZ = minefieldZ / 2;
        int oddX = -2;// 1 - (minefieldX % 2); //when the width is a even number, oddX = 1
        int oddZ = -2;// 1 - (minefieldZ % 2);

        switch(direction){
            case 0:
                generateMinefield(world, rand, maxX, Y, minZ - middleZ - oddZ, maxX + minefieldX, minZ - middleZ + minefieldZ - oddZ, difficulty);
                break;
            case 1:
                generateMinefield(world, rand, minX - minefieldX, Y, minZ - middleZ - oddZ, minX, minZ - middleZ + minefieldZ - oddZ, difficulty);
                break;
            case 2:
                generateMinefield(world, rand, minX - middleX - oddX, Y, maxZ, minX - middleX + minefieldX - oddX, maxZ + minefieldZ, difficulty);
                break;
            case 3:
                generateMinefield(world, rand, minX - middleX - oddX, Y, minZ - minefieldZ, minX - middleX + minefieldX - oddX, minZ, difficulty);
        }

        // generate the actual corridor
        if(difficulty == 3) fillWithMetadataBlocks(world, minX, Y - 1, minZ, maxX, Y - 1, maxZ, Block.netherrack.blockID, 0);
        generateMinefieldTiles(world, minX, minZ, Y, maxX, maxZ, difficulty);
        fillWithMetadataBlocks(world, minX, Y + CORRIDOR_HEIGHT, minZ, maxX, Y + CORRIDOR_HEIGHT, maxZ, getDifficultyBlock(difficulty), getDifficultyMetadata(difficulty));

        if(direction < 2) { // when the corridor is 'moving' on the X-axis
            fillWithMetadataBlocks(world, minX, Y, minZ, maxX, Y + CORRIDOR_HEIGHT, minZ, getDifficultyBlock(difficulty), getDifficultyMetadata(difficulty));
            fillWithMetadataBlocks(world, minX, Y, maxZ, maxX, Y + CORRIDOR_HEIGHT, maxZ, getDifficultyBlock(difficulty), getDifficultyMetadata(difficulty));
            if(difficulty == 2) {
                fillWithMetadataBlocks(world, minX, Y + 1, minZ, minX, Y + CORRIDOR_HEIGHT, maxZ, Block.stoneBrick.blockID, 3);
                fillWithMetadataBlocks(world, maxX, Y + 1, minZ, maxX, Y + CORRIDOR_HEIGHT, maxZ, Block.stoneBrick.blockID, 3);
            }
            fillWithMetadataBlocks(world, maxX + 1, Y + 1, minZ + 1, maxX + 1, Y + CORRIDOR_HEIGHT - 1, maxZ - 1, 0, 0); // first remove all the torches
            fillWithMetadataBlocks(world, minX - 1, Y + 1, minZ + 1, maxX, Y + CORRIDOR_HEIGHT - 1, maxZ - 1, 0, 0);
            world.setBlock(minX - 1, Y + 3, minZ, Block.torchWood.blockID, 2, 3);
            world.setBlock(minX - 1, Y + 3, maxZ, Block.torchWood.blockID, 2, 3);
            world.setBlock(maxX + 1, Y + 3, minZ, Block.torchWood.blockID, 1, 3);
            world.setBlock(maxX + 1, Y + 3, maxZ, Block.torchWood.blockID, 1, 3);
        } else { // 'moving' on the Z-axis
            fillWithMetadataBlocks(world, minX, Y, minZ, minX, Y + CORRIDOR_HEIGHT, maxZ, getDifficultyBlock(difficulty), getDifficultyMetadata(difficulty));
            fillWithMetadataBlocks(world, maxX, Y, minZ, maxX, Y + CORRIDOR_HEIGHT, maxZ, getDifficultyBlock(difficulty), getDifficultyMetadata(difficulty));
            if(difficulty == 2) {
                fillWithMetadataBlocks(world, minX, Y + 1, minZ, maxX, Y + CORRIDOR_HEIGHT, minZ, Block.stoneBrick.blockID, 3);
                fillWithMetadataBlocks(world, minX, Y + 1, maxZ, maxX, Y + CORRIDOR_HEIGHT, maxZ, Block.stoneBrick.blockID, 3);
            }
            fillWithMetadataBlocks(world, minX + 1, Y + 1, maxZ + 1, maxX - 1, Y + CORRIDOR_HEIGHT - 1, maxZ + 1, 0, 0); // first remove the torches
            fillWithMetadataBlocks(world, minX + 1, Y + 1, minZ - 1, maxX - 1, Y + CORRIDOR_HEIGHT - 1, maxZ, 0, 0);
            world.setBlock(minX, Y + 3, maxZ + 1, Block.torchWood.blockID, 3, 3);
            world.setBlock(maxX, Y + 3, maxZ + 1, Block.torchWood.blockID, 3, 3);
            world.setBlock(minX, Y + 3, minZ - 1, Block.torchWood.blockID, 4, 3);
            world.setBlock(maxX, Y + 3, minZ - 1, Block.torchWood.blockID, 4, 3);
        }
    }

    private boolean collidingWithCave(World world, int baseX, int baseY, int baseZ, int maxX, int maxZ){
        int middleX = baseX + (maxX - baseX) / 2;
        int middleZ = baseZ + (maxZ - baseZ) / 2;
        for(int i = baseY + 1; i <= baseY + 4; i++) {
            for(int j = 0; j < 2; j++) {
                if(world.getBlockId(baseX, i, middleZ + j) == 0) return true;
                if(world.getBlockId(maxX, i, middleZ + j) == 0) return true;
                if(world.getBlockId(middleX + j, i, baseZ) == 0) return true;
                if(world.getBlockId(middleX + j, i, maxZ) == 0) return true;
            }
        }
        return false;
    }

    private int getDifficultyBlock(int difficulty){
        switch(difficulty){
            case 0:
                return Block.planks.blockID;
            case 1:
                return Block.stoneBrick.blockID;
            case 2:
                return Block.stoneBrick.blockID;
            default:
                return Block.netherBrick.blockID;
        }
    }

    private int getDifficultyMetadata(int difficulty){
        return 0;
    }

    private void fillWithMetadataBlocks(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int blockID, int metadata){
        for(int i = minX; i <= maxX; i++) {
            for(int j = minY; j <= maxY; j++) {
                for(int k = minZ; k <= maxZ; k++) {
                    world.setBlock(i, j, k, blockID, metadata, 3);
                }
            }
        }
    }

    // difficulty: 0 = easy, 1 = medium, 2 = hard, 3 = hardcore
    private void generateMinefieldTiles(World world, int minX, int minZ, int Y, int maxX, int maxZ, int difficulty){
        int tileBombRatio;
        switch(difficulty){
            case 0:
                tileBombRatio = 10;
                break;
            case 1:
                tileBombRatio = 7;
                break;
            default:
                tileBombRatio = 5;
        }

        for(int i = minX; i <= maxX; i++) {
            for(int j = minZ; j <= maxZ; j++) {
                Random rand = new Random();
                int meta;
                if(rand.nextInt(tileBombRatio) == 0) {
                    meta = difficulty == 3 ? 12 : 14; // generate hardcore bombs instead of normal bombs
                } else {
                    meta = 9;
                }
                world.setBlock(i, Y, j, MinesweeperMod.blockMinesweeper.blockID, meta, 3);
            }
        }
    }

    private int getFlatLandLevel(World world, int minX, int minZ, int maxX, int maxZ){
        int lastLevel = 0;
        int currentLevel = 0;
        for(int Y = 100; Y > 0; Y--) {
            lastLevel = currentLevel; // begin a new measurement.
            currentLevel = 0; // reset the counter
            for(int i = minX; i <= maxX; i++) {
                for(int j = minZ; j <= maxZ; j++) {
                    if(world.getBlockId(i, Y, j) != 0 && world.getBlockId(i, Y, j) != Block.tallGrass.blockID) currentLevel++;
                    if(world.getBlockId(i, Y, j) == Block.waterStill.blockID) return 0;
                }
            }
            if(lastLevel < currentLevel - (maxX - minX) * (maxZ - minZ) * 0.9F && Y != 100) { // if the underlying Y level has 90% more blocks then the Y level above call it flat.
                return Y;
            }
        }
        return 0; //returning 0 will indicate no flat surface has been found.
    }

    private boolean isCollidingWithMinefield(World world, int minX, int minZ, int maxX, int maxZ, int Y){
        for(int i = minX; i <= maxX; i++) {
            for(int j = minZ; j <= maxZ; j++) {
                if(world.getBlockId(i, Y, j) == MinesweeperMod.blockMinesweeper.blockID) return true;
            }
        }
        return false;
    }

}
