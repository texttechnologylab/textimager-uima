package org.hucompute.services.uima.database.mongo;


import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * A wrapper for a connection to Mongo.
 *
 * @author renaud.richardet@epfl.ch
 */
public class MongoConnection {

	final public String host, dbName, collectionName,user,pw;
	final public String authSource;
//	final public Mongo m;
	final public DB db;
	final public DBCollection coll;

	/**
	 * @param db_connection
	 *            an array {host, dbName, collectionName, user, pw [, authSource]}. Leave user
	 *            and pw empty ("") to skip authentication. Leave authSource empty for default
	 */
	public MongoConnection(String[] db_connection) throws UnknownHostException,
			MongoException {
		this(db_connection[0],db_connection[1],db_connection[2],db_connection[3],db_connection[4], true, (db_connection.length == 6 ? db_connection[5] : ""));
	}

	/**
	 * @param db_connection
	 *            an array {host, dbName, collectionName, user, pw}. Leave user
	 *            and pw empty ("") to skip authentication
	 */
	@SuppressWarnings("deprecation") // TODO replace with MongoClient
    public MongoConnection(String host,String dbName,String collectionName,String user,String pw, boolean safe, String authSource)
			throws UnknownHostException, MongoException {


		this.host = host ;
		this.dbName = dbName;
		this.collectionName = collectionName;
		this.user = user;
		this.pw = pw;
		this.authSource = authSource;

//		checkNotNull(host, "host is NULL");
//		checkNotNull(dbName, "dbName is NULL");
//		checkNotNull(collectionName, "collectionName is NULL");
//		checkNotNull(user, "user is NULL");
//		checkNotNull(pw, "pw is NULL");

		MongoClient mongoClient = null;

		// Skip authentication if user and pass are empty/null
		if ((this.user == null || this.user.isEmpty())
				&& (this.pw == null || this.pw.isEmpty())) {

			mongoClient = new MongoClient( host );

		} else {

//			m = new Mongo(host, 27017);
//			if (safe)
//				m.setWriteConcern(WriteConcern.SAFE);
//			m.getDatabaseNames();// to test connection
//			db = m.getDB(dbName);
			MongoCredential credential = MongoCredential.createScramSha1Credential(user,
					(this.authSource.isEmpty() ? "admin" : this.authSource),
					pw.toCharArray());

			System.out.println(credential);

			mongoClient = new MongoClient(new ServerAddress(host, 27017), Arrays.asList(credential));
		}


		db = mongoClient.getDB(dbName);
//		if (user.length() > 0) {
//			if (!db.authenticate(user, pw.toCharArray())) {
//				throw new MongoException(-1, "cannot login with user " + user);
//			}
//		}
		System.out.println(db.getName());
		coll = db.getCollection(collectionName);
	}

	@Override
	public String toString() {
		return "MongoConnection: " + host + ":" + dbName + "::"
				+ collectionName;
	}
}
