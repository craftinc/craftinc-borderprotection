/*  Craft Inc. BorderProtection
    Copyright (C) 2013  Paul Schulze, Tobias Ottenweller

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package de.craftinc.borderprotection;

import de.craftinc.borderprotection.borders.Border;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.HashMap;

public class Messages
{
    private static final String NEWLINE = "\n";

    private static final String pluginName = Plugin.instance.getDescription().getName();

    /**
     * For every player and every message of that player save the time when he got the last one.
     */
    private static final HashMap<String, HashMap<String, Long>> lastMessage = new HashMap<String, HashMap<String, Long>>();

    private static String makeCmd( String command, String explanation, String... args )
    {
        StringBuilder sb = new StringBuilder();

        // command
        sb.append(ChatColor.YELLOW);
        sb.append(command);
        if ( args.length > 0 )
        {
            sb.append(" ");
            sb.append(ChatColor.BLUE);
            for ( int i = 0; i < args.length; i++ )
            {
                String s = args[i];
                sb.append(s);
                if ( i != args.length - 1 )
                {
                    sb.append(" ");
                }
            }
        }

        sb.append(ChatColor.YELLOW);
        sb.append(": ");
        sb.append(ChatColor.WHITE);
        sb.append(explanation);
        sb.append(NEWLINE);

        return sb.toString();
    }

    private static String borderExplanation =
            "One day the holy mods and administrators will expand the border. It is then your mission to explore " +
            "strange new worlds, to seek out new life and new civilizations, to boldly go where no one has gone before.";

    public static String borderMessage =
            ChatColor.YELLOW + "Sorry Dude! " +
            ChatColor.WHITE + "This is the border... the final frontier! " + borderExplanation + NEWLINE +
            makeCmd("/cibp get", "shows the borders of the current world");

    public static String borderTeleportMessage =
            ChatColor.YELLOW + "Sorry Dude! " +
            ChatColor.WHITE + "You cannot teleport outside the border. " + borderExplanation + NEWLINE +
            makeCmd("/cibp get", "shows the borders of the current world");

    public static String helpGeneral =
            ChatColor.GREEN + pluginName + " - Usage:" + NEWLINE +
            ChatColor.WHITE + "Commands are always related to the current world." + NEWLINE +
            makeCmd("help", "shows this help") +
            makeCmd("get | info", "Shows information about the border.") +
            makeCmd("on | off", "Enables/disables the border.") +
            makeCmd("set", "Square border with distance (d) from 0,0.", "r", "<d>") +
            makeCmd("set", "Rectangle defined by two points. Point=x,z.", "r", "<p1>", "<p2>") +
            makeCmd("set", "Circle border with radius from 0,0.", "c", "<radius>") +
            makeCmd("set", "Circle defined by center and radius. Center=x,z.", "c", "<c>", "<r>") +
            makeCmd("checkversion", "Checks for a newer version.");

    public static String borderCreationSuccessful
            = ChatColor.YELLOW + "New border was set " +
              ChatColor.GREEN + "successfully" +
              ChatColor.YELLOW + "!";

    public static String commandIssuedByNonPlayer
            = ChatColor.RED + "Only a player can use " + pluginName + " commands!";

    public static String borderInfo( String worldName, Border border )
    {
        String borderEnabled;
        if ( border.isActive() )
            borderEnabled = ChatColor.GREEN + "enabled";
        else
            borderEnabled = ChatColor.RED + "disabled";

        return ChatColor.WHITE + "Border of world " + ChatColor.YELLOW + worldName + ChatColor.WHITE + ": " + NEWLINE +
               ChatColor.YELLOW + "Type: " + ChatColor.WHITE + border.getBorderTypeString() + NEWLINE +
               border.getBorderInfoString() + NEWLINE +
               ChatColor.WHITE + "Border is " + borderEnabled + ChatColor.WHITE + ".";
    }

    public static String borderInfoNoBorderSet =
            ChatColor.YELLOW + "No border in this world.";

    public static String noPermissionSet =
            ChatColor.RED + "Sorry, you don't have permission to change the border.";

    public static String noPermissionCheckversion =
            ChatColor.RED + "Sorry, you don't have permission to check for new versions.";

    public static String borderEnabled =
            ChatColor.YELLOW + "Border enabled.";

    public static String borderDisabled =
            ChatColor.YELLOW + "Border disabled.";

    public static String borderSaveException =
            ChatColor.RED + "Error: Could not save border on server. After the next reload this border will be lost!";

    public static String borderEnableDisableException =
            ChatColor.RED +
            "Error: Could not save border state on server. After the next reload this border state will be lost!";

    public static String updateMessage( String newVersion, String curVersion )
    {
        return ChatColor.RED + pluginName + ": New version available!" + NEWLINE +
               ChatColor.YELLOW + "Current version: " + ChatColor.WHITE + curVersion + NEWLINE +
               ChatColor.YELLOW + "New version: " + ChatColor.WHITE + newVersion + NEWLINE +
               ChatColor.YELLOW + "Please visit:" + NEWLINE +
               ChatColor.AQUA + "http://dev.bukkit.org/bukkit-mods/craftinc-borderprotection" + NEWLINE +
               ChatColor.YELLOW + "to get the latest version!";
    }

    public static String noUpdateAvailable =
            ChatColor.YELLOW + "No updates available.";

    /**
     * Display a message to a player and then wait for timeout seconds before displaying it again.
     *
     * @param player  Player who will see the message.
     * @param message The message String.
     * @param timeout Timeout in seconds until the message will be displayed earliest.
     */
    public static void showMessageWithTimeout( final Player player, final String message, final Integer timeout )
    {
        // get the current time
        final Long now = Calendar.getInstance().getTimeInMillis();

        if ( ( lastMessage.get(player.getName()) != null && lastMessage.get(player.getName()).get(message) != null &&
               now - timeout * 1000 > lastMessage.get(player.getName()).get(message) ) ||
             lastMessage.get(player.getName()) != null && lastMessage.get(player.getName()).get(message) == null ||
             lastMessage.get(player.getName()) == null )
        {
            // show message
            player.sendMessage(message);

            // set last sent message for this player to now
            if ( lastMessage.get(player.getName()) == null )
            {
                lastMessage.put(player.getName(), new HashMap<String, Long>()
                {{
                        put(message, now);
                    }});
            }
            else
            {
                lastMessage.get(player.getName()).put(message, now);
            }
        }
    }
}
