package de.timmyrs.varo;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Varo extends JavaPlugin
{
	static Varo instance;
	static World world;
	static final HashMap<Integer, ItemStack> startItems = new HashMap<>();

	private static void shrinkWorld()
	{
		if(Varo.world != null && Varo.instance.getConfig().getBoolean("donttouchthis.ongoing"))
		{
			double worldSize = Varo.instance.getConfig().getDouble("donttouchthis.worldSize");
			if(worldSize > Varo.instance.getConfig().getInt("baseWorldSize"))
			{
				worldSize -= (Varo.instance.getConfig().getDouble("baseWorldShrinkPerSecond") * 4 * Varo.instance.getConfig().getInt("donttouchthis.shrinkFactor"));
				if(worldSize < Varo.instance.getConfig().getInt("baseWorldSize"))
				{
					worldSize = Varo.instance.getConfig().getInt("baseWorldSize");
				}
				Varo.instance.getConfig().set("donttouchthis.worldSize", worldSize);
				Varo.world.getWorldBorder().setSize(worldSize, 4);
			}
		}
	}

	private static void recursivelyDelete(File file)
	{
		if(file.isDirectory())
		{
			for(File f : Objects.requireNonNull(file.listFiles()))
			{
				recursivelyDelete(f);
			}
		}
		//noinspection ResultOfMethodCallIgnored
		file.delete();
	}

	static Player getPlayer(String username)
	{
		for(Player p : Bukkit.getOnlinePlayers())
		{
			if(p.getName().equalsIgnoreCase(username))
			{
				return p;
			}
		}
		return null;
	}

	static void clearPlayer(Player p)
	{
		p.setHealth(20);
		p.setExhaustion(0);
		p.setFoodLevel(20);
		p.setExp(0);
		for(PotionEffect pe : p.getActivePotionEffects())
		{
			p.removePotionEffect(pe.getType());
		}
	}

	@Override
	public void onEnable()
	{
		Varo.instance = this;
		for(File f : Objects.requireNonNull(new File(".").listFiles()))
		{
			if(f.getName().startsWith("varo") && f.isDirectory())
			{
				if(Varo.world != null || new File(f.getName() + "/DELETE").exists())
				{
					if(Bukkit.getWorld(f.getName()) == null)
					{
						recursivelyDelete(f);
					}
				}
				else
				{
					Varo.world = Bukkit.getWorld(f.getName());
					if(Varo.world == null)
					{
						Varo.world = Bukkit.createWorld(new WorldCreator(f.getName()));
					}
				}
			}
		}
		this.getConfig().addDefault("info", "This file should NOT be edited WHILE a Varo round is ONGOING.");
		this.getConfig().addDefault("maxTeamSize", 2);
		this.getConfig().addDefault("livesPerPlayer", 1);
		this.getConfig().addDefault("baseWorldSize", 50);
		this.getConfig().addDefault("extraWorldSizePerPlayer", 200);
		this.getConfig().addDefault("baseWorldShrinkPerSecond", 0.20D);
		this.getConfig().addDefault("announceAdvancements", false);
		this.getConfig().addDefault("keepInventory", false);
		this.getConfig().addDefault("doFireTick", true);
		this.getConfig().addDefault("mobGriefing", true);
		this.getConfig().addDefault("showDeathMessages", true);
		final ArrayList<HashMap<String, Object>> defaultStartItems = new ArrayList<>();
		final HashMap<String, Object> apples = new HashMap<>();
		apples.put("slot", 1);
		apples.put("type", "APPLE");
		apples.put("amount", 3);
		apples.put("durability", 0);
		defaultStartItems.add(apples);
		final HashMap<String, Object> compass = new HashMap<>();
		compass.put("slot", 2);
		compass.put("type", "COMPASS");
		compass.put("amount", 1);
		compass.put("durability", 0);
		defaultStartItems.add(compass);
		this.getConfig().addDefault("startItems", defaultStartItems);
		this.getConfig().addDefault("donttouchthis.info", "The following should NEVER be edited.");
		this.getConfig().addDefault("donttouchthis.ongoing", false);
		this.getConfig().addDefault("donttouchthis.teams", new ArrayList<HashMap<String, Object>>());
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		reloadConfig();
		Bukkit.getPluginManager().registerEvents(new EventHandler(), this);
		this.getCommand("team").setExecutor(new CommandTeam());
		this.getCommand("t").setExecutor(new CommandTeamMessage());
		this.getCommand("varo").setExecutor(new CommandVaro());
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, Varo::shrinkWorld, 0, 75);
	}

	@Override
	public void reloadConfig()
	{
		super.reloadConfig();
		if(this.getConfig().getInt("maxTeamSize") < 1)
		{
			this.getConfig().set("maxTeamSize", 1);
		}
		//noinspection unchecked
		final ArrayList<HashMap<String, Object>> startItems = (ArrayList<HashMap<String, Object>>) this.getConfig().getList("startItems");
		if(startItems != null)
		{
			synchronized(Varo.startItems)
			{
				Varo.startItems.clear();
				for(HashMap<String, Object> i : startItems)
				{
					final ItemStack item = new ItemStack(Material.valueOf(((String) i.get("type")).toUpperCase()), (Integer) i.get("amount"));
					if(i.containsKey("durability"))
					{
						item.setDurability(((Integer) i.get("durability")).shortValue());
					}
					Varo.startItems.put((Integer) i.get("slot"), item);
				}
			}
		}
		if(this.getConfig().getBoolean("donttouchthis.ongoing") && Varo.world == null)
		{
			this.getConfig().set("donttouchthis.ongoing", false);
		}
		//noinspection unchecked
		final ArrayList<HashMap<String, Object>> teams = (ArrayList<HashMap<String, Object>>) this.getConfig().getList("donttouchthis.teams");
		final boolean fixTeams = (teams != null || !this.getConfig().getBoolean("donttouchthis.ongoing"));
		if(teams == null)
		{
			Team.updateConfig();
		}
		else
		{
			synchronized(Team.teams)
			{
				Team.teams.clear();
				for(HashMap<String, Object> t : teams)
				{
					final Team team = new Team();
					//noinspection unchecked
					for(Map.Entry<String, Integer> entry : ((HashMap<String, Integer>) t.get("players")).entrySet())
					{
						team.players.put(UUID.fromString(entry.getKey()), entry.getValue());
					}
					if(t.containsKey("spawnPoint") && Varo.world != null)
					{
						//noinspection unchecked
						final ArrayList<Double> coords = (ArrayList<Double>) t.get("spawnPoint");
						if(coords.size() == 3)
						{
							team.spawnPoint = new Location(Varo.world, coords.get(0), coords.get(1), coords.get(2));
						}
					}
					Team.teams.add(team);
				}
			}
		}
		if(fixTeams)
		{
			synchronized(Team.teams)
			{
				final ArrayList<Team> _teams = new ArrayList<>(Team.teams);
				for(Team t : _teams)
				{
					synchronized(t.players)
					{
						if(t.players.size() > this.getConfig().getInt("maxTeamSize"))
						{
							t.handleDelete();
						}
					}
				}
			}
		}
	}

	@Override
	public void onDisable()
	{
		this.saveConfig();
	}
}
