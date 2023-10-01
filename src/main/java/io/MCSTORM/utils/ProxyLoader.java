package io.MCSTORM.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Matcher;

public class ProxyLoader {
    public volatile List<Proxy> finals = Collections.synchronizedList(new CopyOnWriteArrayList<>());
    public ExecutorService exe = Executors.newFixedThreadPool(50, Thread::new);

    public ExecutorService executor = Executors.newFixedThreadPool(50, Thread::new);

    public ConcurrentHashMap<Proxy, Long> disabledProxies = new ConcurrentHashMap<>(114514);
    private File file;
    private BufferedReader bufferedReader;
    private volatile int at = 0;
    private long lastUpdate;
    public volatile List<Proxy> workProxies = Collections.synchronizedList(new CopyOnWriteArrayList<>());
    private final String[] ips = new String[]{"http://www.66ip.cn/mo.php?tqsl=9999", "https://www.89ip.cn/tqdl.html?api=1&num=9999", "http://www.kxdaili.com/", "https://www.kuaidaili.com/", "http://proxylist.fatezero.org", "https://ip.ihuan.me/", "http://ip.jiangxianli.com/", "http://www.ip3366.net/", "https://api.proxyscrape.com/?request=getproxies&proxytype=http&timeout=10000&country=all&ssl=all&anonymity=all", "https://www.proxy-list.download/api/v1/get?type=http", "https://www.proxy-list.download/api/v1/get?type=https", "https://www.proxy-list.download/api/v1/get?type=socks4", "https://www.proxy-list.download/api/v1/get?type=socks5", "https://shieldcommunity.net/sockets.txt", "https://raw.githubusercontent.com/TheSpeedX/PROXY-List/master/http.txt", "https://raw.githubusercontent.com/TheSpeedX/PROXY-List/master/socks4.txt", "https://raw.githubusercontent.com/TheSpeedX/PROXY-List/master/socks5.txt", "https://raw.githubusercontent.com/ShiftyTR/Proxy-List/master/http.txt", "https://raw.githubusercontent.com/ShiftyTR/Proxy-List/master/https.txt", "https://raw.githubusercontent.com/ShiftyTR/Proxy-List/master/socks4.txt", "https://raw.githubusercontent.com/ShiftyTR/Proxy-List/master/socks5.txt", "https://raw.githubusercontent.com/monosans/proxy-list/main/proxies/http.txt", "https://raw.githubusercontent.com/monosans/proxy-list/main/proxies/socks4.txt", "https://raw.githubusercontent.com/monosans/proxy-list/main/proxies/socks5.txt", "https://raw.githubusercontent.com/jetkai/proxy-list/main/online-proxies/txt/proxies-http.txt", "https://raw.githubusercontent.com/jetkai/proxy-list/main/online-proxies/txt/proxies-https.txt", "https://raw.githubusercontent.com/jetkai/proxy-list/main/online-proxies/txt/proxies-socks4.txt", "https://raw.githubusercontent.com/jetkai/proxy-list/main/online-proxies/txt/proxies-socks5.txt", "https://raw.githubusercontent.com/rdavydov/proxy-list/main/proxies/http.txt", "https://raw.githubusercontent.com/rdavydov/proxy-list/main/proxies/socks4.txt", "https://raw.githubusercontent.com/rdavydov/proxy-list/main/proxies/socks5.txt", "https://raw.githubusercontent.com/clarketm/proxy-list/master/proxy-list-raw.txt", "https://raw.githubusercontent.com/mertguvencli/http-proxy-list/main/proxy-list/data.txt", "https://raw.githubusercontent.com/scriptzteam/ProtonVPN-VPN-IPs/main/exit_ips.txt", "https://raw.githubusercontent.com/scriptzteam/ProtonVPN-VPN-IPs/main/entry_ips.txt", "https://raw.githubusercontent.com/mmpx12/proxy-list/master/http.txt", "https://raw.githubusercontent.com/mmpx12/proxy-list/master/https.txt", "https://raw.githubusercontent.com/mmpx12/proxy-list/master/socks4.txt", "https://raw.githubusercontent.com/mmpx12/proxy-list/master/socks5.txt"};

    private String proxiesText = "";
    private final File workProxiesPath = new File("working_proxies.txt");

    public ProxyLoader(File file) throws IOException {
        if (file == null) {
            return;
        }
        this.file = file;

        if (!workProxiesPath.exists()) workProxiesPath.createNewFile();

        loadFile();
    }

