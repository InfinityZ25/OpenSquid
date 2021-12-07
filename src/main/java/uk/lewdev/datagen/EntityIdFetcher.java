package uk.lewdev.datagen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.bukkit.entity.EntityType;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * Fetch Entity IDs used by Minecraft's Protocol.
 * Uses framework <a href="https://pokechu22.github.io/Burger/">https://pokechu22.github.io/Burger/</a> and caches the results.
 * 
 * <br>Since {@link EntityType#getTypeId()} is deprecated and often incorrect for use with the Minecraft Protocol, this
 * utility is a more robust method.
 * 
 * <br><br><b>Usage:<br></b>
 * <pre>
 * EntityIdFetcher fetcher = new EntityIdFetcher("1.14.2");
 * fetcher.fetch();
 * fetcher.getId(EntityType.BAT)
 * </pre>
 * 
 * @author Lewys Davies (Lew_)
 * @version 1.0
 */
public class EntityIdFetcher {
	
	private static final Map<String, EntityType> nameRemapper = new HashMap<>();
	
	// Some names from the framework or not directly EntityType enums.
	// This re-mapper is used to compensate for those edge cases
	static {
		nameRemapper.put("CHEST_MINECART", EntityType.MINECART_CHEST);
		nameRemapper.put("FURNACE_MINECART", EntityType.MINECART_FURNACE);
		nameRemapper.put("HOPPER_MINECART", EntityType.MINECART_HOPPER);
		nameRemapper.put("TNT_MINECART", EntityType.MINECART_TNT);
		nameRemapper.put("COMMAND_BLOCK_MINECART", EntityType.MINECART_COMMAND);
		nameRemapper.put("SPAWNER_MINECART", EntityType.MINECART_MOB_SPAWNER);
		nameRemapper.put("END_CRYSTAL", EntityType.ENDER_CRYSTAL);
		nameRemapper.put("EXPERIENCE_BOTTLE", EntityType.THROWN_EXP_BOTTLE);
		nameRemapper.put("EYE_OF_ENDER", EntityType.ENDER_SIGNAL);
		nameRemapper.put("FIREWORK_ROCKET", EntityType.FIREWORK);
		nameRemapper.put("FISHING_BOBBER", EntityType.FISHING_HOOK);
		nameRemapper.put("ITEM", EntityType.DROPPED_ITEM);
		nameRemapper.put("LEASH_KNOT", EntityType.LEASH_HITCH);
		nameRemapper.put("LIGHTNING_BOLT", EntityType.LIGHTNING);
		nameRemapper.put("MOOSHROOM", EntityType.MUSHROOM_COW);
		nameRemapper.put("POTION", EntityType.SPLASH_POTION);
		nameRemapper.put("SNOW_GOLEM", EntityType.SNOWMAN);
		nameRemapper.put("TNT", EntityType.PRIMED_TNT);
	}
	
	// Cached Ids for faster access
	private final Map<EntityType, Integer> parsedIds = new HashMap<>();
	// Minecraft Version
	private final String version;
	
	// Thread-safe fetch completion boolean
	private final AtomicBoolean hasFetched = new AtomicBoolean(false);

	/**
	 * @param version String expected typical format: "1.9" -> "1.14.2" -> "1.15"
	 * 
	 * @apiNote Will not check for invalid format errors. Incompatible version strings will error during the {@link #fetch()}
	 */
	public EntityIdFetcher(String version) {
		this.version = version;
	}
	
	/**
	 * Fetch and parse entity information from the web framework, for the provided version.
	 * 
	 * @apiNote This method does network IO.
	 * 
	 * @throws IOException if fetch failed
	 * @throws JsonParseException if fetched Json String fails to parse
	 * 
	 */
	public void fetch() throws IllegalStateException, JsonParseException, IOException {
		synchronized (this) {
			// Check fetch isn't unnecessarily being called twice
			if(this.hasFetched.get()) {
				throw new IllegalStateException("EntityIdFetcher: #fetch has already been called");
			}
			
			// Fetch and parse from web framework
			JsonObject jsonObj = this.jsonFetch();
			
			// Loop through each entity and read the required information
			jsonObj.entrySet().forEach(jsonSet -> {
				JsonObject jsonEntity = jsonSet.getValue().getAsJsonObject();
				
				// Check the entity has a valid ID and name, we don't need this entity ID if it's 
				if(! jsonEntity.has("id") || ! jsonEntity.has("name")) {
					return;
				}
				
				int id = jsonEntity.get("id").getAsInt();
				String typeStr = jsonEntity.get("name").getAsString().toUpperCase();
				
				// Some names from the framework are different from EntityType enum names
				// Remapper handles this
				if(nameRemapper.containsKey(typeStr)) {
					this.parsedIds.put(nameRemapper.get(typeStr), id);
				} else {
					try {
						this.parsedIds.put(EntityType.valueOf(typeStr), id);
					} catch (Exception e) {
						// Any valid entity would need to be added to the nameRemapper
						System.out.println("EntityIdFetcher: Failed to find EntityType enum with name " + typeStr + ". Add it to the nameRemapper?");
					}
				}
			});
			
			this.hasFetched.set(true);
		}
	}
	
	/**
	 * Check if {@link #fetch()} has completed
	 * 
	 * @apiNote Most of the time, correct asynchronous logic flow should not require use of this method.
	 * 
	 * @return True when {@link #fetch()} has finished
	 */
	public boolean hasFetched() {
		return this.hasFetched.get();
	}
	
	/**
	 * Get the integer ID this version of Minecraft uses for an EntityType.
	 * 
	 * @apiNote Fetch should be completed asynchronously first before using. This method can be called synchronously once completed.
	 * 
	 * @param type {@link EntityType}
	 * 
	 * @throws IllegalStateException if {@link #fetch()} hasn't completed
	 * @throws IllegalArgumentException if EntityType has not been fetched
	 * 
	 * @return int for entity ID
	 */
	public int getId(EntityType type) {
		if(! this.hasFetched.get() || this.parsedIds.isEmpty()) {
			throw new IllegalStateException("EntityIdFetcher: Uninitialised. #fetch should be completed asynchronously first before using #getId");
		}
		
		if(this.parsedIds.containsKey(type)) {
			return this.parsedIds.get(type);
		} else {
			throw new IllegalArgumentException("EntityIdFetcher: EntityType " + type + " has not been fetched from the Burger protocol framework. "
					+ "Is this a mob entity? Is this entity in version " + this.version + "?");
		}
	}
	
	public Map<EntityType, Integer> getAll() {
		return this.parsedIds;
	}
	
	/**
	 * Download a raw Json String from <a href="https://pokechu22.github.io/Burger/">pokechu22.github.io/Burger</a>
	 * and parse it into a Gson {@link JsonObject} <br>
	 * 
	 * @apiNote Method should always be called asynchronously, otherwise it will error
	 * 
	 * @throws IOException if fetch failed
	 * @throws IllegalStateException if being called synchronously
	 * @throws JsonParseException if fetched Json String fails to parse
	 * 
	 * @return {@link JsonObject} containing fetched Json Elements
	 */
	private JsonObject jsonFetch() throws IOException, IllegalStateException, JsonParseException {
		// Pokechu22.github.io/Burger is a framework to extract our protocol information
		URL url = new URL("https://pokechu22.github.io/Burger/" + this.version + ".json");
		String rawString;
		
		// Try-with-resource block, everything will automatically close
		try (InputStream in = url.openStream()) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			rawString = reader.lines().collect(Collectors.joining(System.lineSeparator()));
		}
		
		// Ensure we a String returned
		if(rawString == null) {
			throw new IOException("EntityIdFetcher: #jsonFetch failed to download any String from " + url.toString());
		}
		
		// Parse our String into the Json Array. This will throw a exception if it's not valid Json
		JsonParser jsonParser = new JsonParser();
		return jsonParser.parse(rawString)
				.getAsJsonArray().get(0).getAsJsonObject()
				.get("entities").getAsJsonObject()
				.get("entity").getAsJsonObject();
	}
}