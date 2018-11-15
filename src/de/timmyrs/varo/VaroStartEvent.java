package de.timmyrs.varo;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * Called before a Varo round starts.
 * At this point it can still be cancelled and its start items can be edited.
 */
@SuppressWarnings("unused")
public class VaroStartEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	HashMap<Integer, ItemStack> startItems;
	private boolean cancelled;

	VaroStartEvent(HashMap<Integer, ItemStack> startItems)
	{
		this.startItems = startItems;
	}

	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}

	public HashMap<Integer, ItemStack> getStartItems()
	{
		return startItems;
	}

	public void setStartItems(HashMap<Integer, ItemStack> startItems)
	{
		this.startItems = startItems;
	}

	public boolean isCancelled()
	{
		return cancelled;
	}

	public void setCancelled(boolean cancel)
	{
		cancelled = cancel;
	}
}
