package com.pockettrainer.database.dal;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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

	public static List<USER> getUSER_Multiple_ByMap(Context context, Map<String, Object> idMaps) throws SQLException
	{

		Dao<USER, Integer> dao = getDAO(context);

		List<USER> res = dao.queryForFieldValues(idMaps);

		return res;

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
