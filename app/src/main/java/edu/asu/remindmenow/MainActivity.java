package edu.asu.remindmenow;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Calendar;

import edu.asu.remindmenow.models.User;
import edu.asu.remindmenow.userManager.UserSession;

public class MainActivity extends AppCompatActivity {



    private static String TAG = "MainActivity";

    CallbackManager callbackManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login_fb);

        LoginButton btn = (LoginButton)findViewById(R.id.login_button);
        btn.setReadPermissions("user_friends");
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        System.out.println("On sucess");

                        final AccessToken accessToken = loginResult.getAccessToken();

                        GraphRequestAsyncTask request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject user, GraphResponse graphResponse) {

                                Log.d(TAG, user.optString("email"));
                                Log.d(TAG, user.optString("name"));
                                Log.d(TAG, user.optString("id"));

                                User loggedInuser = new User();
                                loggedInuser.setId(user.optString("id"));
                                loggedInuser.setName(user.optString("name"));
                                UserSession.getInstance().setLoggedInUser(loggedInuser);

                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(intent);


                            }
                        }).executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        System.out.println("On error");
                    }

                });



    }

    public boolean isLoggedIn() {
       return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}