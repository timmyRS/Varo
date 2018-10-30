package de.timmyrs.varo;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("UnusedReturnValue")
class Team
{
	static final ArrayList<Team> teams = new ArrayList<>();
	final HashMap<UUID, Integer> players = new HashMap<>();
	Location spawnPoint;

	Team()
	{
	}

	static void updateConfig()
	{
		final ArrayList<HashMap<String, Object>> teams = new ArrayList<>();
		synchronized(Team.teams)
		{
			for(Team t : Team.teams)
			{
				final HashMap<String, Object> team = new HashMap<>();
				final HashMap<String, Integer> players = new HashMap<>();
				for(Map.Entry<UUID, Integer> entry : t.players.entrySet())
				{
					players.put(entry.getKey().toString(), entry.getValue());
				}
				team.put("players", players);
				if(t.spawnPoint != null)
				{
					team.put("spawnPoint", new Double[]{t.spawnPoint.getX(), t.spawnPoint.getY(), t.spawnPoint.getZ()});
				}
				teams.add(team);
			}
		}
		Varo.instance.getConfig().set("donttouchthis.teams", teams);
	}

	static Team of(UUID u)
	{
		for(Team t : teams)
		{
			if(t.players.containsKey(u))
			{
				return t;
			}
		}
		return null;
	}

	static Team of(Player p)
	{
		return Team.of(p.getUniqueId());
	}

	String getName(Player recipient)
	{
		synchronized(players)
		{
			if(players.size() > 1)
			{
				StringBuilder name = new StringBuilder();
				int remaining = players.size();
				for(Map.Entry<UUID, Integer> entry : players.entrySet())
				{
					Player p = Bukkit.getPlayer(entry.getKey());
					if(p == null)
					{
						name.append("null");
					}
					else
					{
						name.append(p.getName());
					}
					if(--remaining > 0)
					{
						if(remaining == 1)
						{
							name.append(Message.LIST_SEPARATOR_FINAL.get(recipient));
						}
						else
						{
							name.append(Message.LIST_SEPARATOR.get(recipient));
						}
					}
				}
				return name.toString();
			}
			else
			{
				//noinspection LoopStatementThatDoesntLoop
				for(Map.Entry<UUID, Integer> entry : players.entrySet())
				{
					return Bukkit.getPlayer(entry.getKey()).getName();
				}
			}
		}
		return "";
	}

	boolean handleLeave(UUID u)
	{
		synchronized(players)
		{
			players.remove(u);
			if(players.size() < (Varo.instance.getConfig().getBoolean("donottouch.ongoing") ? 1 : 2))
			{
				this.handleDelete();
				return false;
			}
			else
			{
				Team.updateConfig();
			}
		}
		return true;
	}

	boolean handleLeave(Player p)
	{
		return this.handleLeave(p.getUniqueId());
	}

	void handleDelete()
	{
		synchronized(teams)
		{
			teams.remove(this);
		}
		Team.updateConfig();
		synchronized(players)
		{
			for(Map.Entry<UUID, Integer> entry : players.entrySet())
			{
				Player p = Bukkit.getPlayer(entry.getKey());
				if(p != null && p.isOnline())
				{
					Message.TEAM_DISBAND.send(p);
				}
			}
		}
		if(Varo.instance.getConfig().getBoolean("donttouchthis.ongoing"))
		{
			synchronized(teams)
			{
				if(teams.size() == 1)
				{
					Team t = teams.get(0);
					synchronized(t.players)
					{
						Varo.instance.getConfig().set("donttouchthis.ongoing", false);
						for(Player p : Bukkit.getOnlinePlayers())
						{
							final String winMessage;
							if(t.players.size() > 1)
							{
								winMessage = Message.WIN_MULTIPLE.get(p).replace("%", t.getName(p));
								p.sendTitle("", winMessage, 0, 50, 50);
							}
							else
							{
								winMessage = Message.WIN_SINGULAR.get(p).replace("%", t.getName(p));
								p.sendTitle(winMessage, Message.NEW_GAME_SOON.get(p), 0, 50, 50);
								Message.NEW_GAME_SOON.send(p);
							}
							p.setGameMode(GameMode.SPECTATOR);
							Varo.clearPlayer(p);
							p.sendMessage(winMessage);
						}
						final File deleteIndicator = new File(Varo.world.getName() + "/DELETE");
						if(!deleteIndicator.exists())
						{
							try
							{
								//noinspection ResultOfMethodCallIgnored
								deleteIndicator.createNewFile();
							}
							catch(IOException ignored)
							{
							}
						}
						Varo.world = null;
					}
				}
			}
		}
	}
}
