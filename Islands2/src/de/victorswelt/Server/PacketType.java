package de.victorswelt.Server;

public class PacketType {
	public static final byte CONNECTION_TYPE_JOIN = 0,
				CONNECTION_TYPE_INFO = 1;
	
	public static final byte CLIENT_REQUEST_MAP = 1,
			CLIENT_ADD_TRANSPORT = 2;
	
	public static final byte SERVER_SET_POPULATION = 0,
			SERVER_SET_POPULATION_AND_TEAM = 1,
			SERVER_ADD_TRANSPORT = 2,
			SERVER_SEND_MAP = 3,
			SERVER_ANNOUNCE_LOGIN = 4,
			SERVER_ANNOUNCE_LOGOUT = 5,
			SERVER_KICK_CLIENT = 6;
	
	
}
