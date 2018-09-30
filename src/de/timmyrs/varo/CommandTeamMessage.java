package de.timmyrs.varo;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class CommandTeamMessage implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] a)
	{
		if(s instanceof Player)
		{
			final Player p = (Player) s;
			if(a.length == 0)
			{
				Message.SYNTAX_TEAMMESSAGE.send(p);
			}
			else
			{
				final Team t = Team.of(p);
				if(t == null)
				{
					Message.ERROR_NO_TEAM.send(p);
				}
				else
				{
					StringBuilder msg = new StringBuilder("[").append(p.getName()).append("]");
					for(String arg : a)
					{
						msg.append(" ").append(arg);
					}
					synchronized(t.players)
					{
						for(Map.Entry<UUID, Integer> entry : t.players.entrySet())
						{
							Player tp = Bukkit.getPlayer(entry.getKey());
							if(tp.isOnline())
							{
								tp.sendMessage(msg.toString());
							}
						}
					}
				}
			}
		}
		else
		{
			Message.ERROR_PLAYERS_ONLY.send(s);
		}
		return true;
	}
}
