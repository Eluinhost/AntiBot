package ca.thederpygolems.antibot;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Borlea
 * @Github https://github.com/borlea/
 * @Website http://thederpygolems.ca/
 * @since Sep 18, 2015
 */
public class AntiBot extends JavaPlugin implements Listener{

	private ConcurrentHashMap<UUID, Location> data;
	private int delay = 3;
	private double distanceSq = 25;
	private boolean checkingDistance = true;

	@Override
	public void onEnable(){
		saveDefaultConfig();
		delay = getConfig().getInt("delay");
		double distance = getConfig().getDouble("distance");
		checkingDistance = distance > 0;
		distanceSq = distance * distance;
		data = new ConcurrentHashMap<>();
		getServer().getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent e){
		if(e.getPlayer().hasPermission("antibot.exempt")) return;

		Player player = e.getPlayer();
		final UUID playerUUID = player.getUniqueId();

		data.put(playerUUID, player.getLocation());

		if (delay != -1) {
			new BukkitRunnable(){
				public void run(){
					data.remove(playerUUID);
				}
			}.runTaskLater(this, delay * 20);
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
		if(!checkingDistance || e.getPlayer().hasPermission("antibot.exempt")) return;

		UUID uuid = e.getPlayer().getUniqueId();

		Location stored = data.get(uuid);

		if (stored == null) return;

		if (stored.distanceSquared(e.getTo()) >= distanceSq) {
			data.remove(uuid);
		}
	}
}
