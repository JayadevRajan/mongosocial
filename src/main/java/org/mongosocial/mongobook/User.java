package org.mongosocial.mongobook;

import java.io.IOException;
import java.io.InputStream;

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
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

import static org.mongosocial.common.Constants.*;

public class User
{
	public static final String USER_COLLECTION = "user";
	
	private final static Log _LOGGER = LogFactory.getLog(User.class);
	private static final int _BYTE_CHUNK = 1024;
	
	private DBObject mUserDBObject;
	private int mId;
	private String mUserName;
	private String mPassword;
	private String mLocation;
	private byte[] mProfilePic;

	private DB mDatabase;
	private boolean isNew;

	private GridFSInputFile mInputFile;
	private GridFSDBFile mOutputFile;
	
	public User()
	{
		mDatabase = MongoUtil.getDB();
	}

	public User build()
	{
		DBCollection collection = mDatabase.getCollection(USER_COLLECTION);
		
		DBCursor cursor = collection.find(new BasicDBObject(USERNAME, mUserName));
		
		GridFS fs = new GridFS(mDatabase);
		
		//there should be only 1 if any 
		while(cursor.hasNext()) 
		{
			mUserDBObject = cursor.next();
			break;
	    }
		
		isNew = true;
		if(mUserDBObject != null)
		{
			mUserName = (String)mUserDBObject.get(USERNAME);
			mPassword = (String)mUserDBObject.get(PASSWORD);
			mLocation = (String)mUserDBObject.get(LOCATION);
			mId = (Integer)mUserDBObject.get(ID);
			
			mOutputFile = fs.findOne(new BasicDBObject(USERNAME, mUserName));
			InputStream inputStream = null;
			if(mOutputFile != null)
			{
				try
				{
					mProfilePic = new byte[(int)mOutputFile.getLength()];
					
					byte[] chunk = new byte[_BYTE_CHUNK];
					inputStream = mOutputFile.getInputStream();
					int bytesRead = inputStream.read(chunk);
					
					int runningCounter = bytesRead;
		            while (bytesRead != -1) 
		            {
		            	System.arraycopy(chunk, 0,  mProfilePic, runningCounter - bytesRead, bytesRead);
		                bytesRead = inputStream.read(chunk);
		                runningCounter += bytesRead;
		            }
		            
		            _LOGGER.debug("User object " + mUserName + " read " + runningCounter + " bytes");
				}
				catch (IOException e)
				{
					_LOGGER.fatal("User object " + mUserName + " encountered problem while reading");
				}
				finally
				{
					
					try
					{
						if(inputStream != null)
							inputStream.close();
					} 
					catch (IOException e)
					{
					}
				}
			}
			isNew = false;
		}
		
		return this;
	}
	
	public void save()
	{
		DBCollection collection = mDatabase.getCollection(USER_COLLECTION);
		GridFS fs = new GridFS(mDatabase);
		
		if(isNew)
		{
			mUserDBObject = new BasicDBObject(ID, IdGenerator.getId(User.USER_COLLECTION))
									.append(USERNAME, mUserName)
									.append(PASSWORD, mPassword)
									.append(LOCATION, mLocation == null? DEFAULT_LOCATION : mLocation);
			isNew = false;
		}
		else
		{
			mUserDBObject.put(USERNAME, mUserName);
			mUserDBObject.put(PASSWORD, mPassword);
			mUserDBObject.put(LOCATION, mLocation);
			
			//remove the old one and save the new one. 
			if(mOutputFile != null)
				fs.remove(mOutputFile);
			
		}
		
		if(mProfilePic != null && mProfilePic.length > 0)
		{
			mInputFile = fs.createFile(mProfilePic);
			mInputFile.put(USERNAME, mUserName);
			mInputFile.save();
		}
		collection.save(mUserDBObject);
	}
	
	public int getId()
	{
		return mId;
	}

	public User setId(int mId)
	{
		this.mId = mId;
		return this;
	}

	public String getUserName()
	{
		return mUserName;
	}

	public User setUserName(String mUserName)
	{
		this.mUserName = mUserName;
		return this;
	}

	public String getPassword()
	{
		return mPassword;
	}

	public User setPassword(String mPassword)
	{
		this.mPassword = mPassword;
		return this;
	}

	public String getLocation()
	{
		return mLocation;
	}

	public User setLocation(String mLocation)
	{
		this.mLocation = mLocation;
		return this;
	}

	public byte[] getProfilePic()
	{
		return mProfilePic;
	}

	public User setProfilePic(byte[] mProfilePic)
	{
		this.mProfilePic = mProfilePic;
		return this;
	}

	public boolean isNew()
	{
		return isNew;
	}

	public void setNew(boolean isNew)
	{
		this.isNew = isNew;
	}
}
