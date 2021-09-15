package net.tez.luckyblocks.constants;

public enum ConfigSection {
	
	ITEM_TYPE("item.type","END_STONE"),
	ITEM_DISPLAY_NAME("item.displayName","&6&lLucky Block"),
	ITEM_LORE("item.lore",""),
	
	COMMAND_CONSOLE_NOT_ALLOWED("command.consoleNotAllowed","&cThis command can be executed by player only!"),
	COMMAND_NO_PERMISSION("command.noPermission","&cYou don't have permissions to use this command!"),
	COMMAND_INVALID("command.invalid","&cInvalid command, Please use &6/lb help &cfor available commands!"),
	COMMAND_TARGET_EMPTY("command.target.empty","&cPlease type a specific player name!"),
	/**
	 * <pre>%player-name%: player name
	 */
	COMMAND_TARGET_NOT_ONLINE("command.target.notOnline","&cPlayer &6%player-name% &cis not online!"),
	/** 
	 *<pre>%name%: finding name
	 */
	COMMAND_TARGET_NOT_EXIST("command.target.notExist","&cCould not find player with name &6%name%"),
	COMMAND_AMOUNT_EMPTY("command.amount.empty","&cPlease type a specific amount!"),
	COMMAND_AMOUNT_NEGATIVE_AMOUNT("command.amount.negativeAmount","&cAmount must equals or greater than &61"),
	COMMAND_AMOUNT_LIMIT("command.amount.limit","&cAmount cannot greater than &664"),
	COMMAND_AMOUNT_INVALID("command.amount.invalid","&cAmount must be a number!"),
	COMMAND_CHANCE_EMPTY("command.chance.empty","&cPlease type a specific chance!"),
	COMMAND_CHANCE_NEGATIVE_AMOUNT("command.amount.negativeAmount","&cChance must greater or equals 1%"),
	COMMAND_CHANCE_LIMIT("command.amount.limit","&cChance cannot greater than &6100%"),
	COMMAND_CHANCE_INVALID("command.amount.invalid","&cChance must be a number!"),
	COMMAND_PAGE_NEGATIVE_AMOUNT("command.page.negativeAmount","&cPage must greater or equals 1"),
	COMMAND_PAGE_LIMIT("command.page.limit","&cChance cannot greater than &6%max-page%"),
	COMMAND_PAGE_INVALID("command.page.invalid","&cPage must be a number!"),
	COMMAND_ITEM_EMPTY("command.itemEmpty","&cYou must hold item in hand!"),
	COMMAND_NAME_EMPTY("command.name.empty","&cPlease type a specific name!"),
	COMMAND_NAME_DUPLICATE("command.name.duplicate","&cNew registry name cannot be the same as old registry name."),
	COMMAND_NAME_ALREADY_TAKEN("command.name.alreadyTaken","&cRegistry name &6%registry-name% &cis already taken, please choose other name."),
	COMMAND_NAME_NOT_FOUND("command.name.notFound","&cCould not find item with name &6%registry-name%"),
	COMMAND_GIVE_SENDER("command.give.sender","&aGiving &6%lucky-block-display-name% x%amount% &ato player &6%player-name%"),
	COMMAND_GIVE_TARGET("command.give.target","&aYou've received &6%lucky-block-display-name% x%amount% &afrom server."),
	COMMAND_GIVEITEM_SENDER("command.giveItem.sender","&aGiving &6%item-display-name% x%amount% from registry name &6%registry-name%"),
	COMMAND_GIVEITEM_TARGET("command.giveItem.target","&aYou've received &6%item-display-name% x%amount% &afrom lucky block!"),
	COMMAND_ADD_SENDER("command.add.sender","&aAdded new item with registry name &6%registry-name% &.senderfully!"),
	COMMAND_ADD_OTHERS("command.add.others","&ePlayer &b%player-name% &ehas added new item with regsitry name &b%registry-name%"),
	COMMAND_REMOVE_SENDER("command.remove.sender","&aRemoved item with registry name &6%registry-name% &asuccessfully!"),
	COMMAND_REMOVE_OTHERS("command.remove.others","&eItem with registry name &b%registry-name% &ehas been removed by &b%player-name%"),
	COMMAND_MODIFY_TYPE("command.modify.type","&cPlease choose data-type to modify: &6chance/amount"),
	COMMAND_MODIFY_EMPTY_VALUE("command.modify.emptyValue","&cPlease type a specific value!"),
	COMMAND_MODIFY_CHANCE("command.modify.chance","&aChance has been changed from &7%old-chance% &ato &6%new-chance%"),
	COMMAND_MODIFY_AMOUNT("command.modify.amount","&aAmount has been changed from &7%old-amount% &ato &6%new-amount%"),
	COMMAND_SETITEM_SENDER("command.setItem.sender","&aChanged item of registry name &6%registry-name% &asuccessfully!"),
	COMMAND_SETITEM_OTHERS("command.setItem.others","&ePlayer &b%player-name% &ehas changed item of registry name &b%registry-name%"),
	COMMAND_RENAME_SENDER("command.rename.sender","&aChanged registry name from &7%old-registry-name% &ato &6%new-registry-name%"),
	COMMAND_RENAME_OTHERS("command.rename.others","&eRegistry name &b%old-registry-name% &ehas been changed to &b%new-registry-name% &eby &c%player-name%"),
	COMMAND_CLEAR_NO_ITEMS("command.clear.noItems","&cThere's no items to be cleared!"),
	COMMAND_CLEAR_SENDER("command.clear.sender","&aCleared total &6%amount% &aitems from lucky block!"),
	COMMAND_CLEAR_OTHERS("command.clear.others","&eTotal &b%amount% &eitems of lucky block has been cleared by &b%player-name%"),
	COMMAND_RELOAD("command.reload","&aReloaded plugin successfully!"),
	COMMAND_HELP("command.help","&7» &6/lb %command-name%&7: %command-description%"),
	
	LUCKY_BLOCK_NO_REWARD("luckyBlock.noReward","&cThere's no reward in lucky block to roll!"),
	LUCKY_BLOCK_PLACE_ALREADY_ROLLING("luckyBlock.place.alreadyRolling","&cYou're already using lucky block, please wait until it finish!"),
	LUCKY_BLOCK_PLACE_ACTIVATE("luckyBlock.place.activate","&aYou've activated lucky block, rolling items..."),
	LUCKY_BLOCK_BREAK("luckyBlock.break","&cUnable to break lucky block!"),
	LUCKY_BLOCK_ANNOUNCE_GLOBAL("luckyBlock.announce.global","&6Wooaa! Player &b%player-name% &6has rolled out &b%reward-item% x%amount% from lucky block!"),
	LUCKY_BLOCK_ANNOUNCE_PLAYER("luckyBlock.announce.player","&aCongratz! You receive &6%reward-item% x%amount% &afrom lucky block!"),
	LUCKY_BLOCK_ANNOUNCE_UNLUCKY("luckyBlock.announce.unlucky","&cUnlucky! You did not receive any items from lucky block, try next time!"),
	
	ITEM_PREVIEW_NAME_COLOR("itemPreview.nameColor","&b&l"),
	ITEM_PREVIEW_LORE_MEMBER("itemPreview.lore.member",""),
	ITEM_PREVIEW_LORE_ADMIN("itemPreview.lore.admin",""),
	
	
	
	Z("","");
	
	private String section;
	
	private String defaultValue;
	
	ConfigSection(String section, String defaultValue) {
		this.section = section;
		this.defaultValue = defaultValue;
	}
	
	public String getSection() {
		return section;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}
}
