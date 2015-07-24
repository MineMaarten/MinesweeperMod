package minesweeperMod.common;

import java.util.Random;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Reward{
    private int weight;
    private RewardItem[] rewardItems;

    public Reward(String reward){
        String[] components = reward.split(";");
        weight = Integer.parseInt(components[0]);
        rewardItems = new RewardItem[components.length - 1];
        for(int i = 0; i < rewardItems.length; i++) {
            rewardItems[i] = new RewardItem(components[i + 1]);
        }
    }

    public ItemStack[] getReward(){
        ItemStack[] reward = new ItemStack[rewardItems.length];
        for(int i = 0; i < rewardItems.length; i++) {
            RewardItem rewardItem = rewardItems[i];
            Item item = GameRegistry.findItem(rewardItem.modId, rewardItem.itemName);
            Random rand = new Random();
            int count = rand.nextInt(rewardItem.max - rewardItem.min + 1) + rewardItem.min;
            reward[i] = new ItemStack(item, count, rewardItem.meta);
        }

        return reward;
    }

    public int getWeight(){
        return weight;
    }
}
