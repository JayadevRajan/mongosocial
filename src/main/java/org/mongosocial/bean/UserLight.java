package org.mongosocial.bean;

public class UserLight
{
	private String mUserName;
	private byte[] mProfilePic;
	private String mLocation;
	private boolean mShowAddFriend = true; 
	
	public String getUserName()
	{
		return mUserName;
	}
	public void setUserName(String userName)
	{
		this.mUserName = userName;
	}
	public byte[] getProfilePic()
	{
		return mProfilePic;
	}
	public void setProfilePic(byte[] profilePic)
	{
		this.mProfilePic = profilePic;
	}
	public String getLocation()
	{
		return mLocation;
	}
	public void setLocation(String location)
	{
		this.mLocation = location;
	}
	
	public boolean getShowAddFriend()
	{
		return mShowAddFriend;
	}
	
	public void setShowAddFriend(boolean friend)
	{
		mShowAddFriend = friend;
	}
	
}