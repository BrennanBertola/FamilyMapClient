package ServerSide;

public class ServerInfo {
    private static ServerInfo instance;
    private static String host;
    private static String port;

    public static ServerInfo getInstance(String givenHost, String givenPort) {
        if (instance == null) {
            instance = new ServerInfo(givenHost, givenPort);
        }
        else if (!host.equals(givenHost) || !port.equals(givenPort)){
            instance = new ServerInfo(givenHost, givenPort);
        }
        return instance;
    }

    public static ServerInfo getInstance() {
        if(instance == null) {
            instance = new ServerInfo();
        }
        return instance;
    }

    //Does it's best using default if need be.
    private ServerInfo () {
        host = "localhost";
        port = "8080";
    }

    private ServerInfo (String givenHost, String givenPort) {
        host = givenHost;
        port = givenPort;
    }

    public static String getHost() {
        return host;
    }

    public static String getPort() {
        return port;
    }
}
