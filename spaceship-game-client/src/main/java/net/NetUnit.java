package net;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.joml.Vector3f;

public class NetUnit {

	private Socket socket;
	private OutputStream os;
	private PrintWriter pw;
	
	private boolean shouldClose = false;
	
	public NetUnit() {
		try {
			socket = new Socket("localhost", 25565);
			os = socket.getOutputStream();
			pw = new PrintWriter(os, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void upload(Vector3f position, Vector3f direction) {
		final var floats = Stream.of(position, direction)
				.flatMap(vec -> Stream.of(vec.x, vec.y, vec.z))
				.collect(Collectors.toList());
		
		var data = "" + floats.get(0);
		floats.remove(0);
		for (var f : floats) {
			data += ", " + f; 
		}
		
		pw.println("[" + data + "]");
		pw.flush();
	}
	
	public void listenForUpdates(Consumer<List<List<Vector3f>>> receiver) {
		new Thread(() -> {
			while(true) {
				if (shouldClose) {
					disconnect();
					break;
				}
				try {
					Scanner scanner = new Scanner(socket.getInputStream());
					if (scanner.hasNextLine()) {
						var line = scanner.nextLine();
						if (line.isEmpty()) {
							receiver.accept(new ArrayList<>());
							continue;
						}
						var players = Stream.of(line.split("\\)\\("))
								.map(p -> p.replace("(", "").replace(")", ""))
								.map(s -> Stream.of(s.split(", "))
										.map(Float::parseFloat)
										.collect(Collectors.toList()))
								.map(fs -> List.of(new Vector3f(fs.get(0), fs.get(1), fs.get(2)), new Vector3f(fs.get(3), fs.get(4), fs.get(5))))
								.collect(Collectors.toList());
						
						receiver.accept(players);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	private void disconnect() {
		pw.println("Disconnect");
		pw.flush();
	}
	
	public void close() {
		shouldClose = true;
	}
	
}
