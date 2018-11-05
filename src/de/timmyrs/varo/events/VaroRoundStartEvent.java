package de.timmyrs.varo.events;

import de.timmyrs.varo.Varo;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

@SuppressWarnings("unused")
public class VaroRoundStartEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers()
	{
		return handlers;
	}

	@SuppressWarnings("unused")
	public static HandlerList getHandlerList()
	{
		return handlers;
	}

	public World getWorld()
	{
		return Varo.world;
	}

	public HashMap<Integer, ItemStack> getStartItems()
	{
		return Varo.startItems;
	}
}
