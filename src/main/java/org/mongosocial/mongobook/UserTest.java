package org.mongosocial.mongobook;

import org.mongosocial.common.IdGenerator;
import org.mongosocial.common.ImageUtil;
import org.mongosocial.common.MongoUtil;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class UserTest
{
	public static void main(String[] args) throws Exception
	{
		MongoClient client = MongoUtil.getMongo();
		User user = new User();
		user.setId(IdGenerator.getId(User.USER_COLLECTION)).setLocation("bangalore").setPassword("chicky").setUserName("jayadev").setProfilePic(ImageUtil.loadImage("pic.jpg")).build();
		
		user.save();
		
		User user1 = new User();
		user1.setId(IdGenerator.getId(User.USER_COLLECTION)).setLocation("bangalore").setPassword("chicky").setUserName("chicky").setProfilePic(ImageUtil.loadImage("pic.jpg")).build();
		user1.save();
		DB db = client.getDB(MongoUtil.DATABASE);
		
		User user2 = new User();
		user2.setUserName("jayadev").build();
		ImageUtil.storeImage(user2.getProfilePic());
		
		System.out.println(db.getCollection("user").count());
	}
}
