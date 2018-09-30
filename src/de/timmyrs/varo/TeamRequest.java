package de.timmyrs.varo;

import java.util.ArrayList;
import java.util.UUID;

class TeamRequest
{
	static final ArrayList<TeamRequest> teamRequests = new ArrayList<>();
	final UUID from;
	final UUID to;

	TeamRequest(UUID from, UUID to)
	{
		this.from = from;
		this.to = to;
	}

	static TeamRequest get(UUID from, UUID to)
	{
		synchronized(teamRequests)
		{
			for(TeamRequest r : teamRequests)
			{
				if(r.from.equals(from) && r.to.equals(to))
				{
					return r;
				}
			}
		}
		return null;
	}

	static ArrayList<TeamRequest> from(UUID from)
	{
		final ArrayList<TeamRequest> requests = new ArrayList<>();
		synchronized(teamRequests)
		{
			for(TeamRequest r : teamRequests)
			{
				if(r.from.equals(from))
				{
					requests.add(r);
				}
			}
		}
		return requests;
	}

	static ArrayList<TeamRequest> to(UUID to)
	{
		final ArrayList<TeamRequest> requests = new ArrayList<>();
		synchronized(teamRequests)
		{
			for(TeamRequest r : teamRequests)
			{
				if(r.to.equals(to))
				{
					requests.add(r);
				}
			}
		}
		return requests;
	}
}
