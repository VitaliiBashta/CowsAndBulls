package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static handlers.Parser.parseQuery;


public class EchoGetHandler implements HttpHandler {
    private static final byte[] EOL = {(byte) '\r', (byte) '\n'};
    private final byte[] buf;

    public EchoGetHandler() {
        buf = new byte[2048];
    }

    @Override
    public void handle(HttpExchange he) throws IOException {
        // parse request
        Map<String, Object> parameters = new HashMap<>();
        URI requestedUri = he.getRequestURI();
        String query = requestedUri.getRawQuery();
        parseQuery(query, parameters);
        // send response
        String response = "";
//        for (String key : parameters.keySet())
//            response += key + " = " + parameters.get(key) + "\n";


        OutputStream os = he.getResponseBody();
        response = readFile(requestedUri.getRawPath());
        he.sendResponseHeaders(200, response.length());
        os.write(response.getBytes());
        os.close();
    }

    private String readFile(String target)  {
        File file =new File(new File("wwwRoot"),target);
        if (file.isDirectory()) return "";
        StringBuilder temp = new StringBuilder();
        try (InputStream is =new FileInputStream(file.getAbsolutePath())) {
            int n;
            while ((n = is.read(buf)) > 0)
                temp.append(new String(buf));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp.toString();
    }
}