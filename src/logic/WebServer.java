package logic;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

class WebServer {
    private static final List<Worker> threads = new ArrayList<>();
    static File root = new File("wwwRoot");
    private static int port=80;

    public static void main(String[] args) throws IOException {
        if (args.length >0) port= Integer.parseInt(args[0]);
        if (args.length >1) root= new File(args[1]);
        if (!root.exists()) throw new Error(root + " doesn't exist as server root");

        ServerSocket ss = new ServerSocket(port);
        for (int i = 0; i < 10; i++) {
            Worker w = new Worker();
            (new Thread(w, "worker #" + i)).start();
            threads.add(w);
        }
        while (true) {
            Socket s = ss.accept();
            Worker w;
            synchronized (threads) {
                if (threads.isEmpty()) {
                    Worker ws = new Worker();
                    ws.setSocket(s);
                    (new Thread(ws, "additional worker")).start();
                } else {
                    w = threads.get(0);
                    threads.remove(0);
                    w.setSocket(s);
                }
            }
        }
    }
}