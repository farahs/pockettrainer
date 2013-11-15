package com.pockettrainer.database.dal;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import com.j256.ormlite.dao.Dao;
import com.pockettrainer.database.DatabaseManager;
import com.pockettrainer.database.model.USER;

public class USER_DAL {
	private static Dao<USER, Integer> getDAO(Context context)
	{
		try {
			return DatabaseManager.getHelper(context).getUSER_DAO();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * GET METHODS
	 * 
	 * @throws SQLException
	 */

	public static List<USER> getUSER_All(Context context) throws SQLException
	{

		Dao<USER, Integer> dao = getDAO(context);
		return dao.queryForAll();

	}

	public static USER getTRAINING_Single(Context context, int id) {
		
		Dao<USER, Integer> dao = getDAO(context);
		USER myUser = new USER();
		
		try {
			myUser = dao.queryForId(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return myUser;
	}

	/**
	 * INSERT METHODS
	 * 
	 * @throws SQLException
	 */

	public static void insertUSER(Context context, USER data) throws SQLException
	{
		Dao<USER, Integer> dao = getDAO(context);

		dao.create(data);

	}

	/**
	 * UPDATE METHODS
	 * 
	 * @throws SQLException
	 */

	public static void updateUSER(Context context, USER data) throws SQLException
	{
		Dao<USER, Integer> dao = getDAO(context);

		dao.update(data);

	}

	/**
	 * DELETE METHODS
	 * 
	 * @throws SQLException
	 */

	public static boolean deleteUSER(Context context, USER data) throws SQLException
	{
		Dao<USER, Integer> dao = getDAO(context);
		dao.delete(data);
		return true;
	}
}
