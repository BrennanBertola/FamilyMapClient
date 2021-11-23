package com.example.familymapclient;

import static org.junit.Assert.*;

import org.junit.Test;

import Request.EventRequest;
import Request.LoginRequest;
import Request.PersonRequest;
import Request.RegisterRequest;
import Result.EventResult;
import Result.LoginResult;
import Result.PersonResult;
import Result.RegisterResult;
import ServerSide.DataCache;
import ServerSide.ServerProxy;

public class ServerTest {

    @Test
    public void fullTest () {
        ServerProxy proxy = new ServerProxy();
        RegisterRequest r = new RegisterRequest("test", "test", "test",
                "test", "Test", "m");
        RegisterResult result = proxy.register(r);
        assertNotNull(result);
        assertTrue(result.getSuccess());

        LoginRequest logRequest = new LoginRequest(r.getUsername(), r.getPassword());
        LoginResult logResult = proxy.login(logRequest);
        assertNotNull(logResult);
        assertTrue(logResult.getSuccess());

        result = proxy.register(r);
        assertNotNull(result);
        assertFalse(result.getSuccess());

        logRequest = new LoginRequest(r.getUsername(), "error");
        logResult = proxy.login(logRequest);
        assertNotNull(logResult);
        assertFalse(logResult.getSuccess());
    }

}
