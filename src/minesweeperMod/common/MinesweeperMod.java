package minesweeperMod.common;

import minesweeperMod.common.network.PacketPipeline;
import minesweeperMod.common.network.PacketSpawnParticle;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Minesweeper Mod
 * @author MineMaarten
 * www.minemaarten.com
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
// TODO increase version
@Mod(modid = Constants.MOD_ID, name = "Minesweeper Mod", version = "1.4.6")
public class MinesweeperMod{

    @SidedProxy(clientSide = "minesweeperMod.client.ClientProxyMinesweeper", serverSide = "minesweeperMod.common.CommonProxyMinesweeper")
    public static CommonProxyMinesweeper proxy;

    @Instance(Constants.MOD_ID)
    public static MinesweeperMod instance;

    public static Block blockMinesweeper;

    public static Item itemFieldGenerator;
    public static Item itemMineDetector;

    public static MinesweeperTickHandler tickHandler;

    public static int configEasySR;
    public static int configMediumSR;
    public static int configHardSR;
    public static int configHardcoreSR;
    public static int configExpansionChance;

    public int configStatDuration;
    public int configStatXPos;
    public int configStatYPos;
    public boolean configStatEnabled;

    public boolean configRenderFlag;

    public static boolean configEnableGeneratorRecipes;
    public static boolean configEnableDetectorRecipe;

    public static final PacketPipeline packetPipeline = new PacketPipeline();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();

        // general
        Property property = config.get(Configuration.CATEGORY_GENERAL, "Easy minefield generation chance", 160);
        property.comment = "The chance of Easy minefields being generated in the world (at the surface). The number says 1 in how many chunks should I try to generate? For example, default it is 1 in 160 chunks. 0 to disable any spawn at all.";
        configEasySR = property.getInt();

        property = config.get(Configuration.CATEGORY_GENERAL, "Medium minefield generation chance", 80);
        property.comment = "The chance of Medium minefields being generated in the world (underground). The number says 1 in how many chunks should I try to generate? For example, default it is 1 in 160 chunks. 0 to disable any spawn at all.";
        configMediumSR = property.getInt();

        property = config.get(Configuration.CATEGORY_GENERAL, "Hard minefield generation chance", 80);
        property.comment = "The chance of Hard minefields being generated in the world (underground). The number says 1 in how many chunks should I try to generate? For example, default it is 1 in 160 chunks. 0 to disable any spawn at all.";
        configHardSR = property.getInt();

        property = config.get(Configuration.CATEGORY_GENERAL, "Hardcore minefield generation chance", 80);
        property.comment = "The chance of Hardcore minefields being generated in the world (in the Nether). The number says 1 in how many chunks should I try to generate? For example, default it is 1 in 80 chunks. 0 to disable any spawn at all.";
        configHardcoreSR = property.getInt();

        property = config.get(Configuration.CATEGORY_GENERAL, "Minefield expansion chance", 4);
        property.comment = "The chance of world generated minefields to expand (generate corridors and another minefield). The higher the number, the higher the chance. 0 means no corridors at all. This number works exponentially, high numbers can cause lag/crash!";
        configExpansionChance = property.getInt();

        property = config.get(Configuration.CATEGORY_GENERAL, "Enable Field Generator Recipes", true);
        property.comment = "When this config is set to false, none of the Field Generators can be crafted in Survival.";
        configEnableGeneratorRecipes = property.getBoolean(false);

        property = config.get(Configuration.CATEGORY_GENERAL, "Enable Mine Detector Recipes", true);
        property.comment = "When this config is set to false, the Mine Detector can't be crafted in Survival.";
        configEnableDetectorRecipe = property.getBoolean(false);

        property = config.get("Minefield Statistic Screen", "Enable Screen", true);
        property.comment = "When this config is set to false, the statistic won't ever show up.";
        configStatEnabled = property.getBoolean(false);

        property = config.get("Minefield Statistic Screen", "Statistic Duration", 10);
        property.comment = "Determines the idle time of the statistic screen. By default, the statistic stops displaying after 10 seconds.";
        configStatDuration = property.getInt();

        property = config.get("Minefield Statistic Screen", "Stat screen X position", 0);
        property.comment = "Determines how left/right the statistics screen should be displayed. 0 is left, 1 is middle, 2 is on the right of the screen.";
        configStatXPos = property.getInt();

