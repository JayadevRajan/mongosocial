package org.mongosocial.mongobook;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mongosocial.common.IdGenerator;
import org.mongosocial.common.MongoUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import static org.mongosocial.common.Constants.*;

public class Friends
{
	public static final String FRIENDS_COLLECTION = "user_friends";
	private final static Log _LOGGER = LogFactory.getLog(Friends.class);

	private DBObject mFriendsDBObject;
	private String mUserName;
	private String mLocation;
	private DB mDatabase;
	
	private List<Friend> mFriends;
	private boolean isNew;
	
	public Friends()
	{
		mDatabase = MongoUtil.getDB();
	}
	
	public Friends setUserName(String username)
	{
		this.mUserName = username;
		return this;
	}
	
	public String getUserName()
	{
		return mUserName;
	}
	
	public Friends setLocation(String location)
	{
		this.mLocation = location;
		return this;
	}
	
	public String getLocation()
	{
		return mLocation;
	}
	
	public Friends build()
	{
		DBCollection collection = mDatabase.getCollection(FRIENDS_COLLECTION);
		
		DBCursor cursor = collection.find(new BasicDBObject(USERNAME, mUserName));
		
		//there should be only 1 if any 
		while(cursor.hasNext()) 
		{
			mFriendsDBObject = cursor.next();
			break;
	    }
		
		isNew = true;
		mFriends = new ArrayList<Friends.Friend>();
		
		if(mFriendsDBObject != null)
		{
			List<BasicDBObject> friendsArray = (List<BasicDBObject>)mFriendsDBObject.get(FRIENDS);
			mFriends = new ArrayList<Friends.Friend>();
			for(DBObject friend : friendsArray)
			{
				Friend dbFriend = new Friend();
				dbFriend.mLocation = (String)friend.get(LOCATION);
				dbFriend.mFriendName = (String)friend.get(FRIEND_NAME);
				mFriends.add(dbFriend);
			}
			isNew = false;
		}
		
		return this;
	}
	
	public void save()
	{
		DBCollection collection = mDatabase.getCollection(FRIENDS_COLLECTION);
		
		List<BasicDBObject> friendsObjectList = new ArrayList<BasicDBObject>();
		for(Friend friend: mFriends)
		{
			friendsObjectList.add(new BasicDBObject(FRIEND_NAME, friend.mFriendName)
										.append(LOCATION, friend.mLocation));
		}
		
		if(isNew)
		{
			mFriendsDBObject = new BasicDBObject(ID, IdGenerator.getId(FRIENDS_COLLECTION))
										.append(USERNAME, mUserName)
										.append(FRIENDS, friendsObjectList);
			isNew = false;
		}
		else
		{
			mFriendsDBObject.put(USERNAME, mUserName);
			mFriendsDBObject.put(FRIENDS, friendsObjectList);
		}
		collection.save(mFriendsDBObject);
	}
	
	public void addFriend(String friendName, String location)
	{
		Friend friend = new Friend();
		friend.mFriendName = friendName;
		friend.mLocation = location;
		mFriends.add(friend);
		save();
		
		//add friend the other way too. no permission required.
		Friend reverseFriend = new Friend();
		reverseFriend.mFriendName = mUserName;
		reverseFriend.mLocation = mLocation;
		Friends reverseFriends = new Friends();
		reverseFriends.setUserName(friendName).build();
		reverseFriends.getFriends().add(reverseFriend);
		reverseFriends.save();
	}
	
	public List<Friend> getFriends()
	{
		return mFriends;
	}
	
	public static class Friend 
	{
		String mFriendName;
		String mLocation;
		public String getFriendName()
		{
			return mFriendName;
		}
		public void setFriendName(String mFriendName)
		{
			this.mFriendName = mFriendName;
		}
		public String getLocation()
		{
			return mLocation;
		}
		public void setLocation(String mLocation)
		{
			this.mLocation = mLocation;
		}
	}
}
