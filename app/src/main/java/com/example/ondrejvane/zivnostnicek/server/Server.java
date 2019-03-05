package com.example.ondrejvane.zivnostnicek.server;

public class Server {

    private static Server self = null;

    private String serverProtocol = "http://";

    private String serverName = "www.zivnostnicek.cz";

    private static final String LOGIN_URL = "/api/login.php";
    private static final String REGISTER_URL = "/api/register.php";
    private static final String PULL_URL = "/api/pull.php";
    private static final String PUSH_URL = "/api/push.php";


    private Server() {
    }

    public synchronized static Server getInstance() {
        if (self == null) {
            self = new Server();
        }
        return self;
    }

    public void setSeverName(String serverName, String serverProtocol) {
        if (self == null) {
            getInstance();
        }
        this.serverName = serverName;
        this.serverProtocol = serverProtocol;
    }

    private String getServerName() {
        return serverProtocol + serverName;
    }

    public String getPushUrl() {
        return getServerName() + PUSH_URL;
    }

    public String getPullUrl() {
        return getServerName() + PULL_URL;
    }

    public String getLoginUrl() {
        return getServerName() + LOGIN_URL;
    }

    public String getRegisterUrl() {
        return getServerName() + REGISTER_URL;
    }


}
