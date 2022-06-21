package server.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.joml.Vector3f;

public class Player {

	private final Socket socket;
	private final PrintWriter pw;
	private final List<Player> players;
	
	private Vector3f position, direction;
	
	public Player(Socket socket, List<Player> players) {
		this.socket = socket;
		PrintWriter pWriter = null;
		
		try {
			pWriter = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		this.pw = pWriter;
		this.players = players;
		
		this.position = new Vector3f(0, 0, 0);
		this.direction = new Vector3f(0, 0, 0);
		
		new Thread(() -> {
			while (true) {
				try {
					Scanner scanner = new Scanner(socket.getInputStream());
					if (scanner.hasNext()) {
						var line = scanner.nextLine();
						if (line.equals("Disconnect")) {
							socket.close();
							break;
						}
						var tokens = Arrays.asList(line.replace("[", "").replace("]", "").split(", "))
								.stream()
								.map(s -> Float.parseFloat(s))
								.collect(Collectors.toList());
						
						position = new Vector3f(tokens.get(0), tokens.get(1), tokens.get(2));
						direction = new Vector3f(tokens.get(3), tokens.get(4), tokens.get(5));
					}		
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void update(List<Player> otherPlayers, List<Player> disconnectedPlayers) {
		if (socket.isClosed()) {
			disconnectedPlayers.add(this);
			return;
		}
		
		var message = "";
		for (var otherPlayer : otherPlayers) {
			var pos = otherPlayer.getPosition();
			var dir = otherPlayer.getDirection();
			message += String.format("(%.3fc %.3fc %.3fc %.3fc %.3fc %.3f)", pos.x, pos.y, pos.z, dir.x, dir.y, dir.z)
					.replace(',', '.').replace('c', ',');
			
		}
		
		pw.println(message);
		pw.flush();
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public Vector3f getDirection() {
		return direction;
	}
	
}
