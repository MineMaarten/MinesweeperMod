package minesweeperMod.common;

public class RewardItem {
	public String modId;
	public String itemName;
	public int meta;
	public int min;
	public int max;
	
	public RewardItem(String data) {
		String[] components = data.split(":");
		modId = components[0];
		itemName = components[1];
		meta = Integer.parseInt(components[2]);
		min = Integer.parseInt(components[3]);
		max = Integer.parseInt(components[4]);
	}
}
