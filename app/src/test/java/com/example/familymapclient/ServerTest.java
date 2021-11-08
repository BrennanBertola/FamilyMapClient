package com.example.familymapclient;

import org.junit.Test;

import Request.EventRequest;
import Request.LoginRequest;
import Request.PersonRequest;
import Request.RegisterRequest;
import Result.EventResult;
import Result.PersonResult;
import Result.RegisterResult;
import ServerSide.DataCache;
import ServerSide.ServerProxy;

public class ServerTest {
    @Test
    public void login () {
        ServerProxy proxy = new ServerProxy();
        LoginRequest r = new LoginRequest("bman", "test");
        proxy.login(r);
    }

    @Test
    public void register () {
        ServerProxy proxy = new ServerProxy();
        RegisterRequest r = new RegisterRequest("bman", "test", "test",
                "test", "Test", "m");
        proxy.register(r);
    }

    @Test
    public void full () {
        ServerProxy proxy = new ServerProxy();
        RegisterRequest rRequest = new RegisterRequest("bman", "test", "test",
                "test", "Test", "m");
        RegisterResult rResult = proxy.register(rRequest);

        DataCache cache = DataCache.getInstance();

    }

}
