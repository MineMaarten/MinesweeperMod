package minesweeperMod.common;

import java.io.File;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import minesweeperMod.common.network.NetworkHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Minesweeper Mod
 * @author MineMaarten
 * www.minemaarten.com
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
// TODO increase version
@Mod(modid = Constants.MOD_ID, name = "Minesweeper Mod", version = "1.5.0")
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

    public static File configFile;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
        configFile = event.getSuggestedConfigurationFile();
        Configuration config = new Configuration(configFile);
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

        StringBuilder sb = new StringBuilder();
        sb.append("Loot table property names are the minimum number of tiles cleared to be eligible for the reward tier. \n");
        sb.append("Syntax for rewards: \n\n");
        sb.append("weight;mod_id:item_name:meta:min_stack_size:max_stack_size[;mod_id:item_name:meta:min_stack_size:max_stack_size[...]]\n\n");
        sb.append("weight: determines the probability of the reward within the tier, a higher value indicates a higher chance of the reward.\n\n");
        sb.append("An example reward tier giving between 1 and 3 iron blocks and between 2 and 8 iron ingots would look like the following: \n\n");
        sb.append("1;minecraft:iron_block:0:1:3;minecraft:iron_ingot:0:2:8\n\n");
        sb.append("If you do not what reward items for a particular difficulty, do not declare any reward tiers for that difficulty.");
        String lootTableConfigComment = sb.toString();

        // Loot tables
        if(!config.hasCategory(Constants.LOOT_TABLE_HARDCORE_CATEGORY)) {
            configLootTable(config, Constants.LOOT_TABLE_HARDCORE_CATEGORY, 51, Blocks.iron_block);
            configLootTable(config, Constants.LOOT_TABLE_HARDCORE_CATEGORY, 101, Blocks.gold_block);
            configLootTable(config, Constants.LOOT_TABLE_HARDCORE_CATEGORY, 201, Blocks.diamond_block);
            configLootTable(config, Constants.LOOT_TABLE_HARDCORE_CATEGORY, 301, Blocks.emerald_block);
            configLootTable(config, Constants.LOOT_TABLE_HARDCORE_CATEGORY, 501, Items.nether_star);
        }
        ConfigCategory configCategory = config.getCategory(Constants.LOOT_TABLE_HARDCORE_CATEGORY);
        configCategory.setComment(lootTableConfigComment);
        if(!config.hasCategory(Constants.LOOT_TABLE_HARD_CATEGORY)) {
            configLootTable(config, Constants.LOOT_TABLE_HARD_CATEGORY, 51, Items.glowstone_dust, 3, 5);
            configLootTable(config, Constants.LOOT_TABLE_HARD_CATEGORY, 101, Blocks.glowstone, 3, 5);
            configLootTable(config, Constants.LOOT_TABLE_HARD_CATEGORY, 201, Items.diamond, 3, 5);
            configLootTable(config, Constants.LOOT_TABLE_HARD_CATEGORY, 301, Items.emerald, 3, 5);
            configLootTable(config, Constants.LOOT_TABLE_HARD_CATEGORY, 501, Items.skull, 1, 1, 1);
        }
        configCategory = config.getCategory(Constants.LOOT_TABLE_HARD_CATEGORY);
        configCategory.setComment(lootTableConfigComment);
        if(!config.hasCategory(Constants.LOOT_TABLE_NORMAL_CATEGORY)) {
            configLootTable(config, Constants.LOOT_TABLE_NORMAL_CATEGORY, 51, Items.gold_ingot, 3, 5);
            configLootTable(config, Constants.LOOT_TABLE_NORMAL_CATEGORY, 101, Items.redstone, 3, 5);
            configLootTable(config, Constants.LOOT_TABLE_NORMAL_CATEGORY, 201, Items.glowstone_dust, 3, 5);
            configLootTable(config, Constants.LOOT_TABLE_NORMAL_CATEGORY, 301, Items.diamond, 3, 5);
            configLootTable(config, Constants.LOOT_TABLE_NORMAL_CATEGORY, 501, Items.emerald, 3, 5);
        }
        configCategory = config.getCategory(Constants.LOOT_TABLE_NORMAL_CATEGORY);
        configCategory.setComment(lootTableConfigComment);
        if(!config.hasCategory(Constants.LOOT_TABLE_EASY_CATEGORY)) {
            configLootTable(config, Constants.LOOT_TABLE_EASY_CATEGORY, 51, Items.iron_ingot, 1, 3);
            configLootTable(config, Constants.LOOT_TABLE_EASY_CATEGORY, 101, Items.gold_ingot, 1, 3);
            configLootTable(config, Constants.LOOT_TABLE_EASY_CATEGORY, 201, Items.redstone, 1, 3);
            configLootTable(config, Constants.LOOT_TABLE_EASY_CATEGORY, 301, Items.glowstone_dust, 1, 3);
            configLootTable(config, Constants.LOOT_TABLE_EASY_CATEGORY, 501, Items.diamond, 1, 3);
        }
        configCategory = config.getCategory(Constants.LOOT_TABLE_EASY_CATEGORY);
        configCategory.setComment(lootTableConfigComment);

        config.save();// save the configuration file

        sanityCheckRewards(config);

        blockMinesweeper = new BlockMinesweeper(Material.ground).setHardness(3.0F).setResistance(1.0F).setUnlocalizedName("minesweeperBlock");// .setCreativeTab(CreativeTabs.tabBlock);

        itemFieldGenerator = new ItemFieldGenerator().setCreativeTab(CreativeTabs.tabTools).setUnlocalizedName("fieldGenerator");
        itemMineDetector = new ItemMineDetector().setCreativeTab(CreativeTabs.tabTools).setUnlocalizedName("mineDetector");

        proxy.registerHandlers();
        tickHandler = new MinesweeperTickHandler();
        //      TickRegistry.registerTickHandler(tickHandler, Side.SERVER);
        FMLCommonHandler.instance().bus().register(tickHandler);
        NetworkHandler.init();

        gameRegisters();
        achievementRegisters();
    }

    private void configLootTable(Configuration config, String category, int boardSize, Block block){
        configLootTable(config, category, boardSize, block, 1, 1);
    }

    private void configLootTable(Configuration config, String category, int boardSize, Block block, int min, int max){
        Item item = Item.getItemFromBlock(block);
        configLootTable(config, category, boardSize, item, 0, min, max);
    }

    private void configLootTable(Configuration config, String category, int boardSize, Item item){
        configLootTable(config, category, boardSize, item, 0, 1, 1);
    }

    private void configLootTable(Configuration config, String category, int boardSize, Item item, int min, int max){
        configLootTable(config, category, boardSize, item, 0, min, max);
    }

    private void configLootTable(Configuration config, String category, int boardSize, Item item, int meta, int min, int max){
        String name = ((ResourceLocation)Item.itemRegistry.getNameForObject(item)).toString();
        config.get(category, String.valueOf(boardSize), new String[]{"1;" + name + ":" + meta + ":" + min + ":" + max});
    }

    private void sanityCheckRewards(Configuration config){
        sanityCheckRewardDifficulty(config, Constants.LOOT_TABLE_HARDCORE_CATEGORY);
        sanityCheckRewardDifficulty(config, Constants.LOOT_TABLE_HARD_CATEGORY);
        sanityCheckRewardDifficulty(config, Constants.LOOT_TABLE_NORMAL_CATEGORY);
        sanityCheckRewardDifficulty(config, Constants.LOOT_TABLE_EASY_CATEGORY);
    }

    private void sanityCheckRewardDifficulty(Configuration config, String difficulty){
        ConfigCategory category = config.getCategory(difficulty);
        Map<String, Property> tiers = category.getValues();
        for(Entry<String, Property> tier : tiers.entrySet()) {
            try {
                Integer.parseInt(tier.getKey());
                sanityCheckRewardTier(difficulty, tier);
            } catch(NumberFormatException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("Configuration error for {0}: Invalid reward tier ''{2}'' in ''{1}''. ");
                sb.append("The tier must be an integer value equal to or greater than zero");
                throw new RuntimeException(MessageFormat.format(sb.toString(), Constants.MOD_ID, difficulty, tier.getKey()), e);
            }
        }
    }

    private void sanityCheckRewardTier(String difficulty, Entry<String, Property> tier){
        if(tier.getValue().getStringList().length == 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Configuration error for {0}: Missing rewards in tier ''{2}'' for ''{1}''. ");
            sb.append("Reward tiers need at least one reward, if you do not what a reward for a specified tier, remove the tier.");
            throw new RuntimeException(MessageFormat.format(sb.toString(), Constants.MOD_ID, difficulty, tier.getKey()));
        }
        Pattern rewardPattern = Pattern.compile("\\d+(;\\w+:\\w+:-?\\d+:\\d+:\\d+)+");
        for(String reward : tier.getValue().getStringList()) {
            if(!rewardPattern.matcher(reward).matches()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Configuration error for {0}: Invalid reward definition ''{3}'' in tier ''{2}'' for ''{1}''. ");
                sb.append("The reward definition must have the following format: \n");
                sb.append("weight;mod_id:item_name:meta:min_stack_size:max_stack_size[;mod_id:item_name:meta:min_stack_size:max_stack_size[...]]");
                throw new RuntimeException(MessageFormat.format(sb.toString(), Constants.MOD_ID, difficulty, tier.getKey(), reward));
            }
            String[] rewardItems = reward.split(";");
            int weight = Integer.parseInt(rewardItems[0]);
            if(weight < 1) {
                StringBuilder sb = new StringBuilder();
                sb.append("Configuration error for {0}: Invalid reward weight for ''{3}'' in tier ''{2}'' for ''{1}''. ");
                sb.append("Weight must be greater than or equal to one.");
                throw new RuntimeException(MessageFormat.format(sb.toString(), Constants.MOD_ID, difficulty, tier.getKey(), reward));
            }
            for(int i = 1; i < rewardItems.length; i++) {
                sanityCheckRewardItem(difficulty, tier.getKey(), rewardItems[i]);

            }
        }
    }

    private void sanityCheckRewardItem(String difficulty, String tier, String configItem){
        RewardItem rewardItem = null;
        try {
            rewardItem = new RewardItem(configItem);
        } catch(Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Configuration error for {0}: Invalid item definition ''{3}'' in tier ''{2}'' for ''{1}''. ");
            sb.append("Item definitions must have the following format: \n");
            sb.append("mod_id:item_name:meta:min_stack_size:max_stack_size");
            throw new RuntimeException(MessageFormat.format(sb.toString(), Constants.MOD_ID, difficulty, tier, configItem), e);
        }
        Item item = GameRegistry.findItem(rewardItem.modId, rewardItem.itemName);
        if(item == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Configuration error for {0}: Unknown item ''{3}:{4}'' in tier ''{2}'' for ''{1}''. ");
            throw new RuntimeException(MessageFormat.format(sb.toString(), Constants.MOD_ID, difficulty, tier, rewardItem.modId, rewardItem.itemName));
        }
        if(rewardItem.min < 0 || rewardItem.min > rewardItem.max) {
            StringBuilder sb = new StringBuilder();
            sb.append("Configuration error for {0}: Invalid stack size range for ''{3}'' in tier ''{2}'' for ''{3}''. ");
            sb.append("Minimum stack size must be equal to or greater than zero and equal to or less than max stack size");
            throw new RuntimeException(MessageFormat.format(sb.toString(), Constants.MOD_ID, difficulty, tier, configItem, rewardItem.min));
        }
    }

    @EventHandler
    public void load(FMLInitializationEvent event){
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());

        // AchievementPage.registerAchievementPage(achievementPageMinesweeper);

        proxy.registerRenders();

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
        achieveTilesCleared[0] = new Achievement("achieveCleared1", "achieveCleared1", 0, -3, itemFieldGenerator, (Achievement)null).setIndependent();
        achieveDifficultyCleared[0] = new Achievement("achieveDifficulty1", "achieveDifficulty1", -1, -1, itemFieldGenerator, (Achievement)null).setIndependent();
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

            achieveTilesCleared[i] = new Achievement("achieveCleared" + (i + 1), "achieveCleared" + (i + 1), xPosTiles, yPosTiles, new ItemStack(itemFieldGenerator, 1, meta), achieveTilesCleared[i - 1]).setIndependent();
            if(i < 4) achieveDifficultyCleared[i] = new Achievement("achieveDifficulty" + (i + 1), "achieveDifficulty" + (i + 1), xPosDifficulty, yPosDifficulty, new ItemStack(itemFieldGenerator, 1, i), (Achievement)null).setIndependent();

        }

        //arranging the achievement page 
        Achievement[] allAchieves = new Achievement[achieveTilesCleared.length + achieveDifficultyCleared.length + 5];
        for(int i = 0; i < achieveTilesCleared.length; i++) {
            allAchieves[i] = achieveTilesCleared[i];
        }
        for(int i = 0; i < achieveDifficultyCleared.length; i++) {
            allAchieves[i + achieveTilesCleared.length] = achieveDifficultyCleared[i];
        }

        allAchieves[achieveTilesCleared.length + achieveDifficultyCleared.length] = new Achievement("achieveTutorial", "achieveTutorial", -5, -3, new ItemStack(itemFieldGenerator, 1, 100), (Achievement)null).setIndependent();
        allAchieves[achieveTilesCleared.length + achieveDifficultyCleared.length + 1] = new Achievement("achieveAdvancedTutorial", "achieveAdvancedTutorial", -5, -1, new ItemStack(itemFieldGenerator, 1, 101), allAchieves[achieveTilesCleared.length + achieveDifficultyCleared.length]).setIndependent();

        allAchieves[achieveTilesCleared.length + achieveDifficultyCleared.length + 2] = new Achievement("achieveUseDetector", "achieveUseDetector", -5, 2, itemMineDetector, (Achievement)null).setIndependent();
        allAchieves[achieveTilesCleared.length + achieveDifficultyCleared.length + 3] = new Achievement("achieve8", "achieve8", 5, -3, new ItemStack(blockMinesweeper, 1, 8), (Achievement)null).setSpecial().setIndependent();
        allAchieves[achieveTilesCleared.length + achieveDifficultyCleared.length + 4] = new Achievement("achieve7", "achieve7", 5, -1, new ItemStack(blockMinesweeper, 1, 7), (Achievement)null).setSpecial().setIndependent();

        for(Achievement achieve : allAchieves) {
            achieve.registerStat();
        }

        AchievementPage.registerAchievementPage(new AchievementPage("Minesweeper", allAchieves));
    }

}
