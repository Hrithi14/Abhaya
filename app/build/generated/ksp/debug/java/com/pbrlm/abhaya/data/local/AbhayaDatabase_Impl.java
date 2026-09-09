package com.pbrlm.abhaya.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.pbrlm.abhaya.data.local.dao.EmergencyRequestDao;
import com.pbrlm.abhaya.data.local.dao.EmergencyRequestDao_Impl;
import com.pbrlm.abhaya.data.local.dao.HelpOfferDao;
import com.pbrlm.abhaya.data.local.dao.HelpOfferDao_Impl;
import com.pbrlm.abhaya.data.local.dao.VolunteerDao;
import com.pbrlm.abhaya.data.local.dao.VolunteerDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AbhayaDatabase_Impl extends AbhayaDatabase {
  private volatile EmergencyRequestDao _emergencyRequestDao;

  private volatile VolunteerDao _volunteerDao;

  private volatile HelpOfferDao _helpOfferDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `emergency_requests` (`emergencyRequestId` TEXT NOT NULL, `userId` TEXT NOT NULL, `hazardId` TEXT, `clusterId` TEXT, `zoneId` TEXT, `emergencyType` TEXT NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `gpsAccuracy` REAL NOT NULL, `timestampMillis` INTEGER NOT NULL, `numberOfPeople` INTEGER NOT NULL, `children` INTEGER NOT NULL, `elderly` INTEGER NOT NULL, `disabled` INTEGER NOT NULL, `peopleTrapped` INTEGER NOT NULL, `injured` INTEGER NOT NULL, `medicalRequired` INTEGER NOT NULL, `waterDepth` TEXT, `waterLevel` TEXT, `waterRising` INTEGER, `medicalEmergencyType` TEXT, `personUnconscious` INTEGER NOT NULL, `breathingProblem` INTEGER NOT NULL, `severeBleeding` INTEGER NOT NULL, `pregnancyRelated` INTEGER NOT NULL, `description` TEXT NOT NULL, `buildingFloor` TEXT, `contactNumber` TEXT NOT NULL, `photoUrl` TEXT, `priority` TEXT NOT NULL, `status` TEXT NOT NULL, `assignedResponderId` TEXT, `responderContact` TEXT, `responseTimeMillis` INTEGER, `resolutionTimeMillis` INTEGER, `responderNotes` TEXT, `escalationRequired` INTEGER NOT NULL, `isDemoData` INTEGER NOT NULL, PRIMARY KEY(`emergencyRequestId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `volunteers` (`volunteerId` TEXT NOT NULL, `userId` TEXT NOT NULL, `name` TEXT NOT NULL, `contactNumber` TEXT NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `safeZoneStatus` INTEGER NOT NULL, `availableToHelp` INTEGER NOT NULL, `helpType` TEXT NOT NULL, `capacity` INTEGER NOT NULL, `notes` TEXT NOT NULL, PRIMARY KEY(`volunteerId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `help_offers` (`helpOfferId` TEXT NOT NULL, `emergencyRequestId` TEXT NOT NULL, `volunteerId` TEXT NOT NULL, `userId` TEXT NOT NULL, `helpType` TEXT NOT NULL, `message` TEXT NOT NULL, `timestampMillis` INTEGER NOT NULL, `status` TEXT NOT NULL, PRIMARY KEY(`helpOfferId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '9c218ea3a5ab516c3f7e58c1d8e666a4')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `emergency_requests`");
        db.execSQL("DROP TABLE IF EXISTS `volunteers`");
        db.execSQL("DROP TABLE IF EXISTS `help_offers`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsEmergencyRequests = new HashMap<String, TableInfo.Column>(38);
        _columnsEmergencyRequests.put("emergencyRequestId", new TableInfo.Column("emergencyRequestId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("userId", new TableInfo.Column("userId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("hazardId", new TableInfo.Column("hazardId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("clusterId", new TableInfo.Column("clusterId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("zoneId", new TableInfo.Column("zoneId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("emergencyType", new TableInfo.Column("emergencyType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("latitude", new TableInfo.Column("latitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("longitude", new TableInfo.Column("longitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("gpsAccuracy", new TableInfo.Column("gpsAccuracy", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("timestampMillis", new TableInfo.Column("timestampMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("numberOfPeople", new TableInfo.Column("numberOfPeople", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("children", new TableInfo.Column("children", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("elderly", new TableInfo.Column("elderly", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("disabled", new TableInfo.Column("disabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("peopleTrapped", new TableInfo.Column("peopleTrapped", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("injured", new TableInfo.Column("injured", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("medicalRequired", new TableInfo.Column("medicalRequired", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("waterDepth", new TableInfo.Column("waterDepth", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("waterLevel", new TableInfo.Column("waterLevel", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("waterRising", new TableInfo.Column("waterRising", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("medicalEmergencyType", new TableInfo.Column("medicalEmergencyType", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("personUnconscious", new TableInfo.Column("personUnconscious", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("breathingProblem", new TableInfo.Column("breathingProblem", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("severeBleeding", new TableInfo.Column("severeBleeding", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("pregnancyRelated", new TableInfo.Column("pregnancyRelated", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("buildingFloor", new TableInfo.Column("buildingFloor", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("contactNumber", new TableInfo.Column("contactNumber", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("photoUrl", new TableInfo.Column("photoUrl", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("priority", new TableInfo.Column("priority", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("assignedResponderId", new TableInfo.Column("assignedResponderId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("responderContact", new TableInfo.Column("responderContact", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("responseTimeMillis", new TableInfo.Column("responseTimeMillis", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("resolutionTimeMillis", new TableInfo.Column("resolutionTimeMillis", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("responderNotes", new TableInfo.Column("responderNotes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("escalationRequired", new TableInfo.Column("escalationRequired", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyRequests.put("isDemoData", new TableInfo.Column("isDemoData", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEmergencyRequests = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesEmergencyRequests = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoEmergencyRequests = new TableInfo("emergency_requests", _columnsEmergencyRequests, _foreignKeysEmergencyRequests, _indicesEmergencyRequests);
        final TableInfo _existingEmergencyRequests = TableInfo.read(db, "emergency_requests");
        if (!_infoEmergencyRequests.equals(_existingEmergencyRequests)) {
          return new RoomOpenHelper.ValidationResult(false, "emergency_requests(com.pbrlm.abhaya.data.local.entity.EmergencyRequestEntity).\n"
                  + " Expected:\n" + _infoEmergencyRequests + "\n"
                  + " Found:\n" + _existingEmergencyRequests);
        }
        final HashMap<String, TableInfo.Column> _columnsVolunteers = new HashMap<String, TableInfo.Column>(11);
        _columnsVolunteers.put("volunteerId", new TableInfo.Column("volunteerId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVolunteers.put("userId", new TableInfo.Column("userId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVolunteers.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVolunteers.put("contactNumber", new TableInfo.Column("contactNumber", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVolunteers.put("latitude", new TableInfo.Column("latitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVolunteers.put("longitude", new TableInfo.Column("longitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVolunteers.put("safeZoneStatus", new TableInfo.Column("safeZoneStatus", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVolunteers.put("availableToHelp", new TableInfo.Column("availableToHelp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVolunteers.put("helpType", new TableInfo.Column("helpType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVolunteers.put("capacity", new TableInfo.Column("capacity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVolunteers.put("notes", new TableInfo.Column("notes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysVolunteers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesVolunteers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoVolunteers = new TableInfo("volunteers", _columnsVolunteers, _foreignKeysVolunteers, _indicesVolunteers);
        final TableInfo _existingVolunteers = TableInfo.read(db, "volunteers");
        if (!_infoVolunteers.equals(_existingVolunteers)) {
          return new RoomOpenHelper.ValidationResult(false, "volunteers(com.pbrlm.abhaya.data.local.entity.VolunteerEntity).\n"
                  + " Expected:\n" + _infoVolunteers + "\n"
                  + " Found:\n" + _existingVolunteers);
        }
        final HashMap<String, TableInfo.Column> _columnsHelpOffers = new HashMap<String, TableInfo.Column>(8);
        _columnsHelpOffers.put("helpOfferId", new TableInfo.Column("helpOfferId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHelpOffers.put("emergencyRequestId", new TableInfo.Column("emergencyRequestId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHelpOffers.put("volunteerId", new TableInfo.Column("volunteerId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHelpOffers.put("userId", new TableInfo.Column("userId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHelpOffers.put("helpType", new TableInfo.Column("helpType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHelpOffers.put("message", new TableInfo.Column("message", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHelpOffers.put("timestampMillis", new TableInfo.Column("timestampMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHelpOffers.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysHelpOffers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesHelpOffers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoHelpOffers = new TableInfo("help_offers", _columnsHelpOffers, _foreignKeysHelpOffers, _indicesHelpOffers);
        final TableInfo _existingHelpOffers = TableInfo.read(db, "help_offers");
        if (!_infoHelpOffers.equals(_existingHelpOffers)) {
          return new RoomOpenHelper.ValidationResult(false, "help_offers(com.pbrlm.abhaya.data.local.entity.HelpOfferEntity).\n"
                  + " Expected:\n" + _infoHelpOffers + "\n"
                  + " Found:\n" + _existingHelpOffers);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "9c218ea3a5ab516c3f7e58c1d8e666a4", "b7db39db2cea4f4e60c97a26f41248b9");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "emergency_requests","volunteers","help_offers");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `emergency_requests`");
      _db.execSQL("DELETE FROM `volunteers`");
      _db.execSQL("DELETE FROM `help_offers`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(EmergencyRequestDao.class, EmergencyRequestDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(VolunteerDao.class, VolunteerDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(HelpOfferDao.class, HelpOfferDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public EmergencyRequestDao emergencyRequestDao() {
    if (_emergencyRequestDao != null) {
      return _emergencyRequestDao;
    } else {
      synchronized(this) {
        if(_emergencyRequestDao == null) {
          _emergencyRequestDao = new EmergencyRequestDao_Impl(this);
        }
        return _emergencyRequestDao;
      }
    }
  }

  @Override
  public VolunteerDao volunteerDao() {
    if (_volunteerDao != null) {
      return _volunteerDao;
    } else {
      synchronized(this) {
        if(_volunteerDao == null) {
          _volunteerDao = new VolunteerDao_Impl(this);
        }
        return _volunteerDao;
      }
    }
  }

  @Override
  public HelpOfferDao helpOfferDao() {
    if (_helpOfferDao != null) {
      return _helpOfferDao;
    } else {
      synchronized(this) {
        if(_helpOfferDao == null) {
          _helpOfferDao = new HelpOfferDao_Impl(this);
        }
        return _helpOfferDao;
      }
    }
  }
}
