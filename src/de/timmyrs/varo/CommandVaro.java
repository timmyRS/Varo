package de.timmyrs.varo;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class CommandVaro implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] a)
	{
		if(a.length == 0)
		{
			s.sendMessage("https://github.com/timmyrs/Varo");
		}
		else
		{
			if(a[0].equalsIgnoreCase("tp") || a[0].equalsIgnoreCase("teleport"))
			{
				if(s instanceof Player)
				{
					final Player p = (Player) s;
					if(a.length != 2)
					{
						Message.SYNTAX_VARO.send(p);
					}
					else
					{

						if(p.getGameMode() == GameMode.SPECTATOR || p.isOp())
						{
							final Player t = Varo.getPlayer(a[1]);
							if(t == null)
							{
								p.sendMessage(Message.ERROR_OFFLINE.get(p).replace("%", a[1]));
							}
							else
							{
								p.teleport(t);
							}
						}
						else
						{
							Message.TELEPORT_UNAUTHORIZED.send(p);
						}
					}
				}
				else
				{
					Message.ERROR_PLAYERS_ONLY.send(s);
				}
			}
			else if(a[0].equalsIgnoreCase("start"))
			{
				if(s instanceof Player && !s.isOp())
				{
					Message.ERROR_UNAUTHORIZED.send(s);
				}
				else if(Varo.instance.getConfig().getBoolean("donttouchthis.ongoing"))
				{
					Message.ERROR_ONGOING.send(s);
				}
				else
				{
					if(Bukkit.getOnlinePlayers().size() / Varo.instance.getConfig().getInt("maxTeamSize") < 2)
					{
						Message.START_INSUFFICIENT_PLAYERS.send(s);
					}
					else
					{
						synchronized(Team.teams)
						{
							boolean changed;
							do
							{
								changed = false;
								for(Team t : Team.teams)
								{
									synchronized(t.players)
									{
										for(Map.Entry<UUID, Integer> entry : t.players.entrySet())
										{
											final Player p = Bukkit.getPlayer(entry.getKey());
											if(p == null || !p.isOnline())
											{
												t.handleLeave(entry.getKey());
												changed = true;
												break;
											}
										}
										if(changed)
										{
											break;
										}
									}
								}
							}
							while(changed);
							final ArrayList<Player> teamless = new ArrayList<>();
							for(Player p : Bukkit.getOnlinePlayers())
							{
								if(Team.of(p) == null)
								{
									teamless.add(p);
								}
							}
							for(Team t : Team.teams)
							{
								synchronized(t.players)
								{
									if(t.players.size() < Varo.instance.getConfig().getInt("maxTeamSize"))
									{
										final ArrayList<Player> removedTeamless = new ArrayList<>();
										for(Player p : teamless)
										{
											synchronized(t.players)
											{
												p.sendMessage(Message.TEAM_JOINED.get(p).replace("%", t.getName()));
												for(Map.Entry<UUID, Integer> entry : t.players.entrySet())
												{
													Player tp = Bukkit.getPlayer(entry.getKey());
													if(tp.isOnline())
													{
														tp.sendMessage(Message.TEAM_JOIN.get(tp).replace("%", p.getName()));
													}
												}
												t.players.put(p.getUniqueId(), 0);
												removedTeamless.add(p);
												if(t.players.size() < Varo.instance.getConfig().getInt("maxTeamSize"))
												{
													break;
												}
											}
										}
										for(Player p : removedTeamless)
										{
											teamless.remove(p);
										}
									}
								}
							}
							if(teamless.size() > 0)
							{
								Team t = new Team();
								for(Player p : teamless)
								{
									if(t.players.size() >= Varo.instance.getConfig().getInt("maxTeamSize"))
									{
										Team.teams.add(t);
										t = new Team();
									}
									t.players.put(p.getUniqueId(), 0);
								}
								if(t.players.size() > 0)
								{
									Team.teams.add(t);
								}
							}
							Team.updateConfig();
							if(Team.teams.size() < 2)
							{
								Message.START_INSUFFICIENT_PLAYERS.send(s);
								return true;
							}
							for(Player p : Bukkit.getOnlinePlayers())
							{
								p.sendTitle(Message.GET_READY.get(p), "", 0, 50, 50);
								Message.GET_READY.send(p);
							}
							if(Varo.world != null)
							{
								Bukkit.unloadWorld(Varo.world, true);
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
							String name;
							do
							{
								name = "varo" + ThreadLocalRandom.current().nextInt(1000, 10000);
							}
							while(new File(name).exists());
							Bukkit.createWorld(new WorldCreator(name));
							Varo.world = Bukkit.getWorld(name);
							final Location worldSpawn = Varo.world.getHighestBlockAt(0, 0).getLocation();
							worldSpawn.setX(worldSpawn.getX() + .5);
							worldSpawn.setZ(worldSpawn.getZ() + .5);
							placeBedrockUnder(worldSpawn);
							Varo.world.setSpawnLocation(worldSpawn);
							Varo.world.setGameRuleValue("announceAdvancements", String.valueOf(Varo.instance.getConfig().getBoolean("announceAdvancements")));
							Varo.world.setGameRuleValue("keepInventory", String.valueOf(Varo.instance.getConfig().getBoolean("keepInventory")));
							Varo.world.setGameRuleValue("doFireTick", String.valueOf(Varo.instance.getConfig().getBoolean("doFireTick")));
							Varo.world.setGameRuleValue("mobGriefing", String.valueOf(Varo.instance.getConfig().getBoolean("mobGriefing")));
							Varo.world.setGameRuleValue("showDeathMessages", String.valueOf(Varo.instance.getConfig().getBoolean("showDeathMessages")));
							final double worldSize = Varo.instance.getConfig().getInt("baseWorldSize") + (Varo.instance.getConfig().getInt("extraWorldSizePerPlayer") * Bukkit.getOnlinePlayers().size());
							Varo.instance.getConfig().set("donttouchthis.worldSize", worldSize + Varo.instance.getConfig().getInt("baseWorldSize"));
							Varo.instance.getConfig().set("donttouchthis.ongoing", true);
							Varo.instance.getConfig().set("donttouchthis.shrinkFactor", 1);
							Varo.world.getWorldBorder().setCenter(Varo.world.getSpawnLocation());
							Varo.world.getWorldBorder().setSize(worldSize);
							Varo.world.getWorldBorder().setWarningDistance(Varo.instance.getConfig().getInt("baseWorldSize") / 2);
							final int min = (int) (worldSize * -0.5);
							final int max = (int) Math.round(worldSize * 0.5) + 1;
							final int spawnThreshold = Varo.instance.getConfig().getInt("baseWorldSize") / 2;
							for(Team t : Team.teams)
							{
								int tries = 0;
								Block highestBlock;
								do
								{
									highestBlock = null;
									final int x = ThreadLocalRandom.current().nextInt(min, max);
									final int z = ThreadLocalRandom.current().nextInt(min, max);
									if(Math.abs(x) < spawnThreshold || Math.abs(z) < spawnThreshold)
									{
										continue;
									}
									highestBlock = Varo.world.getHighestBlockAt(x, z);
								}
								while(highestBlock == null || (!highestBlock.getType().isBlock() && ++tries < 100));
								final Location spawnPoint = highestBlock.getLocation();
								spawnPoint.setX(spawnPoint.getX() + .5);
								spawnPoint.setZ(spawnPoint.getZ() + .5);
								placeBedrockUnder(spawnPoint);
								t.spawnPoint = spawnPoint;
								t.name = t.getName();
							}
							Team.updateConfig();
							for(Player p : Bukkit.getOnlinePlayers())
							{
								p.setGameMode(GameMode.SURVIVAL);
								Varo.clearPlayer(p);
								p.getInventory().clear();
								final Team t = Team.of(p);
								p.teleport(t.spawnPoint);
								synchronized(t.players)
								{
									if(t.players.get(p.getUniqueId()) != 0)
									{
										t.players.put(p.getUniqueId(), 0);
									}
								}
								synchronized(Varo.startItems)
								{
									for(Map.Entry<Integer, ItemStack> i : Varo.startItems.entrySet())
									{
										p.getInventory().setItem(i.getKey(), i.getValue().clone());
									}
								}
								Message.HAVE_FUN.send(p);
							}
						}
					}
				}
			}
			else if(a[0].equalsIgnoreCase("end"))
			{
				if(s instanceof Player && !s.isOp())
				{
					Message.ERROR_UNAUTHORIZED.send(s);
				}
				else if(!Varo.instance.getConfig().getBoolean("donttouchthis.ongoing"))
				{
					Message.ERROR_NOT_ONGOING.send(s);
				}
				else
				{
					Varo.instance.getConfig().set("donttouchthis.ongoing", false);
					if(!(s instanceof Player))
					{
						Message.PREMATURE_END.send(s);
					}
					for(Player p : Bukkit.getOnlinePlayers())
					{
						final String message = s instanceof Player ? Message.PREMATURE_END_BY.get(p).replace("%", s.getName()) : Message.PREMATURE_END.get(p);
						p.sendMessage(message);
						p.sendTitle("", message, 0, 50, 50);
						p.setGameMode(GameMode.SPECTATOR);
						Varo.clearPlayer(p);
						p.getInventory().clear();
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
					for(Player p : Bukkit.getOnlinePlayers())
					{
						Message.NEW_GAME_SOON.send(p);
					}
				}
			}
			else if(a[0].equalsIgnoreCase("savedefaultitems"))
			{
				if(s instanceof Player)
				{
					final Player p = (Player) s;
					final Inventory i = p.getInventory();
					final ArrayList<HashMap<String, Object>> startItems = new ArrayList<>();
					int slot = 0;
					for(ItemStack is : i.getContents())
					{
						if(is != null)
						{
							final HashMap<String, Object> item = new HashMap<>();
							item.put("slot", slot);
							item.put("type", is.getType().name());
							item.put("amount", is.getAmount());
							if(is.getDurability() != 0)
							{
								item.put("durability", is.getDurability());
							}
							startItems.add(item);
						}
						slot++;
					}
					Varo.instance.getConfig().set("startItems", startItems);
					Message.SAVED_DEFAULT_ITEMS.send(p);
				}
				else
				{
					Message.ERROR_PLAYERS_ONLY.send(s);
				}
			}
			else if(a[0].equalsIgnoreCase("flush"))
			{
				if(s instanceof Player && !s.isOp())
				{
					Message.ERROR_UNAUTHORIZED.send(s);
				}
				else
				{
					Varo.instance.saveConfig();
					Message.FLUSH_OK.send(s);
				}
			}
			else if(a[0].equalsIgnoreCase("reload"))
			{
				if(s instanceof Player && !s.isOp())
				{
					Message.ERROR_UNAUTHORIZED.send(s);
				}
				else
				{
					Varo.instance.reloadConfig();
					Message.RELOAD_OK.send(s);
				}
			}
			else
			{
				Message.SYNTAX_VARO.send(s);
			}
		}
		return true;
	}

	private void placeBedrockUnder(Location spawnPoint)
	{
		if(spawnPoint.getBlock().getType().isSolid())
		{
			spawnPoint.getBlock().setType(Material.BEDROCK);
			spawnPoint.setY(spawnPoint.getY() + 1);
		}
		else
		{
			spawnPoint.setY(spawnPoint.getY() - 1);
			spawnPoint.getBlock().setType(Material.BEDROCK);
			spawnPoint.setY(spawnPoint.getY() + 2);
		}
	}
}
