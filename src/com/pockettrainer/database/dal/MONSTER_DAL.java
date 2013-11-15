package com.pockettrainer.database.dal;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import android.content.Context;
import com.j256.ormlite.dao.Dao;
import com.pockettrainer.database.DatabaseManager;
import com.pockettrainer.database.model.MONSTER;

public class MONSTER_DAL {
	private static Dao<MONSTER, Integer> getDAO(Context context)
	{
		try {
			return DatabaseManager.getHelper(context).getMONSTER_DAO();
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

	public static List<MONSTER> getMONSTER_All(Context context) throws SQLException
	{

		Dao<MONSTER, Integer> dao = getDAO(context);
		return dao.queryForAll();

	}

	public static List<MONSTER> getMONSTER_Multiple_ByMap(Context context, Map<String, Object> idMaps) throws SQLException
	{

		Dao<MONSTER, Integer> dao = getDAO(context);

		List<MONSTER> res = dao.queryForFieldValues(idMaps);

		return res;

	}

	/**
	 * INSERT METHODS
	 * 
	 * @throws SQLException
	 */

	public static void insertMONSTER(Context context, MONSTER data) throws SQLException
	{
		Dao<MONSTER, Integer> dao = getDAO(context);

		dao.create(data);

	}

	/**
	 * UPDATE METHODS
	 * 
	 * @throws SQLException
	 */

	public static void updateMONSTER(Context context, MONSTER data) throws SQLException
	{
		Dao<MONSTER, Integer> dao = getDAO(context);

		dao.update(data);

	}

	/**
	 * DELETE METHODS
	 * 
	 * @throws SQLException
	 */

	public static boolean deleteMONSTER(Context context, MONSTER data) throws SQLException
	{
		Dao<MONSTER, Integer> dao = getDAO(context);
		dao.delete(data);
		return true;
	}
}
