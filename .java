package com.yourappname.facebook;
 
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
 
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
 
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
 
import org.json.JSONException;
import org.json.JSONObject;
 
@SuppressWarnings("deprecation")
public class AndroidExampleFacebookActivity extends Activity {
 
    private static String APP_ID = "912301902217993"; 
 
    private AsyncFacebookRunner fbAsyncRunner;
    private SharedPreferences fbPrefs;
     
    private Facebook facebook = new Facebook(APP_ID);
     
     
    Button fbLoginButton;
    Button fbMyProfileButton;
    Button fbFriendButton;
    Button fbPostToWallButton;
     
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
 
        fbLoginButton = (Button) findViewById(R.id.login_btn);
        fbMyProfileButton = (Button) findViewById(R.id.profile_btn);
        fbPostToWallButton = (Button) findViewById(R.id.wall_btn);
        fbFriendButton = (Button) findViewById(R.id.friend_btn);
        fbAsyncRunner = new AsyncFacebookRunner(facebook);
 
        fbLoginButton.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View v) {
                 
                facebookLogin();
                 
            }
        });
 
         
        fbMyProfileButton.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View v) {
                getFacebookProfileInfo();
            }
        });
 
        fbPostToWallButton.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View v) {
                 
                facebookWallPost();
                 
            }
        });
 
        fbFriendButton.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View v) {
                 
                getFacebookFriends();
                 
            }
        });
    }
 
    public void facebookLogin() {
         
         
        fbPrefs = getPreferences(MODE_PRIVATE);
        String access_token = fbPrefs.getString("access_token", null);
         
        long expires = fbPrefs.getLong("access_expires", 0);
 
        if (access_token != null) {
            facebook.setAccessToken(access_token);
             
            fbLoginButton.setVisibility(View.INVISIBLE);
             
            fbMyProfileButton.setVisibility(View.VISIBLE);
            fbPostToWallButton.setVisibility(View.VISIBLE);
            fbFriendButton.setVisibility(View.VISIBLE);
        }
 
        if (expires != 0) {
            facebook.setAccessExpires(expires);
        }
 
        if (!facebook.isSessionValid()) {
            facebook.authorize(this,
                    new String[] { "email", "publish_stream","user_friends" },
                    new DialogListener() {
 
                        @Override
                        public void onCancel() {
                        }
 
                        @Override
                        public void onComplete(Bundle values) {
                             
                            SharedPreferences.Editor editor = fbPrefs.edit();
                            editor.putString("access_token",
                                    facebook.getAccessToken());
                            editor.putLong("access_expires",
                                    facebook.getAccessExpires());
                            editor.commit();
 
                            fbLoginButton.setVisibility(View.INVISIBLE);
 
                            fbMyProfileButton.setVisibility(View.VISIBLE);
                            fbPostToWallButton.setVisibility(View.VISIBLE);
                            fbFriendButton.setVisibility(View.VISIBLE);
                             
                        }
 
                        @Override
                        public void onError(DialogError error) {
    
                        }
 
                        @Override
                        public void onFacebookError(FacebookError fberror) {
    
                        }
 
                    });
        }
    }
 
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebook.authorizeCallback(requestCode, resultCode, data);
    }
 
 
    public void getFacebookProfileInfo() {
         
        // https://graph.facebook.com/me?access_token=<your-api-token>&format=json
 
        fbAsyncRunner.request("me", new RequestListener() {
            @Override
            public void onComplete(String response, Object state) {
                Log.d("Profile", response);
                String json = response;
                try {
                    JSONObject profile = new JSONObject(json);
                     
                    final String name = profile.getString("name");
                     
                     
                    runOnUiThread(new Runnable() {
 
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "My Name: " + name , Toast.LENGTH_LONG).show();
                        }
 
                    });
 
                     
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
 
            @Override
            public void onIOException(IOException e, Object state) {
            }
 
            @Override
            public void onFileNotFoundException(FileNotFoundException e,
                    Object state) {
            }
 
            @Override
            public void onMalformedURLException(MalformedURLException e,
                    Object state) {
            }
 
            @Override
            public void onFacebookError(FacebookError e, Object state) {
            }
        });
    }
 
     
    public void getFacebookFriends() {
         
         // https://graph.facebook.com/me/friends?access_token=<your-api-token>&format=json
 
              fbAsyncRunner.request("me/friends", new RequestListener() {
                @Override
                public void onComplete(String response, Object state) {
                     
                    Log.d("Number Of Freinds", response);
                     
                    String json = response;
                     
                    try {
                        JSONObject profile = new JSONObject(json);
                         
                        final String total_count = profile.getString("summary");
                         
                         
                        runOnUiThread(new Runnable() {
 
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Number Of Friends: " + total_count , Toast.LENGTH_LONG).show();
                            }
 
                        });
 
                         
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
 
                @Override
                public void onIOException(IOException e, Object state) {
                }
 
                @Override
                public void onFileNotFoundException(FileNotFoundException e,
                        Object state) {
                }
 
                @Override
                public void onMalformedURLException(MalformedURLException e,
                        Object state) {
                }
 
                @Override
                public void onFacebookError(FacebookError e, Object state) {
                }
            });
        }
 
     
    public void facebookWallPost() {
         
        facebook.dialog(this, "feed", new DialogListener() {
 
            @Override
            public void onFacebookError(FacebookError e) {
            }
 
            @Override
            public void onError(DialogError e) {
            }
 
            @Override
            public void onComplete(Bundle values) {
            }
 
            @Override
            public void onCancel() {
            }
        });
 
    }
 
}