        property = config.get("Minefield Statistic Screen", "Stat screen Y position", 1);
        property.comment = "Determines how high/low the statistics screen should be displayed. 0 is on the top, 1 is in the middle, 2 is on the bottom of the screen.";
        configStatYPos = property.getInt();

        property = config.get(Configuration.CATEGORY_GENERAL, "Render flag", true);
        property.comment = "If true the flag model will be rendered if you flag a tile.";
        configRenderFlag = property.getBoolean(false);

        config.save();// save the configuration file

        blockMinesweeper = new BlockMinesweeper(Material.ground).setHardness(3.0F).setResistance(1.0F).setBlockName("minesweeperBlock");// .setCreativeTab(CreativeTabs.tabBlock);

        itemFieldGenerator = new ItemFieldGenerator().setCreativeTab(CreativeTabs.tabTools).setUnlocalizedName("fieldGenerator");
        itemMineDetector = new ItemMineDetector().setCreativeTab(CreativeTabs.tabTools).setUnlocalizedName("mineDetector");

        proxy.registerHandlers();
        tickHandler = new MinesweeperTickHandler();
        //      TickRegistry.registerTickHandler(tickHandler, Side.SERVER);
        FMLCommonHandler.instance().bus().register(tickHandler);

        gameRegisters();
        achievementRegisters();
    }

    @EventHandler
    public void load(FMLInitializationEvent event){
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());

        // AchievementPage.registerAchievementPage(achievementPageMinesweeper);

        proxy.registerRenders();

        packetPipeline.registerPacket(PacketSpawnParticle.class);
        packetPipeline.initialise();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event){

    }

    public void gameRegisters(){

        // new blocks
        GameRegistry.registerBlock(blockMinesweeper, "minesweeperBlock");

        // new items
        GameRegistry.registerItem(itemFieldGenerator, "fieldGenerator", Constants.MOD_ID);
        GameRegistry.registerItem(itemMineDetector, "mineDetector", Constants.MOD_ID);

        // crafting recipes

        // orange 1
        // yellow 4
        // red 14

        // field generator recipes
        if(configEnableGeneratorRecipes) {
            GameRegistry.addRecipe(new ItemStack(itemFieldGenerator, 1, 0), " ws", "wws", "  s", 'w', new ItemStack(Blocks.wool, 1, 4), 's', new ItemStack(Items.stick));
            GameRegistry.addRecipe(new ItemStack(itemFieldGenerator, 1, 1), " ws", "wws", "  s", 'w', new ItemStack(Blocks.wool, 1, 1), 's', new ItemStack(Items.stick));
            GameRegistry.addRecipe(new ItemStack(itemFieldGenerator, 1, 2), " ws", "wws", "  s", 'w', new ItemStack(Blocks.wool, 1, 14), 's', new ItemStack(Items.stick));
            GameRegistry.addRecipe(new ItemStack(itemFieldGenerator, 1, 3), " ws", "wws", "  s", 'w', new ItemStack(Blocks.wool, 1, 12), 's', new ItemStack(Items.stick));
            GameRegistry.addRecipe(new ItemStack(itemFieldGenerator, 1, 100), " ws", "wws", "  s", 'w', new ItemStack(Blocks.wool, 1, 11), 's', new ItemStack(Items.stick));
            // generate the field size recipes (small --> medium --> big)
            for(int i = 0; i < 8; i++) {
                ItemStack inputStack = new ItemStack(itemFieldGenerator, 1, i);
                GameRegistry.addShapelessRecipe(new ItemStack(itemFieldGenerator, 1, i + 4), inputStack, inputStack, inputStack, inputStack);
            }
        }

        // mine detector recipe
        if(configEnableDetectorRecipe) {
            GameRegistry.addRecipe(new ItemStack(itemMineDetector), "  i", " i ", "ww ", 'i', new ItemStack(Items.iron_ingot), 'w', new ItemStack(Blocks.wool, 1, 15));
        }

        // tile entities
        // GameRegistry.registerTileEntity(entityCannonMod.common.TileEntityEntityCannon.class,
        // "tileEntityEntityCannon");

        // worldgenerators
        GameRegistry.registerWorldGenerator(new WorldGeneratorMinesweeper(), 0);
        ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(new WeightedRandomChestContent(new ItemStack(itemFieldGenerator, 1, 101), 1, 1, 5));
        ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(itemFieldGenerator, 1, 101), 1, 1, 5));

        EntityRegistry.registerModEntity(EntityFlag.class, "minesweeperFlag", 0, this, 80, 1, true);

    }

    public void achievementRegisters(){

        Achievement[] achieveTilesCleared = new Achievement[5];
        Achievement[] achieveDifficultyCleared = new Achievement[4];
        achieveTilesCleared[0] = new Achievement("achieveCleared1", "achieveCleared1", 0, -3, itemFieldGenerator, (Achievement)null).initIndependentStat();
        achieveDifficultyCleared[0] = new Achievement("achieveDifficulty1", "achieveDifficulty1", -1, -1, itemFieldGenerator, (Achievement)null).initIndependentStat();
        for(int i = 1; i < 5; i++) {
            int meta;
            int xPosTiles;
            int xPosDifficulty = 0;
            int yPosTiles;
            int yPosDifficulty = 0;
            switch(i){
                case 1:
                    xPosDifficulty = 1;
                    yPosDifficulty = -1;
                    xPosTiles = 3;
                    yPosTiles = 0;
                    meta = 4;
                    break;
                case 2:
                    xPosDifficulty = 1;
                    yPosDifficulty = 1;
                    xPosTiles = 2;
                    yPosTiles = 3;
                    meta = 4;
                    break;
                case 3:
                    xPosDifficulty = -1;
                    yPosDifficulty = 1;
                    xPosTiles = -2;
                    yPosTiles = 3;
                    meta = 8;
                    break;
                default:
                    xPosTiles = -3;
                    yPosTiles = 0;
                    meta = 8;
            }

            achieveTilesCleared[i] = new Achievement("achieveCleared" + (i + 1), "achieveCleared" + (i + 1), xPosTiles, yPosTiles, new ItemStack(itemFieldGenerator, 1, meta), achieveTilesCleared[i - 1]).initIndependentStat();
            if(i < 4) achieveDifficultyCleared[i] = new Achievement("achieveDifficulty" + (i + 1), "achieveDifficulty" + (i + 1), xPosDifficulty, yPosDifficulty, new ItemStack(itemFieldGenerator, 1, i), (Achievement)null).initIndependentStat();

        }

        //arranging the achievement page 
        Achievement[] allAchieves = new Achievement[achieveTilesCleared.length + achieveDifficultyCleared.length + 5];
        for(int i = 0; i < achieveTilesCleared.length; i++) {
            allAchieves[i] = achieveTilesCleared[i];
        }
        for(int i = 0; i < achieveDifficultyCleared.length; i++) {
            allAchieves[i + achieveTilesCleared.length] = achieveDifficultyCleared[i];
        }

        allAchieves[achieveTilesCleared.length + achieveDifficultyCleared.length] = new Achievement("achieveTutorial", "achieveTutorial", -5, -3, new ItemStack(itemFieldGenerator, 1, 100), (Achievement)null).initIndependentStat();
        allAchieves[achieveTilesCleared.length + achieveDifficultyCleared.length + 1] = new Achievement("achieveAdvancedTutorial", "achieveAdvancedTutorial", -5, -1, new ItemStack(itemFieldGenerator, 1, 101), allAchieves[achieveTilesCleared.length + achieveDifficultyCleared.length]).initIndependentStat();

        allAchieves[achieveTilesCleared.length + achieveDifficultyCleared.length + 2] = new Achievement("achieveUseDetector", "achieveUseDetector", -5, 2, itemMineDetector, (Achievement)null).initIndependentStat();
        allAchieves[achieveTilesCleared.length + achieveDifficultyCleared.length + 3] = new Achievement("achieve8", "achieve8", 5, -3, new ItemStack(blockMinesweeper, 1, 8), (Achievement)null).setSpecial().initIndependentStat();
        allAchieves[achieveTilesCleared.length + achieveDifficultyCleared.length + 4] = new Achievement("achieve7", "achieve7", 5, -1, new ItemStack(blockMinesweeper, 1, 7), (Achievement)null).setSpecial().initIndependentStat();

        for(Achievement achieve : allAchieves) {
            achieve.registerStat();
        }

        AchievementPage.registerAchievementPage(new AchievementPage("Minesweeper", allAchieves));
    }

}
