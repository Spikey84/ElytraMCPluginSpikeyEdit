package me.jet315.elytraparkour.commands;

import me.jet315.elytraparkour.Core;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Particles implements CommandExecutor {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(Core.particles == false) {
            Core.particles = true;
            commandSender.sendMessage("Particles are now on");
        } else {
            Core.particles = false;
            commandSender.sendMessage("Particles are now off");
        }
        return true;
    }
}
