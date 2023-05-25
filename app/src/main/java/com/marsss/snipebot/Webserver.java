package com.marsss.snipebot;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import kotlin.Pair;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
                    escapeJson(SnipeChanBot.config.getPrefix()),
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
                    escapeJson(SnipeChanBot.config.getSnipeDeletedLogsID()),
                    escapeJson(SnipeChanBot.config.getSnipeEditedLogsID()));

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

                SnipeChanBot.config.setSnipeDeletedMessages(Boolean.parseBoolean((String) data.get("snipedeletedmessages")));
                SnipeChanBot.config.setSnipeDeletedFiles(Boolean.parseBoolean((String) data.get("snipedeletedfiles")));
                SnipeChanBot.config.setSnipeEditedMessages(Boolean.parseBoolean((String) data.get("snipeeditedmessages")));
                SnipeChanBot.config.setSnipeEditedFiles(Boolean.parseBoolean((String) data.get("snipeeditedfiles")));
                SnipeChanBot.config.setSendSnipeNotifs(Boolean.parseBoolean((String) data.get("sendsnipenotifs")));
                SnipeChanBot.config.setSnipeNonhumans(Boolean.parseBoolean((String) data.get("snipenonhumans")));
                SnipeChanBot.config.setSnipeMessageManagers(Boolean.parseBoolean((String) data.get("snipemessagemanagers")));
                SnipeChanBot.config.setEnableSnipeCommand(Boolean.parseBoolean((String) data.get("enablesnipecommand")));

                SnipeChanBot.config.setMaxMessageCache(Integer.parseInt((String) data.get("maxmessagecache")));
                SnipeChanBot.config.setMaxSnipedCache(Integer.parseInt((String) data.get("maxsnipecache")));

                SnipeChanBot.config.setSnipeDeletedLogsID((String) data.get("snipedeletedlogsid"));
                SnipeChanBot.config.setSnipeEditedLogsID((String) data.get("snipeeditedlogsid"));

                SnipeChanBot.writeConfigYML();

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
                        "user": "%s",
                        "titles": [%s],
                        "values": [%s],
                        "time": "%s",
                        "files": [%s],
                        "links": [%s],
                        "avatarurl": "%s",
                        "msgid": "%s",
                        "msgurl": "%s"
                    },
                    """;

            StringBuilder data = new StringBuilder();

            data.append("""
                    {
                    "cache":[
                    """);

            for (MessageInfo q : cache) {
                LinkedList<String> titles = new LinkedList<>();
                LinkedList<String> values = new LinkedList<>();

                for (int i = 0; i < 3; i++) {
                    try {
                        MessageEmbed.Field field = q.getEmbed().getFields().get(i);
                        titles.add(convertToHtml(field.getName()));
                        values.add(convertToHtml(field.getValue()));
                    } catch (Exception e) {
                    }
                }

                LinkedList<String> files = new LinkedList<>();
                LinkedList<String> links = new LinkedList<>();
                for (Message.Attachment atts : q.getMessage().getAttachments()) {
                    try {
                        files.add(convertToHtml(atts.getFileName()));
                        links.add(convertToHtml(atts.getUrl()));
                    } catch (Exception e) {
                    }
                }

                String time = q.getEmbed().getFooter().getText();
                time = time.substring(time.indexOf('\n') + 1);
                time = time.replaceAll("\n", "<br>");
                time = time.replaceAll("\u2022", "&bull;");

                data.append(String.format(template,
                        escapeJson(q.getMessage().getAuthor().getAsTag()),
                        convertListToJSON(titles),
                        convertListToJSON(values),
                        escapeJson(time),
                        convertListToJSON(files),
                        convertListToJSON(links),
                        escapeJson(q.getMessage().getAuthor().getAvatarUrl()),
                        escapeJson(q.getMessage().getId()),
                        escapeJson(q.getMessage().getJumpUrl())));
            }

            data.append("]}");

            String response = replaceLast(data.toString(), ",", "");

            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private String convertListToJSON(LinkedList<String> list) {
            String vals = "";
            for (String val : list) {
                vals += "\"" + escapeJson(val) + "\",";
            }
            return replaceLast(vals, ",", "");
        }

        public static String convertToHtml(String markdown) {
            String html = markdown
                    .replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>")
                    .replaceAll("\\*(.*?)\\*", "<em>$1</em>")
                    .replaceAll("__(.*?)__", "<strong>$1</strong>")
                    .replaceAll("_(.*?)_", "<em>$1</em>")
                    .replaceAll("```(.*?)```", "<code-block>$1</code-block>")
                    .replaceAll("`(.*?)`", "<code>$1</code>")
                    .replaceAll("\n", "<br>");

            return html;
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

    public static String escapeJson(String jsonString) {
        StringBuilder escapedJson = new StringBuilder();

        for (int i = 0; i < jsonString.length(); i++) {
            char ch = jsonString.charAt(i);

            switch (ch) {
                case '\"':
                    escapedJson.append("\\\"");
                    break;
                case '\\':
                    escapedJson.append("\\\\");
                    break;
                case '/':
                    escapedJson.append("\\/");
                    break;
                case '\b':
                    escapedJson.append("\\b");
                    break;
                case '\f':
                    escapedJson.append("\\f");
                    break;
                case '\n':
                    escapedJson.append("\\n");
                    break;
                case '\r':
                    escapedJson.append("\\r");
                    break;
                case '\t':
                    escapedJson.append("\\t");
                    break;
                default:
                    if (Character.isISOControl(ch)) {
                        escapedJson.append(String.format("\\u%04X", (int) ch));
                    } else {
                        escapedJson.append(ch);
                    }
            }
        }

        return escapedJson.toString();
    }
}