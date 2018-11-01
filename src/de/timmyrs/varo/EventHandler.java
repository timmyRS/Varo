package de.timmyrs.varo;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public class EventHandler implements Listener
{
	@org.bukkit.event.EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		final Player p = e.getPlayer();
		if(Varo.world != null && !p.getWorld().equals(Varo.world))
		{
			p.teleport(Varo.world.getSpawnLocation());
		}
		if(Varo.instance.getConfig().getBoolean("donttouchthis.ongoing"))
		{
			final Team t = Team.of(p);
			if(t == null)
			{
				p.setGameMode(GameMode.SPECTATOR);
				Bukkit.getScheduler().scheduleSyncDelayedTask(Varo.instance, ()->
				{
					Message.JOIN_SPECTATE.send(p);
					Message.SPECTATE.send(p);
				}, 40);
			}
			else
			{
				p.setGameMode(GameMode.SURVIVAL);
				Bukkit.getScheduler().scheduleSyncDelayedTask(Varo.instance, ()->Message.JOIN_CONTINUE.send(p), 40);
			}
		}
		else
		{
			p.setGameMode(GameMode.SPECTATOR);
			Bukkit.getScheduler().scheduleSyncDelayedTask(Varo.instance, ()->
			{
				Message.NEW_GAME_SOON.send(p);
				if(Varo.instance.getConfig().getInt("maxTeamSize") > 1)
				{
					Message.TEAM_INFO_1.send(p);
					Message.TEAM_INFO_2.send(p);
				}
			}, 40);
		}
	}

	@org.bukkit.event.EventHandler
	public void onEntityDamage(EntityDamageEvent e)
	{
		if(!(e.getEntity() instanceof Player))
		{
			return;
		}
		if(!Varo.instance.getConfig().getBoolean("donttouchthis.ongoing"))
		{
			e.setCancelled(true);
			return;
		}
		final Player p = (Player) e.getEntity();
		final Team t = Team.of(p);
		if(e instanceof EntityDamageByEntityEvent)
		{
			final EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e;
			if(ee.getDamager() instanceof Player)
			{
				final Player d = ((Player) ee.getDamager());
				synchronized(t.players)
				{
					if(t.players.containsKey(d.getUniqueId()))
					{
						if((p.getHealth() - e.getFinalDamage()) < 1)
						{
							ee.setCancelled(true);
						}
						else
						{
							e.setDamage(0);
						}
					}
				}
			}
		}
	}

	@org.bukkit.event.EventHandler
	public void onPlayerDeath(PlayerDeathEvent e)
	{
		if(!Varo.instance.getConfig().getBoolean("donttouchthis.ongoing"))
		{
			return;
		}
		final Player p = e.getEntity();
		final Team t = Team.of(p);
		if(t != null)
		{
			synchronized(t.players)
			{
				int deaths = t.players.get(p.getUniqueId()) + 1;
				if(deaths < Varo.instance.getConfig().getInt("livesPerPlayer"))
				{
					p.sendMessage(Message.DEATH.get(p).replace("%", String.valueOf(Varo.instance.getConfig().getInt("livesPerPlayer") - deaths)));
					t.players.put(p.getUniqueId(), deaths);
					Team.updateConfig();
				}
				else
				{
					p.setGameMode(GameMode.SPECTATOR);
					Message.DEATH_FINAL.send(p);
					Message.SPECTATE.send(p);
					t.handleLeave(p);
					p.getInventory().clear();
				}
				Varo.instance.getConfig().set("donttouchthis.shrinkFactor", Varo.instance.getConfig().getInt("donttouchthis.shrinkFactor") + 1);
			}
		}
	}

	@org.bukkit.event.EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e)
	{
		final Player p = e.getPlayer();
		final Team t = Team.of(p);
		if(!Varo.instance.getConfig().getBoolean("donttouchthis.ongoing"))
		{
			return;
		}
		Varo.clearPlayer(p);
		if(t == null || t.players.get(p.getUniqueId()) == Varo.instance.getConfig().getInt("livesPerPlayer"))
		{
			e.setRespawnLocation(Varo.world.getSpawnLocation());
			if(t != null)
			{
				for(Map.Entry<UUID, Integer> entry : t.players.entrySet())
				{
					Player m = Bukkit.getPlayer(entry.getKey());
					if(m.isOnline())
					{
						p.teleport(m);
						p.setSpectatorTarget(m);
						break;
					}
				}
			}
		}
		else
		{
			if(t.spawnPoint != null && Varo.world.getWorldBorder().isInside(t.spawnPoint))
			{
				e.setRespawnLocation(t.spawnPoint);
			}
			else
			{
				e.setRespawnLocation(Varo.world.getSpawnLocation());
			}
			if(Varo.world.getGameRuleValue("keepInventory").equals("false"))
			{
				synchronized(Varo.startItems)
				{
					for(Map.Entry<Integer, ItemStack> i : Varo.startItems.entrySet())
					{
						p.getInventory().setItem(i.getKey(), i.getValue().clone());
					}
				}
			}
		}
	}

	@org.bukkit.event.EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent e)
	{
		if(Varo.world != null && !e.getPlayer().getWorld().equals(Varo.world))
		{
			e.getPlayer().teleport(Varo.world.getSpawnLocation());
		}
	}

	@org.bukkit.event.EventHandler
	public void onPlayerMove(PlayerMoveEvent e)
	{
		final Player p = e.getPlayer();
		final Location l = p.getLocation();
		if(!p.getWorld().getWorldBorder().isInside(l) && Varo.instance.getConfig().getBoolean("donttouchthis.ongoing"))
		{
			final int y = p.getGameMode() == GameMode.SURVIVAL ? 2 : 0;
			if(Math.abs(l.getX()) > Math.abs(l.getZ()))
			{
				if(l.getX() > 0)
				{
					p.setVelocity(new Vector(-10, y, 0));
				}
				else
				{
					p.setVelocity(new Vector(10, y, 0));
				}
			}
			else
			{
				if(l.getZ() > 0)
				{
					p.setVelocity(new Vector(0, y, -10));
				}
				else
				{
					p.setVelocity(new Vector(0, y, 10));
				}
			}
		}
	}

	@org.bukkit.event.EventHandler
	public void onPlayerKick(PlayerKickEvent e)
	{
		final Player p = e.getPlayer();
		if(Bukkit.getServer().getBannedPlayers().contains(p))
		{
			final Team t = Team.of(p);
			if(t != null)
			{
				t.handleLeave(p);
			}
		}
	}
}
