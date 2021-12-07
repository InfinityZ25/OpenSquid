package uk.lewdev.datagen;

import java.io.IOException;
import java.util.Scanner;

import com.google.gson.JsonParseException;

/**
 * @author Lewys Davies (Lew_)
 */
public class EntityEnumGenerator {
	
	public static void main(String[] args) throws IllegalStateException, JsonParseException, IOException {
		try(Scanner in = new Scanner(System.in)) {
			// Ask for version
			System.out.println("Please enter minecraft version: ");
			String version = in.nextLine();
			System.out.println("Generating Entity Enum for MC Version " + version);
		
			// Run our fetcher
			EntityIdFetcher fetcher = new EntityIdFetcher(version);
			fetcher.fetch();
			
			StringBuilder builder = new StringBuilder();
			
			// File header
			builder.append("/**\r\n" + 
					" * @version " + version + "\r\n" + 
					" */\r\n");
			
			builder.append("public enum EntityId {\r\n");
			
			// Enums
			fetcher.getAll().forEach((type, id) -> {
				builder.append("    " + type.name() + "(" + id + "), \r\n");
			});
			
			// Finish off enum section
			builder.replace(builder.length() - 4, builder.length() - 3, ";");
			builder.append("\r\n");
			
			// Generate constructor, getter and setters
			builder.append("    private int id;\r\n" + 
					"    \r\n" + 
					"    private EntityId(int id) {\r\n" + 
					"        this.id = id;\r\n" + 
					"    }\r\n" + 
					"\r\n" + 
					"    public int getId() {\r\n" + 
					"        return this.id;\r\n" + 
					"    }\r\n" +
					"}");
			
			System.out.println("\n\n" + builder.toString());
		}
	}
}