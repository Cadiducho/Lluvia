package es.corecraft.cadi;

import java.util.HashMap;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class lluvia extends JavaPlugin implements Listener {

    private FileConfiguration config;
    private HashMap<String, Boolean> rainWorlds = new HashMap<>();
    
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        config = getConfig();
        List<World> worlds = getServer().getWorlds();
        int worldCount = worlds.size();
        for (int i = 0; i < worldCount; i++) {
            World thisWorld = worlds.get(i);
            if (thisWorld.getEnvironment().equals(Environment.NORMAL)) {
                boolean setting = config.getBoolean(thisWorld.getName(), true);
                rainWorlds.put(thisWorld.getName(), setting);
                config.set(thisWorld.getName(), setting);
            }
        }
        saveConfig();
    }
    
    @Override
    public void onDisable() {
    }
    
    @EventHandler (priority = EventPriority.NORMAL)
    public void onRainStart(WeatherChangeEvent event) {
        if (!event.isCancelled()) {
            boolean setting = rainWorlds.get(event.getWorld().getName());
            if (event.toWeatherState() && rainWorlds.get(event.getWorld().getName())) {
                event.setCancelled(true);
            }
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (commandLabel.equalsIgnoreCase("lluvia")) {
            if (!sender.hasPermission("lluvia.usar")) {
                sender.sendMessage(ChatColor.RED + "No tienes permiso para este comando!");
            } else {
                if (args.length == 0) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("Debes especificar un mundo para usar el comando desde consola");
                        sender.sendMessage("lluvia [mundo] <si/no>");
                        return false;
                    }
                    Player player = (Player) sender;
                    String world = player.getWorld().getName();
                    boolean setting = rainWorlds.get(world);
                    if (setting) {
                        player.sendMessage(ChatColor.GOLD + "Lluvia " + ChatColor.RED + "DESACTIVADA" + ChatColor.GOLD + " en este mundo!");
                    } else {
                        player.sendMessage(ChatColor.GOLD + "Lluvia " + ChatColor.GREEN + "ACTIVADA" + ChatColor.GOLD + " en este mundo!");
                    }
                } else if (args.length == 1) {
                    String world = args[0];
                    if (getServer().getWorld(world) == null) {
                        sender.sendMessage(ChatColor.RED + "El mundo " + ChatColor.WHITE + world + ChatColor.RED + " no se he encontrado!");
                        return false;
                    }
                    if (!getServer().getWorld(world).getEnvironment().equals(Environment.NORMAL)) {
                        sender.sendMessage(ChatColor.RED + "Solo puedes cambiar la lluvia en el mundo normal!");
                        return false;
                    }
                    boolean setting = rainWorlds.get(world);
                    if (setting) {
                        sender.sendMessage(ChatColor.GOLD + "Lluvia " + ChatColor.RED + "DESACTIVADA" + ChatColor.GOLD + " en este mundo!");
                    } else {
                        sender.sendMessage(ChatColor.GOLD + "Lluvia " + ChatColor.GREEN + "ACTIVADA" + ChatColor.GOLD + " en este mundo!");
                    }
                } else if (args.length == 2) {
                    String world = args[0];
                    if (getServer().getWorld(world) == null) {
                        sender.sendMessage(ChatColor.RED + "El mundo " + ChatColor.WHITE + world + ChatColor.RED + " no se he encontrado!");
                        return false;
                    }
                    if (!getServer().getWorld(world).getEnvironment().equals(Environment.NORMAL)) {
                        sender.sendMessage(ChatColor.RED + "Solo puedes cambiar la lluvia en el mundo normal!");
                        return false;
                    }
                    if (!args[1].equalsIgnoreCase("si") && !args[1].equalsIgnoreCase("no")) {
                        sender.sendMessage(ChatColor.RED + "Argumento no reconocido!");
                        sender.sendMessage(ChatColor.RED + "/lluvia [mundo] <si/no>");
                        return false;
                    }
                    if (args[1].equalsIgnoreCase("si")) {
                        rainWorlds.put(world, true);
                        config.set(world, true);
                        saveConfig();
                        sender.sendMessage(ChatColor.GOLD + "Lluvia " + ChatColor.RED + "DESACTIVADA" + ChatColor.GOLD + " en este mundo!");
                        getServer().getWorld(world).setWeatherDuration(1);
                        return true;
                    } else {
                        rainWorlds.put(world, false);
                        config.set(world, false);
                        saveConfig();
                        sender.sendMessage(ChatColor.GOLD + "Lluvia " + ChatColor.GREEN + "ACTIVADA" + ChatColor.GOLD + " en este mundo!");
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
}