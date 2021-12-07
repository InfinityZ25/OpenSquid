package us.jcedeno.skins;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.bukkit.Bukkit;

public class SkinsMain {

    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {

        var playersSkinsOptional = SkinToolApi
                .getElseComputeSkins(UUID.fromString("5de6e184-af8d-498a-bbde-055e50653316")).get(5, TimeUnit.SECONDS);

        playersSkinsOptional.forEach(System.out::println);

    }

    void skinAllRandomCommandExample() {
        // Ask the api for all skins of this type
        var optionalSkins = SkinToolApi.getAllVariant("civilian");
        if (optionalSkins.isPresent()) {
            // Fitler out the not signed skins.
            var skins = optionalSkins.get().stream().filter(PlayerSkin::isItSigned).toList();
            if (skins.size() > 0) {
                // Take a snapshot o of all players online to change their skins.
                var players = Bukkit.getOnlinePlayers().stream().map(m -> m.getPlayer()).toList();
                int skinIndex = 0;
                // Get an iterator to easily loop through the players.
                var playerIter = players.iterator();

                while (playerIter.hasNext()) {
                    var nextPlayer = playerIter.next();
                    // Get current index and add 1
                    var skin = skins.get(skinIndex++);
                    // Ensure skin not null.
                    if (skin != null)
                        SkinCMD.skinSwapper(nextPlayer, skin.getValue(), skin.getSignature()); // Swap the Player's skin
                    // Handle possible out of bounds exception.
                    if (skinIndex > skins.size() - 1)
                        skinIndex = 0;

                }

            }

        }
    }

}
