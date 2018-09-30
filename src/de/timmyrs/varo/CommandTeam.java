package de.timmyrs.varo;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class CommandTeam implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] a)
	{
		if(s instanceof Player)
		{
			final Player p = (Player) s;
			if(a.length == 0 || a[0].equalsIgnoreCase("info"))
			{
				final Team t = Team.of(p);
				if(t == null)
				{
					Message.ERROR_NO_TEAM.send(p);
				}
				else
				{
					p.sendMessage(t.getName(p));
				}
			}
			else if(Varo.instance.getConfig().getBoolean("donttouchthis.ongoing"))
			{
				Message.ERROR_ONGOING.send(p);
			}
			else if(Varo.instance.getConfig().getInt("maxTeamSize") < 2)
			{
				Message.ERROR_NO_TEAMS.send(p);
			}
			else
			{
				if(a[0].equalsIgnoreCase("invite"))
				{
					if(a.length == 2)
					{
						handleInvite(p, a[1]);
					}
					else
					{
						Message.SYNTAX_TEAM.send(p);
					}
				}
				else if(a[0].equalsIgnoreCase("requests"))
				{
					final UUID u = p.getUniqueId();
					ArrayList<TeamRequest> requests = TeamRequest.from(u);
					if(requests.size() == 0)
					{
						Message.TEAMREQ_OUT_NONE.send(p);
					}
					else
					{
						Team t = new Team();
						for(TeamRequest r : requests)
						{
							t.players.put(r.to, 0);
						}
						p.sendMessage(Message.TEAMREQ_OUT + " " + t.getName(p));
					}
					requests = TeamRequest.to(u);
					if(requests.size() == 0)
					{
						Message.TEAMREQ_IN_NONE.send(p);
					}
					else
					{
						Team t = new Team();
						for(TeamRequest r : requests)
						{
							t.players.put(r.from, 0);
						}
						p.sendMessage(Message.TEAMREQ_IN + " " + t.getName(p));
					}
				}
				else if(a[0].equalsIgnoreCase("leave"))
				{
					final Team t = Team.of(p);
					if(t == null)
					{
						Message.ERROR_NO_TEAM.send(p);
					}
					else
					{
						t.handleLeave(p);
						Message.TEAM_LEFT.send(p);
					}
				}
				else if(a.length == 1 && !a[0].equalsIgnoreCase("help"))
				{
					handleInvite(p, a[0]);
				}
				else
				{
					Message.SYNTAX_TEAM.send(p);
				}
			}
		}
		else
		{
			Message.ERROR_PLAYERS_ONLY.send(s);
		}
		return true;
	}

	private void handleInvite(Player s, String a)
	{
		final Player p = Varo.getPlayer(a);
		if(p == null)
		{
			s.sendMessage(Message.ERROR_OFFLINE.get(s).replace("%", a));
		}
		else if(p.equals(s))
		{
			Message.ERROR_SELFTEAM.send(s);
		}
		else
		{
			final UUID su = s.getUniqueId();
			final UUID pu = p.getUniqueId();
			synchronized(TeamRequest.teamRequests)
			{
				TeamRequest r = TeamRequest.get(pu, su);
				if(r != null)
				{
					Team t = Team.of(pu);
					final Team _t = Team.of(su);
					if(t == null)
					{
						synchronized(Team.teams)
						{
							t = new Team();
							t.players.put(pu, 0);
							t.players.put(su, 0);
							Team.teams.add(t);
						}
						if(p.isOnline())
						{
							p.sendMessage(Message.TEAM_JOIN.get(s).replace("%", s.getName()));
						}
						s.sendMessage(Message.TEAM_JOINED.get(s).replace("%", p.getName()));
					}
					else
					{
						synchronized(t.players)
						{
							if(t.players.size() == Varo.instance.getConfig().getInt("maxTeamSize"))
							{
								s.sendMessage(Message.ERROR_TEAM_FULL.get(p).replace("%", p.getName()));
							}
							else
							{
								s.sendMessage(Message.TEAM_JOINED.get(s).replace("%", t.getName(s)));
								for(Map.Entry<UUID, Integer> entry : t.players.entrySet())
								{
									Player tp = Bukkit.getPlayer(entry.getKey());
									if(tp.isOnline())
									{
										tp.sendMessage(Message.TEAM_JOIN.get(tp).replace("%", p.getName()));
									}
								}
								t.players.put(su, 0);
							}
						}
					}
					if(_t != null && _t != t)
					{
						_t.handleLeave(su);
					}
					TeamRequest.teamRequests.remove(r);
					Team.updateConfig();
				}
				else
				{
					if(TeamRequest.get(su, pu) == null)
					{
						TeamRequest.teamRequests.add(new TeamRequest(su, pu));
					}
					s.sendMessage(Message.TEAMREQ_SENT_1.get(s).replace("%", p.getName()));
					s.sendMessage(Message.TEAMREQ_SENT_2.get(s).replace("%", s.getName()));
				}
			}
		}
	}
}
