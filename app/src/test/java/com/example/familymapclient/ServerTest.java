package com.example.familymapclient;

import org.junit.Test;

import Request.LoginRequest;
import Request.RegisterRequest;
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

}
