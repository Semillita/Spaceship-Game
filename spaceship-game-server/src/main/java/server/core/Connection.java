package server.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Optional;
import java.util.Scanner;

public class Connection extends Thread {

	private final Socket socket;

	public Connection(Socket socket) {
		this.socket = socket;
		
		super.start();
	}
	
	@Override
	public void run() {
		// Listen for messages
		try {
			var inputStream = socket.getInputStream();
			var scanner = new Scanner(inputStream);
			
			while (true) {
				if (scanner.hasNext("\\[.+\\]")) {
					var message = scanner.next("\\[.+\\]");
					System.out.println("Message from connection: " + message);
//					System.out.println("Delimiter: " + scanner.delimiter());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void receive(String message) {
		
	}

}
