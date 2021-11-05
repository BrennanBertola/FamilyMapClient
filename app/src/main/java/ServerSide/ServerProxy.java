package ServerSide;

import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import Request.EventRequest;
import Request.LoginRequest;
import Request.PersonRequest;
import Request.RegisterRequest;
import Result.EventResult;
import Result.LoginResult;
import Result.PersonResult;
import Result.RegisterResult;

public class ServerProxy {

    public LoginResult login(LoginRequest request) {
        LoginResult result = null;

        try {
            ServerInfo serverInfo = ServerInfo.getInstance();
            URL url = new URL("http://" + serverInfo.getHost() + ":" +
                    serverInfo.getPort() + "/user/login");

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);

            Gson gson = new Gson();
            StringBuilder reqData = new StringBuilder();
            gson.toJson(request, reqData);

            OutputStream reqBody = http.getOutputStream();
            writeString(reqData.toString(), reqBody);
            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData= readString(respBody);

                result = (LoginResult) gson.fromJson(respData, LoginResult.class);
            }
            else {
                InputStream respBody = http.getInputStream();
                String respData= readString(respBody);

                result = (LoginResult) gson.fromJson(respData, LoginResult.class);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public RegisterResult register(RegisterRequest request) {
        RegisterResult result = null;

        try {
            ServerInfo serverInfo = ServerInfo.getInstance();
            URL url = new URL("http://" + serverInfo.getHost() + ":" +
                    serverInfo.getPort() + "/user/register");

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);

            Gson gson = new Gson();
            StringBuilder reqData = new StringBuilder();
            gson.toJson(request, reqData);

            OutputStream reqBody = http.getOutputStream();
            writeString(reqData.toString(), reqBody);
            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData= readString(respBody);

                result = (RegisterResult) gson.fromJson(respData, RegisterResult.class);
            }
            else {
                InputStream respBody = http.getInputStream();
                String respData= readString(respBody);

                result = (RegisterResult) gson.fromJson(respData, RegisterResult.class);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public PersonResult getPeople(PersonRequest request) {return null;}

    public EventResult getEvents(EventRequest request) {return null;}

    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }


    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}
