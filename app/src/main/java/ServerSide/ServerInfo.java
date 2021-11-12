package ServerSide;

public class ServerInfo {
    private static ServerInfo instance;
    private static String host;
    private static String port;

    public static void setInfo(String givenHost, String givenPort) {
        instance = new ServerInfo(givenHost, givenPort);
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
