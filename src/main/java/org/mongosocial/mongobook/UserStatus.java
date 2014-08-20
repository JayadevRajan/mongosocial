package org.mongosocial.mongobook;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mongosocial.common.IdGenerator;
import org.mongosocial.common.MongoUtil;
import org.mongosocial.mongobook.Friends.Friend;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import static org.mongosocial.common.Constants.*;

public class UserStatus
{
	public static final String USER_STATUS_COLLECTION = "user_status";
	private final static Log _LOGGER = LogFactory.getLog(UserStatus.class);
	
	private String mUserName;
	private List<UserPost> mUserPosts;
	
	private DB mDatabase;
	
	public UserStatus()
	{
		mDatabase = MongoUtil.getDB();
	}
	
	public UserStatus build()
	{
		DBCollection collection = mDatabase.getCollection(USER_STATUS_COLLECTION);
		
		DBCursor cursor = collection.find(new BasicDBObject(USERNAME, mUserName));
		
		//sort by time descending.  
		cursor.sort(new BasicDBObject(TIME,-1));
		
		mUserPosts = new ArrayList<UserStatus.UserPost>();

		while(cursor.hasNext()) 
		{
			DBObject object = cursor.next();
			UserPost post = new UserPost();
			post.setPostDate((Date)object.get(TIME));
			post.setPostMessage((String)object.get(STATUS));;
			mUserPosts.add(post);
	    }
		return this;
	}

	public void postMessage(String message, Friends friends)
	{
		UserPost post = new UserPost();
		post.postDate = new Date();
		post.postMessage = message;
		mUserPosts.add(post);
		
		//save it in user status first. 
		DBCollection userStatusCollection = mDatabase.getCollection(USER_STATUS_COLLECTION);
		userStatusCollection.save(new BasicDBObject(USERNAME, mUserName)
								.append(STATUS,message)
								.append(TIME, post.postDate)
								.append(ID, IdGenerator.getId(USER_STATUS_COLLECTION)));
		
		//create a like document to be accessed globally. 
		DBCollection likeCollection = mDatabase.getCollection(NewsFeed.LIKES_COLLECTION);
		int likeId = IdGenerator.getId(NewsFeed.LIKES_COLLECTION);
		likeCollection.save(new BasicDBObject(ID, likeId)
									.append(COUNT, 0));
		
		for(Friend friend : friends.getFriends())
		{
			NewsFeed.saveInFriendsNewsFeed(friend.getFriendName(), message, mUserName, post.postDate, likeId);
		}
	}
	
	public List<UserPost> getUserPosts()
	{
		return mUserPosts;
	}
	
	public String getUserName()
	{
		return mUserName;
	}

	public UserStatus setUserName(String userName)
	{
		this.mUserName = userName;
		return this;
	}
	
	public static class UserPost
	{
		String postMessage;
		Date postDate;
		
		public String getPostMessage()
		{
			return postMessage;
		}
		public void setPostMessage(String postMessage)
		{
			this.postMessage = postMessage;
		}
		public Date getPostDate()
		{
			return postDate;
		}
		public void setPostDate(Date postDate)
		{
			this.postDate = postDate;
		}
	}
}
