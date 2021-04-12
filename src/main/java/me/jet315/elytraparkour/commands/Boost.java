package me.jet315.elytraparkour.commands;

import me.jet315.elytraparkour.Core;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Boost implements CommandExecutor {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(Core.boost == false) {
            Core.boost = true;
            commandSender.sendMessage("Boosts are now on");
        } else {
            Core.boost = false;
            commandSender.sendMessage("Boost are now off");
        }
        return true;
    }
}
