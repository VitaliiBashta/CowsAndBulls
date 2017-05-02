package logic;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URI;
import java.util.Arrays;

import static logic.Player.players;

class DynamicHandler implements HttpHandler {
    private final byte[] buf = new byte[16 * 1024];


    @Override
    public void handle(HttpExchange he) throws IOException {
        URI requestedUri = he.getRequestURI();

        OutputStream os = he.getResponseBody();
        synchronized (this) {
            for (String player : players.keySet()) {
                long ttl = (System.currentTimeMillis() - players.get(player).lastActivity) / 1000;
                if (ttl > 540) players.remove(player);
            }
        }
        if (he.getRequestMethod().equals("GET")) {
            System.out.println("uri>" + requestedUri);
            System.out.println("query=" + he.getRequestURI().getQuery());
            if (requestedUri.getPath().equals("/"))
                sendFile("index.html", he);
            else
                sendFile(requestedUri.getRawPath(), he);
        }

        if (he.getRequestMethod().equals("POST")) {
            BufferedReader br = new BufferedReader(new InputStreamReader(he.getRequestBody(), "utf-8"));
            String query = br.readLine();
            String params[] = query.split("[,]");
            if (params[0].equalsIgnoreCase("shutdown")) System.exit(0);
            Player player = players.get(params[0]);
            System.out.println(Arrays.toString(params));
            if (player != null) player.lastActivity = System.currentTimeMillis();
            if (player == null && !params[1].equals("register")) return;
            if (player == null && params[1].equals("register")) {
                player = new Player(params[0]);
                players.put(params[0], player);
            }
            String response = Utils.getResponse(player, params[1], params);
            if (!response.equals("")) {
                he.sendResponseHeaders(200, response.length());
                os.write(response.getBytes());
            }
            os.flush();
        }
        os.close();
    }

    private void sendFile(String target, HttpExchange he) throws IOException {
        File file = new File(new File("wwwRoot"), target);
        if (file.exists()) {
            OutputStream os = he.getResponseBody();
            he.sendResponseHeaders(200, file.length());
            int n;
            try (InputStream is = new FileInputStream(file.getAbsolutePath())) {
                while ((n = is.read(buf)) > 0)
                    os.write(buf, 0, n);
            }
        }
    }
}