package com.pockettrainer.database.dal;

import java.sql.SQLException;
import java.util.List;

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

	public static MONSTER getTRAINING_Single(Context context, int id) {
		
		Dao<MONSTER, Integer> dao = getDAO(context);
		MONSTER myMonster = new MONSTER();
		
		try {
			myMonster = dao.queryForId(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return myMonster;
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
