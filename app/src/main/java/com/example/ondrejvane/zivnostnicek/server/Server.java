package com.example.ondrejvane.zivnostnicek.server;

public class Server {

    private static final String SEVER_NAME = "http://zivnostnicek.cz";

    private static final String PULL_URL = "/api/pull.php";
    private static final String PUSH_URL = "/api/push.php";

    public static String getSeverName() {
        return SEVER_NAME;
    }

    public static String getPushUrl(){
        return SEVER_NAME + PUSH_URL;
    }

    public static String getPullUrl(){
        return SEVER_NAME + PULL_URL;
    }


}
