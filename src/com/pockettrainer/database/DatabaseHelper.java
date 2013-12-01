package com.pockettrainer.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableUtils;
import com.pockettrainer.database.dal.MONSTER_DAL;
import com.pockettrainer.database.model.MONSTER;
import com.pockettrainer.database.model.PET;
import com.pockettrainer.database.model.TRAINING;
import com.pockettrainer.database.model.USER;
import com.pockettrainer.helper.UserSession;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "pockettrainer_db.sqlite";
	private Context context;
	private static final int DATABASE_VERSION = 7;

	private static DatabaseConnection databaseConnection;

	// MAIN TABLES
	private Dao<USER, Integer> USER_DAO = null;
	private Dao<PET, Integer> PET_DAO = null;
	private Dao<TRAINING, Integer> TRAINING_DAO = null;
	private Dao<MONSTER, Integer> MONSTER_DAO = null;

	public DatabaseHelper(Context c) {

		super(c, DATABASE_NAME, null, DATABASE_VERSION);

		this.context = c;

	}

	/**
	 * This is called when the database is first created. Usually you should
	 * call createTable statements here to create the tables that will store
	 * your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			DatabaseHelper.databaseConnection = connectionSource
					.getReadWriteConnection();
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			this.createAllTable();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This is called when your application is upgraded and it has a higher
	 * version number. This allows you to adjust the various data to match the
	 * new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int oldVersion, int newVersion) {

		try {
			DatabaseHelper.databaseConnection = connectionSource
					.getReadWriteConnection();
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");

			this.dropAllTable();
			this.onCreate(db, connectionSource);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private void createAllTable() {
		try {
			TableUtils.createTable(this.connectionSource, USER.class);
			TableUtils.createTable(this.connectionSource, PET.class);
			TableUtils.createTable(this.connectionSource, TRAINING.class);
			TableUtils.createTable(this.connectionSource, MONSTER.class);
			
			setInitData();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void dropAllTable() {
		try {
			TableUtils.dropTable(this.connectionSource, USER.class, true);
			TableUtils.dropTable(this.connectionSource, PET.class, true);
			TableUtils.dropTable(this.connectionSource, TRAINING.class, true);
			TableUtils.dropTable(this.connectionSource, MONSTER.class, true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the Database Access Object (DAO) for our classes. It will create
	 * it or just give the cached value.
	 */
	
	public Dao<USER, Integer> getUSER_DAO() throws SQLException {
		if (this.USER_DAO == null)
		{
			try
			{
				this.USER_DAO = this.getDao(USER.class);
			}
			catch (java.sql.SQLException e)
			{
				e.printStackTrace();
			}
		}
		return this.USER_DAO;
	}
	
	public Dao<PET, Integer> getPET_DAO() throws SQLException {
		if (this.PET_DAO == null)
		{
			try
			{
				this.PET_DAO = this.getDao(PET.class);
			}
			catch (java.sql.SQLException e)
			{
				e.printStackTrace();
			}
		}
		return this.PET_DAO;
	}
	
	public Dao<TRAINING, Integer> getTRAINING_DAO() throws SQLException {
		if (this.TRAINING_DAO == null)
		{
			try
			{
				this.TRAINING_DAO = this.getDao(TRAINING.class);
			}
			catch (java.sql.SQLException e)
			{
				e.printStackTrace();
			}
		}
		return this.TRAINING_DAO;
	}
	
	public Dao<MONSTER, Integer> getMONSTER_DAO() throws SQLException {
		if (this.MONSTER_DAO == null)
		{
			try
			{
				this.MONSTER_DAO = this.getDao(MONSTER.class);
			}
			catch (java.sql.SQLException e)
			{
				e.printStackTrace();
			}
		}
		return this.MONSTER_DAO;
	}
	
	/*
	 * TO SET INITIAL DATA TO DATABASE
	 */
	
	private void setInitData() {
		
		List<MONSTER> listMonster = new ArrayList<MONSTER>();
		
		MONSTER a = new MONSTER();
		a.setCODE("01");
		a.setNAME("Cyclops");
		a.setBASE_EXPERIENCE(150);
		a.setIMAGE("monster_cyclops");
		listMonster.add(a);
		
		MONSTER b = new MONSTER();
		b.setCODE("02");
		b.setNAME("Observer");
		b.setBASE_EXPERIENCE(150);
		b.setIMAGE("monster_observer");
		listMonster.add(b);
		
		MONSTER c = new MONSTER();
		c.setCODE("03");
		c.setNAME("Golem");
		c.setBASE_EXPERIENCE(150);
		b.setIMAGE("monster_golem");
		listMonster.add(c);
		
		MONSTER d = new MONSTER();
		d.setCODE("04");
		d.setNAME("Darkness");
		d.setBASE_EXPERIENCE(150);
		d.setIMAGE("monster_darkness");
		listMonster.add(d);
		
		MONSTER e = new MONSTER();
		e.setCODE("05");
		e.setNAME("Gillian");
		e.setBASE_EXPERIENCE(75);
		e.setIMAGE("monster_gillian");
		listMonster.add(e);
		
		MONSTER f = new MONSTER();
		f.setCODE("06");
		f.setNAME("Frogger");
		f.setBASE_EXPERIENCE(75);
		f.setIMAGE("monster_frogger");
		listMonster.add(f);
		
		MONSTER g = new MONSTER();
		g.setCODE("07");
		g.setNAME("Papabear");
		g.setBASE_EXPERIENCE(300);
		g.setIMAGE("monster_papabear");
		listMonster.add(g);
		
		MONSTER h = new MONSTER();
		h.setCODE("08");
		h.setNAME("Slime");
		h.setBASE_EXPERIENCE(50);
		h.setIMAGE("monster_slime");
		listMonster.add(h);
		
		MONSTER i = new MONSTER();
		i.setCODE("09");
		i.setNAME("Ogre");
		i.setBASE_EXPERIENCE(100);
		i.setIMAGE("monster_ogre");
		listMonster.add(i);
		
		MONSTER j = new MONSTER();
		j.setCODE("10");
		j.setNAME("Owl");
		j.setBASE_EXPERIENCE(500);
		j.setIMAGE("monster_owl");
		listMonster.add(j);
		
		for (MONSTER monster : listMonster) {
			try {
				MONSTER_DAL.insertMONSTER(context, monster);
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		
	}
	
} 
