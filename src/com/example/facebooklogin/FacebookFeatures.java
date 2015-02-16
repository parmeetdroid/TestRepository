package com.example.facebooklogin;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.FeatureInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Request.GraphUserListCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.android.Facebook;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class FacebookFeatures extends Activity implements View.OnClickListener{
	Button Btn_GetFriends, Btn_Share;
	Facebook facebook;
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.facebookfeature);

	init();
	Btn_GetFriends.setOnClickListener(this);
	Btn_Share.setOnClickListener(this);
}
	void init()
	{
		Btn_GetFriends = (Button)findViewById(R.id.btn_getfriends);
		Btn_Share = (Button)findViewById(R.id.btn_share);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == Btn_GetFriends)
		{
		//	GetFriendsList();
			test();
		}
		else if(v == Btn_Share)
		{
			publishFeedDialog(FacebookFeatures.this);
		}
	}
	
	void GetFriendsList()
	{
		Session session = Session.getActiveSession();
		if(session.isOpened())
		{
			String fqlQuery = "SELECT uid,name,pic_square FROM user WHERE uid IN " +
			        "(SELECT uid2 FROM friend WHERE uid1 = 883717041638505)";
			/*String secondfql = "SELECT uid, name, pic_square FROM user WHERE uid IN "+
	"(SELECT uid2 FROM friend WHERE uid1 = me() LIMIT 25)";*/

			Bundle params = new Bundle();
			params.putString("q", fqlQuery);
			Request request = new Request(session,
			        "/fql",                         
			        params,                         
			        HttpMethod.GET,                 
			        new Request.Callback(){       
			    public void onCompleted(Response response) {
			        Log.i("FACEBOOK FEATURE", "Result: " + response.toString());

			       /* try{
			            GraphObject graphObject = response.getGraphObject();
			            JSONObject jsonObject = graphObject.getInnerJSONObject();
			            Log.d("data", jsonObject.toString(0));

			            JSONArray array = jsonObject.getJSONArray("data");
			            for(int i=0;i<array.length();i++){

			                JSONObject friend = array.getJSONObject(i);

			                Log.d("uid",friend.getString("uid"));
			                Log.d("name", friend.getString("name"));
			                Log.d("pic_square",friend.getString("pic_square"));             
			            }
			        }catch(JSONException e){
			            e.printStackTrace();
			        }*/
			    }                  
			}); 
			Request.executeBatchAsync(request); 
		}
		else
		{
			Log.d("Session Is Not Opened ", "");
		}
		
	}
	
	/*protected void onSessionStateChange(SessionState state, Exception exception) {
	    if (state.isOpened()) {
	        Session activeSession = Session.getActiveSession();
	        facebook.setAccessToken(activeSession.getAccessToken());
	        facebook.setAccessExpires(activeSession.getExpirationDate().getTime());

	        Request req = Request.newMyFriendsRequest(activeSession, new GraphUserListCallback() {

	            @Override
	            public void onCompleted(List<GraphUser> users, Response response) {
	                System.out.println(response.toString());

	            }
	        });
	        Bundle params = new Bundle();
	        params.putString("method", "fql.query");
	        params.putString("query", "select uid, name, pic_square, is_app_user " + "from user where uid in "
	                + "(select uid2 from friend where uid1 = me())");
	        req.setParameters(params);
	        Request.executeBatchAsync(req);

	    }
	}*/
	void test()
	{
		Request.newMyFriendsRequest(Session.getActiveSession(), new GraphUserListCallback() {
			
			@Override
			public void onCompleted(List<GraphUser> users, Response response) {
				// TODO Auto-generated method stub
				String fqlQuery = "{" +
		                "'friends':'SELECT uid2 FROM friend WHERE uid1 = me() LIMIT 25'," +
		                "'friendinfo':'SELECT uid, name, pic_square FROM user WHERE uid IN " +
		                "(SELECT uid2 FROM #friends)'," +
		                "}";
		          Bundle params = new Bundle();
		          params.putString("q", fqlQuery);
		          Session session = Session.getActiveSession();
		          Request request = new Request(session,
		              "/fql",                         
		              params,                         
		              HttpMethod.GET,                 
		              new Request.Callback(){         
		                  public void onCompleted(Response response) {
		                      Log.i("", "Result: " + response.toString());
		                  }                  
		          }); 
		          Request.executeBatchAsync(request);    
			}
		}).executeAsync();
		
	}
	
	
	private void publishFeedDialog(final Context ctx) {
	    Bundle params = new Bundle();
	    params.putString("name", "Testing share");
	    params.putString("caption", "Build great social apps and get more installs.");
	    params.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
	    params.putString("link", "https://developers.facebook.com/android");
	    params.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

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
