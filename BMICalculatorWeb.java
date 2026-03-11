import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BMICalculatorWeb {
    private static final DecimalFormat BMI_FORMAT = new DecimalFormat("0.00");

    public static void main(String[] args) throws Exception {
        int port = 8080;
        String host = "0.0.0.0";

        for (int i = 0; i < args.length; i++) {
            String a = args[i];
            if ((a.equals("--port") || a.equals("-p")) && i + 1 < args.length) {
                port = Integer.parseInt(args[++i]);
            } else if ((a.equals("--host") || a.equals("-h")) && i + 1 < args.length) {
                host = args[++i];
            }
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(host, port), 0);

        server.createContext("/", ex -> {
            if (!ex.getRequestMethod().equalsIgnoreCase("GET")) {
                sendText(ex, 405, "Method Not Allowed", "text/plain; charset=utf-8");
                return;
            }
            serveFile(ex, Path.of("web", "index.html"), "text/html; charset=utf-8");
        });

        server.createContext("/app.js", ex -> {
            if (!ex.getRequestMethod().equalsIgnoreCase("GET")) {
                sendText(ex, 405, "Method Not Allowed", "text/plain; charset=utf-8");
                return;
            }
            serveFile(ex, Path.of("web", "app.js"), "text/javascript; charset=utf-8");
        });

        server.createContext("/styles.css", ex -> {
            if (!ex.getRequestMethod().equalsIgnoreCase("GET")) {
                sendText(ex, 405, "Method Not Allowed", "text/plain; charset=utf-8");
                return;
            }
            serveFile(ex, Path.of("web", "styles.css"), "text/css; charset=utf-8");
        });

        server.createContext("/api/bmi", ex -> {
            if (!ex.getRequestMethod().equalsIgnoreCase("GET")) {
                sendText(ex, 405, "Method Not Allowed", "text/plain; charset=utf-8");
                return;
            }

            Map<String, String> q = parseQuery(ex.getRequestURI().getRawQuery());
            String unit = (q.getOrDefault("unit", "")).trim().toLowerCase();

            double height;
            double weight;
            try {
                height = Double.parseDouble(q.getOrDefault("height", ""));
                weight = Double.parseDouble(q.getOrDefault("weight", ""));
            } catch (Exception e) {
                sendJson(ex, 400, "{\"error\":\"Please enter valid numbers for height and weight.\"}");
                return;
            }

            if (!(unit.equals("metric") || unit.equals("us"))) {
                sendJson(ex, 400, "{\"error\":\"Invalid unit. Use metric or us.\"}");
                return;
            }
            if (!(height > 0 && weight > 0)) {
                sendJson(ex, 400, "{\"error\":\"Height and weight must be greater than 0.\"}");
                return;
            }

            double bmi = calculateBmi(unit, height, weight);
            String category = getCategory(bmi);
            String advice = getAdvice(category);

            String json = "{"
                    + "\"bmi\":" + quote(BMI_FORMAT.format(bmi)) + ","
                    + "\"category\":" + quote(category) + ","
                    + "\"advice\":" + quote(advice)
                    + "}";
            sendJson(ex, 200, json);
        });

        server.setExecutor(null);
        server.start();
        System.out.println("BMI web app running.");
        System.out.println("Listening on: " + host + ":" + port);
        System.out.println("Open (same computer): http://localhost:" + port + "/");
        if (host.equals("0.0.0.0") || host.equals("::")) {
            List<String> urls = getLanUrls(port);
            if (!urls.isEmpty()) {
                System.out.println("Open (same Wi-Fi / LAN):");
                for (String url : urls) {
                    System.out.println("  " + url);
                }
            } else {
                System.out.println("LAN URL not detected automatically. Use your computer's local IP, e.g. http://<your-ip>:" + port + "/");
            }
        }
        System.out.println("Stop with Ctrl+C");
    }

    private static List<String> getLanUrls(int port) {
        List<String> out = new ArrayList<>();
        try {
            for (NetworkInterface ni : java.util.Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) continue;
                for (InetAddress addr : java.util.Collections.list(ni.getInetAddresses())) {
                    if (addr.isLoopbackAddress() || addr.isLinkLocalAddress()) continue;
                    String ip = addr.getHostAddress();
                    if (ip == null || ip.isEmpty()) continue;
                    // Remove IPv6 zone id if present (e.g. fe80::1%en0)
                    int pct = ip.indexOf('%');
                    if (pct >= 0) ip = ip.substring(0, pct);
                    if (ip.contains(":")) {
                        out.add("http://[" + ip + "]:" + port + "/");
                    } else {
                        out.add("http://" + ip + ":" + port + "/");
                    }
                }
            }
        } catch (SocketException ignored) {
            return out;
        }
        return out;
    }

    private static void serveFile(HttpExchange ex, Path path, String contentType) throws IOException {
        if (!Files.exists(path)) {
            sendText(ex, 404, "Not Found", "text/plain; charset=utf-8");
            return;
        }
        byte[] bytes = Files.readAllBytes(path);
        Headers headers = ex.getResponseHeaders();
        headers.set("Content-Type", contentType);
        headers.set("Cache-Control", "no-store");
        ex.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void sendText(HttpExchange ex, int status, String body, String contentType) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", contentType);
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void sendJson(HttpExchange ex, int status, String json) throws IOException {
        sendText(ex, status, json, "application/json; charset=utf-8");
    }

    private static Map<String, String> parseQuery(String rawQuery) {
        Map<String, String> out = new HashMap<>();
        if (rawQuery == null || rawQuery.isEmpty()) return out;
        String[] pairs = rawQuery.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            String k = idx >= 0 ? pair.substring(0, idx) : pair;
            String v = idx >= 0 ? pair.substring(idx + 1) : "";
            k = urlDecode(k);
            v = urlDecode(v);
            out.put(k, v);
        }
        return out;
    }

    private static String urlDecode(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

    private static String quote(String s) {
        StringBuilder sb = new StringBuilder();
        sb.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\': sb.append("\\\\"); break;
                case '"': sb.append("\\\""); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
        return sb.toString();
    }

    // Mirrors BMICalculator.calculateBMI() math (without console I/O)
    private static double calculateBmi(String unit, double height, double weight) {
        if (unit.equals("metric")) {
            return weight / Math.pow(height / 100.0, 2);
        }
        return (703.0 * weight) / Math.pow(height, 2);
    }

    // Mirrors BMICalculator.getCategory()
    private static String getCategory(double bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        }
        if (bmi >= 18.5 && bmi < 25) {
            return "Normal weight";
        }
        if (bmi >= 25 && bmi < 30) {
            return "Overweight";
        }
        if (bmi >= 30 && bmi < 35) {
            return "Obesity Class I";
        }
        if (bmi >= 35 && bmi < 40) {
            return "Obesity Class II";
        }
        return "Obesity Class III";
    }

    // Mirrors BMICalculator.getAdvice()
    private static String getAdvice(String category) {
        if (category.equals("Underweight")) {
            return "Your BMI indicates that you are underweight.\n"
                    + "It may suggest insufficient nutrition or low body fat.\n"
                    + "Consider increasing calorie intake and engaging in strength training.\n"
                    + "Consult a healthcare professional if necessary.";
        }
        if (category.equals("Normal weight")) {
            return "Your BMI falls within the normal weight range.\n"
                    + "Maintain a balanced diet and regular physical activity to keep a healthy lifestyle.";
        }
        if (category.equals("Overweight")) {
            return "Your BMI indicates that you are overweight.\n"
                    + "Consider improving diet quality and increasing physical activity.\n"
                    + "Weight management may reduce potential health risks.";
        }
        if (category.equals("Obesity Class I")) {
            return "Your BMI falls into Obesity Class I.\n"
                    + "Lifestyle changes including improved diet and regular exercise are recommended.\n"
                    + "Consider consulting a healthcare professional.";
        }
        if (category.equals("Obesity Class II")) {
            return "Your BMI indicates Obesity Class II.\n"
                    + "There is an increased risk of serious health conditions.\n"
                    + "Professional medical advice is strongly recommended.";
        }
        if (category.equals("Obesity Class III")) {
            return "Your BMI indicates Obesity Class III (severe obesity).\n"
                    + "This level significantly increases the risk of major health problems.\n"
                    + "Immediate medical consultation is recommended.";
        }
        return "";
    }
}
