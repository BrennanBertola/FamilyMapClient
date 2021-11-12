package com.example.familymapclient;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class LoginFragment extends Fragment {

    private Listener listener;
    private boolean signInEnabled;
    private boolean registerEnabled;


    public interface Listener {
        void signIn();
        void register();
    }

    public void registerListener(Listener listener) {
        signInEnabled = false;
        registerEnabled = false;
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);

        setUpKeyListeners(view);

        Button signInButton = view.findViewById(R.id.signInButton);
        Button registerButton = view.findViewById(R.id.registerButton);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.signIn();
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.register();
                }
            }
        });

        return view;
    }

    private void setUpKeyListeners(View view) {
        EditText editText = view.findViewById(R.id.hostField);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                enabledButtonCheck(view);
                Button button = view.findViewById(R.id.signInButton);
                button.setEnabled(signInEnabled);
                button = view.findViewById(R.id.registerButton);
                button.setEnabled(registerEnabled);
            }
        });

        editText = view.findViewById(R.id.portField);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                enabledButtonCheck(view);
                Button button = view.findViewById(R.id.signInButton);
                button.setEnabled(signInEnabled);
                button = view.findViewById(R.id.registerButton);
                button.setEnabled(registerEnabled);
            }
        });

        editText = view.findViewById(R.id.usernameField);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                enabledButtonCheck(view);
                Button button = view.findViewById(R.id.signInButton);
                button.setEnabled(signInEnabled);
                button = view.findViewById(R.id.registerButton);
                button.setEnabled(registerEnabled);
            }
        });

        editText = view.findViewById(R.id.passwordField);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                enabledButtonCheck(view);
                Button button = view.findViewById(R.id.signInButton);
                button.setEnabled(signInEnabled);
                button = view.findViewById(R.id.registerButton);
                button.setEnabled(registerEnabled);
            }
        });

        editText = view.findViewById(R.id.firstNameField);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                enabledButtonCheck(view);
                Button button = view.findViewById(R.id.signInButton);
                button.setEnabled(signInEnabled);
                button = view.findViewById(R.id.registerButton);
                button.setEnabled(registerEnabled);
            }
        });

        editText = view.findViewById(R.id.lastNameField);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                enabledButtonCheck(view);
                Button button = view.findViewById(R.id.signInButton);
                button.setEnabled(signInEnabled);
                button = view.findViewById(R.id.registerButton);
                button.setEnabled(registerEnabled);
            }
        });

        editText = view.findViewById(R.id.emailField);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                enabledButtonCheck(view);
                Button button = view.findViewById(R.id.signInButton);
                button.setEnabled(signInEnabled);
                button = view.findViewById(R.id.registerButton);
                button.setEnabled(registerEnabled);
            }
        });
    }

    private void enabledButtonCheck(View v) {
        signInEnabled = true;
        registerEnabled = true;

        EditText editText = v.findViewById(R.id.hostField);
        String host = editText.getText().toString();
        if (host.equals("")) {
            signInEnabled = false;
            registerEnabled = false;
            return;
        }

        editText = v.findViewById(R.id.portField);
        String port = editText.getText().toString();
        if (port.equals("")) {
            signInEnabled = false;
            registerEnabled = false;
            return;
        }

        editText = v.findViewById(R.id.usernameField);
        String username = editText.getText().toString();
        if (username.equals("")) {
            signInEnabled = false;
            registerEnabled = false;
            return;
        }

        editText = v.findViewById(R.id.passwordField);
        String password = editText.getText().toString();
        if (password.equals("")) {
            signInEnabled = false;
            registerEnabled = false;
            return;
        }

        editText = v.findViewById(R.id.firstNameField);
        String firstName = editText.getText().toString();
        if (firstName.equals("")) {
            registerEnabled = false;
            return;
        }

        editText = v.findViewById(R.id.lastNameField);
        String lastName = editText.getText().toString();
        if (lastName.equals("")) {
            registerEnabled = false;
            return;
        }

        editText = v.findViewById(R.id.emailField);
        String email = editText.getText().toString();
        if (email.equals("")) {
            registerEnabled = false;
            return;
        }
    }
}
