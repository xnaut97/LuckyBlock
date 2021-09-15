package net.tez.luckyblocks.utils.language;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.bukkit.plugin.Plugin;

import net.tez.luckyblocks.constants.ConfigSection;

/**
 * A class represents for a specific language, which contains translated
 * messages.<br>
 * To use languague, it need configuring in {@code config.yml} file with correct
 * locate code (can be seen in {@link Locale #}).
 * 
 * @see LanguageManager
 */
public class Language {

	/**
	 * The plugin that owns this language.
	 */
	private Plugin plugin;

	/**
	 * The locale of the language.
	 */
	private String locale;

	/**
	 * A {@link TreeMap} that will store all {@code K-V} pair, which keys are
	 * sections and values are translated strings in this language.
	 */
	private TreeMap<String, String> translatedMessages = new TreeMap<>();

	private TreeMap<String,List<String>> translatedList = new TreeMap<>();
	
	public Language(Plugin plugin, String locale) {
		super();
		this.plugin = plugin;
		this.locale = locale;
	}

	public String getLocale() {
		return locale;
	}

	/**
	 * Set the {@code section} with {@code value}, which is an already translated
	 * string in this language.
	 * 
	 * @param section
	 *            The section
	 * @param value
	 *            The translated string
	 */
	public void put(String section, String value) {
		translatedMessages.put(section, value);
	}

	public void putList(String section, List<String> list) {
		this.translatedList.put(section, list);
	}
	/**
	 * Return a specific translated message in {@code section}.
	 * 
	 * @param section
	 *            The section used to retrieve translated message.
	 * @param defaultValue
	 *            The default string if the {@code section} is not available.
	 * @return The translated message.
	 */
	public String get(String section, String defaultValue) {
		return translatedMessages.getOrDefault(section, defaultValue);
	}

	public String get(ConfigSection config) {
		return translatedMessages.getOrDefault(config.getSection(), config.getDefaultValue());
	}
	
	public int getInt(ConfigSection config) {
		return Integer.parseInt(translatedMessages.getOrDefault(config.getSection(), config.getDefaultValue()));
	}
	
	public double getDouble(ConfigSection config) {
		return Double.parseDouble(translatedMessages.getOrDefault(config.getSection(), config.getDefaultValue()));
	}
	
	public long getLong(ConfigSection config) {
		return Long.parseLong(translatedMessages.getOrDefault(config.getSection(), config.getDefaultValue()));
	}
	
	public float getFloat(ConfigSection config) {
		return Float.parseFloat(translatedMessages.getOrDefault(config.getSection(), config.getDefaultValue()));
	}
	
	public byte getByte(ConfigSection config) {
		return Byte.parseByte(translatedMessages.getOrDefault(config.getSection(), config.getDefaultValue()));
	}
	
	public boolean getBoolean(ConfigSection config) {
		return Boolean.parseBoolean(translatedMessages.getOrDefault(config.getSection(), config.getDefaultValue()));
	}
	
	public List<String> getList(String section){
		return this.translatedList.getOrDefault(section, new ArrayList<>());
	}

	public List<String> getList(ConfigSection config){
		return this.translatedList.getOrDefault(config.getSection(), new ArrayList<>());
	}
	
	public TreeMap<String, String> getTranslatedMessages() {
		return translatedMessages;
	}
	
	public TreeMap<String, List<String>> getTranslatedList() {
		return this.translatedList;
	}
	
	public Plugin getOwner() {
		return plugin;
	}

	@Override
	public String toString() {
		return "Language[plugin=" + getOwner().getName() + ";locale=" + getLocale() + ";translated-messages="
				+ getTranslatedMessages() + "]";
	}

}
