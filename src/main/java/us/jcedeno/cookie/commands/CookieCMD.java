package us.jcedeno.cookie.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.core.paper.Core;
import net.md_5.bungee.api.ChatColor;
import us.jcedeno.cookie.CookieManager;
import us.jcedeno.cookie.objects.CookieEnum;

/**
 * 
 * A bukkit command that allows users to interact with the cookie system in the
 * squid game plugin.
 * 
 * @author jcedeno
 */

@CommandPermission("admin.cookie.cmd")
@CommandAlias("cookie")
public class CookieCMD extends BaseCommand {

    private @NonNull CookieManager cookieManager;
    private @NonNull Core instance;

    public CookieCMD(Core instance, CookieManager cookieManager) {
        this.instance = instance;
        this.cookieManager = cookieManager;
        instance.getCommandManager().registerCommand(this);
        instance.getCommandManager().getCommandCompletions().registerStaticCompletion("cookies",
                CookieEnum.getAll().stream().map(m -> m.name()).toList());

    }

    @CommandCompletion("@cookies")
    @Subcommand("give")
    public void giveCookie(Player sender, String cookieType) {

        CookieEnum cookie = CookieEnum.valueOf(cookieType.toUpperCase());
        if (cookie == null) {
            sender.sendMessage("Invalid cookie type");
            return;
        }

        var cookieCase = new ItemStack(Material.FERMENTED_SPIDER_EYE);
        var itemMeta = cookieCase.getItemMeta();
        itemMeta.setCustomModelData(cookie.getModelData());

        cookieCase.setItemMeta(itemMeta);

        sender.getInventory().addItem(cookieCase);

    }

    @CommandCompletion("[byte]")
    @Subcommand("paint-color")
    public void changePaintColor(CommandSender sender, @Default("24") Integer paintColor) {
        // Keep color between -128 and 128
        if (paintColor < -128 || paintColor > 127) {
            sender.sendMessage(ChatColor.RED + "Invalid color");
            return;
        }
        // Parse to byte
        byte color = Byte.parseByte("" + paintColor);
        // Set the color
        CookieManager.setPaintColor(color);
        sender.sendMessage(ChatColor.GREEN + "Paint color set to " + color);

    }

    // Command that toggles edit or not
    @Subcommand("edit")
    public void toggleEdit(CommandSender sender, @Optional Boolean bol) {
        if (bol == null) {
            CookieManager.EDIT = !CookieManager.EDIT;
        } else {
            CookieManager.EDIT = bol;
        }

        sender.sendMessage("Cookie edit mode is now: " + CookieManager.EDIT);
    }

    @Subcommand("reload assets")
    public void reloadAssets(CommandSender sender) {
        sender.sendMessage("Reloading assets...");
        CookieEnum.reloadAllAssets();
    }

    @Subcommand("door")
    public void door(CommandSender sender, Boolean bool) {
        var tools = instance.getGame().getCookieGame();
        tools.mainDoor(bool);
        sender.sendMessage(ChatColor.DARK_AQUA + "Cookie main door " + bool);
    }

    @Subcommand("door")
    public void cookieDoor(CommandSender sender, Integer i, Boolean bool) {
        sender.sendMessage(ChatColor.DARK_AQUA + "Cookie door " + i + " " + bool);
        var tools = instance.getGame().getCookieGame();
        switch (i) {
            case 1:
                tools.cookieDoor1(bool);
                break;
            case 2:
                tools.cookieDoor2(bool);
                break;
            case 3:
                tools.cookieDoor3(bool);
                break;
            case 4:
                tools.cookieDoor4(bool);
                break;

            default:
                break;
        }
    }
}
