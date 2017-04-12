package logic;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import static logic.Player.players;

class Worker implements Runnable {
    private final static int BUF_SIZE = 2048;

    private static final byte[] EOL = {(byte) '\r', (byte) '\n'};
    private static final HashMap<String, String> map = new HashMap<>();

    static {
        map.put("", "content/unknown");
        map.put(".htm", "text/html");
        map.put(".html", "text/html");
        map.put(".css", "text/css");
    }

    private final byte[] buf;
    private Socket socket;

    Worker() {
        buf = new byte[BUF_SIZE];
        socket = null;
    }

    synchronized void setSocket(Socket s) {
        this.socket = s;
        notify();
    }

    public synchronized void run() {
        while (true) {
            if (socket == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    continue;
                }
            }
            try {
                handleClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
            socket = null;
        }
    }

    private void handleClient() throws SocketException {
        socket.setSoTimeout(0);
        socket.setTcpNoDelay(true);
        for (int i = 0; i < BUF_SIZE; i++) buf[i] = 0;
        try (        InputStream is = new BufferedInputStream(socket.getInputStream());
                     PrintStream ps = new PrintStream(socket.getOutputStream());){
            int nRead = 0, r;

            outerLoop:
            while (nRead < BUF_SIZE) {
                r = is.read(buf, nRead, BUF_SIZE - nRead);
                if (r == -1) return;
                int i = nRead;
                nRead += r;
                for (; i < nRead; i++)
                    if (buf[i] == (byte) '\n' || buf[i] == (byte) '\r') break outerLoop;
            }
            String str = new String(buf);
            String firstLine = str.substring(0, str.indexOf(" HTTP"));
            synchronized (this) {
                for (String player : players.keySet()) {
                    long ttl = (System.currentTimeMillis() - players.get(player).lastActivity) / 1000;
                    if (ttl > 540) players.remove(player);
                }
            }

            if (firstLine.startsWith("POST")) {
                String params[] = firstLine.substring(6).split(",");
                if (params[0].equalsIgnoreCase("shutdown"))
                    System.exit(0);
                Player player = players.get(params[0]);
                System.out.println(Arrays.toString(params));
                if (player != null) player.lastActivity = System.currentTimeMillis();
                if (player == null && !params[1].equals("register")) {
                    File target = new File(WebServer.root, "index.html");
                    if (printHeaders(target, ps)) sendFile(target, ps);
                    return;
                }
                if (player == null && params[1].equals("register")) {
                    player = new Player(params[0]);
                    players.put(params[0], player);
                }
                ps.write(Utils.getResponse(player, params[1], params).getBytes());
            }
            if (firstLine.startsWith("GET /")) {
                File target = new File(WebServer.root, firstLine.substring(4));
                if (target.isDirectory()) target = new File(WebServer.root, "index.html");
                if (printHeaders(target, ps)) sendFile(target, ps);
                else send404(target, ps);
            }
            ps.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private boolean printHeaders(File target, PrintStream ps) throws IOException {
        boolean ret;
        if (!target.exists()) {
            ps.print("HTTP/1.0 404 not found");
            ps.write(EOL);
            ret = false;
        } else {
            ps.print("HTTP/1.0 200 OK");
            ps.write(EOL);
            ret = true;
        }
        ps.print("Server: Simple Java");
        ps.write(EOL);
        ps.print("Date: " + (new Date()));
        ps.write(EOL);
        if (ret) {
            if (!target.isDirectory()) {
                ps.print("Content-length: " + target.length());
                ps.write(EOL);
                ps.print("Last Modified: " + (new Date(target.lastModified())));
                ps.write(EOL);
                String name = target.getName();
                int ind = name.lastIndexOf('.');
                String ct = null;
                if (ind > 0) ct = map.get(name.substring(ind));
                if (ct == null) ct = "unknown/unknown";
                ps.print("Content-type: " + ct);
                ps.write(EOL);
            } else {
                ps.print("Content-type: text/html");
                ps.write(EOL);
            }
        }
        return ret;
    }

    private void send404(File target, PrintStream ps) throws IOException {
        ps.write(EOL);
        ps.println("Not Found\n\n" + target.toString() + " The requested resource was not found.\n");
    }

    private void sendFile(File target, PrintStream ps) throws IOException {
        if (target.isDirectory()) return;
        ps.write(EOL);
        try (InputStream is =new FileInputStream(target.getAbsolutePath())) {
            int n;
            while ((n = is.read(buf)) > 0) ps.write(buf, 0, n);
        }
    }
}