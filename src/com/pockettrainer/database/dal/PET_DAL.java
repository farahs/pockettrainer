package com.pockettrainer.database.dal;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import android.content.Context;
import com.j256.ormlite.dao.Dao;
import com.pockettrainer.database.DatabaseManager;
import com.pockettrainer.database.model.PET;

public class PET_DAL {
	private static Dao<PET, Integer> getDAO(Context context)
	{
		try {
			return DatabaseManager.getHelper(context).getPET_DAO();
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

	public static List<PET> getPET_All(Context context) throws SQLException
	{

		Dao<PET, Integer> dao = getDAO(context);
		return dao.queryForAll();

	}

	public static List<PET> getPET_Multiple_ByMap(Context context, Map<String, Object> idMaps) throws SQLException
	{

		Dao<PET, Integer> dao = getDAO(context);

		List<PET> res = dao.queryForFieldValues(idMaps);

		return res;

	}

	/**
	 * INSERT METHODS
	 * 
	 * @throws SQLException
	 */

	public static void insertPET(Context context, PET data) throws SQLException
	{
		Dao<PET, Integer> dao = getDAO(context);

		dao.create(data);

	}

	/**
	 * UPDATE METHODS
	 * 
	 * @throws SQLException
	 */

	public static void updatePET(Context context, PET data) throws SQLException
	{
		Dao<PET, Integer> dao = getDAO(context);

		dao.update(data);

	}

	/**
	 * DELETE METHODS
	 * 
	 * @throws SQLException
	 */

	public static boolean deletePET(Context context, PET data) throws SQLException
	{
		Dao<PET, Integer> dao = getDAO(context);
		dao.delete(data);
		return true;
	}
}