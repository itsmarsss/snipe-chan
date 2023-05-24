package com.marsss.snipebot;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;

public class Webserver {

    private int port;
    private HttpServer server;
    private String html;
    private String js;
    private String css;

    public int getPort() {
        return port;
    }

    public void terminate() {
        server.stop(0);
    }

    public void startServer() throws Exception {
        server = HttpServer.create(new InetSocketAddress("0.0.0.0", 0), 0);
        server.createContext("/", new MainPage());
        server.createContext("/index.js", new JS());
        server.createContext("/index.css", new CSS());
        server.createContext("/api/v1/getconfig", new GetConfig());
        server.createContext("/api/v1/setconfig", new SetConfig());
        server.createContext("/api/v1/getsnipelist", new GetSnipeList());
        server.createContext("/api/v1/delete", new Delete());
        server.setExecutor(null);
        server.start();
        port = server.getAddress().getPort();

        html = loadFile("webassets/index.html").replace("0.0.0", SnipeChanBot.getVersion());
        js = loadFile("webassets/index.js");
        css = loadFile("webassets/index.css");

        System.out.println("Finished reading files.");
    }

    private String loadFile(String path) {
        InputStream inputStream = Webserver.class.getClassLoader().getResourceAsStream(path);

        if (inputStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                System.out.println("File found: " + path);

                StringBuilder file = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    file.append(line);
                    file.append("\n");
                }

                System.out.println("File loaded: " + path);

                return file.toString();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Encountered an error reading: " + path);
            }
        } else {
            System.out.println("File not found: " + path);
        }

        return "";
    }

    private class MainPage implements HttpHandler {
        @Override
        public void handle(HttpExchange he) throws IOException {
            System.out.println("Main page queried");

            String response = html;
            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private class JS implements HttpHandler {
        @Override
        public void handle(HttpExchange he) throws IOException {
            String response = js;
            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private class CSS implements HttpHandler {
        @Override
        public void handle(HttpExchange he) throws IOException {
            String response = css;
            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private static class GetConfig implements HttpHandler {
        @Override
        public void handle(HttpExchange he) throws IOException {
            System.out.println("Config queried");

            String response = String.format("""
                            {
                                "prefix": "%s",
                                "snipedeletedmessages": "%s",
                                "snipedeletedfiles": "%s",
                                "snipeeditedmessages": "%s",
                                "snipeeditedfiles": "%s",
                                "sendsnipenotifs": "%s",
                                "snipenonhumans": "%s",
                                "snipemessagemanagers": "%s",
                                "enablesnipecommand": "%s",
                                
                                "maxmessagecache": "%s",
                                "maxsnipecache": "%s",
                                
                                "snipedeletedlogsid": "%s",
                                "snipeeditedlogsid": "%s"
                            }
                            """,
                    SnipeChanBot.config.getPrefix(),
                    SnipeChanBot.config.isSnipeDeletedMessages(),
                    SnipeChanBot.config.isSnipeDeletedFiles(),
                    SnipeChanBot.config.isSnipeEditedMessages(),
                    SnipeChanBot.config.isSnipeEditedFiles(),
                    SnipeChanBot.config.isSendSnipeNotifs(),
                    SnipeChanBot.config.isSnipeNonhumans(),
                    SnipeChanBot.config.isSnipeMessageManagers(),
                    SnipeChanBot.config.isEnableSnipeCommand(),
                    SnipeChanBot.config.getMaxMessageCache(),
                    SnipeChanBot.config.getMaxSnipedCache(),
                    SnipeChanBot.config.getSnipeDeletedLogsID(),
                    SnipeChanBot.config.getSnipeEditedLogsID());

            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private class SetConfig implements HttpHandler {
        @Override
        public void handle(HttpExchange he) throws IOException {
            String body = readRequestBody(he.getRequestBody());

            JSONParser parser = new JSONParser();
            JSONObject data;
            try {
                data = (JSONObject) parser.parse(body);
                System.out.println();
                System.out.println("Snipe Bot config has been updated:");
                System.out.println("\t" + body);

                SnipeChanBot.config.setPrefix((String) data.get("prefix"));
                SnipeChanBot.config.setSnipeDeletedMessages((boolean) data.get("snipedeletedmessages"));
                SnipeChanBot.config.setSnipeDeletedFiles((boolean) data.get("snipedeletedfiles"));
                SnipeChanBot.config.setSnipeEditedMessages((boolean) data.get("snipeeditedmessages"));
                SnipeChanBot.config.setSnipeEditedFiles((boolean) data.get("snipeeditedfiles"));
                SnipeChanBot.config.setSendSnipeNotifs((boolean) data.get("sendsnipenotifs"));
                SnipeChanBot.config.setSnipeNonhumans((boolean) data.get("snipenonhumans"));
                SnipeChanBot.config.setSnipeMessageManagers((boolean) data.get("snipemessagemanagers"));
                SnipeChanBot.config.setEnableSnipeCommand((boolean) data.get("enablesnipecommand"));
                SnipeChanBot.config.setMaxMessageCache((int) data.get("maxmessagecache"));
                SnipeChanBot.config.setMaxSnipedCache((int) data.get("maxsnipecache"));
                SnipeChanBot.config.setSnipeDeletedLogsID((String) data.get("snipedeletedlogsid"));
                SnipeChanBot.config.setSnipeEditedLogsID((String) data.get("snipeeditedlogsid"));

                String response = "Success";
                he.sendResponseHeaders(200, response.length());
                OutputStream os = he.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (ParseException e) {
                e.printStackTrace();
                System.out.println("Unable to read POST (GET) JSON");

                String response = "Failed";
                he.sendResponseHeaders(200, response.length());
                OutputStream os = he.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    private static class GetSnipeList implements HttpHandler {
        @Override
        public void handle(HttpExchange he) throws IOException {
            System.out.println("Cache queried");

            ArrayList<MessageInfo> cache = SnipeChanBot.snipedCache;

            final String template = """

                    {
                        "type": "%s",
                        "from": "%s",
                        "to": "%s",
                        "user": "%s",
                        "avatarurl": "%s",
                        "msgid": "%s"
                    },
                    """;

            StringBuilder data = new StringBuilder();

            data.append("""
                    {
                    "cache":[
                    """);

            for (MessageInfo q : cache) {
                data.append(String.format(template,
                        q.getMessage().getId(),
                        q.getEmbed().getFields().get(0).getValue(),
                        q.getMessage().getContentRaw(),
                        q.getMessage().getAuthor().getAsTag(),
                        q.getMessage().getAuthor().getAvatarUrl(),
                        q.getMessage().getId()));
            }

            data.append("]}");

            String response = replaceLast(data.toString(), ",", "");
            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private class Delete implements HttpHandler {
        @Override
        public void handle(HttpExchange he) throws IOException {
            String body = readRequestBody(he.getRequestBody());

            JSONParser parser = new JSONParser();
            JSONObject data;
            try {
                data = (JSONObject) parser.parse(body);
                System.out.println();
                System.out.println("Snipe Bot post has been requested to be deleted:");

                String msgid = (String) data.get("msgid");

                System.out.println("\t" + msgid);

                SnipeChanBot.remove(msgid);

                String response = "Success";

                he.sendResponseHeaders(200, response.length());
                OutputStream os = he.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (ParseException e) {
                e.printStackTrace();
                System.out.println("Unable to read POST (GET) JSON");

                String response = "Failed";
                he.sendResponseHeaders(200, response.length());
                OutputStream os = he.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }

        }
    }

    private String readRequestBody(InputStream requestBodyStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(requestBodyStream));
        StringBuilder requestBodyBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBodyBuilder.append(line);
        }
        reader.close();
        return requestBodyBuilder.toString();
    }

    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ")", replacement);
    }
}