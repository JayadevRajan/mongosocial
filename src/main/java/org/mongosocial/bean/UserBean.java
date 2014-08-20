package org.mongosocial.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;
import javax.servlet.http.HttpSession;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.mongosocial.common.IdGenerator;
import org.mongosocial.common.MongoUtil;
import org.mongosocial.mongobook.Friends;
import org.mongosocial.mongobook.NewsFeed;
import org.mongosocial.mongobook.User;
import org.mongosocial.mongobook.UserStatus;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import static org.mongosocial.common.Constants.*;

public class UserBean
{
	private User user;
	private String mUserName;
	private String mPassword;
	private String mLocation;
	private byte[] mProfilePic;
	private UploadedFile mUploadedFile;
	private Friends mFriends;
	private NewsFeed mNewsFeed;
	private UserStatus mUserStatuses;
	private String mAction = "";
	
	private String mCurrentStatus;
	private FacesMessage mFacesMessage; 	
	
	public UserBean()
	{
		
	}
	
	public String action()
	{
		if(mAction.equals("login"))
		{
			FacesContext.getCurrentInstance().addMessage("loginForm:username", mFacesMessage);
		}
		return mAction;
	}

	public String getStatus()
	{
		return mCurrentStatus;
	}

	public void setStatus(String status)
	{
		this.mCurrentStatus = status;
	}
	
	public String getUserName()
	{
		return mUserName;
	}

	public void setUserName(String userName)
	{
		this.mUserName = userName;
	}

	public UploadedFile getUploadedFile()
	{
		return mUploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile)
	{
		this.mUploadedFile = uploadedFile;
	}

	public String getPassword()
	{
		return mPassword;
	}

	public void setPassword(String password)
	{
		this.mPassword = password;
	}

	public String getLocation()
	{
		return mLocation;
	}

	public void setLocation(String location)
	{
		this.mLocation = location;
	}

	public byte[] getProfilePic()
	{
		return mProfilePic;
	}

	public void setProfilePic(byte[] profilePic)
	{
		this.mProfilePic = profilePic;
	}
	

	public void signIn(ActionEvent e)
	{
		// no authentication, just log in.
		user = new User();
		user.setUserName(mUserName).build();
		
		if(user.isNew())
		{
			mFacesMessage = new FacesMessage("Sign up first..");
			mAction = "login";
			return;
		}
		mAction = "signin";
		_buildOthers();
	}

	public void signUp(ActionEvent e)
	{
		// set the user name and password and just fall in.
		// later the user will set their profile pic.
		user = new User();
		user.setUserName(mUserName).build();
		
		if(!user.isNew())
		{
			mFacesMessage = new FacesMessage("Username taken..");
			mAction = "login";
			return;
		}
		mAction = "signup";
		user.setUserName(mUserName).setPassword(mPassword).build();
		user.save();
		
		_buildOthers();
	}
	
	private void _buildOthers()
	{
		mFriends = new Friends();
		mUserStatuses = new UserStatus();
		mNewsFeed = new NewsFeed();
		
		mFriends.setUserName(mUserName).setLocation(mLocation).build().save();
		mUserStatuses.setUserName(mUserName).build();
		mNewsFeed.setUserName(mUserName).build();

		mProfilePic = user.getProfilePic();
		mPassword = user.getPassword();
		mLocation = user.getLocation();
	}
	
	public void signOut(ActionEvent e)
	{
		//remove the user from the session.
		HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		session.removeAttribute("userBean");
	}
	
	public void editProfilePic(ActionEvent e)
	{
		if(mUploadedFile != null)
		{
			try
			{
				mProfilePic = mUploadedFile.getBytes();
				user.setProfilePic(mProfilePic);
			} 
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
			this.user.save();
		}
	}
	
	public void editLocation(ActionEvent e)
	{
		//save the user
		this.user.setLocation(mLocation).save();

		//save the location in friends. 
		//Get the entries from friends collection who has the current user as friend. 
		//update friends document. 
		DB db = MongoUtil.getDB();
		DBCollection friendsCollection = db.getCollection(Friends.FRIENDS_COLLECTION);
		DBCursor cursor = friendsCollection.find(new BasicDBObject(FRIENDS, new BasicDBObject("$elemMatch",new BasicDBObject(FRIEND_NAME, mUserName))));
		
		while(cursor.hasNext())
		{
			DBObject friendObject = (DBObject)cursor.next();
			List<BasicDBObject> friends = (List<BasicDBObject>)friendObject.get(FRIENDS);
			int index = 0;
			for(DBObject friend : friends)
			{
				if(friend.get(FRIEND_NAME).equals(mUserName))
				{
					break;
				}
				index++;
			}
			friends.remove(index);
			friends.add(new BasicDBObject(LOCATION, mLocation)
									.append(FRIEND_NAME, mUserName) );
			friendObject.put(FRIENDS, friends);
			friendsCollection.save(friendObject);
		}
	}
	
	public void updateLike(ActionEvent e)
	{
		DB db = MongoUtil.getDB();
		DBCollection likesCollection = db.getCollection(NewsFeed.LIKES_COLLECTION);
		Integer likeId = (Integer)e.getComponent().getAttributes().get(LIKE_ID);
		Integer newsId = (Integer)e.getComponent().getAttributes().get(NEWS_ID);
		DBObject likeDocument = likesCollection.findOne(new BasicDBObject(ID, likeId ));
		int count = (Integer)likeDocument.get(COUNT);
		List<String> friends = (List<String>)likeDocument.get(FRIENDS);
		if(friends == null)
			friends = new ArrayList<String>();
		friends.add(mUserName);
		count++;
		likeDocument.put(COUNT, count);
		likeDocument.put(FRIENDS, friends);
		likesCollection.save(likeDocument);
		
		mNewsFeed.updateNewsFeedWithLike(likeId, newsId, mUserName);
	}
	
	public void addFriend(ActionEvent e)
	{
		String friendName = (String)e.getComponent().getAttributes().get("friendname");
		String friendLocation = (String)e.getComponent().getAttributes().get("friendlocation");
		mFriends.setLocation(mLocation).addFriend(friendName, friendLocation);
	}
	
	public void postStatus(ActionEvent e)
	{
		mUserStatuses.postMessage(mCurrentStatus, mFriends);
	}
	
	public void refreshNews(ActionEvent e)
	{
		mNewsFeed.setUserName(mUserName).build();
	}
	
	public DataModel getFriends()
	{
		return new ArrayDataModel(mFriends.getFriends().toArray());
	}
	
	public DataModel getNewsFeed()
	{
		return new ArrayDataModel(mNewsFeed.getNews().toArray());
	}
	
	
	public DataModel getUserStatuses()
	{
		if(mUserStatuses.getUserPosts() != null)
			return new ArrayDataModel(mUserStatuses.getUserPosts().toArray());
		else
			return new ArrayDataModel();
	}
}
