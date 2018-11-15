package de.timmyrs.varo;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a Varo round has started and is no longer cancellable.
 */
@SuppressWarnings("unused")
public class VaroStartedEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}

	public World getWorld()
	{
		return Varo.world;
	}
}
