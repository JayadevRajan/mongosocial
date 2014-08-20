package org.mongosocial.common;

import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoUtil
{
	private final static Log _LOGGER = LogFactory.getLog(MongoUtil.class);

	private static final int _PORT = 27017;
	private static final String _HOST = "localhost";
	private static MongoClient _MONGO_CLIENT = null;
	
	public static final String DATABASE = "mongobook";

	public static synchronized MongoClient getMongo()
	{
		if (_MONGO_CLIENT == null)
		{
			try
			{
				_MONGO_CLIENT = new MongoClient(_HOST, _PORT);
				_LOGGER.debug("New Mongo created with [" + _HOST + "] and ["
						+ _PORT + "]");
			} 
			catch (UnknownHostException e)
			{
				_LOGGER.error(e.getMessage());
			}
		}
		return _MONGO_CLIENT;
	}
	
	public static DB getDB()
	{
		return getMongo().getDB(DATABASE);
	}
}
