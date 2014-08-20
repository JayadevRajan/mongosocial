package org.mongosocial.common;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class IdGenerator
{
	private static MongoClient mMongo;
	private static DB mDatabase;

	static
	{
		 mMongo = MongoUtil.getMongo();
		 mDatabase = mMongo.getDB(MongoUtil.DATABASE);
	}
	
	public static int getId(String collectionName)
	{
		DBCollection collection = mDatabase.getCollection("sequences");
		DBCursor cursor = collection.find(new BasicDBObject("collection_name",collectionName) );
		
		int returnCounter;
		
		DBObject collSeqObj = null;
		while(cursor.hasNext())
		{
			collSeqObj = cursor.next();
			break;
		}
		
		if(collSeqObj == null)
		{
			returnCounter = 0;
			collSeqObj = new BasicDBObject("collection_name",collectionName).append("counter", returnCounter);
			collection.insert(collSeqObj);
		}
		else
		{
			returnCounter = (Integer)collSeqObj.get("counter");
			returnCounter++;
			collSeqObj.put("counter", returnCounter);
			collection.save(collSeqObj);
		}
		return returnCounter;
	}
}
