package ca.thederpygolems.antibot;

import java.util.*;

import org.bukkit.Location;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @Author Borlea
 * @Github https://github.com/borlea/
 * @Website http://thederpygolems.ca/
 * @since Sep 18, 2015
 */
public class AntiBot extends JavaPlugin implements Listener{

	private HashMap<UUID, Location> data;
	private int delay = 3;
	private double distance = 5;

	@Override
	public void onEnable(){
		saveDefaultConfig();
		delay = getConfig().getInt("delay");
		distance = getConfig().getDouble("distance");
		data = new HashMap<>();
		getServer().getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent e){
		if(!e.getPlayer().hasPermission("antibot.exempt")) {
			data.put(e.getPlayer().getUniqueId(), e.getPlayer().getLocation());
			if(delay != -1) {
				new BukkitRunnable(){

					public void run(){
						data.remove(e.getPlayer().getUniqueId());
					}
				}.runTaskLater(this, delay * 20);
			}
		}
	}

	@EventHandler
	public void onLogout(PlayerQuitEvent e){
		data.remove(e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void playerChat(AsyncPlayerChatEvent e){
		if(data.containsKey(e.getPlayer().getUniqueId())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void commandPreProcessEvent(PlayerCommandPreprocessEvent e){
		if(data.containsKey(e.getPlayer().getUniqueId())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void playerMoveEvent(PlayerMoveEvent e){
		if(distance == -1 || e.getPlayer().hasPermission("antibot.exempt")) {
			return;
		}
		if(data.containsKey(e.getPlayer().getUniqueId())) {
			if(data.get(e.getPlayer().getUniqueId()).distance(e.getTo()) >= distance) {
				data.remove(e.getPlayer().getUniqueId());
			}
		}
	}
}
