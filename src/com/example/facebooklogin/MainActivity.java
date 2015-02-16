package com.example.facebooklogin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class MainActivity extends Activity implements View.OnClickListener{

	private static final String TAG = "MainFragment";
	private UiLifecycleHelper uiHelper;
	
	Button Btn_FacebookLogin , Btn_FacebookLogout;
	LoginButton fb_authbtn;
	private static final List<String> PERMISSIONS = Arrays.asList("email","user_location", "user_birthday",
			 "user_likes", "publish_actions" ,"read_friendlists", "manage_friendlists");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    //    uiHelper = new UiLifecycleHelper(MainActivity.this, callback);
   //     uiHelper.onCreate(savedInstanceState);
        
        init();
        getKeyHash(MainActivity.this);
        //Click Listeners
        Btn_FacebookLogin.setOnClickListener(this);
        Btn_FacebookLogout.setOnClickListener(this);
    }

    void init()
    {
    	Btn_FacebookLogin = (Button) findViewById (R.id.btn_facebook_login);
    	Btn_FacebookLogout = (Button)findViewById(R.id.btn_facebook_logout);
    /*	fb_authbtn = (LoginButton)findViewById(R.id.authButton);
    	fb_authbtn.setReadPermissions(Arrays.asList("user_likes", "user_status"));*/
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == Btn_FacebookLogin)
		{
			InitiateFacebookLogin();
		}
		if(v == Btn_FacebookLogout)
		{
			LogoutFb(MainActivity.this);
		}
	}
	//Get KeyHash for Application.
	public static void getKeyHash(Context ctx) {
		try {
			PackageInfo info = ctx.getPackageManager().getPackageInfo(
					"com.example.facebooklogin", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("KeyHash:",
						Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {

		} catch (NoSuchAlgorithmException e) {

		}
	}
	//Initiate Facebook Login.
	void InitiateFacebookLogin()
	{
		try {
			Session session = Session.getActiveSession();
			session = null;
			System.out.println("Session is-->>>" + session);
			if (session == null) {
				System.out.println("@@@ Session is closed, new session has to be created  @@@");
				session = new Session(this);
				session.setActiveSession(session);
				session.openActiveSession(this, true, statusCallback);
		    }
			else
			{
				System.out.println("@@@ If Case Session is already opened  @@@");
				session.openForRead(new Session.OpenRequest(this).setCallback(
						statusCallback).setPermissions(PERMISSIONS));
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
	
	//Logout From Facebook
	void LogoutFb(Context ctx)
	{
		Session session = Session.getActiveSession();
	    if (session != null) {

	        if (!session.isClosed()) {
	            session.closeAndClearTokenInformation();
	            //clear your preferences if saved
	        }
	    } else {

	        session = new Session(ctx);
	        Session.setActiveSession(session);
	        session.closeAndClearTokenInformation();
	            //clear your preferences if saved

	    }
	    session = null;
	    System.out.println("@@ Session is null now @@");
	    System.out.println("@@ User Logged Out @@ ");
	    ShowToast(MainActivity.this, "Logged out Successfully ");
	}
   //Status Callback method return status and information.
	StatusCallback statusCallback = new Session.StatusCallback() {
		
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			// TODO Auto-generated method stub
			if(session.isOpened())
			{
				Log.d(TAG, "Session IsOpened->> " + session.isOpened());
				Request.executeMeRequestAsync(session, new GraphUserCallback() {
					
					@Override
					public void onCompleted(GraphUser user, Response response) {
						// TODO Auto-generated method stub
						if(response != null)
						{
							String Id = "", Name = "", Email = "";
							try {
								 Id = user.getId();
								 Name = user.getName();
								 Email =(String) user.getProperty("email");
								Log.d("ID", Id);
								Log.d("Name", Name);
								Log.d("Email", Email);
							} catch (Exception e) {
								// TODO: handle exception
							}
							ShowToast(MainActivity.this, "Welcome-> " + Name);
							publishFeedDialog(MainActivity.this);
						//	startActivity(new Intent(MainActivity.this, FacebookFeatures.class));
							
							
						}
					}
				});
				
			}
			else
			{
				Log.d(TAG, "Session IsOpened->> " + session.isOpened());
				Log.d(TAG, "Session is not Opened");
				
			}
		}
	};
	
	
	//Open Active Session 
	private  Session openActiveSession(Activity activity, boolean allowLoginUI,
			 StatusCallback callback, List<String> permissions, Bundle savedInstanceState) {
			     Session session = Session.getActiveSession();
			    Log.d(TAG, "" + session);
			    if (session == null) {
			        Log.d(TAG, "" + savedInstanceState);
			        if (savedInstanceState != null) {
			            session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
			        }
			        if (session == null) {
			            session = new Session(this);
			        }
			        Session.setActiveSession(session);
			        if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED) || allowLoginUI) {
			            session.openForRead(new Session.OpenRequest(this).setCallback(
								statusCallback).setPermissions(PERMISSIONS));
			            return session;
			        }
			    }
			    return null;
			}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (Session.getActiveSession() != null) {
			Session.getActiveSession().onActivityResult(this, requestCode,
					resultCode, data);
		}
	}
	
	public void ShowToast(Context ctx, String msg)
	{
		Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
	}
	
	
	private void publishFeedDialog(final Context ctx) {
	    Bundle params = new Bundle();
	    params.putString("name", "https://lh3.googleusercontent.com/--L0Km39l5J8/URquXHGcdNI/AAAAAAAAAbs/3ZrSJNrSomQ/s1024/Antelope%252520Butte.jpg,https://lh6.googleusercontent.com/-55osAWw3x0Q/URquUtcFr5I/AAAAAAAAAbs/rWlj1RUKrYI/s1024/A%252520Photographer.jpg");
	    params.putString("caption", "Build great social apps and get more installs.");
	    params.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
	 //   params.putString("link", "https://developers.facebook.com/android");
	//    params.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png,https://lh6.googleusercontent.com/-55osAWw3x0Q/URquUtcFr5I/AAAAAAAAAbs/rWlj1RUKrYI/s1024/A%252520Photographer.jpg");
	//    params.putString("picture", "https://lh6.googleusercontent.com/-55osAWw3x0Q/URquUtcFr5I/AAAAAAAAAbs/rWlj1RUKrYI/s1024/A%252520Photographer.jpg");

	    WebDialog feedDialog = (
	        new WebDialog.FeedDialogBuilder(ctx,
	            Session.getActiveSession(),
	            params))
	        .setOnCompleteListener(new OnCompleteListener() {

	            @Override
	            public void onComplete(Bundle values,
	                FacebookException error) {
	                if (error == null) {
	                    // When the story is posted, echo the success
	                    // and the post Id.
	                    final String postId = values.getString("post_id");
	                    if (postId != null) {
	                        Toast.makeText(ctx,
	                            "Posted story, id: "+postId,
	                            Toast.LENGTH_SHORT).show();
	                    } else {
	                        // User clicked the Cancel button
	                        Toast.makeText(ctx, 
	                            "Publish cancelled", 
	                            Toast.LENGTH_SHORT).show();
	                    }
	                } else if (error instanceof FacebookOperationCanceledException) {
	                    // User clicked the "x" button
	                    Toast.makeText(ctx, 
	                        "Publish cancelled", 
	                        Toast.LENGTH_SHORT).show();
	                } else {
	                    // Generic, ex: network error
	                    Toast.makeText(ctx, 
	                        "Error posting story", 
	                        Toast.LENGTH_SHORT).show();
	                }
	            }

	        })
	        .build();
	    feedDialog.show();
	}
	
	
}
