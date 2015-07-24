package minesweeperMod.common;

import java.util.Random;

import minesweeperMod.common.BlockMinesweeper.EnumState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderFlat;
import net.minecraftforge.fml.common.IWorldGenerator;

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
            switch(world.provider.getDimensionId()){
                case 0:
                    generateSurface(world, random, chunkX * 16, chunkZ * 16);
                    break;
                case -1:
                    generateNether(world, random, chunkX * 16, chunkZ * 16);
                    break;
                case 1:
                    generateEnd(world, random, chunkX * 16, chunkZ * 16);
                default:
                    generateSurface(world, random, chunkX * 16, chunkZ * 16);
                    break;
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
        fillWithMetadataBlocks(world, baseX, baseY - 1, baseZ, maxX, baseY - 1, maxZ, Blocks.netherrack);
        fillWithMetadataBlocks(world, baseX + 1, baseY + 1, baseZ + 1, maxX - 1, baseY + 5, maxZ - 1, Blocks.air);// clear some space
        fillWithMetadataBlocks(world, baseX, baseY + 6, baseZ, maxX, baseY + 6, maxZ, getDifficultyBlock(difficulty));// roof
        fillWithMetadataBlocks(world, baseX, baseY, baseZ, maxX, baseY + 6, baseZ, getDifficultyBlock(difficulty));// -Z wall
        fillWithMetadataBlocks(world, baseX, baseY, maxZ, maxX, baseY + 6, maxZ, getDifficultyBlock(difficulty));// +Z wall
        fillWithMetadataBlocks(world, baseX, baseY, baseZ, baseX, baseY + 6, maxZ, getDifficultyBlock(difficulty));// -X wall
        fillWithMetadataBlocks(world, maxX, baseY, baseZ, maxX, baseY + 6, maxZ, getDifficultyBlock(difficulty));// +X wall
        if(difficulty == 2) {
            fillWithMetadataBlocks(world, middleX - oddX - 1, baseY, baseZ, middleX + 2, baseY + 5, baseZ, Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED));// -Z chiseled stone doorway
            fillWithMetadataBlocks(world, baseX, baseY, middleZ - oddZ - 1, baseX, baseY + 5, middleZ + 2, Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED));// -X doorway
            fillWithMetadataBlocks(world, maxX, baseY, middleZ - oddZ - 1, maxX, baseY + 5, middleZ + 2, Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED));// +X doorway
        }

        fillWithMetadataBlocks(world, middleX - oddX, baseY + 1, baseZ, middleX + 1, baseY + 4, baseZ, Blocks.air);// -Z doorway
        fillWithMetadataBlocks(world, middleX - oddX, baseY + 1, maxZ, middleX + 1, baseY + 4, maxZ, Blocks.air);// +Z doorway
        fillWithMetadataBlocks(world, baseX, baseY + 1, middleZ - oddZ, baseX, baseY + 4, middleZ + 1, Blocks.air);// -X doorway
        fillWithMetadataBlocks(world, maxX, baseY + 1, middleZ - oddZ, maxX, baseY + 4, middleZ + 1, Blocks.air);// +X doorway

        setTorch(world, middleX - 1 - oddX, baseY + 3, baseZ + 1, EnumFacing.SOUTH);
        setTorch(world, middleX + 2, baseY + 3, baseZ + 1, EnumFacing.SOUTH);

        setTorch(world, middleX - 1 - oddX, baseY + 3, maxZ - 1, EnumFacing.NORTH);
        setTorch(world, middleX + 2, baseY + 3, maxZ - 1, EnumFacing.NORTH);

        setTorch(world, baseX + 1, baseY + 3, middleZ - 1 - oddZ, EnumFacing.EAST);
        setTorch(world, baseX + 1, baseY + 3, middleZ + 2, EnumFacing.EAST);

        setTorch(world, maxX - 1, baseY + 3, middleZ - 1 - oddZ, EnumFacing.WEST);
        setTorch(world, maxX - 1, baseY + 3, middleZ + 2, EnumFacing.WEST);

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

    private void setTorch(World world, int x, int y, int z, EnumFacing facing){
        world.setBlockState(new BlockPos(x, y, z), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, facing));
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
        if(difficulty == 3) fillWithMetadataBlocks(world, minX, Y - 1, minZ, maxX, Y - 1, maxZ, Blocks.netherrack);
        generateMinefieldTiles(world, minX, minZ, Y, maxX, maxZ, difficulty);
        fillWithMetadataBlocks(world, minX, Y + CORRIDOR_HEIGHT, minZ, maxX, Y + CORRIDOR_HEIGHT, maxZ, getDifficultyBlock(difficulty));

        if(direction < 2) { // when the corridor is 'moving' on the X-axis
            fillWithMetadataBlocks(world, minX, Y, minZ, maxX, Y + CORRIDOR_HEIGHT, minZ, getDifficultyBlock(difficulty));
            fillWithMetadataBlocks(world, minX, Y, maxZ, maxX, Y + CORRIDOR_HEIGHT, maxZ, getDifficultyBlock(difficulty));
            if(difficulty == 2) {
                fillWithMetadataBlocks(world, minX, Y + 1, minZ, minX, Y + CORRIDOR_HEIGHT, maxZ, Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED));
                fillWithMetadataBlocks(world, maxX, Y + 1, minZ, maxX, Y + CORRIDOR_HEIGHT, maxZ, Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED));
            }
            fillWithMetadataBlocks(world, maxX + 1, Y + 1, minZ + 1, maxX + 1, Y + CORRIDOR_HEIGHT - 1, maxZ - 1, Blocks.air); // first remove all the torches
            fillWithMetadataBlocks(world, minX - 1, Y + 1, minZ + 1, maxX, Y + CORRIDOR_HEIGHT - 1, maxZ - 1, Blocks.air);
            setTorch(world, minX - 1, Y + 3, minZ, EnumFacing.WEST);
            setTorch(world, minX - 1, Y + 3, maxZ, EnumFacing.WEST);
            setTorch(world, maxX + 1, Y + 3, minZ, EnumFacing.EAST);
            setTorch(world, maxX + 1, Y + 3, maxZ, EnumFacing.EAST);
        } else { // 'moving' on the Z-axis
            fillWithMetadataBlocks(world, minX, Y, minZ, minX, Y + CORRIDOR_HEIGHT, maxZ, getDifficultyBlock(difficulty));
            fillWithMetadataBlocks(world, maxX, Y, minZ, maxX, Y + CORRIDOR_HEIGHT, maxZ, getDifficultyBlock(difficulty));
            if(difficulty == 2) {
                fillWithMetadataBlocks(world, minX, Y + 1, minZ, maxX, Y + CORRIDOR_HEIGHT, minZ, Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED));
                fillWithMetadataBlocks(world, minX, Y + 1, maxZ, maxX, Y + CORRIDOR_HEIGHT, maxZ, Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED));
            }
            fillWithMetadataBlocks(world, minX + 1, Y + 1, maxZ + 1, maxX - 1, Y + CORRIDOR_HEIGHT - 1, maxZ + 1, Blocks.air); // first remove the torches
            fillWithMetadataBlocks(world, minX + 1, Y + 1, minZ - 1, maxX - 1, Y + CORRIDOR_HEIGHT - 1, maxZ, Blocks.air);
            setTorch(world, minX, Y + 3, maxZ + 1, EnumFacing.SOUTH);
            setTorch(world, maxX, Y + 3, maxZ + 1, EnumFacing.SOUTH);
            setTorch(world, minX, Y + 3, minZ - 1, EnumFacing.NORTH);
            setTorch(world, maxX, Y + 3, minZ - 1, EnumFacing.NORTH);
        }
    }

    private boolean collidingWithCave(World world, int baseX, int baseY, int baseZ, int maxX, int maxZ){
        int middleX = baseX + (maxX - baseX) / 2;
        int middleZ = baseZ + (maxZ - baseZ) / 2;
        for(int i = baseY + 1; i <= baseY + 4; i++) {
            for(int j = 0; j < 2; j++) {
                if(world.isAirBlock(new BlockPos(baseX, i, middleZ + j))) return true;
                if(world.isAirBlock(new BlockPos(maxX, i, middleZ + j))) return true;
                if(world.isAirBlock(new BlockPos(middleX + j, i, baseZ))) return true;
                if(world.isAirBlock(new BlockPos(middleX + j, i, maxZ))) return true;
            }
        }
        return false;
    }

    private Block getDifficultyBlock(int difficulty){
        switch(difficulty){
            case 0:
                return Blocks.planks;
            case 1:
                return Blocks.stonebrick;
            case 2:
                return Blocks.stonebrick;
            default:
                return Blocks.nether_brick;
        }
    }

    private void fillWithMetadataBlocks(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Block block){
        fillWithMetadataBlocks(world, minX, minY, minZ, maxX, maxY, maxZ, block.getDefaultState());
    }

    private void fillWithMetadataBlocks(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, IBlockState state){
        for(int i = minX; i <= maxX; i++) {
            for(int j = minY; j <= maxY; j++) {
                for(int k = minZ; k <= maxZ; k++) {
                    world.setBlockState(new BlockPos(i, j, k), state);
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
                EnumState state;
                if(rand.nextInt(tileBombRatio) == 0) {
                    state = difficulty == 3 ? EnumState.CLOSED_BOMB_HARDCORE : EnumState.CLOSED_BOMB; // generate hardcore bombs instead of normal bombs
                } else {
                    state = EnumState.CLOSED;
                }
                world.setBlockState(new BlockPos(i, Y, j), MinesweeperMod.blockMinesweeper.getDefaultState().withProperty(BlockMinesweeper.STATE, state));

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
                    BlockPos pos = new BlockPos(i, Y, j);
                    if(!world.isAirBlock(pos) && !world.getBlockState(pos).getBlock().isReplaceable(world, pos)) currentLevel++;
                    if(world.getBlockState(pos).getBlock() == Blocks.water) return 0;
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
                if(world.getBlockState(new BlockPos(i, Y, j)).getBlock() == MinesweeperMod.blockMinesweeper) return true;
            }
        }
        return false;
    }

}
