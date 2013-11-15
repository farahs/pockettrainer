package com.pockettrainer.database.dal;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import android.content.Context;
import com.j256.ormlite.dao.Dao;
import com.pockettrainer.database.DatabaseManager;
import com.pockettrainer.database.model.TRAINING;

public class TRAINING_DAL {
	private static Dao<TRAINING, Integer> getDAO(Context context)
	{
		try {
			return DatabaseManager.getHelper(context).getTRAINING_DAO();
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

	public static List<TRAINING> getTRAINING_All(Context context) throws SQLException
	{

		Dao<TRAINING, Integer> dao = getDAO(context);
		return dao.queryForAll();

	}

	public static List<TRAINING> getTRAINING_Multiple_ByMap(Context context, Map<String, Object> idMaps) throws SQLException
	{

		Dao<TRAINING, Integer> dao = getDAO(context);

		List<TRAINING> res = dao.queryForFieldValues(idMaps);

		return res;

	}

	/**
	 * INSERT METHODS
	 * 
	 * @throws SQLException
	 */

	public static void insertTRAINING(Context context, TRAINING data) throws SQLException
	{
		Dao<TRAINING, Integer> dao = getDAO(context);

		dao.create(data);

	}

	/**
	 * UPDATE METHODS
	 * 
	 * @throws SQLException
	 */

	public static void updateTRAINING(Context context, TRAINING data) throws SQLException
	{
		Dao<TRAINING, Integer> dao = getDAO(context);

		dao.update(data);

	}

	/**
	 * DELETE METHODS
	 * 
	 * @throws SQLException
	 */

	public static boolean deleteTRAINING(Context context, TRAINING data) throws SQLException
	{
		Dao<TRAINING, Integer> dao = getDAO(context);
		dao.delete(data);
		return true;
	}
}
