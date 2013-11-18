package com.pockettrainer.database.dal;

import java.sql.SQLException;
import java.util.List;

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

	public static PET getPET_Single(Context context, int id) {
		
		Dao<PET, Integer> dao = getDAO(context);
		PET myPet = new PET();
		
		try {
			myPet = dao.queryForId(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return myPet;
	}

	public static PET getPET_SingleByUserId(Context context, int userId) {
		
		Dao<PET, Integer> dao = getDAO(context);
		PET myPet = new PET();
		
		try {
			myPet = dao.queryForEq("USER_ID", userId).get(0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return myPet;
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
