package org.mongosocial.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;
import javax.servlet.http.HttpSession;

import org.mongosocial.common.MongoUtil;
import org.mongosocial.mongobook.Friends;
import org.mongosocial.mongobook.Friends.Friend;
import org.mongosocial.mongobook.User;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import static org.mongosocial.common.Constants.*;
/* 
 * One which holds all the users 
 * An example of how a user caching should NOT be done :) 
 */
public class SearchBean
{
	private List<UserLight> mUsers;
	private MongoClient mMongo;
	
	private DataModel mModel;
	
	public SearchBean()
	{
		mUsers = new ArrayList<UserLight>();
		mMongo = MongoUtil.getMongo();
		refresh(null);
	}
	
	public void refresh(ActionEvent e)
	{
		mUsers = new ArrayList<UserLight>();
		DB database = MongoUtil.getDB();
		DBCollection userCollection = database.getCollection(User.USER_COLLECTION);
		
		DBCursor cursor = userCollection.find();
		
		while(cursor.hasNext())
		{
			DBObject userObj = cursor.next();
			User user = new User();
			user.setUserName((String)userObj.get(USERNAME)).build();
			
			UserLight lightUser = new UserLight();
			lightUser.setLocation( user.getLocation() );
			lightUser.setProfilePic( user.getProfilePic() );
			lightUser.setUserName( user.getUserName() );
			
			mUsers.add(lightUser);
		}
	}
	
	public byte[] getUserPic(String userName)
	{
		for(UserLight user : mUsers)
		{
			if(user.getUserName().equals(userName))
			{
				return user.getProfilePic();
			}
		}
		return null;
	}

	public DataModel getUsers()
	{
		DB database = MongoUtil.getDB();
		
		//get the current user and knock off from the return list
		HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		UserBean currentUser = (UserBean)session.getAttribute("userBean");
		
		String currentUserName = "";
		DBCollection friendsCollection = database.getCollection(Friends.FRIENDS_COLLECTION);
		
		List<String> currentUsersFriends = new ArrayList<String>();
		
		if(currentUser != null && !currentUser.getUserName().equals("") )
		{
			currentUserName = currentUser.getUserName();
			
			//already friends with the current user? 
			DBObject friendsObject = friendsCollection.findOne(new BasicDBObject(USERNAME, currentUserName) );
			if(friendsObject != null)
			{
				List<DBObject> friends = (List<DBObject>)friendsObject.get(FRIENDS);
				
				for(DBObject friend : friends)
				{
					currentUsersFriends.add((String)friend.get(FRIEND_NAME));
				}
			}
		}

		List<UserLight> newArr = new ArrayList<UserLight>();
		
		for(UserLight user : mUsers)
		{
			//exclude current use from list.
			if(user.getUserName().equals(currentUserName))
				continue;
			
			//do not show friend as add friend option. 
			if(currentUsersFriends.contains(user.getUserName()))
			{
				user.setShowAddFriend(false);
			}
				
			newArr.add(user);
		}
		mModel = new ArrayDataModel(newArr.toArray());
		return mModel;
	}
}
