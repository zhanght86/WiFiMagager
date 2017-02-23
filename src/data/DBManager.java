package data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

	private DBHelper helper;
	private SQLiteDatabase db;

	public DBManager(Context context) {
		helper = DBHelper.getInstance(context);
		db = helper.getWritableDatabase();
	}

	public void add(List<MyWifiInfo> wifis) {
		db.beginTransaction();
		try {
			for (MyWifiInfo wifi : wifis) {
				db.execSQL(
						"insert into wifis values(null,?,?,?,?)",
						new Object[] { wifi.getName(), wifi.getNet_id(),
								wifi.getPriority(), wifi.getBelong() });
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public boolean addOne(MyWifiInfo wifi) {
		ContentValues cv = new ContentValues();
		cv.put("name", wifi.getName());
		cv.put("net_id", wifi.getNet_id());
		cv.put("priority", wifi.getPriority());
		cv.put("belong", wifi.getBelong());
		if (this.db.insert("wifis", null, cv) == -1) {
			return false;
		}
		return true;
	}

	public boolean updateName(MyWifiInfo wifi, String name) {
		ContentValues cv = new ContentValues();
		cv.put("name", name);
		if (db.update("wifis", cv, "name=?", new String[] { wifi.getName() }) == 0) {
			return false;
		}
		return true;
	}

	public boolean updateNetid(MyWifiInfo wifi, int newid) {
		ContentValues cv = new ContentValues();
		cv.put("net_id", newid);
		if (db.update("wifis", cv, "net_id=?",
				new String[] { Integer.toString(wifi.getNet_id()) }) == 0) {
			return false;
		}
		return true;
	}

	public boolean updateNetid(String name, int newid) {
		ContentValues cv = new ContentValues();
		cv.put("net_id", newid);
		if (db.update("wifis", cv, "name=?", new String[] { name }) == 0) {
			return false;
		}
		return true;
	}

	public boolean updatePriority(MyWifiInfo wifi, int priority) {
		ContentValues cv = new ContentValues();
		cv.put("priority", priority);
		if (db.update(
				"wifis",
				cv,
				"name=? and net_id=?",
				new String[] { wifi.getName(),
						Integer.toString(wifi.getNet_id()) }) == 0) {
			return false;
		}
		return true;
	}

	public boolean updatePriorityAndBelong(String name, int netid,
			int priority, String belong) {
		ContentValues cv = new ContentValues();
		cv.put("priority", priority);
		cv.put("belong", belong);
		if (db.update("wifis", cv, "name=? and net_id=?", new String[] { name,
				Integer.toString(netid) }) == 0) {
			return false;
		}
		return true;
	}

	public boolean updateBelong(MyWifiInfo wifi, String name) {
		ContentValues cv = new ContentValues();
		cv.put("belong", name);
		if (db.update(
				"wifis",
				cv,
				"name=? and net_id=?",
				new String[] { wifi.getName(),
						Integer.toString(wifi.getNet_id()) }) == 0) {
			return false;
		}
		return true;
	}

	public boolean deleteWifiByName(String name) {
		if (db.delete("wifis", "name=?", new String[] { name }) == 0) {
			return false;
		}
		return true;
	}

	public boolean deleteWifiInfo(MyWifiInfo wifi) {
		if (db.delete(
				"wifis",
				"name=? and net_id=?",
				new String[] { String.valueOf(wifi.getName()),
						Integer.toString(wifi.getNet_id()) }) == 0) {
			return false;
		}
		return true;
	}

	public List<MyWifiInfo> findLastWifi() {
		return null;
	}

	public boolean deleteWifiById(int netid) {
		if (db.delete("wifis", "net_id=?",
				new String[] { Integer.toString(netid) }) == 0) {
			return false;
		}
		return true;
	}

	public ArrayList<MyWifiInfo> query(int cc) {
		ArrayList<MyWifiInfo> wifis = new ArrayList<MyWifiInfo>();
		Cursor c;
		if (cc == 2) {
			c = queryTheCursor();
		} else if (cc == 1) {
			c = queryThePCursor();
		} else {
			c = queryTheLCursor();
		}
		if (c.moveToFirst()) {
			do {
				MyWifiInfo wifi = new MyWifiInfo();
				wifi.setNet_id(c.getInt(c.getColumnIndex("net_id")));
				wifi.setName(c.getString(c.getColumnIndex("name")));
				wifi.setPriority(c.getInt(c.getColumnIndex("priority")));
				wifi.setBelong(c.getString(c.getColumnIndex("belong")));
				wifis.add(wifi);
			} while (c.moveToNext());
		}
		c.close();
		return wifis;
	}

	public boolean isHaveThisWifi(String name, int id) {
		Cursor c = db.rawQuery(
				"select * from wifis  where name=? and net_id=?", new String[] {
						name, Integer.toString(id) });
		if (c.moveToNext()) {
			c.close();
			return true;
		}
		return false;
	}

	public boolean isHaveThisWifi(String name) {
		Cursor c = db.rawQuery("select * from wifis where name=?",
				new String[] { name });
		if (c.moveToNext()) {
			c.close();
			return true;
		}
		return false;
	}

	public MyWifiInfo findByNameAndId(String name, int id) {
		Cursor c = db.rawQuery(
				"select * from wifis  where name=? and net_id=?", new String[] {
						name, Integer.toString(id) });
		if (c.moveToNext()) {
			MyWifiInfo wifi = new MyWifiInfo();
			wifi.setNet_id(c.getInt(c.getColumnIndex("net_id")));
			wifi.setName(c.getString(c.getColumnIndex("name")));
			wifi.setPriority(c.getInt(c.getColumnIndex("priority")));
			wifi.setBelong(c.getString(c.getColumnIndex("belong")));
			c.close();
			return wifi;
		}
		return null;
	}

	public MyWifiInfo findById(int id) {
		Cursor c = db.rawQuery("select * from wifis  where net_id=?",
				new String[] { Integer.toString(id) });
		if (c.moveToNext()) {
			MyWifiInfo wifi = new MyWifiInfo();
			wifi.setNet_id(c.getInt(c.getColumnIndex("net_id")));
			wifi.setName(c.getString(c.getColumnIndex("name")));
			wifi.setPriority(c.getInt(c.getColumnIndex("priority")));
			wifi.setBelong(c.getString(c.getColumnIndex("belong")));
			c.close();
			return wifi;
		}
		return null;
	}

	public Cursor queryTheCursor() {
		Cursor c = db.rawQuery("select * from wifis", null);
		return c;
	}

	public Cursor queryThePCursor() {
		Cursor c = db.rawQuery("select * from wifis where belong=?",
				new String[] { "first" });
		return c;
	}

	public Cursor queryTheLCursor() {
		Cursor c = db.rawQuery("select * from wifis where belong=?",
				new String[] { "last" });
		return c;
	}

	public void closeDB() {
		db.close();
	}
}
