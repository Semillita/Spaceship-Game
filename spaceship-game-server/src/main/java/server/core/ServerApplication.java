package server.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServerApplication {

	private static List<Player> players;
	private static List<Player> disconnectedPlayers;
	
	public static void main(String[] args) {
		System.out.println("Hello world!");
		
		players = new ArrayList<>();
		disconnectedPlayers = new ArrayList<>();
		
		createUpdateLoop();
		
		try {
			var serverSocket = new ServerSocket(25565);
			
			while (true) {
				var socket = serverSocket.accept();
				//var connection = new Connection(socket);
				players.add(new Player(socket, players));
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void createUpdateLoop() {
		new Thread(() -> {
			while(true) {
//				System.out.println("Amount of players: " + players.size());
				for (var player : players) {
					var otherPlayers = players
							.stream()
							.filter(somePlayer -> somePlayer != player)
							.collect(Collectors.toList());
					player.update(otherPlayers, disconnectedPlayers);
				}
				
				players.removeAll(disconnectedPlayers);
				disconnectedPlayers.clear();
				
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
}
