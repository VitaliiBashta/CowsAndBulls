package happylife.demo;

import java.io.IOException;

public class Main {
	private static final int port = 81;
	public static void main(String[] args) throws IOException {
//		// start http server
		SimpleHttpServer httpServer = new SimpleHttpServer();
		httpServer.start(port);
	}
}
