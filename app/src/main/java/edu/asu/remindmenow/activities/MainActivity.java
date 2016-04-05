package edu.asu.remindmenow.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
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

import edu.asu.remindmenow.R;
import edu.asu.remindmenow.exception.ApplicationRuntimeException;
import edu.asu.remindmenow.models.Message;
import edu.asu.remindmenow.models.User;
import edu.asu.remindmenow.userManager.UserSession;
import edu.asu.remindmenow.util.DBConnection;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    CallbackManager callbackManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            DBConnection.getInstance().setContext(getApplicationContext());

        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {

                if (newAccessToken != null && newAccessToken.isExpired() == false) {
                    Log.i(TAG, "On current access token " + oldAccessToken + " " + newAccessToken);
                    fetchUserDetails(newAccessToken);
                }

            }
        };
        setContentView(R.layout.activity_login_fb);

        LoginButton btn = (LoginButton)findViewById(R.id.login_button);
        btn.setReadPermissions("user_friends");


        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException exception) {

                    }

                });

    }

    private void fetchUserDetails(AccessToken token) {
        final AccessToken accessToken = token;

        GraphRequestAsyncTask request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject user, GraphResponse graphResponse) {

                Log.d(TAG, user.optString("Fetch UserDetails"));
                Log.d(TAG, user.optString("email"));
                Log.d(TAG, user.optString("name"));
                Log.d(TAG, user.optString("id"));

                User loggedInuser = new User();
                loggedInuser.setId(user.optString("id"));
                loggedInuser.setName(user.optString("name"));
                try {
                    DBConnection.getInstance().insertContact(loggedInuser);
                } catch (ApplicationRuntimeException ex) {
                    ex.printStackTrace();
                    Message m=ex.getErrorMessage();
                    Log.i(TAG,m.getDescription() );
                }
                UserSession.getInstance().setLoggedInUser(loggedInuser);

                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();


            }
        }).executeAsync();
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