package io.MCSTORM;


import io.MCSTORM.methods.*;
import io.MCSTORM.utils.RandomUtils;

import java.util.HashMap;


public class Methods {
    public static final HashMap<String, Method> METHODS = new HashMap<>();


    public static Method getByID(int i) {

        return METHODS.getOrDefault(Integer.valueOf(i), (c, p) -> {

            c.close();

            System.err.println("invalid method id: " + i);

        });

    }


    private static void registerMethod(String name, Method m) {

        if (METHODS.containsKey(name)) {
            throw new IllegalStateException("Method with id " + name + " is already existing.");

        }

        METHODS.put(name, m);

    }


    public static void setupMethods() {

        YooniksCry.bert = RandomUtils.randomUTF16String1(600000);

        registerMethod("join", new Join());

        registerMethod("legitjoin", new LegitJoin());

        registerMethod("localhost", new Localhost());

        registerMethod("connect", new Connect());

        registerMethod("invalidnames", new InvalidNames());

        registerMethod("longnames", new LongNames());

        registerMethod("botjoiner", new BotJoiner());

        registerMethod("spoof", new IPSpoofFLood());

        registerMethod("ping", new Ping());

        registerMethod("nullping", new NullPing());

        registerMethod("multikiller", new LoginPingMulticrasher());

        registerMethod("handshake", new BigHandshake());

        registerMethod("bighandshake", new NullPing());

        registerMethod("http", new HTTP());

        registerMethod("query", new QueryFlood());

        registerMethod("bigpacket", new BigPacket());

        registerMethod("network", new Network());

        registerMethod("randombytes", new RandomBytes());

        registerMethod("extremejoin", new ExtremeJoin());

        registerMethod("spamjoin", new SpamJoin());

        registerMethod("nettydowner", new NettyDowner());

        registerMethod("ram", new RAM());

        registerMethod("yoonikscry", new YooniksCry());

        registerMethod("colorcrasher", new ColorCrasher());

        registerMethod("tcphit", new TCPHIT());

        registerMethod("queue", new queue());

        registerMethod("botnet", new BOTNET());

        registerMethod("tcpbypass", new TCPBYPASS());

        registerMethod("ultimatesmasher", new UltimateSmasher());

        registerMethod("sf", new ServerFucker());

        registerMethod("nabcry", new nAntiBotCry());

    }


    public static Method getMethod(String methodID) {

        return METHODS.get(methodID);

    }

}


