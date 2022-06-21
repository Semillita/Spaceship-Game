package server.core;

import java.util.ArrayList;
import java.util.List;

public class Room {

	private List<Player> players;
	
	public Room(String roomId) {
		players = new ArrayList<>();
	}
	
	public void playerJoin(Player newPlayer) {
		players.add(newPlayer);
	}
	
}
