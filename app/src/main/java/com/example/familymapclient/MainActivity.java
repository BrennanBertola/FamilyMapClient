package com.example.familymapclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.*;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Model.Person;
import Request.LoginRequest;
import Request.RegisterRequest;
import Result.LoginResult;
import Result.RegisterResult;
import ServerSide.DataCache;
import ServerSide.ServerInfo;
import ServerSide.ServerProxy;



public class MainActivity extends AppCompatActivity implements LoginFragment.Listener, MapFragment.Listener {
    private static final String LOGIN_KEY = "login";
    private static final String REGISTER_KEY = "register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Family Map");

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = createLoginFragment();
        fragmentManager.beginTransaction()
                .add(R.id.fragmentFrameLayout, fragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DataCache cache = DataCache.getInstance();
        if (cache == null) return;
        if(!cache.isLoggedIn()) {
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            Fragment fragment = createLoginFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentFrameLayout, fragment)
                    .commit();
        }
    }

    private Fragment createLoginFragment() {
        LoginFragment fragment = new LoginFragment();
        fragment.registerListener(this);
        return fragment;
    }

    private Fragment createMapFragment() {
        MapFragment fragment = new MapFragment();
        fragment.registerListener(this);
        return fragment;
    }

    @Override
    public void switchToPerson(Person person) {
        Intent intent = new Intent(MainActivity.this, PersonActivity.class);
        intent.putExtra(PersonActivity.PERSON_KEY, person.getPersonID());
        startActivity(intent);
    }

    @Override
    public void switchToSearch() {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    @Override
    public void switchToSettings() {
        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    @Override
    public void signIn() {
        serverSetUp();

        //gets info for login request
        EditText editText = findViewById(R.id.usernameField);
        String username = editText.getText().toString();
        editText = findViewById(R.id.passwordField);
        String password = editText.getText().toString();

        @SuppressLint("HandlerLeak") Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                boolean success = bundle.getBoolean(LOGIN_KEY, false);
                String toast;

                if (success) {
                    FragmentManager fragManager = getSupportFragmentManager();
                    Fragment frag = createMapFragment();
                    fragManager.beginTransaction()
                            .replace(R.id.fragmentFrameLayout, frag)
                            .commit();
                }
                else {
                    toast = "Login Failed";
                    Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_LONG).show();
                }


            }
        };

        LoginRequest request = new LoginRequest(username, password);
        SignInTask task = new SignInTask(handler, request);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(task);
    }

    @Override
    public void register() {
        serverSetUp();

        //gets information for register request
        EditText editText = findViewById(R.id.usernameField);
        String username = editText.getText().toString();
        editText = findViewById(R.id.passwordField);
        String password = editText.getText().toString();
        editText = findViewById(R.id.firstNameField);
        String firstName = editText.getText().toString();
        editText = findViewById(R.id.lastNameField);
        String lastName = editText.getText().toString();
        editText = findViewById(R.id.emailField);
        String email = editText.getText().toString();

        RadioGroup radioGroup = findViewById(R.id.gender);
        int selected = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selected);
        String gender = "f";
        if (radioButton.getText().toString().equals("Male")) {
            gender = "m";
        }

        @SuppressLint("HandlerLeak") Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                boolean success = bundle.getBoolean(REGISTER_KEY, false);
                String toast;

                if (success) {
                    FragmentManager fragManager = getSupportFragmentManager();
                    Fragment frag = createMapFragment();
                    fragManager.beginTransaction()
                            .replace(R.id.fragmentFrameLayout, frag)
                            .commit();
                }
                else {
                    toast = "Register Failed";
                    Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_LONG).show();
                }


            }
        };

        RegisterRequest request = new RegisterRequest(username, password, email, firstName,
                lastName, gender);
        RegisterTask task = new RegisterTask(handler, request);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(task);
    }

    private void serverSetUp() {
        EditText editText = findViewById(R.id.hostField);
        String host = editText.getText().toString();

        editText = findViewById(R.id.portField);
        String port = editText.getText().toString();

        ServerInfo.setInfo(host, port);
    }

    /*=================== Background Tasks ==================*/

    private class SignInTask implements Runnable {
        LoginRequest request;
        private final Handler handler;


        public SignInTask(Handler handler, LoginRequest request) {
            this.request = request;
            this.handler = handler;
        }

        @Override
        public void run() {
            ServerProxy proxy = new ServerProxy();
            LoginResult result = proxy.login(request);
            if (result.getSuccess()) {
                DataCache.createCache(result.getAuthtoken(), result.getPersonID());
            }

            Message msg = Message.obtain();
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(LOGIN_KEY, result.getSuccess());
            msg.setData(msgBundle);
            handler.sendMessage(msg);
        }
    }

    private class RegisterTask implements Runnable {
        RegisterRequest request;
        private final Handler handler;

        public RegisterTask(Handler handler, RegisterRequest request) {
            this.request = request;
            this.handler = handler;
        }

        @Override
        public void run() {
            ServerProxy proxy = new ServerProxy();
            RegisterResult result = proxy.register(request);
            if (result.getSuccess()) {
                DataCache.createCache(result.getAuthtoken(), result.getPersonID());
            }

            Message msg = Message.obtain();
            Bundle msgBundle = new Bundle();
            msgBundle.putBoolean(REGISTER_KEY, result.getSuccess());
            msg.setData(msgBundle);
            handler.sendMessage(msg);
        }
    }

    /*================= Map Stuff ===================*/

}