import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.BasicBSONList;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class MongoDBQuery {
	//query into mongodb
	// you can get the json format in data/samplejson
	Mongo mg = null;
	DB db = null;
	DBCollection collection = null;

	/**
	 * get collections
	 * 
	 * @param collection
	 */
	public DBCollection getCollection(String collection) {
		return db.getCollection(collection);
	}

	public MongoDBQuery() {
		try {
			mg = new Mongo("localhost", 27017);
			db = mg.getDB("test");
			collection = db.getCollection("chillingeffect");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int timeIntervalCount(String startTimeStr, String endTimeStr) {
		try {
			Date startDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
					.parse(startTimeStr);
			Date endDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
					.parse(endTimeStr);

			BasicDBObject query = new BasicDBObject("date_sent",
					new BasicDBObject("$gte", startDate).append("$lt", endDate));
			return collection.find(query).count();

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	public void monthlyCountQuery() {
		int y = 2013;
		int count = 0;
		while (y < 2015) {
			for (int m = 1; m < 13; m++) {
				int y1 = y, y2 = y;
				String m1 = String.valueOf(m), m2 = String.valueOf(m + 1);
				if (m == 12) {
					y2++;
					m2 = "01";
				}
				if (m1.length() == 1) {
					m1 = "0" + m1;
				}
				if (m2.length() == 1) {
					m2 = "0" + m2;
				}
				count = timeIntervalCount(y1 + "-" + m1 + "-01T00:00:00Z", y2
						+ "-" + m2 + "-01T00:00:00Z");
				System.out.println(y1 + "-" + m1 + ": " + count);
			}
			y++;
		}
	}

	public List<String> getFrequentUser(String startTimeStr, String endTimeStr,
			String user) {
		try {

			Date startDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
					.parse(startTimeStr);
			Date endDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
					.parse(endTimeStr);

			BasicDBObject query = new BasicDBObject("date_sent",
					new BasicDBObject("$gte", startDate).append("$lt", endDate));
			DBObject match = new BasicDBObject("$match", query);

			DBObject project = new BasicDBObject("$project", new BasicDBObject(
					user, 1));

			DBObject groupFields = new BasicDBObject("_id", "$" + user);

			// we use the $sum operator to increment the "count"
			// for each unique dolaznaStr
			groupFields.put("count", new BasicDBObject("$sum", 1));
			DBObject group = new BasicDBObject("$group", groupFields);

			// You can add a sort to order by count descending

			DBObject sortFields = new BasicDBObject("count", -1);
			DBObject sort = new BasicDBObject("$sort", sortFields);

			List<DBObject> pipeline = Arrays
					.asList(match, project, group, sort);
			AggregationOutput output = collection.aggregate(pipeline);

			System.out.println(startTimeStr);
			for (DBObject result : output.results()) {
				if (Integer.parseInt(result.get("count").toString()) > 80
						&& result.get("_id") != null) {
					System.out.println(result);
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void monthlyUserCount(String user) {
		int y = 2013;
		while (y < 2015) {
			for (int m = 1; m < 13; m++) {
				int y1 = y, y2 = y;
				String m1 = String.valueOf(m), m2 = String.valueOf(m + 1);
				if (m == 12) {
					y2++;
					m2 = "01";
				}
				if (m1.length() == 1) {
					m1 = "0" + m1;
				}
				if (m2.length() == 1) {
					m2 = "0" + m2;
				}
				getFrequentUser(y1 + "-" + m1 + "-01T00:00:00Z", y2 + "-" + m2
						+ "-01T00:00:00Z", user);
			}
			y++;
		}
	}

	public AggregationOutput getFrequentPair(String startTimeStr,
			String endTimeStr) {
		try {

			Date startDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
					.parse(startTimeStr);
			Date endDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
					.parse(endTimeStr);

			BasicDBObject query = new BasicDBObject("date_sent",
					new BasicDBObject("$gte", startDate).append("$lt", endDate));
			DBObject match = new BasicDBObject("$match", query);
			// DBObject match = new BasicDBObject("$match", new
			// BasicDBObject("type", "Dmca"));

			DBObject project = new BasicDBObject("$project", new BasicDBObject(
					"recipient_name", 1).append("sender_name", 1));

			Map<String, Object> dbObjIdMap = new HashMap<String, Object>();
			dbObjIdMap.put("recipient_name", "$recipient_name");
			dbObjIdMap.put("sender_name", "$sender_name");
			DBObject groupFields = new BasicDBObject("_id", new BasicDBObject(
					dbObjIdMap));

			// we use the $sum operator to increment the "count"
			// for each unique dolaznaStr
			groupFields.put("count", new BasicDBObject("$sum", 1));
			DBObject group = new BasicDBObject("$group", groupFields);

			// You can add a sort to order by count descending

			DBObject sortFields = new BasicDBObject("count", -1);
			DBObject sort = new BasicDBObject("$sort", sortFields);

			List<DBObject> pipeline = Arrays
					.asList(match, project, group, sort);
			AggregationOutput output = collection.aggregate(pipeline);

			/*
			 * System.out.println(startTimeStr); for (DBObject result :
			 * output.results()) {
			 * if(Integer.parseInt(result.get("count").toString()) > 80 &&
			 * ((BasicBSONObject) result.get("_id")).get("sender_name")!=null){
			 * System.out.println(result); } }
			 */
			return output;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public AggregationOutput getUserNet(String startTimeStr, String endTimeStr,
			String userName, String userType) {
		try {

			Date startDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
					.parse(startTimeStr);
			Date endDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
					.parse(endTimeStr);

			BasicDBObject query = new BasicDBObject("date_sent",
					new BasicDBObject("$gte", startDate).append("$lt", endDate))
					.append(userType + "_name", userName);
			DBObject match = new BasicDBObject("$match", query);

			DBObject project = new BasicDBObject("$project", new BasicDBObject(
					"recipient_name", 1).append("sender_name", 1));

			Map<String, Object> dbObjIdMap = new HashMap<String, Object>();
			dbObjIdMap.put("recipient_name", "$recipient_name");
			dbObjIdMap.put("sender_name", "$sender_name");
			DBObject groupFields = new BasicDBObject("_id", new BasicDBObject(
					dbObjIdMap));

			// we use the $sum operator to increment the "count"
			// for each unique dolaznaStr
			groupFields.put("count", new BasicDBObject("$sum", 1));
			DBObject group = new BasicDBObject("$group", groupFields);

			// You can add a sort to order by count descending

			DBObject sortFields = new BasicDBObject("count", -1);
			DBObject sort = new BasicDBObject("$sort", sortFields);

			List<DBObject> pipeline = Arrays
					.asList(match, project, group, sort);
			AggregationOutput output = collection.aggregate(pipeline);

			/*
			 * System.out.println(startTimeStr); for (DBObject result :
			 * output.results()) {
			 * if(Integer.parseInt(result.get("count").toString()) > 80 &&
			 * ((BasicBSONObject) result.get("_id")).get("sender_name")!=null){
			 * System.out.println(result); } }
			 */
			return output;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void monthlyPairCount() {
		int y = 2013;
		while (y < 2015) {
			for (int m = 1; m < 13; m++) {
				int y1 = y, y2 = y;
				String m1 = String.valueOf(m), m2 = String.valueOf(m + 1);
				if (m == 12) {
					y2++;
					m2 = "01";
				}
				if (m1.length() == 1) {
					m1 = "0" + m1;
				}
				if (m2.length() == 1) {
					m2 = "0" + m2;
				}
				getFrequentPair(y1 + "-" + m1 + "-01T00:00:00Z", y2 + "-" + m2
						+ "-01T00:00:00Z");
			}
			y++;
		}
	}

	public void monthlyTwitterCmoplaint() {
		int y = 2013;
		for (int m = 9; m < 10; m++) {
			int y1 = y, y2 = y;
			String m1 = String.valueOf(m), m2 = String.valueOf(m + 1);
			if (m == 12) {
				y2++;
				m2 = "01";
			}
			if (m1.length() == 1) {
				m1 = "0" + m1;
			}
			if (m2.length() == 1) {
				m2 = "0" + m2;
			}
			getMonthlyDescription(y1 + "-" + m1, y2 + "-" + m2);
		}
		y++;

	}

	public void getMonthlyDescription(String startTimeStr, String endTimeStr) {
		try {
			String fileName = "data/" + startTimeStr + ".txt";
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName));

			Date startDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
					.parse(startTimeStr + "-01T00:00:00Z");
			Date endDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
					.parse(endTimeStr + "-01T00:00:00Z");
			BasicDBObject query = new BasicDBObject("date_sent",
					new BasicDBObject("$gte", startDate).append("$lt", endDate));

			DBCursor cursor = collection.find(query);
			while (cursor.hasNext()) {
				DBObject o = cursor.next();
				if (o.get("sender_name") != null
						&& o.get("sender_name").toString()
								.equals("nike, nike, nike")) {
					System.out.println(o);

					// String s = ((DBObject)((BasicBSONList)
					// o.get("works")).get(0)).get("description").toString();
					// if(!s.equals("N/A")){
					// System.out.println(s);
					// out.write(s);
					// out.newLine();
					// }
				}
			}
			cursor.close();
			out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getUserDescription(String userName, String userType,
			String startTimeStr, String endTimeStr) {
		try {
			String fileName = "data/topics/" + userName + startTimeStr + ".txt";
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName));

			Date startDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
					.parse(startTimeStr + "T00:00:00Z");
			Date endDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
					.parse(endTimeStr + "T00:00:00Z");
			BasicDBObject query = new BasicDBObject("date_sent",
					new BasicDBObject("$gte", startDate).append("$lt", endDate))
					.append(userType + "_name", userName);
			;

			DBCursor cursor = collection.find(query);
			while (cursor.hasNext()) {
				DBObject o = cursor.next();
				if (o.get("sender_name") != null) {
					//System.out.println(o);

					BasicBSONList blist = (BasicBSONList) o.get("works");
					if(blist==null){
						blist = (BasicBSONList) o.get("marks");
					}
					DBObject dbobj = null;
					if (blist != null) {
						dbobj = ((DBObject) blist.get(0));
					}
					Object obj = null;
					if (dbobj != null) {
						obj = dbobj.get("description");
						if (obj == null) {
							obj = dbobj.get("complaint");
						}
					}
					String s2 = (String) o.get("body");
					String s = "N/A";
					if (obj != null) {
						s = obj.toString().replaceAll("\n", " ")
								.replaceAll("\r", " ");
					}
					if (s2 != null) {
						s2 = s2.toString().replaceAll("\n", " ")
								.replaceAll("\r", " ");
					}
					if (!s.equals("N/A")) {
						// System.out.println(s);
						out.write(s);
						out.newLine();
					} else if (s2 != null) {
						// System.out.println(s2);
						out.write(s2);
						out.newLine();
					}
				}
			}
			cursor.close();
			out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Spike spike = new Spike();
		MongoDBQuery mg = new MongoDBQuery();

		for (int i = 0; i < spike.spike.length / 4; i++) {
			int index = 4 * i;
			mg.getUserDescription(spike.spike[index], spike.spike[index + 1],
					spike.spike[index + 2], spike.spike[index + 3]);
		}
	}
}