    public ProxyLoader(BufferedReader in) {
        this.bufferedReader = in;
        loadBuffer();
    }

    private void loadFile() {
        try {
            List<String> lines = Files.readAllLines(this.workProxiesPath.toPath());
            for (Iterator<String> iterator = lines.iterator(); iterator.hasNext(); ) {
                String s = iterator.next();
                this.exe.execute(() -> {
                    try {
                        String[] split = s.split(":", 2);
                        this.workProxies.add(new Proxy(new InetSocketAddress(split[0], Integer.parseInt(split[1]))));
                        this.proxiesText+=s+"\n";
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return;
        }

        try {
            List<String> lines = Files.readAllLines(this.file.toPath());
            for (Iterator<String> iterator = lines.iterator(); iterator.hasNext(); ) {
                String s = iterator.next();
                this.exe.execute(() -> {
                    try {
                        String[] split = s.split(":", 2);
                        this.finals.add(new Proxy(new InetSocketAddress(split[0], Integer.parseInt(split[1]))));
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return;
        }

        for (String url : ips) {
            this.exe.execute(() -> {
                Runnable task = () -> {
                    List<Proxy> proxyList = new CopyOnWriteArrayList<>();
                    String ips = HTTPUtil.sendGet(url);
                    Matcher matcher = OtherUtils.matches(ips, "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\:\\d{1,5}");
                    while (matcher.find()) {
                        String ip = matcher.group();
                        String[] split = ip.split(":", 2);
                        Proxy p = new Proxy(new InetSocketAddress(split[0], Integer.parseInt(split[1])));
                        if (!finals.contains(p)) {
                            proxyList.add(p);
                        }
                    }
                    System.out.println("IP: " + url + " loaded " + proxyList.size() + " proxies.");
                    this.finals.addAll(proxyList);
                };

                if (url.equals("https://www.89ip.cn/tqdl.html?api=1&num=9999")) {
                    for (int i = 0; i < 10; i++) {
                        task.run();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    task.run();
                }
            });
        }

        this.exe.shutdown();
        try {
            this.exe.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(this.finals.size() + " proxies loaded.");
        lastUpdate = System.currentTimeMillis();
    }

    private void loadBuffer() {
        try {
            String inputLine;
            while ((inputLine = this.bufferedReader.readLine()) != null) {
                String[] split = inputLine.split(":", 2);
                this.finals.add(new Proxy(new InetSocketAddress(split[0], Integer.parseInt(split[1]))));
            }
            this.bufferedReader.close();
        } catch (Throwable e) {
            e.printStackTrace();
            return;
        }
        this.exe.shutdown();
        try {
            this.exe.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(this.finals.size() + " proxies loaded.");
    }

    public void saveWorkProxy(Proxy proxy) {
        if (!this.workProxies.contains(proxy)) {
            this.workProxies.add(proxy);
            this.proxiesText += proxy.addrs.getAddress().getHostAddress() + ":" + proxy.addrs.getPort() + "\n";
            executor.execute(() -> {
                try {
                    Files.writeString(workProxiesPath.toPath(), proxiesText);
                } catch (IOException e) {
                    return;
                }
            });
        }
    }

    public Proxy getProxy() {
        if ((System.currentTimeMillis() - lastUpdate) > 5 * 60 * 1000) {
            loadFile();
            System.out.println("[Proxy] Updated proxy in " + this.lastUpdate + " ms.");
        }

        int get;
        if ((get = this.at++) > this.finals.size() - 1) {
            get = 0;
            this.at = 1;
        }
        if (NettyBootstrap.disableFailedProxies) {
            return this.finals.get(get);
        }
        Proxy proxie = this.finals.get(get);
        Long time = this.disabledProxies.get(proxie);
        if (time != null) {
            if (System.currentTimeMillis() > time.longValue() + 10000L) {
                proxie = getProxy();
            } else {
                this.disabledProxies.remove(proxie);
            }
        }
        return proxie;
    }

    public static class Proxy {
        public final InetSocketAddress addrs;

        public Proxy(InetSocketAddress addrs) {
            this.addrs = addrs;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Proxy proxy = (Proxy) o;
            return addrs != null ? addrs.equals(proxy.addrs) : proxy.addrs == null;
        }

        @Override
        public int hashCode() {
            return addrs != null ? addrs.hashCode() : 0;
        }
    }
}