package de.timmyrs.varo;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;

/**
 * Called after a Varo round has been won or terminated.
 */
@SuppressWarnings("unused")
public class VaroEndedEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();
	private final ArrayList<OfflinePlayer> winners;

	VaroEndedEvent(ArrayList<OfflinePlayer> winners)
	{
		this.winners = winners;
	}

	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}

	public ArrayList<OfflinePlayer> getWinners()
	{
		return winners;
	}
}
