package de.craftinc.borderprotection;

import org.bukkit.ChatColor;

public class Messages
{
    private static final String NEWLINE = "\n";

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

    public static String borderMessage =
            "Sorry Dude! This is the border... the final frontier! One day the holy mods " +
            "and administrators will expand the border. It is then your mission to explore " +
            "strange new worlds, to seek out new life and new civilizations, to boldly go " +
            "where no one has gone before.";

    public static String helpGeneral =
            ChatColor.GREEN + "CraftInc BorderProtection - Usage:" + NEWLINE +
            makeCmd("help", "shows this help") +
            makeCmd("set", "Border rectangle edges will be this far away from point of origin.", "<integer>") +
            makeCmd("set", "Border rectangle is defined by the two points. A point is specified as: x,z",
                    "<point1>", "<point2>");

    public static String commandIssuedByNonPlayer
            = ChatColor.RED + "Only a player can use CraftInc BorderProtection commands!";

    public static String borderInfo( String worldName, String borderDef )
    {

        return ChatColor.WHITE + "Border definition of world " +
               ChatColor.YELLOW + worldName +
               ChatColor.WHITE + ": " +
               ChatColor.YELLOW + borderDef;
    }

    public static String borderInfoNoBorderSet =
            ChatColor.YELLOW + "No border here.";

    public static String noPermissionSet =
            ChatColor.RED + "Sorry, you don't have permission to set the border.";
}