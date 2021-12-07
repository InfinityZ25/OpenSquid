package us.jcedeno.skins;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.bukkit.Bukkit;

/**
 * The driver to interact with the skin tool api.
 * 
 * @author jcedeno
 */
public class SkinToolApi {
    /** Skin Tool IPFS Rest endpoint. */
    private final static String SKIN_TOOL_URI = "http://45.32.172.208:42069";
    private final static String ASHCON_URI = "https://api.ashcon.app/mojang/v2/user/";
    /**
     * The endpoint to check if a player has a skin already created. Get Request.
     */
    private final static String GET_SKIN_URI = SKIN_TOOL_URI + "/skin/get/%s";
    /** The endpoint to create a new skin. PUT Request. */
    private final static String CREATE_SKINS_URI = SKIN_TOOL_URI + "/skin/create/%s";
    /** The endpoint to add many users to the queue. POST Request. */
    private final static String ADD_TO_QUEUE_URI = SKIN_TOOL_URI + "/skin/add/";
    /** The endpoint to get all cached skins of a type. GET Request. */
    private final static String GET_ALL_VARIANTS_URI = SKIN_TOOL_URI + "/skin/get-all/%s";
    /** The endpoint to delete cached skins. DELETE Request */
    private final static String DELETE_CACHED_URI = SKIN_TOOL_URI + "/skin/delete/%s";
    /** A http client to make the queries and parse data. */
    private final static HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    /** Predicate to compute the negation of a player skin being signed. */
    private final static Predicate<PlayerSkin> predicate = Predicate.not(PlayerSkin::isItSigned);
    /** Executor to handle many tasks. */
    protected static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    /**
     * ✅ A function that gets or creates a player's skin variants. This will return
     * signed skin object, so this method is the one we'd like to use to change
     * skins or create npcs.
     * 
     * @param uuid The player's uuid
     * @return A list of player skins
     */
    public static CompletableFuture<List<PlayerSkin>> getElseComputeSkins(UUID uuid) {

        final var future = new CompletableFuture<List<PlayerSkin>>();

        EXECUTOR_SERVICE.submit(() -> {
            try {
                var optionalJson = getPlayerSkins(uuid);

                if (optionalJson.isPresent()) {
                    future.complete(ensureSigned(optionalJson.get(), uuid));
                } else {
                    // Generate skins if not found
                    var playerSkins = createPlayerSkins(uuid);

                    while (playerSkins.isEmpty()) {
                        playerSkins = createPlayerSkins(uuid);
                    }
                    System.out.println("Moving to ensuring skins being signed.");

                    // If present, print out, else say it is empty
                    if (playerSkins.isPresent()) {
                        future.complete(ensureSigned(playerSkins.get(), uuid));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return future;
    }

    public static boolean addSkinsToComputeQueue(List<UUID> users){
        return addSkinsToComputeQueue(users.toArray(new UUID[]{}));
    }

    /**
     * ✅ A function that adds several users to the queue of skins to be created.
     * 
     * @param users An array of uuids to add to the queue.
     * @return True if the operation was acknowledged by the server, False
     *         otherwise.
     */
    public static boolean addSkinsToComputeQueue(UUID... users) {
        if (users == null || users.length == 0)
            return false;
        final var request = HttpRequest.newBuilder(URI.create(ADD_TO_QUEUE_URI))
                .POST(BodyPublishers.ofString(gson.toJson(users))).header("Content-Type", "application/json")
                .header("accept", "application/json").build();
        try {
            final var response = client.send(request, BodyHandlers.ofString());
            return Boolean.parseBoolean(response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * ✅ A function that deletes the cached skin of a player from long term storage.
     * 
     * @param uuid The player uuid.
     * @return A list containing the removed skins or null if nothing was present.
     */
    public static Optional<List<PlayerSkin>> deleteUserSkins(UUID uuid) {
        final var request = HttpRequest.newBuilder(URI.create(String.format(DELETE_CACHED_URI, uuid.toString())))
                .DELETE().header("accept", "application/json").build();
        try {
            final var response = client.send(request, BodyHandlers.ofString());
            // Return if the response is not null. Assuming a player has already created a
            // skin before.
            if (response.body() != null && !response.body().equalsIgnoreCase("null"))
                return Optional.of(List.of(gson.fromJson(response.body(), PlayerSkin[].class)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * ✅ Returns all cached skins that match the provided variants.
     * 
     * @param variant The skin variant to be queried.
     * @return An optional containing a list of player skins or empty if no matches
     *         present.
     */
    public static Optional<List<PlayerSkin>> getAllVariant(String variant) {
        final var request = HttpRequest.newBuilder(URI.create(String.format(GET_ALL_VARIANTS_URI, variant))).GET()
                .header("accept", "application/json").build();
        try {
            final var response = client.send(request, BodyHandlers.ofString());
            // Return if the response is not null. Assuming a player has already created a
            // skin before.
            if (response.body() != null && !response.body().equalsIgnoreCase("null"))
                return Optional.of(List.of(gson.fromJson(response.body(), PlayerSkin[].class)));

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        /** If exists exceptionally, return empty optional */
        return Optional.empty();
    }

    /**
     * ✅ Returns a player's skin collection if the player has one. 😉
     * 
     * @param uuid The player's uuid.
     * @return An optional containing a player skin's collection or empty if not
     *         present.
     */
    public static Optional<List<PlayerSkin>> getPlayerSkins(UUID uuid) {
        final var request = HttpRequest.newBuilder(URI.create(String.format(GET_SKIN_URI, uuid.toString()))).GET()
                .header("accept", "application/json").build();
        try {
            final var response = client.send(request, BodyHandlers.ofString());
            // Return if the response is not null. Assuming a player has already created a
            // skin before.
            if (response.body() != null && !response.body().equalsIgnoreCase("null"))
                return Optional.of(List.of(gson.fromJson(response.body(), PlayerSkin[].class)));

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        /** If exists exceptionally, return empty optional */
        return Optional.empty();
    }

    /**
     * ✅ Creates a new skin for a player.
     * 
     * @param uuid The player's uuid.
     * @return And Optional containing the player's skin collection or empty if not
     */
    public static Optional<List<PlayerSkin>> createPlayerSkins(UUID uuid) {
        /** Build the request */
        final var request = HttpRequest.newBuilder(URI.create(String.format(CREATE_SKINS_URI, uuid.toString())))
                .PUT(BodyPublishers.noBody()).header("accept", "application/json").build();
        try {
            /**
             * The api will return a Json Array containing a series of skins objects. Parse
             * that and return as a list of PlayerSkins.
             */
            var response = client.send(request, BodyHandlers.ofString());
            /** Take care of nulls. */
            if (response != null && response.body() != null && !response.body().equalsIgnoreCase("null")) {
                return Optional.of(List.of(gson.fromJson(response.body(), PlayerSkin[].class)));
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        /** If there's an exception just return it empty. */
        return Optional.empty();
    }

    /**
     * A function that returns the actual skin of any player in the game.
     * 
     * @param uuid The player's uuid
     * @return A list of player skins
     */
    public static PlayerSkin getCurrentUserSkin(UUID uuid, boolean useOnline) {
        if (useOnline) {
            var player = Bukkit.getOnlinePlayers().stream().filter(p -> p.getUniqueId().compareTo(uuid) == 0)
                    .findFirst();
            if (player.isPresent()) {
                var playerSKin = player.get();
                var properties = playerSKin.getPlayerProfile().getProperties().iterator().next();
                return PlayerSkin.of(null, properties.getValue(), properties.getSignature());
            }
        }

        var request = HttpRequest.newBuilder(URI.create(ASHCON_URI + uuid.toString())).GET()
                .header("accept", "application/json").build();
        try {
            var json = gson.fromJson(client.send(request, BodyHandlers.ofString()).body(), JsonObject.class)
                    .getAsJsonObject("textures").getAsJsonObject("raw");

            return gson.fromJson(json, PlayerSkin.class);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * A function that ensures all skin variants are signed by mojang servers.
     * 
     * @param playerSkins The list of player skins
     * @param uuid        The player's uuid
     * @return A list of player skins signed
     */
    private static List<PlayerSkin> ensureSigned(List<PlayerSkin> playerSkins, UUID uuid) {
        var anyNotSigned = playerSkins.stream().anyMatch(predicate);
        var signedSkins = new ArrayList<PlayerSkin>();
        if (!anyNotSigned) {
            // Return self
            return playerSkins;
        }
        while (anyNotSigned) {
            var playerSkinOptional = getPlayerSkins(uuid);

            if (playerSkinOptional.isPresent()) {
                var localPSkins = playerSkinOptional.get();
                anyNotSigned = localPSkins.stream().anyMatch(predicate);
                if (!anyNotSigned) {
                    signedSkins = new ArrayList<PlayerSkin>(localPSkins);
                }
            } else {
                System.err.println("Exception, skin optional on ensure signed is empty.");
            }
        }

        return signedSkins;
    }

    /**
     * A method that calls the ashcon app to obtain the uuid of a given player name.
     * This method will hold the thread it runs on son proceed with caution.
     * 
     * @param name Player name in question
     * @return The player's UUID or null if not found.
     */
    public static UUID getUserProfile(String name) {

        var uri = URI.create("https://api.ashcon.app/mojang/v2/user/" + name);
        var request = HttpRequest.newBuilder(uri).header("accept", "application/json").build();
        try {
            var response = client.send(request, BodyHandlers.ofString());
            var json = gson.fromJson(response.body(), JsonObject.class);
            return UUID.fromString(json.get("uuid").getAsString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
