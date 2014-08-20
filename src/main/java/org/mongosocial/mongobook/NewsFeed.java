package org.mongosocial.mongobook;

import java.util.ArrayList;
import java.util.Date;
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

public class NewsFeed
{
	public static final String NEWS_FEED_COLLECTION = "news_feed";
	public static final String LIKES_COLLECTION = "likes";
	private final static Log _LOGGER = LogFactory.getLog(NewsFeed.class);
	
	private DB mDatabase;
	private String mUserName;
	
	private List<NewsFeed.NewsFeedPost> mNewsFeed;
	
	public NewsFeed()
	{
		this.mDatabase = MongoUtil.getDB();
	}

	public NewsFeed build()
	{
		DBCollection newsFeedCollection = mDatabase.getCollection(NEWS_FEED_COLLECTION);
		DBCollection likesCollection = mDatabase.getCollection(LIKES_COLLECTION);
		
		DBCursor cursor = newsFeedCollection.find(new BasicDBObject(USERNAME, mUserName));
		
		//sort by time descending. 
		cursor.sort(new BasicDBObject(TIME,-1));
		
		mNewsFeed = new ArrayList<NewsFeed.NewsFeedPost>();
		
		while(cursor.hasNext()) 
		{
			DBObject newsFeedObj = cursor.next();
			Integer newsId = (Integer)newsFeedObj.get(ID);
			String friendName = (String)newsFeedObj.get(FRIEND_NAME);
			String friendStatus = (String)newsFeedObj.get(FRIEND_STATUS);
			Date postDate = (Date)newsFeedObj.get(TIME);
			int likeId = (Integer)newsFeedObj.get(LIKES_ID);
			
			//get the like document. 
			DBObject likes = likesCollection.findOne(new BasicDBObject(ID,likeId));
			int count = (Integer)likes.get(COUNT);
			
			//needed so that we can say 'xxx and 100 others like this'. 
			List<String> likedFriendNames = (List<String>)likes.get(FRIENDS);
			String likedFriendName = null;
			if(likedFriendNames != null && likedFriendNames.size() > 0 )
				likedFriendName = likedFriendNames.get(0);
			
			NewsFeedPost post = new NewsFeedPost();
			post.mNewsId = newsId;
			post.mFriendName = friendName;
			post.mFriendStatus = friendStatus;
			post.mPostDate = postDate;
			post.mLikesCount = count;
			post.mLikedFriendName = likedFriendName;
			post.mLikeId = likeId;
			mNewsFeed.add(post);
	    }
		return this;
	}
	
	public void updateNewsFeedWithLike(int likeId, int newsId, String username)
	{
		NewsFeedPost post = _getNewsPostById(newsId);
		if(post != null)
		{
			post.mLikesCount++;
			post.mLikedFriendName = username;
		}
	}
	
	private NewsFeedPost _getNewsPostById(int id)
	{
		for(NewsFeedPost post : mNewsFeed)
		{
			if(post.mNewsId == id)
				return post;
		}
		return null;
	}
	
	public static void saveInFriendsNewsFeed(String userName, String message, String friendName, Date postDate, int likeId )
	{
		DB db = MongoUtil.getMongo().getDB(MongoUtil.DATABASE);
		DBCollection collection = db.getCollection(NEWS_FEED_COLLECTION);
		collection.save(new BasicDBObject(ID,IdGenerator.getId(NEWS_FEED_COLLECTION))
									.append(USERNAME, userName)
									.append(FRIEND_NAME, friendName)
									.append(FRIEND_STATUS, message)
									.append(TIME, postDate)
									.append(LIKES_ID, likeId));
	}
	
	public List<NewsFeedPost> getNews()
	{
		return mNewsFeed;
	}
	
	public NewsFeed setUserName(String username)
	{
		this.mUserName = username;
		return this;
	}
	
	public String getUserName()
	{
		return mUserName;
	}
	
	
	public static class NewsFeedPost
	{
		String mFriendName;
		String mFriendStatus;
		Date mPostDate;
		int mLikesCount;
		String mLikedFriendName;
		int mLikeId;
		int mNewsId; 
		
		public String getLikeString()
		{
			if(mLikesCount == 0)
				return "";
			if(mLikesCount == 1)
				return mLikedFriendName + " likes this.";
			else
				return mLikedFriendName + " and " + (mLikesCount - 1) + " others like this.";
		}
		
		public String getFriendName()
		{
			return mFriendName;
		}
		public void setFriendName(String friendName)
		{
			this.mFriendName = friendName;
		}
		public String getFriendStatus()
		{
			return mFriendStatus;
		}
		public void setFriendStatus(String friendStatus)
		{
			this.mFriendStatus = friendStatus;
		}
		public Date getPostDate()
		{
			return mPostDate;
		}
		public void setPostDate(Date postDate)
		{
			this.mPostDate = postDate;
		}
		public int getLikesCount()
		{
			return mLikesCount;
		}
		public void setLikesCount(int likesCount)
		{
			this.mLikesCount = likesCount;
		}
		public String getLikedFriendName()
		{
			return mLikedFriendName;
		}
		public void setLikedFriendName(String likedFriendName)
		{
			this.mLikedFriendName = likedFriendName;
		}
		public int getLikesId()
		{
			return mLikeId;
		}
		public void setLikesId(int mLikeId)
		{
			this.mLikeId = mLikeId;
		}
		
		public int getNewsId()
		{
			return mNewsId;
		}
		public void setNewsId(int mNewsId)
		{
			this.mNewsId = mNewsId;
		}
	}
}
