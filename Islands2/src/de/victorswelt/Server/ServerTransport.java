package de.victorswelt.Server;

import java.util.List;

import de.victorswelt.Island;
import de.victorswelt.LevelAbstract;
import de.victorswelt.Transport;

public class ServerTransport extends Transport {
	Server server;
	
	public ServerTransport(Server s, LevelAbstract l, int nx, int ny, float ndx, float ndy, int nteam, int nsize, Island ntarget) {
		super(l, nx, ny, ndx, ndy, nteam, nsize, ntarget);
		server = s;
	}
	
	protected void arrivalEvent(boolean changedTeam) {
		List clients = server.getClients();
		if(changedTeam) {
			for(int i = 0; i<clients.size(); i++) {
				Client client = (Client) clients.get(i);
				client.sendIslandPopulationAndTeamUpdate(server.getMap().getIslandId(target), target.population, target.team);
			}
		} else {
			for(int i = 0; i<clients.size(); i++) {
				Client client = (Client) clients.get(i);
				client.sendIslandPopulationUpdate(server.getMap().getIslandId(target), target.population);
			}
		}
	}
}
