package com.example.ondrejvane.zivnostnicek.server;

import android.app.Activity;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Server {

    private static Server self = null;

    private String serverProtocol = "http://";
    private int serverProtocolId = 0;

    private String serverName = "zivnostnicek.000webhostapp.com";

    private static final String LOGIN_URL = "/api/login.php";
    private static final String REGISTER_URL = "/api/register.php";
    private static final String PULL_URL = "/api/pull.php";
    private static final String PUSH_URL = "/api/push.php";

    //klíče pro uložení do SP pro pozdější použití
    private static final String SERVER_NAME_SP = "ServerSettings";
    private static final String SERVER_PROTOCOL_KEY = "server_protocol";
    private static final String SERVER_NAME_KEY = "server_name";

    //pole možných protokolů
    private static final String[] PROTOCOLS_ARRAY = {"http://", "https://"};


    private Server() {
    }

    public synchronized static Server getInstance() {
        if (self == null) {
            self = new Server();
        }
        return self;
    }

    public void setServerNameAndProtocol(String serverName, int serverProtocol) {
        if (self == null) {
            getInstance();
        }
        this.serverName = removeWWWFromServerName(serverName);
        this.serverProtocol = PROTOCOLS_ARRAY[serverProtocol];
        this.serverProtocolId = serverProtocol;
    }

    public String getServerName(){
        return serverName;
    }

    public String getServerNameAndProtocol() {
        return serverProtocol + serverName;
    }

    public String getPushUrl() {
        return getServerNameAndProtocol() + PUSH_URL;
    }

    public String getPullUrl() {
        return getServerNameAndProtocol() + PULL_URL;
    }

    public String getLoginUrl() {
        return getServerNameAndProtocol() + LOGIN_URL;
    }

    public String getRegisterUrl() {
        return getServerNameAndProtocol() + REGISTER_URL;
    }

    public int getServerProtocolId(){
        return serverProtocolId;
    }

    public void readDataFromSharedPref(Activity activity){
        SharedPreferences sp = activity.getSharedPreferences(SERVER_NAME_SP, MODE_PRIVATE);

        //pokus o načtení hosnot ze SP
        serverProtocolId = sp.getInt(SERVER_PROTOCOL_KEY , 0);
        serverName = sp.getString(SERVER_NAME_KEY, serverName);
        serverProtocol = PROTOCOLS_ARRAY[serverProtocolId];

    }

    public void saveDataToSharedPref(Activity activity){
        SharedPreferences sp = activity.getSharedPreferences(SERVER_NAME_SP, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(SERVER_PROTOCOL_KEY, serverProtocolId);
        editor.putString(SERVER_NAME_KEY, serverName);
        editor.apply();
    }

    private String removeWWWFromServerName(String address){
        if(address.contains("www.")){
            return address.substring(4);
        }

        return address;
    }


}
