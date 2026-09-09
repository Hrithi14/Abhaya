package com.pbrlm.abhaya.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.pbrlm.abhaya.data.local.entity.EmergencyRequestEntity;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class EmergencyRequestDao_Impl implements EmergencyRequestDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<EmergencyRequestEntity> __insertionAdapterOfEmergencyRequestEntity;

  private final EntityDeletionOrUpdateAdapter<EmergencyRequestEntity> __updateAdapterOfEmergencyRequestEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateStatus;

  private final SharedSQLiteStatement __preparedStmtOfMarkEscalationRequired;

  private final SharedSQLiteStatement __preparedStmtOfDelete;

  public EmergencyRequestDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEmergencyRequestEntity = new EntityInsertionAdapter<EmergencyRequestEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `emergency_requests` (`emergencyRequestId`,`userId`,`hazardId`,`clusterId`,`zoneId`,`emergencyType`,`latitude`,`longitude`,`gpsAccuracy`,`timestampMillis`,`numberOfPeople`,`children`,`elderly`,`disabled`,`peopleTrapped`,`injured`,`medicalRequired`,`waterDepth`,`waterLevel`,`waterRising`,`medicalEmergencyType`,`personUnconscious`,`breathingProblem`,`severeBleeding`,`pregnancyRelated`,`description`,`buildingFloor`,`contactNumber`,`photoUrl`,`priority`,`status`,`assignedResponderId`,`responderContact`,`responseTimeMillis`,`resolutionTimeMillis`,`responderNotes`,`escalationRequired`,`isDemoData`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EmergencyRequestEntity entity) {
        statement.bindString(1, entity.getEmergencyRequestId());
        statement.bindString(2, entity.getUserId());
        if (entity.getHazardId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getHazardId());
        }
        if (entity.getClusterId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getClusterId());
        }
        if (entity.getZoneId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getZoneId());
        }
        statement.bindString(6, entity.getEmergencyType());
        statement.bindDouble(7, entity.getLatitude());
        statement.bindDouble(8, entity.getLongitude());
        statement.bindDouble(9, entity.getGpsAccuracy());
        statement.bindLong(10, entity.getTimestampMillis());
        statement.bindLong(11, entity.getNumberOfPeople());
        final int _tmp = entity.getChildren() ? 1 : 0;
        statement.bindLong(12, _tmp);
        final int _tmp_1 = entity.getElderly() ? 1 : 0;
        statement.bindLong(13, _tmp_1);
        final int _tmp_2 = entity.getDisabled() ? 1 : 0;
        statement.bindLong(14, _tmp_2);
        final int _tmp_3 = entity.getPeopleTrapped() ? 1 : 0;
        statement.bindLong(15, _tmp_3);
        final int _tmp_4 = entity.getInjured() ? 1 : 0;
        statement.bindLong(16, _tmp_4);
        final int _tmp_5 = entity.getMedicalRequired() ? 1 : 0;
        statement.bindLong(17, _tmp_5);
        if (entity.getWaterDepth() == null) {
          statement.bindNull(18);
        } else {
          statement.bindString(18, entity.getWaterDepth());
        }
        if (entity.getWaterLevel() == null) {
          statement.bindNull(19);
        } else {
          statement.bindString(19, entity.getWaterLevel());
        }
        final Integer _tmp_6 = entity.getWaterRising() == null ? null : (entity.getWaterRising() ? 1 : 0);
        if (_tmp_6 == null) {
          statement.bindNull(20);
        } else {
          statement.bindLong(20, _tmp_6);
        }
        if (entity.getMedicalEmergencyType() == null) {
          statement.bindNull(21);
        } else {
          statement.bindString(21, entity.getMedicalEmergencyType());
        }
        final int _tmp_7 = entity.getPersonUnconscious() ? 1 : 0;
        statement.bindLong(22, _tmp_7);
        final int _tmp_8 = entity.getBreathingProblem() ? 1 : 0;
        statement.bindLong(23, _tmp_8);
        final int _tmp_9 = entity.getSevereBleeding() ? 1 : 0;
        statement.bindLong(24, _tmp_9);
        final int _tmp_10 = entity.getPregnancyRelated() ? 1 : 0;
        statement.bindLong(25, _tmp_10);
        statement.bindString(26, entity.getDescription());
        if (entity.getBuildingFloor() == null) {
          statement.bindNull(27);
        } else {
          statement.bindString(27, entity.getBuildingFloor());
        }
        statement.bindString(28, entity.getContactNumber());
        if (entity.getPhotoUrl() == null) {
          statement.bindNull(29);
        } else {
          statement.bindString(29, entity.getPhotoUrl());
        }
        statement.bindString(30, entity.getPriority());
        statement.bindString(31, entity.getStatus());
        if (entity.getAssignedResponderId() == null) {
          statement.bindNull(32);
        } else {
          statement.bindString(32, entity.getAssignedResponderId());
        }
        if (entity.getResponderContact() == null) {
          statement.bindNull(33);
        } else {
          statement.bindString(33, entity.getResponderContact());
        }
        if (entity.getResponseTimeMillis() == null) {
          statement.bindNull(34);
        } else {
          statement.bindLong(34, entity.getResponseTimeMillis());
        }
        if (entity.getResolutionTimeMillis() == null) {
          statement.bindNull(35);
        } else {
          statement.bindLong(35, entity.getResolutionTimeMillis());
        }
        if (entity.getResponderNotes() == null) {
          statement.bindNull(36);
        } else {
          statement.bindString(36, entity.getResponderNotes());
        }
        final int _tmp_11 = entity.getEscalationRequired() ? 1 : 0;
        statement.bindLong(37, _tmp_11);
        final int _tmp_12 = entity.isDemoData() ? 1 : 0;
        statement.bindLong(38, _tmp_12);
      }
    };
    this.__updateAdapterOfEmergencyRequestEntity = new EntityDeletionOrUpdateAdapter<EmergencyRequestEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `emergency_requests` SET `emergencyRequestId` = ?,`userId` = ?,`hazardId` = ?,`clusterId` = ?,`zoneId` = ?,`emergencyType` = ?,`latitude` = ?,`longitude` = ?,`gpsAccuracy` = ?,`timestampMillis` = ?,`numberOfPeople` = ?,`children` = ?,`elderly` = ?,`disabled` = ?,`peopleTrapped` = ?,`injured` = ?,`medicalRequired` = ?,`waterDepth` = ?,`waterLevel` = ?,`waterRising` = ?,`medicalEmergencyType` = ?,`personUnconscious` = ?,`breathingProblem` = ?,`severeBleeding` = ?,`pregnancyRelated` = ?,`description` = ?,`buildingFloor` = ?,`contactNumber` = ?,`photoUrl` = ?,`priority` = ?,`status` = ?,`assignedResponderId` = ?,`responderContact` = ?,`responseTimeMillis` = ?,`resolutionTimeMillis` = ?,`responderNotes` = ?,`escalationRequired` = ?,`isDemoData` = ? WHERE `emergencyRequestId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EmergencyRequestEntity entity) {
        statement.bindString(1, entity.getEmergencyRequestId());
        statement.bindString(2, entity.getUserId());
        if (entity.getHazardId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getHazardId());
        }
        if (entity.getClusterId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getClusterId());
        }
        if (entity.getZoneId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getZoneId());
        }
        statement.bindString(6, entity.getEmergencyType());
        statement.bindDouble(7, entity.getLatitude());
        statement.bindDouble(8, entity.getLongitude());
        statement.bindDouble(9, entity.getGpsAccuracy());
        statement.bindLong(10, entity.getTimestampMillis());
        statement.bindLong(11, entity.getNumberOfPeople());
        final int _tmp = entity.getChildren() ? 1 : 0;
        statement.bindLong(12, _tmp);
        final int _tmp_1 = entity.getElderly() ? 1 : 0;
        statement.bindLong(13, _tmp_1);
        final int _tmp_2 = entity.getDisabled() ? 1 : 0;
        statement.bindLong(14, _tmp_2);
        final int _tmp_3 = entity.getPeopleTrapped() ? 1 : 0;
        statement.bindLong(15, _tmp_3);
        final int _tmp_4 = entity.getInjured() ? 1 : 0;
        statement.bindLong(16, _tmp_4);
        final int _tmp_5 = entity.getMedicalRequired() ? 1 : 0;
        statement.bindLong(17, _tmp_5);
        if (entity.getWaterDepth() == null) {
          statement.bindNull(18);
        } else {
          statement.bindString(18, entity.getWaterDepth());
        }
        if (entity.getWaterLevel() == null) {
          statement.bindNull(19);
        } else {
          statement.bindString(19, entity.getWaterLevel());
        }
        final Integer _tmp_6 = entity.getWaterRising() == null ? null : (entity.getWaterRising() ? 1 : 0);
        if (_tmp_6 == null) {
          statement.bindNull(20);
        } else {
          statement.bindLong(20, _tmp_6);
        }
        if (entity.getMedicalEmergencyType() == null) {
          statement.bindNull(21);
        } else {
          statement.bindString(21, entity.getMedicalEmergencyType());
        }
        final int _tmp_7 = entity.getPersonUnconscious() ? 1 : 0;
        statement.bindLong(22, _tmp_7);
        final int _tmp_8 = entity.getBreathingProblem() ? 1 : 0;
        statement.bindLong(23, _tmp_8);
        final int _tmp_9 = entity.getSevereBleeding() ? 1 : 0;
        statement.bindLong(24, _tmp_9);
        final int _tmp_10 = entity.getPregnancyRelated() ? 1 : 0;
        statement.bindLong(25, _tmp_10);
        statement.bindString(26, entity.getDescription());
        if (entity.getBuildingFloor() == null) {
          statement.bindNull(27);
        } else {
          statement.bindString(27, entity.getBuildingFloor());
        }
        statement.bindString(28, entity.getContactNumber());
        if (entity.getPhotoUrl() == null) {
          statement.bindNull(29);
        } else {
          statement.bindString(29, entity.getPhotoUrl());
        }
        statement.bindString(30, entity.getPriority());
        statement.bindString(31, entity.getStatus());
        if (entity.getAssignedResponderId() == null) {
          statement.bindNull(32);
        } else {
          statement.bindString(32, entity.getAssignedResponderId());
        }
        if (entity.getResponderContact() == null) {
          statement.bindNull(33);
        } else {
          statement.bindString(33, entity.getResponderContact());
        }
        if (entity.getResponseTimeMillis() == null) {
          statement.bindNull(34);
        } else {
          statement.bindLong(34, entity.getResponseTimeMillis());
        }
        if (entity.getResolutionTimeMillis() == null) {
          statement.bindNull(35);
        } else {
          statement.bindLong(35, entity.getResolutionTimeMillis());
        }
        if (entity.getResponderNotes() == null) {
          statement.bindNull(36);
        } else {
          statement.bindString(36, entity.getResponderNotes());
        }
        final int _tmp_11 = entity.getEscalationRequired() ? 1 : 0;
        statement.bindLong(37, _tmp_11);
        final int _tmp_12 = entity.isDemoData() ? 1 : 0;
        statement.bindLong(38, _tmp_12);
        statement.bindString(39, entity.getEmergencyRequestId());
      }
    };
    this.__preparedStmtOfUpdateStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE emergency_requests SET status = ? WHERE emergencyRequestId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkEscalationRequired = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE emergency_requests SET escalationRequired = 1 WHERE emergencyRequestId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM emergency_requests WHERE emergencyRequestId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final EmergencyRequestEntity entity,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfEmergencyRequestEntity.insert(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final EmergencyRequestEntity entity,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfEmergencyRequestEntity.handle(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateStatus(final String id, final String status,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markEscalationRequired(final String id,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkEscalationRequired.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfMarkEscalationRequired.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDelete.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDelete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final String id,
      final Continuation<? super EmergencyRequestEntity> $completion) {
    final String _sql = "SELECT * FROM emergency_requests WHERE emergencyRequestId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<EmergencyRequestEntity>() {
      @Override
      @Nullable
      public EmergencyRequestEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfEmergencyRequestId = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyRequestId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfHazardId = CursorUtil.getColumnIndexOrThrow(_cursor, "hazardId");
          final int _cursorIndexOfClusterId = CursorUtil.getColumnIndexOrThrow(_cursor, "clusterId");
          final int _cursorIndexOfZoneId = CursorUtil.getColumnIndexOrThrow(_cursor, "zoneId");
          final int _cursorIndexOfEmergencyType = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyType");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfGpsAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "gpsAccuracy");
          final int _cursorIndexOfTimestampMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "timestampMillis");
          final int _cursorIndexOfNumberOfPeople = CursorUtil.getColumnIndexOrThrow(_cursor, "numberOfPeople");
          final int _cursorIndexOfChildren = CursorUtil.getColumnIndexOrThrow(_cursor, "children");
          final int _cursorIndexOfElderly = CursorUtil.getColumnIndexOrThrow(_cursor, "elderly");
          final int _cursorIndexOfDisabled = CursorUtil.getColumnIndexOrThrow(_cursor, "disabled");
          final int _cursorIndexOfPeopleTrapped = CursorUtil.getColumnIndexOrThrow(_cursor, "peopleTrapped");
          final int _cursorIndexOfInjured = CursorUtil.getColumnIndexOrThrow(_cursor, "injured");
          final int _cursorIndexOfMedicalRequired = CursorUtil.getColumnIndexOrThrow(_cursor, "medicalRequired");
          final int _cursorIndexOfWaterDepth = CursorUtil.getColumnIndexOrThrow(_cursor, "waterDepth");
          final int _cursorIndexOfWaterLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "waterLevel");
          final int _cursorIndexOfWaterRising = CursorUtil.getColumnIndexOrThrow(_cursor, "waterRising");
          final int _cursorIndexOfMedicalEmergencyType = CursorUtil.getColumnIndexOrThrow(_cursor, "medicalEmergencyType");
          final int _cursorIndexOfPersonUnconscious = CursorUtil.getColumnIndexOrThrow(_cursor, "personUnconscious");
          final int _cursorIndexOfBreathingProblem = CursorUtil.getColumnIndexOrThrow(_cursor, "breathingProblem");
          final int _cursorIndexOfSevereBleeding = CursorUtil.getColumnIndexOrThrow(_cursor, "severeBleeding");
          final int _cursorIndexOfPregnancyRelated = CursorUtil.getColumnIndexOrThrow(_cursor, "pregnancyRelated");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfBuildingFloor = CursorUtil.getColumnIndexOrThrow(_cursor, "buildingFloor");
          final int _cursorIndexOfContactNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "contactNumber");
          final int _cursorIndexOfPhotoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "photoUrl");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAssignedResponderId = CursorUtil.getColumnIndexOrThrow(_cursor, "assignedResponderId");
          final int _cursorIndexOfResponderContact = CursorUtil.getColumnIndexOrThrow(_cursor, "responderContact");
          final int _cursorIndexOfResponseTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "responseTimeMillis");
          final int _cursorIndexOfResolutionTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "resolutionTimeMillis");
          final int _cursorIndexOfResponderNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "responderNotes");
          final int _cursorIndexOfEscalationRequired = CursorUtil.getColumnIndexOrThrow(_cursor, "escalationRequired");
          final int _cursorIndexOfIsDemoData = CursorUtil.getColumnIndexOrThrow(_cursor, "isDemoData");
          final EmergencyRequestEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpEmergencyRequestId;
            _tmpEmergencyRequestId = _cursor.getString(_cursorIndexOfEmergencyRequestId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpHazardId;
            if (_cursor.isNull(_cursorIndexOfHazardId)) {
              _tmpHazardId = null;
            } else {
              _tmpHazardId = _cursor.getString(_cursorIndexOfHazardId);
            }
            final String _tmpClusterId;
            if (_cursor.isNull(_cursorIndexOfClusterId)) {
              _tmpClusterId = null;
            } else {
              _tmpClusterId = _cursor.getString(_cursorIndexOfClusterId);
            }
            final String _tmpZoneId;
            if (_cursor.isNull(_cursorIndexOfZoneId)) {
              _tmpZoneId = null;
            } else {
              _tmpZoneId = _cursor.getString(_cursorIndexOfZoneId);
            }
            final String _tmpEmergencyType;
            _tmpEmergencyType = _cursor.getString(_cursorIndexOfEmergencyType);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final float _tmpGpsAccuracy;
            _tmpGpsAccuracy = _cursor.getFloat(_cursorIndexOfGpsAccuracy);
            final long _tmpTimestampMillis;
            _tmpTimestampMillis = _cursor.getLong(_cursorIndexOfTimestampMillis);
            final int _tmpNumberOfPeople;
            _tmpNumberOfPeople = _cursor.getInt(_cursorIndexOfNumberOfPeople);
            final boolean _tmpChildren;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfChildren);
            _tmpChildren = _tmp != 0;
            final boolean _tmpElderly;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfElderly);
            _tmpElderly = _tmp_1 != 0;
            final boolean _tmpDisabled;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfDisabled);
            _tmpDisabled = _tmp_2 != 0;
            final boolean _tmpPeopleTrapped;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfPeopleTrapped);
            _tmpPeopleTrapped = _tmp_3 != 0;
            final boolean _tmpInjured;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfInjured);
            _tmpInjured = _tmp_4 != 0;
            final boolean _tmpMedicalRequired;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfMedicalRequired);
            _tmpMedicalRequired = _tmp_5 != 0;
            final String _tmpWaterDepth;
            if (_cursor.isNull(_cursorIndexOfWaterDepth)) {
              _tmpWaterDepth = null;
            } else {
              _tmpWaterDepth = _cursor.getString(_cursorIndexOfWaterDepth);
            }
            final String _tmpWaterLevel;
            if (_cursor.isNull(_cursorIndexOfWaterLevel)) {
              _tmpWaterLevel = null;
            } else {
              _tmpWaterLevel = _cursor.getString(_cursorIndexOfWaterLevel);
            }
            final Boolean _tmpWaterRising;
            final Integer _tmp_6;
            if (_cursor.isNull(_cursorIndexOfWaterRising)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getInt(_cursorIndexOfWaterRising);
            }
            _tmpWaterRising = _tmp_6 == null ? null : _tmp_6 != 0;
            final String _tmpMedicalEmergencyType;
            if (_cursor.isNull(_cursorIndexOfMedicalEmergencyType)) {
              _tmpMedicalEmergencyType = null;
            } else {
              _tmpMedicalEmergencyType = _cursor.getString(_cursorIndexOfMedicalEmergencyType);
            }
            final boolean _tmpPersonUnconscious;
            final int _tmp_7;
            _tmp_7 = _cursor.getInt(_cursorIndexOfPersonUnconscious);
            _tmpPersonUnconscious = _tmp_7 != 0;
            final boolean _tmpBreathingProblem;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfBreathingProblem);
            _tmpBreathingProblem = _tmp_8 != 0;
            final boolean _tmpSevereBleeding;
            final int _tmp_9;
            _tmp_9 = _cursor.getInt(_cursorIndexOfSevereBleeding);
            _tmpSevereBleeding = _tmp_9 != 0;
            final boolean _tmpPregnancyRelated;
            final int _tmp_10;
            _tmp_10 = _cursor.getInt(_cursorIndexOfPregnancyRelated);
            _tmpPregnancyRelated = _tmp_10 != 0;
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpBuildingFloor;
            if (_cursor.isNull(_cursorIndexOfBuildingFloor)) {
              _tmpBuildingFloor = null;
            } else {
              _tmpBuildingFloor = _cursor.getString(_cursorIndexOfBuildingFloor);
            }
            final String _tmpContactNumber;
            _tmpContactNumber = _cursor.getString(_cursorIndexOfContactNumber);
            final String _tmpPhotoUrl;
            if (_cursor.isNull(_cursorIndexOfPhotoUrl)) {
              _tmpPhotoUrl = null;
            } else {
              _tmpPhotoUrl = _cursor.getString(_cursorIndexOfPhotoUrl);
            }
            final String _tmpPriority;
            _tmpPriority = _cursor.getString(_cursorIndexOfPriority);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpAssignedResponderId;
            if (_cursor.isNull(_cursorIndexOfAssignedResponderId)) {
              _tmpAssignedResponderId = null;
            } else {
              _tmpAssignedResponderId = _cursor.getString(_cursorIndexOfAssignedResponderId);
            }
            final String _tmpResponderContact;
            if (_cursor.isNull(_cursorIndexOfResponderContact)) {
              _tmpResponderContact = null;
            } else {
              _tmpResponderContact = _cursor.getString(_cursorIndexOfResponderContact);
            }
            final Long _tmpResponseTimeMillis;
            if (_cursor.isNull(_cursorIndexOfResponseTimeMillis)) {
              _tmpResponseTimeMillis = null;
            } else {
              _tmpResponseTimeMillis = _cursor.getLong(_cursorIndexOfResponseTimeMillis);
            }
            final Long _tmpResolutionTimeMillis;
            if (_cursor.isNull(_cursorIndexOfResolutionTimeMillis)) {
              _tmpResolutionTimeMillis = null;
            } else {
              _tmpResolutionTimeMillis = _cursor.getLong(_cursorIndexOfResolutionTimeMillis);
            }
            final String _tmpResponderNotes;
            if (_cursor.isNull(_cursorIndexOfResponderNotes)) {
              _tmpResponderNotes = null;
            } else {
              _tmpResponderNotes = _cursor.getString(_cursorIndexOfResponderNotes);
            }
            final boolean _tmpEscalationRequired;
            final int _tmp_11;
            _tmp_11 = _cursor.getInt(_cursorIndexOfEscalationRequired);
            _tmpEscalationRequired = _tmp_11 != 0;
            final boolean _tmpIsDemoData;
            final int _tmp_12;
            _tmp_12 = _cursor.getInt(_cursorIndexOfIsDemoData);
            _tmpIsDemoData = _tmp_12 != 0;
            _result = new EmergencyRequestEntity(_tmpEmergencyRequestId,_tmpUserId,_tmpHazardId,_tmpClusterId,_tmpZoneId,_tmpEmergencyType,_tmpLatitude,_tmpLongitude,_tmpGpsAccuracy,_tmpTimestampMillis,_tmpNumberOfPeople,_tmpChildren,_tmpElderly,_tmpDisabled,_tmpPeopleTrapped,_tmpInjured,_tmpMedicalRequired,_tmpWaterDepth,_tmpWaterLevel,_tmpWaterRising,_tmpMedicalEmergencyType,_tmpPersonUnconscious,_tmpBreathingProblem,_tmpSevereBleeding,_tmpPregnancyRelated,_tmpDescription,_tmpBuildingFloor,_tmpContactNumber,_tmpPhotoUrl,_tmpPriority,_tmpStatus,_tmpAssignedResponderId,_tmpResponderContact,_tmpResponseTimeMillis,_tmpResolutionTimeMillis,_tmpResponderNotes,_tmpEscalationRequired,_tmpIsDemoData);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<EmergencyRequestEntity>> observeActive(final String userId) {
    final String _sql = "SELECT * FROM emergency_requests WHERE userId = ? AND status NOT IN ('RESOLVED','CANCELLED','FALSE_REPORT') ORDER BY timestampMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"emergency_requests"}, new Callable<List<EmergencyRequestEntity>>() {
      @Override
      @NonNull
      public List<EmergencyRequestEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfEmergencyRequestId = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyRequestId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfHazardId = CursorUtil.getColumnIndexOrThrow(_cursor, "hazardId");
          final int _cursorIndexOfClusterId = CursorUtil.getColumnIndexOrThrow(_cursor, "clusterId");
          final int _cursorIndexOfZoneId = CursorUtil.getColumnIndexOrThrow(_cursor, "zoneId");
          final int _cursorIndexOfEmergencyType = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyType");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfGpsAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "gpsAccuracy");
          final int _cursorIndexOfTimestampMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "timestampMillis");
          final int _cursorIndexOfNumberOfPeople = CursorUtil.getColumnIndexOrThrow(_cursor, "numberOfPeople");
          final int _cursorIndexOfChildren = CursorUtil.getColumnIndexOrThrow(_cursor, "children");
          final int _cursorIndexOfElderly = CursorUtil.getColumnIndexOrThrow(_cursor, "elderly");
          final int _cursorIndexOfDisabled = CursorUtil.getColumnIndexOrThrow(_cursor, "disabled");
          final int _cursorIndexOfPeopleTrapped = CursorUtil.getColumnIndexOrThrow(_cursor, "peopleTrapped");
          final int _cursorIndexOfInjured = CursorUtil.getColumnIndexOrThrow(_cursor, "injured");
          final int _cursorIndexOfMedicalRequired = CursorUtil.getColumnIndexOrThrow(_cursor, "medicalRequired");
          final int _cursorIndexOfWaterDepth = CursorUtil.getColumnIndexOrThrow(_cursor, "waterDepth");
          final int _cursorIndexOfWaterLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "waterLevel");
          final int _cursorIndexOfWaterRising = CursorUtil.getColumnIndexOrThrow(_cursor, "waterRising");
          final int _cursorIndexOfMedicalEmergencyType = CursorUtil.getColumnIndexOrThrow(_cursor, "medicalEmergencyType");
          final int _cursorIndexOfPersonUnconscious = CursorUtil.getColumnIndexOrThrow(_cursor, "personUnconscious");
          final int _cursorIndexOfBreathingProblem = CursorUtil.getColumnIndexOrThrow(_cursor, "breathingProblem");
          final int _cursorIndexOfSevereBleeding = CursorUtil.getColumnIndexOrThrow(_cursor, "severeBleeding");
          final int _cursorIndexOfPregnancyRelated = CursorUtil.getColumnIndexOrThrow(_cursor, "pregnancyRelated");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfBuildingFloor = CursorUtil.getColumnIndexOrThrow(_cursor, "buildingFloor");
          final int _cursorIndexOfContactNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "contactNumber");
          final int _cursorIndexOfPhotoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "photoUrl");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAssignedResponderId = CursorUtil.getColumnIndexOrThrow(_cursor, "assignedResponderId");
          final int _cursorIndexOfResponderContact = CursorUtil.getColumnIndexOrThrow(_cursor, "responderContact");
          final int _cursorIndexOfResponseTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "responseTimeMillis");
          final int _cursorIndexOfResolutionTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "resolutionTimeMillis");
          final int _cursorIndexOfResponderNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "responderNotes");
          final int _cursorIndexOfEscalationRequired = CursorUtil.getColumnIndexOrThrow(_cursor, "escalationRequired");
          final int _cursorIndexOfIsDemoData = CursorUtil.getColumnIndexOrThrow(_cursor, "isDemoData");
          final List<EmergencyRequestEntity> _result = new ArrayList<EmergencyRequestEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EmergencyRequestEntity _item;
            final String _tmpEmergencyRequestId;
            _tmpEmergencyRequestId = _cursor.getString(_cursorIndexOfEmergencyRequestId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpHazardId;
            if (_cursor.isNull(_cursorIndexOfHazardId)) {
              _tmpHazardId = null;
            } else {
              _tmpHazardId = _cursor.getString(_cursorIndexOfHazardId);
            }
            final String _tmpClusterId;
            if (_cursor.isNull(_cursorIndexOfClusterId)) {
              _tmpClusterId = null;
            } else {
              _tmpClusterId = _cursor.getString(_cursorIndexOfClusterId);
            }
            final String _tmpZoneId;
            if (_cursor.isNull(_cursorIndexOfZoneId)) {
              _tmpZoneId = null;
            } else {
              _tmpZoneId = _cursor.getString(_cursorIndexOfZoneId);
            }
            final String _tmpEmergencyType;
            _tmpEmergencyType = _cursor.getString(_cursorIndexOfEmergencyType);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final float _tmpGpsAccuracy;
            _tmpGpsAccuracy = _cursor.getFloat(_cursorIndexOfGpsAccuracy);
            final long _tmpTimestampMillis;
            _tmpTimestampMillis = _cursor.getLong(_cursorIndexOfTimestampMillis);
            final int _tmpNumberOfPeople;
            _tmpNumberOfPeople = _cursor.getInt(_cursorIndexOfNumberOfPeople);
            final boolean _tmpChildren;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfChildren);
            _tmpChildren = _tmp != 0;
            final boolean _tmpElderly;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfElderly);
            _tmpElderly = _tmp_1 != 0;
            final boolean _tmpDisabled;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfDisabled);
            _tmpDisabled = _tmp_2 != 0;
            final boolean _tmpPeopleTrapped;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfPeopleTrapped);
            _tmpPeopleTrapped = _tmp_3 != 0;
            final boolean _tmpInjured;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfInjured);
            _tmpInjured = _tmp_4 != 0;
            final boolean _tmpMedicalRequired;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfMedicalRequired);
            _tmpMedicalRequired = _tmp_5 != 0;
            final String _tmpWaterDepth;
            if (_cursor.isNull(_cursorIndexOfWaterDepth)) {
              _tmpWaterDepth = null;
            } else {
              _tmpWaterDepth = _cursor.getString(_cursorIndexOfWaterDepth);
            }
            final String _tmpWaterLevel;
            if (_cursor.isNull(_cursorIndexOfWaterLevel)) {
              _tmpWaterLevel = null;
            } else {
              _tmpWaterLevel = _cursor.getString(_cursorIndexOfWaterLevel);
            }
            final Boolean _tmpWaterRising;
            final Integer _tmp_6;
            if (_cursor.isNull(_cursorIndexOfWaterRising)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getInt(_cursorIndexOfWaterRising);
            }
            _tmpWaterRising = _tmp_6 == null ? null : _tmp_6 != 0;
            final String _tmpMedicalEmergencyType;
            if (_cursor.isNull(_cursorIndexOfMedicalEmergencyType)) {
              _tmpMedicalEmergencyType = null;
            } else {
              _tmpMedicalEmergencyType = _cursor.getString(_cursorIndexOfMedicalEmergencyType);
            }
            final boolean _tmpPersonUnconscious;
            final int _tmp_7;
            _tmp_7 = _cursor.getInt(_cursorIndexOfPersonUnconscious);
            _tmpPersonUnconscious = _tmp_7 != 0;
            final boolean _tmpBreathingProblem;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfBreathingProblem);
            _tmpBreathingProblem = _tmp_8 != 0;
            final boolean _tmpSevereBleeding;
            final int _tmp_9;
            _tmp_9 = _cursor.getInt(_cursorIndexOfSevereBleeding);
            _tmpSevereBleeding = _tmp_9 != 0;
            final boolean _tmpPregnancyRelated;
            final int _tmp_10;
            _tmp_10 = _cursor.getInt(_cursorIndexOfPregnancyRelated);
            _tmpPregnancyRelated = _tmp_10 != 0;
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpBuildingFloor;
            if (_cursor.isNull(_cursorIndexOfBuildingFloor)) {
              _tmpBuildingFloor = null;
            } else {
              _tmpBuildingFloor = _cursor.getString(_cursorIndexOfBuildingFloor);
            }
            final String _tmpContactNumber;
            _tmpContactNumber = _cursor.getString(_cursorIndexOfContactNumber);
            final String _tmpPhotoUrl;
            if (_cursor.isNull(_cursorIndexOfPhotoUrl)) {
              _tmpPhotoUrl = null;
            } else {
              _tmpPhotoUrl = _cursor.getString(_cursorIndexOfPhotoUrl);
            }
            final String _tmpPriority;
            _tmpPriority = _cursor.getString(_cursorIndexOfPriority);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpAssignedResponderId;
            if (_cursor.isNull(_cursorIndexOfAssignedResponderId)) {
              _tmpAssignedResponderId = null;
            } else {
              _tmpAssignedResponderId = _cursor.getString(_cursorIndexOfAssignedResponderId);
            }
            final String _tmpResponderContact;
            if (_cursor.isNull(_cursorIndexOfResponderContact)) {
              _tmpResponderContact = null;
            } else {
              _tmpResponderContact = _cursor.getString(_cursorIndexOfResponderContact);
            }
            final Long _tmpResponseTimeMillis;
            if (_cursor.isNull(_cursorIndexOfResponseTimeMillis)) {
              _tmpResponseTimeMillis = null;
            } else {
              _tmpResponseTimeMillis = _cursor.getLong(_cursorIndexOfResponseTimeMillis);
            }
            final Long _tmpResolutionTimeMillis;
            if (_cursor.isNull(_cursorIndexOfResolutionTimeMillis)) {
              _tmpResolutionTimeMillis = null;
            } else {
              _tmpResolutionTimeMillis = _cursor.getLong(_cursorIndexOfResolutionTimeMillis);
            }
            final String _tmpResponderNotes;
            if (_cursor.isNull(_cursorIndexOfResponderNotes)) {
              _tmpResponderNotes = null;
            } else {
              _tmpResponderNotes = _cursor.getString(_cursorIndexOfResponderNotes);
            }
            final boolean _tmpEscalationRequired;
            final int _tmp_11;
            _tmp_11 = _cursor.getInt(_cursorIndexOfEscalationRequired);
            _tmpEscalationRequired = _tmp_11 != 0;
            final boolean _tmpIsDemoData;
            final int _tmp_12;
            _tmp_12 = _cursor.getInt(_cursorIndexOfIsDemoData);
            _tmpIsDemoData = _tmp_12 != 0;
            _item = new EmergencyRequestEntity(_tmpEmergencyRequestId,_tmpUserId,_tmpHazardId,_tmpClusterId,_tmpZoneId,_tmpEmergencyType,_tmpLatitude,_tmpLongitude,_tmpGpsAccuracy,_tmpTimestampMillis,_tmpNumberOfPeople,_tmpChildren,_tmpElderly,_tmpDisabled,_tmpPeopleTrapped,_tmpInjured,_tmpMedicalRequired,_tmpWaterDepth,_tmpWaterLevel,_tmpWaterRising,_tmpMedicalEmergencyType,_tmpPersonUnconscious,_tmpBreathingProblem,_tmpSevereBleeding,_tmpPregnancyRelated,_tmpDescription,_tmpBuildingFloor,_tmpContactNumber,_tmpPhotoUrl,_tmpPriority,_tmpStatus,_tmpAssignedResponderId,_tmpResponderContact,_tmpResponseTimeMillis,_tmpResolutionTimeMillis,_tmpResponderNotes,_tmpEscalationRequired,_tmpIsDemoData);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<EmergencyRequestEntity>> observeAll(final String userId) {
    final String _sql = "SELECT * FROM emergency_requests WHERE userId = ? ORDER BY timestampMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"emergency_requests"}, new Callable<List<EmergencyRequestEntity>>() {
      @Override
      @NonNull
      public List<EmergencyRequestEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfEmergencyRequestId = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyRequestId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfHazardId = CursorUtil.getColumnIndexOrThrow(_cursor, "hazardId");
          final int _cursorIndexOfClusterId = CursorUtil.getColumnIndexOrThrow(_cursor, "clusterId");
          final int _cursorIndexOfZoneId = CursorUtil.getColumnIndexOrThrow(_cursor, "zoneId");
          final int _cursorIndexOfEmergencyType = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyType");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfGpsAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "gpsAccuracy");
          final int _cursorIndexOfTimestampMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "timestampMillis");
          final int _cursorIndexOfNumberOfPeople = CursorUtil.getColumnIndexOrThrow(_cursor, "numberOfPeople");
          final int _cursorIndexOfChildren = CursorUtil.getColumnIndexOrThrow(_cursor, "children");
          final int _cursorIndexOfElderly = CursorUtil.getColumnIndexOrThrow(_cursor, "elderly");
          final int _cursorIndexOfDisabled = CursorUtil.getColumnIndexOrThrow(_cursor, "disabled");
          final int _cursorIndexOfPeopleTrapped = CursorUtil.getColumnIndexOrThrow(_cursor, "peopleTrapped");
          final int _cursorIndexOfInjured = CursorUtil.getColumnIndexOrThrow(_cursor, "injured");
          final int _cursorIndexOfMedicalRequired = CursorUtil.getColumnIndexOrThrow(_cursor, "medicalRequired");
          final int _cursorIndexOfWaterDepth = CursorUtil.getColumnIndexOrThrow(_cursor, "waterDepth");
          final int _cursorIndexOfWaterLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "waterLevel");
          final int _cursorIndexOfWaterRising = CursorUtil.getColumnIndexOrThrow(_cursor, "waterRising");
          final int _cursorIndexOfMedicalEmergencyType = CursorUtil.getColumnIndexOrThrow(_cursor, "medicalEmergencyType");
          final int _cursorIndexOfPersonUnconscious = CursorUtil.getColumnIndexOrThrow(_cursor, "personUnconscious");
          final int _cursorIndexOfBreathingProblem = CursorUtil.getColumnIndexOrThrow(_cursor, "breathingProblem");
          final int _cursorIndexOfSevereBleeding = CursorUtil.getColumnIndexOrThrow(_cursor, "severeBleeding");
          final int _cursorIndexOfPregnancyRelated = CursorUtil.getColumnIndexOrThrow(_cursor, "pregnancyRelated");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfBuildingFloor = CursorUtil.getColumnIndexOrThrow(_cursor, "buildingFloor");
          final int _cursorIndexOfContactNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "contactNumber");
          final int _cursorIndexOfPhotoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "photoUrl");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAssignedResponderId = CursorUtil.getColumnIndexOrThrow(_cursor, "assignedResponderId");
          final int _cursorIndexOfResponderContact = CursorUtil.getColumnIndexOrThrow(_cursor, "responderContact");
          final int _cursorIndexOfResponseTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "responseTimeMillis");
          final int _cursorIndexOfResolutionTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "resolutionTimeMillis");
          final int _cursorIndexOfResponderNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "responderNotes");
          final int _cursorIndexOfEscalationRequired = CursorUtil.getColumnIndexOrThrow(_cursor, "escalationRequired");
          final int _cursorIndexOfIsDemoData = CursorUtil.getColumnIndexOrThrow(_cursor, "isDemoData");
          final List<EmergencyRequestEntity> _result = new ArrayList<EmergencyRequestEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EmergencyRequestEntity _item;
            final String _tmpEmergencyRequestId;
            _tmpEmergencyRequestId = _cursor.getString(_cursorIndexOfEmergencyRequestId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpHazardId;
            if (_cursor.isNull(_cursorIndexOfHazardId)) {
              _tmpHazardId = null;
            } else {
              _tmpHazardId = _cursor.getString(_cursorIndexOfHazardId);
            }
            final String _tmpClusterId;
            if (_cursor.isNull(_cursorIndexOfClusterId)) {
              _tmpClusterId = null;
            } else {
              _tmpClusterId = _cursor.getString(_cursorIndexOfClusterId);
            }
            final String _tmpZoneId;
            if (_cursor.isNull(_cursorIndexOfZoneId)) {
              _tmpZoneId = null;
            } else {
              _tmpZoneId = _cursor.getString(_cursorIndexOfZoneId);
            }
            final String _tmpEmergencyType;
            _tmpEmergencyType = _cursor.getString(_cursorIndexOfEmergencyType);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final float _tmpGpsAccuracy;
            _tmpGpsAccuracy = _cursor.getFloat(_cursorIndexOfGpsAccuracy);
            final long _tmpTimestampMillis;
            _tmpTimestampMillis = _cursor.getLong(_cursorIndexOfTimestampMillis);
            final int _tmpNumberOfPeople;
            _tmpNumberOfPeople = _cursor.getInt(_cursorIndexOfNumberOfPeople);
            final boolean _tmpChildren;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfChildren);
            _tmpChildren = _tmp != 0;
            final boolean _tmpElderly;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfElderly);
            _tmpElderly = _tmp_1 != 0;
            final boolean _tmpDisabled;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfDisabled);
            _tmpDisabled = _tmp_2 != 0;
            final boolean _tmpPeopleTrapped;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfPeopleTrapped);
            _tmpPeopleTrapped = _tmp_3 != 0;
            final boolean _tmpInjured;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfInjured);
            _tmpInjured = _tmp_4 != 0;
            final boolean _tmpMedicalRequired;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfMedicalRequired);
            _tmpMedicalRequired = _tmp_5 != 0;
            final String _tmpWaterDepth;
            if (_cursor.isNull(_cursorIndexOfWaterDepth)) {
              _tmpWaterDepth = null;
            } else {
              _tmpWaterDepth = _cursor.getString(_cursorIndexOfWaterDepth);
            }
            final String _tmpWaterLevel;
            if (_cursor.isNull(_cursorIndexOfWaterLevel)) {
              _tmpWaterLevel = null;
            } else {
              _tmpWaterLevel = _cursor.getString(_cursorIndexOfWaterLevel);
            }
            final Boolean _tmpWaterRising;
            final Integer _tmp_6;
            if (_cursor.isNull(_cursorIndexOfWaterRising)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getInt(_cursorIndexOfWaterRising);
            }
            _tmpWaterRising = _tmp_6 == null ? null : _tmp_6 != 0;
            final String _tmpMedicalEmergencyType;
            if (_cursor.isNull(_cursorIndexOfMedicalEmergencyType)) {
              _tmpMedicalEmergencyType = null;
            } else {
              _tmpMedicalEmergencyType = _cursor.getString(_cursorIndexOfMedicalEmergencyType);
            }
            final boolean _tmpPersonUnconscious;
            final int _tmp_7;
            _tmp_7 = _cursor.getInt(_cursorIndexOfPersonUnconscious);
            _tmpPersonUnconscious = _tmp_7 != 0;
            final boolean _tmpBreathingProblem;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfBreathingProblem);
            _tmpBreathingProblem = _tmp_8 != 0;
            final boolean _tmpSevereBleeding;
            final int _tmp_9;
            _tmp_9 = _cursor.getInt(_cursorIndexOfSevereBleeding);
            _tmpSevereBleeding = _tmp_9 != 0;
            final boolean _tmpPregnancyRelated;
            final int _tmp_10;
            _tmp_10 = _cursor.getInt(_cursorIndexOfPregnancyRelated);
            _tmpPregnancyRelated = _tmp_10 != 0;
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpBuildingFloor;
            if (_cursor.isNull(_cursorIndexOfBuildingFloor)) {
              _tmpBuildingFloor = null;
            } else {
              _tmpBuildingFloor = _cursor.getString(_cursorIndexOfBuildingFloor);
            }
            final String _tmpContactNumber;
            _tmpContactNumber = _cursor.getString(_cursorIndexOfContactNumber);
            final String _tmpPhotoUrl;
            if (_cursor.isNull(_cursorIndexOfPhotoUrl)) {
              _tmpPhotoUrl = null;
            } else {
              _tmpPhotoUrl = _cursor.getString(_cursorIndexOfPhotoUrl);
            }
            final String _tmpPriority;
            _tmpPriority = _cursor.getString(_cursorIndexOfPriority);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpAssignedResponderId;
            if (_cursor.isNull(_cursorIndexOfAssignedResponderId)) {
              _tmpAssignedResponderId = null;
            } else {
              _tmpAssignedResponderId = _cursor.getString(_cursorIndexOfAssignedResponderId);
            }
            final String _tmpResponderContact;
            if (_cursor.isNull(_cursorIndexOfResponderContact)) {
              _tmpResponderContact = null;
            } else {
              _tmpResponderContact = _cursor.getString(_cursorIndexOfResponderContact);
            }
            final Long _tmpResponseTimeMillis;
            if (_cursor.isNull(_cursorIndexOfResponseTimeMillis)) {
              _tmpResponseTimeMillis = null;
            } else {
              _tmpResponseTimeMillis = _cursor.getLong(_cursorIndexOfResponseTimeMillis);
            }
            final Long _tmpResolutionTimeMillis;
            if (_cursor.isNull(_cursorIndexOfResolutionTimeMillis)) {
              _tmpResolutionTimeMillis = null;
            } else {
              _tmpResolutionTimeMillis = _cursor.getLong(_cursorIndexOfResolutionTimeMillis);
            }
            final String _tmpResponderNotes;
            if (_cursor.isNull(_cursorIndexOfResponderNotes)) {
              _tmpResponderNotes = null;
            } else {
              _tmpResponderNotes = _cursor.getString(_cursorIndexOfResponderNotes);
            }
            final boolean _tmpEscalationRequired;
            final int _tmp_11;
            _tmp_11 = _cursor.getInt(_cursorIndexOfEscalationRequired);
            _tmpEscalationRequired = _tmp_11 != 0;
            final boolean _tmpIsDemoData;
            final int _tmp_12;
            _tmp_12 = _cursor.getInt(_cursorIndexOfIsDemoData);
            _tmpIsDemoData = _tmp_12 != 0;
            _item = new EmergencyRequestEntity(_tmpEmergencyRequestId,_tmpUserId,_tmpHazardId,_tmpClusterId,_tmpZoneId,_tmpEmergencyType,_tmpLatitude,_tmpLongitude,_tmpGpsAccuracy,_tmpTimestampMillis,_tmpNumberOfPeople,_tmpChildren,_tmpElderly,_tmpDisabled,_tmpPeopleTrapped,_tmpInjured,_tmpMedicalRequired,_tmpWaterDepth,_tmpWaterLevel,_tmpWaterRising,_tmpMedicalEmergencyType,_tmpPersonUnconscious,_tmpBreathingProblem,_tmpSevereBleeding,_tmpPregnancyRelated,_tmpDescription,_tmpBuildingFloor,_tmpContactNumber,_tmpPhotoUrl,_tmpPriority,_tmpStatus,_tmpAssignedResponderId,_tmpResponderContact,_tmpResponseTimeMillis,_tmpResolutionTimeMillis,_tmpResponderNotes,_tmpEscalationRequired,_tmpIsDemoData);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getActive(final String userId,
      final Continuation<? super List<EmergencyRequestEntity>> $completion) {
    final String _sql = "SELECT * FROM emergency_requests WHERE userId = ? AND status NOT IN ('RESOLVED','CANCELLED','FALSE_REPORT') ORDER BY timestampMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<EmergencyRequestEntity>>() {
      @Override
      @NonNull
      public List<EmergencyRequestEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfEmergencyRequestId = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyRequestId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfHazardId = CursorUtil.getColumnIndexOrThrow(_cursor, "hazardId");
          final int _cursorIndexOfClusterId = CursorUtil.getColumnIndexOrThrow(_cursor, "clusterId");
          final int _cursorIndexOfZoneId = CursorUtil.getColumnIndexOrThrow(_cursor, "zoneId");
          final int _cursorIndexOfEmergencyType = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyType");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfGpsAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "gpsAccuracy");
          final int _cursorIndexOfTimestampMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "timestampMillis");
          final int _cursorIndexOfNumberOfPeople = CursorUtil.getColumnIndexOrThrow(_cursor, "numberOfPeople");
          final int _cursorIndexOfChildren = CursorUtil.getColumnIndexOrThrow(_cursor, "children");
          final int _cursorIndexOfElderly = CursorUtil.getColumnIndexOrThrow(_cursor, "elderly");
          final int _cursorIndexOfDisabled = CursorUtil.getColumnIndexOrThrow(_cursor, "disabled");
          final int _cursorIndexOfPeopleTrapped = CursorUtil.getColumnIndexOrThrow(_cursor, "peopleTrapped");
          final int _cursorIndexOfInjured = CursorUtil.getColumnIndexOrThrow(_cursor, "injured");
          final int _cursorIndexOfMedicalRequired = CursorUtil.getColumnIndexOrThrow(_cursor, "medicalRequired");
          final int _cursorIndexOfWaterDepth = CursorUtil.getColumnIndexOrThrow(_cursor, "waterDepth");
          final int _cursorIndexOfWaterLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "waterLevel");
          final int _cursorIndexOfWaterRising = CursorUtil.getColumnIndexOrThrow(_cursor, "waterRising");
          final int _cursorIndexOfMedicalEmergencyType = CursorUtil.getColumnIndexOrThrow(_cursor, "medicalEmergencyType");
          final int _cursorIndexOfPersonUnconscious = CursorUtil.getColumnIndexOrThrow(_cursor, "personUnconscious");
          final int _cursorIndexOfBreathingProblem = CursorUtil.getColumnIndexOrThrow(_cursor, "breathingProblem");
          final int _cursorIndexOfSevereBleeding = CursorUtil.getColumnIndexOrThrow(_cursor, "severeBleeding");
          final int _cursorIndexOfPregnancyRelated = CursorUtil.getColumnIndexOrThrow(_cursor, "pregnancyRelated");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfBuildingFloor = CursorUtil.getColumnIndexOrThrow(_cursor, "buildingFloor");
          final int _cursorIndexOfContactNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "contactNumber");
          final int _cursorIndexOfPhotoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "photoUrl");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAssignedResponderId = CursorUtil.getColumnIndexOrThrow(_cursor, "assignedResponderId");
          final int _cursorIndexOfResponderContact = CursorUtil.getColumnIndexOrThrow(_cursor, "responderContact");
          final int _cursorIndexOfResponseTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "responseTimeMillis");
          final int _cursorIndexOfResolutionTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "resolutionTimeMillis");
          final int _cursorIndexOfResponderNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "responderNotes");
          final int _cursorIndexOfEscalationRequired = CursorUtil.getColumnIndexOrThrow(_cursor, "escalationRequired");
          final int _cursorIndexOfIsDemoData = CursorUtil.getColumnIndexOrThrow(_cursor, "isDemoData");
          final List<EmergencyRequestEntity> _result = new ArrayList<EmergencyRequestEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EmergencyRequestEntity _item;
            final String _tmpEmergencyRequestId;
            _tmpEmergencyRequestId = _cursor.getString(_cursorIndexOfEmergencyRequestId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpHazardId;
            if (_cursor.isNull(_cursorIndexOfHazardId)) {
              _tmpHazardId = null;
            } else {
              _tmpHazardId = _cursor.getString(_cursorIndexOfHazardId);
            }
            final String _tmpClusterId;
            if (_cursor.isNull(_cursorIndexOfClusterId)) {
              _tmpClusterId = null;
            } else {
              _tmpClusterId = _cursor.getString(_cursorIndexOfClusterId);
            }
            final String _tmpZoneId;
            if (_cursor.isNull(_cursorIndexOfZoneId)) {
              _tmpZoneId = null;
            } else {
              _tmpZoneId = _cursor.getString(_cursorIndexOfZoneId);
            }
            final String _tmpEmergencyType;
            _tmpEmergencyType = _cursor.getString(_cursorIndexOfEmergencyType);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final float _tmpGpsAccuracy;
            _tmpGpsAccuracy = _cursor.getFloat(_cursorIndexOfGpsAccuracy);
            final long _tmpTimestampMillis;
            _tmpTimestampMillis = _cursor.getLong(_cursorIndexOfTimestampMillis);
            final int _tmpNumberOfPeople;
            _tmpNumberOfPeople = _cursor.getInt(_cursorIndexOfNumberOfPeople);
            final boolean _tmpChildren;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfChildren);
            _tmpChildren = _tmp != 0;
            final boolean _tmpElderly;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfElderly);
            _tmpElderly = _tmp_1 != 0;
            final boolean _tmpDisabled;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfDisabled);
            _tmpDisabled = _tmp_2 != 0;
            final boolean _tmpPeopleTrapped;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfPeopleTrapped);
            _tmpPeopleTrapped = _tmp_3 != 0;
            final boolean _tmpInjured;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfInjured);
            _tmpInjured = _tmp_4 != 0;
            final boolean _tmpMedicalRequired;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfMedicalRequired);
            _tmpMedicalRequired = _tmp_5 != 0;
            final String _tmpWaterDepth;
            if (_cursor.isNull(_cursorIndexOfWaterDepth)) {
              _tmpWaterDepth = null;
            } else {
              _tmpWaterDepth = _cursor.getString(_cursorIndexOfWaterDepth);
            }
            final String _tmpWaterLevel;
            if (_cursor.isNull(_cursorIndexOfWaterLevel)) {
              _tmpWaterLevel = null;
            } else {
              _tmpWaterLevel = _cursor.getString(_cursorIndexOfWaterLevel);
            }
            final Boolean _tmpWaterRising;
            final Integer _tmp_6;
            if (_cursor.isNull(_cursorIndexOfWaterRising)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getInt(_cursorIndexOfWaterRising);
            }
            _tmpWaterRising = _tmp_6 == null ? null : _tmp_6 != 0;
            final String _tmpMedicalEmergencyType;
            if (_cursor.isNull(_cursorIndexOfMedicalEmergencyType)) {
              _tmpMedicalEmergencyType = null;
            } else {
              _tmpMedicalEmergencyType = _cursor.getString(_cursorIndexOfMedicalEmergencyType);
            }
            final boolean _tmpPersonUnconscious;
            final int _tmp_7;
            _tmp_7 = _cursor.getInt(_cursorIndexOfPersonUnconscious);
            _tmpPersonUnconscious = _tmp_7 != 0;
            final boolean _tmpBreathingProblem;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfBreathingProblem);
            _tmpBreathingProblem = _tmp_8 != 0;
            final boolean _tmpSevereBleeding;
            final int _tmp_9;
            _tmp_9 = _cursor.getInt(_cursorIndexOfSevereBleeding);
            _tmpSevereBleeding = _tmp_9 != 0;
            final boolean _tmpPregnancyRelated;
            final int _tmp_10;
            _tmp_10 = _cursor.getInt(_cursorIndexOfPregnancyRelated);
            _tmpPregnancyRelated = _tmp_10 != 0;
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpBuildingFloor;
            if (_cursor.isNull(_cursorIndexOfBuildingFloor)) {
              _tmpBuildingFloor = null;
            } else {
              _tmpBuildingFloor = _cursor.getString(_cursorIndexOfBuildingFloor);
            }
            final String _tmpContactNumber;
            _tmpContactNumber = _cursor.getString(_cursorIndexOfContactNumber);
            final String _tmpPhotoUrl;
            if (_cursor.isNull(_cursorIndexOfPhotoUrl)) {
              _tmpPhotoUrl = null;
            } else {
              _tmpPhotoUrl = _cursor.getString(_cursorIndexOfPhotoUrl);
            }
            final String _tmpPriority;
            _tmpPriority = _cursor.getString(_cursorIndexOfPriority);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpAssignedResponderId;
            if (_cursor.isNull(_cursorIndexOfAssignedResponderId)) {
              _tmpAssignedResponderId = null;
            } else {
              _tmpAssignedResponderId = _cursor.getString(_cursorIndexOfAssignedResponderId);
            }
            final String _tmpResponderContact;
            if (_cursor.isNull(_cursorIndexOfResponderContact)) {
              _tmpResponderContact = null;
            } else {
              _tmpResponderContact = _cursor.getString(_cursorIndexOfResponderContact);
            }
            final Long _tmpResponseTimeMillis;
            if (_cursor.isNull(_cursorIndexOfResponseTimeMillis)) {
              _tmpResponseTimeMillis = null;
            } else {
              _tmpResponseTimeMillis = _cursor.getLong(_cursorIndexOfResponseTimeMillis);
            }
            final Long _tmpResolutionTimeMillis;
            if (_cursor.isNull(_cursorIndexOfResolutionTimeMillis)) {
              _tmpResolutionTimeMillis = null;
            } else {
              _tmpResolutionTimeMillis = _cursor.getLong(_cursorIndexOfResolutionTimeMillis);
            }
            final String _tmpResponderNotes;
            if (_cursor.isNull(_cursorIndexOfResponderNotes)) {
              _tmpResponderNotes = null;
            } else {
              _tmpResponderNotes = _cursor.getString(_cursorIndexOfResponderNotes);
            }
            final boolean _tmpEscalationRequired;
            final int _tmp_11;
            _tmp_11 = _cursor.getInt(_cursorIndexOfEscalationRequired);
            _tmpEscalationRequired = _tmp_11 != 0;
            final boolean _tmpIsDemoData;
            final int _tmp_12;
            _tmp_12 = _cursor.getInt(_cursorIndexOfIsDemoData);
            _tmpIsDemoData = _tmp_12 != 0;
            _item = new EmergencyRequestEntity(_tmpEmergencyRequestId,_tmpUserId,_tmpHazardId,_tmpClusterId,_tmpZoneId,_tmpEmergencyType,_tmpLatitude,_tmpLongitude,_tmpGpsAccuracy,_tmpTimestampMillis,_tmpNumberOfPeople,_tmpChildren,_tmpElderly,_tmpDisabled,_tmpPeopleTrapped,_tmpInjured,_tmpMedicalRequired,_tmpWaterDepth,_tmpWaterLevel,_tmpWaterRising,_tmpMedicalEmergencyType,_tmpPersonUnconscious,_tmpBreathingProblem,_tmpSevereBleeding,_tmpPregnancyRelated,_tmpDescription,_tmpBuildingFloor,_tmpContactNumber,_tmpPhotoUrl,_tmpPriority,_tmpStatus,_tmpAssignedResponderId,_tmpResponderContact,_tmpResponseTimeMillis,_tmpResolutionTimeMillis,_tmpResponderNotes,_tmpEscalationRequired,_tmpIsDemoData);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAll(final String userId,
      final Continuation<? super List<EmergencyRequestEntity>> $completion) {
    final String _sql = "SELECT * FROM emergency_requests WHERE userId = ? ORDER BY timestampMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<EmergencyRequestEntity>>() {
      @Override
      @NonNull
      public List<EmergencyRequestEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfEmergencyRequestId = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyRequestId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfHazardId = CursorUtil.getColumnIndexOrThrow(_cursor, "hazardId");
          final int _cursorIndexOfClusterId = CursorUtil.getColumnIndexOrThrow(_cursor, "clusterId");
          final int _cursorIndexOfZoneId = CursorUtil.getColumnIndexOrThrow(_cursor, "zoneId");
          final int _cursorIndexOfEmergencyType = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyType");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfGpsAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "gpsAccuracy");
          final int _cursorIndexOfTimestampMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "timestampMillis");
          final int _cursorIndexOfNumberOfPeople = CursorUtil.getColumnIndexOrThrow(_cursor, "numberOfPeople");
          final int _cursorIndexOfChildren = CursorUtil.getColumnIndexOrThrow(_cursor, "children");
          final int _cursorIndexOfElderly = CursorUtil.getColumnIndexOrThrow(_cursor, "elderly");
          final int _cursorIndexOfDisabled = CursorUtil.getColumnIndexOrThrow(_cursor, "disabled");
          final int _cursorIndexOfPeopleTrapped = CursorUtil.getColumnIndexOrThrow(_cursor, "peopleTrapped");
          final int _cursorIndexOfInjured = CursorUtil.getColumnIndexOrThrow(_cursor, "injured");
          final int _cursorIndexOfMedicalRequired = CursorUtil.getColumnIndexOrThrow(_cursor, "medicalRequired");
          final int _cursorIndexOfWaterDepth = CursorUtil.getColumnIndexOrThrow(_cursor, "waterDepth");
          final int _cursorIndexOfWaterLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "waterLevel");
          final int _cursorIndexOfWaterRising = CursorUtil.getColumnIndexOrThrow(_cursor, "waterRising");
          final int _cursorIndexOfMedicalEmergencyType = CursorUtil.getColumnIndexOrThrow(_cursor, "medicalEmergencyType");
          final int _cursorIndexOfPersonUnconscious = CursorUtil.getColumnIndexOrThrow(_cursor, "personUnconscious");
          final int _cursorIndexOfBreathingProblem = CursorUtil.getColumnIndexOrThrow(_cursor, "breathingProblem");
          final int _cursorIndexOfSevereBleeding = CursorUtil.getColumnIndexOrThrow(_cursor, "severeBleeding");
          final int _cursorIndexOfPregnancyRelated = CursorUtil.getColumnIndexOrThrow(_cursor, "pregnancyRelated");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfBuildingFloor = CursorUtil.getColumnIndexOrThrow(_cursor, "buildingFloor");
          final int _cursorIndexOfContactNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "contactNumber");
          final int _cursorIndexOfPhotoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "photoUrl");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAssignedResponderId = CursorUtil.getColumnIndexOrThrow(_cursor, "assignedResponderId");
          final int _cursorIndexOfResponderContact = CursorUtil.getColumnIndexOrThrow(_cursor, "responderContact");
          final int _cursorIndexOfResponseTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "responseTimeMillis");
          final int _cursorIndexOfResolutionTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "resolutionTimeMillis");
          final int _cursorIndexOfResponderNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "responderNotes");
          final int _cursorIndexOfEscalationRequired = CursorUtil.getColumnIndexOrThrow(_cursor, "escalationRequired");
          final int _cursorIndexOfIsDemoData = CursorUtil.getColumnIndexOrThrow(_cursor, "isDemoData");
          final List<EmergencyRequestEntity> _result = new ArrayList<EmergencyRequestEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EmergencyRequestEntity _item;
            final String _tmpEmergencyRequestId;
            _tmpEmergencyRequestId = _cursor.getString(_cursorIndexOfEmergencyRequestId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpHazardId;
            if (_cursor.isNull(_cursorIndexOfHazardId)) {
              _tmpHazardId = null;
            } else {
              _tmpHazardId = _cursor.getString(_cursorIndexOfHazardId);
            }
            final String _tmpClusterId;
            if (_cursor.isNull(_cursorIndexOfClusterId)) {
              _tmpClusterId = null;
            } else {
              _tmpClusterId = _cursor.getString(_cursorIndexOfClusterId);
            }
            final String _tmpZoneId;
            if (_cursor.isNull(_cursorIndexOfZoneId)) {
              _tmpZoneId = null;
            } else {
              _tmpZoneId = _cursor.getString(_cursorIndexOfZoneId);
            }
            final String _tmpEmergencyType;
            _tmpEmergencyType = _cursor.getString(_cursorIndexOfEmergencyType);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final float _tmpGpsAccuracy;
            _tmpGpsAccuracy = _cursor.getFloat(_cursorIndexOfGpsAccuracy);
            final long _tmpTimestampMillis;
            _tmpTimestampMillis = _cursor.getLong(_cursorIndexOfTimestampMillis);
            final int _tmpNumberOfPeople;
            _tmpNumberOfPeople = _cursor.getInt(_cursorIndexOfNumberOfPeople);
            final boolean _tmpChildren;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfChildren);
            _tmpChildren = _tmp != 0;
            final boolean _tmpElderly;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfElderly);
            _tmpElderly = _tmp_1 != 0;
            final boolean _tmpDisabled;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfDisabled);
            _tmpDisabled = _tmp_2 != 0;
            final boolean _tmpPeopleTrapped;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfPeopleTrapped);
            _tmpPeopleTrapped = _tmp_3 != 0;
            final boolean _tmpInjured;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfInjured);
            _tmpInjured = _tmp_4 != 0;
            final boolean _tmpMedicalRequired;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfMedicalRequired);
            _tmpMedicalRequired = _tmp_5 != 0;
            final String _tmpWaterDepth;
            if (_cursor.isNull(_cursorIndexOfWaterDepth)) {
              _tmpWaterDepth = null;
            } else {
              _tmpWaterDepth = _cursor.getString(_cursorIndexOfWaterDepth);
            }
            final String _tmpWaterLevel;
            if (_cursor.isNull(_cursorIndexOfWaterLevel)) {
              _tmpWaterLevel = null;
            } else {
              _tmpWaterLevel = _cursor.getString(_cursorIndexOfWaterLevel);
            }
            final Boolean _tmpWaterRising;
            final Integer _tmp_6;
            if (_cursor.isNull(_cursorIndexOfWaterRising)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getInt(_cursorIndexOfWaterRising);
            }
            _tmpWaterRising = _tmp_6 == null ? null : _tmp_6 != 0;
            final String _tmpMedicalEmergencyType;
            if (_cursor.isNull(_cursorIndexOfMedicalEmergencyType)) {
              _tmpMedicalEmergencyType = null;
            } else {
              _tmpMedicalEmergencyType = _cursor.getString(_cursorIndexOfMedicalEmergencyType);
            }
            final boolean _tmpPersonUnconscious;
            final int _tmp_7;
            _tmp_7 = _cursor.getInt(_cursorIndexOfPersonUnconscious);
            _tmpPersonUnconscious = _tmp_7 != 0;
            final boolean _tmpBreathingProblem;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfBreathingProblem);
            _tmpBreathingProblem = _tmp_8 != 0;
            final boolean _tmpSevereBleeding;
            final int _tmp_9;
            _tmp_9 = _cursor.getInt(_cursorIndexOfSevereBleeding);
            _tmpSevereBleeding = _tmp_9 != 0;
            final boolean _tmpPregnancyRelated;
            final int _tmp_10;
            _tmp_10 = _cursor.getInt(_cursorIndexOfPregnancyRelated);
            _tmpPregnancyRelated = _tmp_10 != 0;
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpBuildingFloor;
            if (_cursor.isNull(_cursorIndexOfBuildingFloor)) {
              _tmpBuildingFloor = null;
            } else {
              _tmpBuildingFloor = _cursor.getString(_cursorIndexOfBuildingFloor);
            }
            final String _tmpContactNumber;
            _tmpContactNumber = _cursor.getString(_cursorIndexOfContactNumber);
            final String _tmpPhotoUrl;
            if (_cursor.isNull(_cursorIndexOfPhotoUrl)) {
              _tmpPhotoUrl = null;
            } else {
              _tmpPhotoUrl = _cursor.getString(_cursorIndexOfPhotoUrl);
            }
            final String _tmpPriority;
            _tmpPriority = _cursor.getString(_cursorIndexOfPriority);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpAssignedResponderId;
            if (_cursor.isNull(_cursorIndexOfAssignedResponderId)) {
              _tmpAssignedResponderId = null;
            } else {
              _tmpAssignedResponderId = _cursor.getString(_cursorIndexOfAssignedResponderId);
            }
            final String _tmpResponderContact;
            if (_cursor.isNull(_cursorIndexOfResponderContact)) {
              _tmpResponderContact = null;
            } else {
              _tmpResponderContact = _cursor.getString(_cursorIndexOfResponderContact);
            }
            final Long _tmpResponseTimeMillis;
            if (_cursor.isNull(_cursorIndexOfResponseTimeMillis)) {
              _tmpResponseTimeMillis = null;
            } else {
              _tmpResponseTimeMillis = _cursor.getLong(_cursorIndexOfResponseTimeMillis);
            }
            final Long _tmpResolutionTimeMillis;
            if (_cursor.isNull(_cursorIndexOfResolutionTimeMillis)) {
              _tmpResolutionTimeMillis = null;
            } else {
              _tmpResolutionTimeMillis = _cursor.getLong(_cursorIndexOfResolutionTimeMillis);
            }
            final String _tmpResponderNotes;
            if (_cursor.isNull(_cursorIndexOfResponderNotes)) {
              _tmpResponderNotes = null;
            } else {
              _tmpResponderNotes = _cursor.getString(_cursorIndexOfResponderNotes);
            }
            final boolean _tmpEscalationRequired;
            final int _tmp_11;
            _tmp_11 = _cursor.getInt(_cursorIndexOfEscalationRequired);
            _tmpEscalationRequired = _tmp_11 != 0;
            final boolean _tmpIsDemoData;
            final int _tmp_12;
            _tmp_12 = _cursor.getInt(_cursorIndexOfIsDemoData);
            _tmpIsDemoData = _tmp_12 != 0;
            _item = new EmergencyRequestEntity(_tmpEmergencyRequestId,_tmpUserId,_tmpHazardId,_tmpClusterId,_tmpZoneId,_tmpEmergencyType,_tmpLatitude,_tmpLongitude,_tmpGpsAccuracy,_tmpTimestampMillis,_tmpNumberOfPeople,_tmpChildren,_tmpElderly,_tmpDisabled,_tmpPeopleTrapped,_tmpInjured,_tmpMedicalRequired,_tmpWaterDepth,_tmpWaterLevel,_tmpWaterRising,_tmpMedicalEmergencyType,_tmpPersonUnconscious,_tmpBreathingProblem,_tmpSevereBleeding,_tmpPregnancyRelated,_tmpDescription,_tmpBuildingFloor,_tmpContactNumber,_tmpPhotoUrl,_tmpPriority,_tmpStatus,_tmpAssignedResponderId,_tmpResponderContact,_tmpResponseTimeMillis,_tmpResolutionTimeMillis,_tmpResponderNotes,_tmpEscalationRequired,_tmpIsDemoData);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getCriticalUnresolved(
      final Continuation<? super List<EmergencyRequestEntity>> $completion) {
    final String _sql = "SELECT * FROM emergency_requests WHERE priority = 'CRITICAL' AND status IN ('ACTIVE','ACKNOWLEDGED') ORDER BY timestampMillis ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<EmergencyRequestEntity>>() {
      @Override
      @NonNull
      public List<EmergencyRequestEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfEmergencyRequestId = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyRequestId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfHazardId = CursorUtil.getColumnIndexOrThrow(_cursor, "hazardId");
          final int _cursorIndexOfClusterId = CursorUtil.getColumnIndexOrThrow(_cursor, "clusterId");
          final int _cursorIndexOfZoneId = CursorUtil.getColumnIndexOrThrow(_cursor, "zoneId");
          final int _cursorIndexOfEmergencyType = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyType");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfGpsAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "gpsAccuracy");
          final int _cursorIndexOfTimestampMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "timestampMillis");
          final int _cursorIndexOfNumberOfPeople = CursorUtil.getColumnIndexOrThrow(_cursor, "numberOfPeople");
          final int _cursorIndexOfChildren = CursorUtil.getColumnIndexOrThrow(_cursor, "children");
          final int _cursorIndexOfElderly = CursorUtil.getColumnIndexOrThrow(_cursor, "elderly");
          final int _cursorIndexOfDisabled = CursorUtil.getColumnIndexOrThrow(_cursor, "disabled");
          final int _cursorIndexOfPeopleTrapped = CursorUtil.getColumnIndexOrThrow(_cursor, "peopleTrapped");
          final int _cursorIndexOfInjured = CursorUtil.getColumnIndexOrThrow(_cursor, "injured");
          final int _cursorIndexOfMedicalRequired = CursorUtil.getColumnIndexOrThrow(_cursor, "medicalRequired");
          final int _cursorIndexOfWaterDepth = CursorUtil.getColumnIndexOrThrow(_cursor, "waterDepth");
          final int _cursorIndexOfWaterLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "waterLevel");
          final int _cursorIndexOfWaterRising = CursorUtil.getColumnIndexOrThrow(_cursor, "waterRising");
          final int _cursorIndexOfMedicalEmergencyType = CursorUtil.getColumnIndexOrThrow(_cursor, "medicalEmergencyType");
          final int _cursorIndexOfPersonUnconscious = CursorUtil.getColumnIndexOrThrow(_cursor, "personUnconscious");
          final int _cursorIndexOfBreathingProblem = CursorUtil.getColumnIndexOrThrow(_cursor, "breathingProblem");
          final int _cursorIndexOfSevereBleeding = CursorUtil.getColumnIndexOrThrow(_cursor, "severeBleeding");
          final int _cursorIndexOfPregnancyRelated = CursorUtil.getColumnIndexOrThrow(_cursor, "pregnancyRelated");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfBuildingFloor = CursorUtil.getColumnIndexOrThrow(_cursor, "buildingFloor");
          final int _cursorIndexOfContactNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "contactNumber");
          final int _cursorIndexOfPhotoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "photoUrl");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAssignedResponderId = CursorUtil.getColumnIndexOrThrow(_cursor, "assignedResponderId");
          final int _cursorIndexOfResponderContact = CursorUtil.getColumnIndexOrThrow(_cursor, "responderContact");
          final int _cursorIndexOfResponseTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "responseTimeMillis");
          final int _cursorIndexOfResolutionTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "resolutionTimeMillis");
          final int _cursorIndexOfResponderNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "responderNotes");
          final int _cursorIndexOfEscalationRequired = CursorUtil.getColumnIndexOrThrow(_cursor, "escalationRequired");
          final int _cursorIndexOfIsDemoData = CursorUtil.getColumnIndexOrThrow(_cursor, "isDemoData");
          final List<EmergencyRequestEntity> _result = new ArrayList<EmergencyRequestEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EmergencyRequestEntity _item;
            final String _tmpEmergencyRequestId;
            _tmpEmergencyRequestId = _cursor.getString(_cursorIndexOfEmergencyRequestId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpHazardId;
            if (_cursor.isNull(_cursorIndexOfHazardId)) {
              _tmpHazardId = null;
            } else {
              _tmpHazardId = _cursor.getString(_cursorIndexOfHazardId);
            }
            final String _tmpClusterId;
            if (_cursor.isNull(_cursorIndexOfClusterId)) {
              _tmpClusterId = null;
            } else {
              _tmpClusterId = _cursor.getString(_cursorIndexOfClusterId);
            }
            final String _tmpZoneId;
            if (_cursor.isNull(_cursorIndexOfZoneId)) {
              _tmpZoneId = null;
            } else {
              _tmpZoneId = _cursor.getString(_cursorIndexOfZoneId);
            }
            final String _tmpEmergencyType;
            _tmpEmergencyType = _cursor.getString(_cursorIndexOfEmergencyType);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final float _tmpGpsAccuracy;
            _tmpGpsAccuracy = _cursor.getFloat(_cursorIndexOfGpsAccuracy);
            final long _tmpTimestampMillis;
            _tmpTimestampMillis = _cursor.getLong(_cursorIndexOfTimestampMillis);
            final int _tmpNumberOfPeople;
            _tmpNumberOfPeople = _cursor.getInt(_cursorIndexOfNumberOfPeople);
            final boolean _tmpChildren;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfChildren);
            _tmpChildren = _tmp != 0;
            final boolean _tmpElderly;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfElderly);
            _tmpElderly = _tmp_1 != 0;
            final boolean _tmpDisabled;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfDisabled);
            _tmpDisabled = _tmp_2 != 0;
            final boolean _tmpPeopleTrapped;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfPeopleTrapped);
            _tmpPeopleTrapped = _tmp_3 != 0;
            final boolean _tmpInjured;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfInjured);
            _tmpInjured = _tmp_4 != 0;
            final boolean _tmpMedicalRequired;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfMedicalRequired);
            _tmpMedicalRequired = _tmp_5 != 0;
            final String _tmpWaterDepth;
            if (_cursor.isNull(_cursorIndexOfWaterDepth)) {
              _tmpWaterDepth = null;
            } else {
              _tmpWaterDepth = _cursor.getString(_cursorIndexOfWaterDepth);
            }
            final String _tmpWaterLevel;
            if (_cursor.isNull(_cursorIndexOfWaterLevel)) {
              _tmpWaterLevel = null;
            } else {
              _tmpWaterLevel = _cursor.getString(_cursorIndexOfWaterLevel);
            }
            final Boolean _tmpWaterRising;
            final Integer _tmp_6;
            if (_cursor.isNull(_cursorIndexOfWaterRising)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getInt(_cursorIndexOfWaterRising);
            }
            _tmpWaterRising = _tmp_6 == null ? null : _tmp_6 != 0;
            final String _tmpMedicalEmergencyType;
            if (_cursor.isNull(_cursorIndexOfMedicalEmergencyType)) {
              _tmpMedicalEmergencyType = null;
            } else {
              _tmpMedicalEmergencyType = _cursor.getString(_cursorIndexOfMedicalEmergencyType);
            }
            final boolean _tmpPersonUnconscious;
            final int _tmp_7;
            _tmp_7 = _cursor.getInt(_cursorIndexOfPersonUnconscious);
            _tmpPersonUnconscious = _tmp_7 != 0;
            final boolean _tmpBreathingProblem;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfBreathingProblem);
            _tmpBreathingProblem = _tmp_8 != 0;
            final boolean _tmpSevereBleeding;
            final int _tmp_9;
            _tmp_9 = _cursor.getInt(_cursorIndexOfSevereBleeding);
            _tmpSevereBleeding = _tmp_9 != 0;
            final boolean _tmpPregnancyRelated;
            final int _tmp_10;
            _tmp_10 = _cursor.getInt(_cursorIndexOfPregnancyRelated);
            _tmpPregnancyRelated = _tmp_10 != 0;
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpBuildingFloor;
            if (_cursor.isNull(_cursorIndexOfBuildingFloor)) {
              _tmpBuildingFloor = null;
            } else {
              _tmpBuildingFloor = _cursor.getString(_cursorIndexOfBuildingFloor);
            }
            final String _tmpContactNumber;
            _tmpContactNumber = _cursor.getString(_cursorIndexOfContactNumber);
            final String _tmpPhotoUrl;
            if (_cursor.isNull(_cursorIndexOfPhotoUrl)) {
              _tmpPhotoUrl = null;
            } else {
              _tmpPhotoUrl = _cursor.getString(_cursorIndexOfPhotoUrl);
            }
            final String _tmpPriority;
            _tmpPriority = _cursor.getString(_cursorIndexOfPriority);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpAssignedResponderId;
            if (_cursor.isNull(_cursorIndexOfAssignedResponderId)) {
              _tmpAssignedResponderId = null;
            } else {
              _tmpAssignedResponderId = _cursor.getString(_cursorIndexOfAssignedResponderId);
            }
            final String _tmpResponderContact;
            if (_cursor.isNull(_cursorIndexOfResponderContact)) {
              _tmpResponderContact = null;
            } else {
              _tmpResponderContact = _cursor.getString(_cursorIndexOfResponderContact);
            }
            final Long _tmpResponseTimeMillis;
            if (_cursor.isNull(_cursorIndexOfResponseTimeMillis)) {
              _tmpResponseTimeMillis = null;
            } else {
              _tmpResponseTimeMillis = _cursor.getLong(_cursorIndexOfResponseTimeMillis);
            }
            final Long _tmpResolutionTimeMillis;
            if (_cursor.isNull(_cursorIndexOfResolutionTimeMillis)) {
              _tmpResolutionTimeMillis = null;
            } else {
              _tmpResolutionTimeMillis = _cursor.getLong(_cursorIndexOfResolutionTimeMillis);
            }
            final String _tmpResponderNotes;
            if (_cursor.isNull(_cursorIndexOfResponderNotes)) {
              _tmpResponderNotes = null;
            } else {
              _tmpResponderNotes = _cursor.getString(_cursorIndexOfResponderNotes);
            }
            final boolean _tmpEscalationRequired;
            final int _tmp_11;
            _tmp_11 = _cursor.getInt(_cursorIndexOfEscalationRequired);
            _tmpEscalationRequired = _tmp_11 != 0;
            final boolean _tmpIsDemoData;
            final int _tmp_12;
            _tmp_12 = _cursor.getInt(_cursorIndexOfIsDemoData);
            _tmpIsDemoData = _tmp_12 != 0;
            _item = new EmergencyRequestEntity(_tmpEmergencyRequestId,_tmpUserId,_tmpHazardId,_tmpClusterId,_tmpZoneId,_tmpEmergencyType,_tmpLatitude,_tmpLongitude,_tmpGpsAccuracy,_tmpTimestampMillis,_tmpNumberOfPeople,_tmpChildren,_tmpElderly,_tmpDisabled,_tmpPeopleTrapped,_tmpInjured,_tmpMedicalRequired,_tmpWaterDepth,_tmpWaterLevel,_tmpWaterRising,_tmpMedicalEmergencyType,_tmpPersonUnconscious,_tmpBreathingProblem,_tmpSevereBleeding,_tmpPregnancyRelated,_tmpDescription,_tmpBuildingFloor,_tmpContactNumber,_tmpPhotoUrl,_tmpPriority,_tmpStatus,_tmpAssignedResponderId,_tmpResponderContact,_tmpResponseTimeMillis,_tmpResolutionTimeMillis,_tmpResponderNotes,_tmpEscalationRequired,_tmpIsDemoData);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<EmergencyRequestEntity>> observeEmergenciesForCommunityHelp() {
    final String _sql = "SELECT * FROM emergency_requests WHERE status NOT IN ('RESOLVED','CANCELLED','FALSE_REPORT') AND isDemoData = 0 ORDER BY timestampMillis DESC LIMIT 50";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"emergency_requests"}, new Callable<List<EmergencyRequestEntity>>() {
      @Override
      @NonNull
      public List<EmergencyRequestEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfEmergencyRequestId = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyRequestId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfHazardId = CursorUtil.getColumnIndexOrThrow(_cursor, "hazardId");
          final int _cursorIndexOfClusterId = CursorUtil.getColumnIndexOrThrow(_cursor, "clusterId");
          final int _cursorIndexOfZoneId = CursorUtil.getColumnIndexOrThrow(_cursor, "zoneId");
          final int _cursorIndexOfEmergencyType = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyType");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfGpsAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "gpsAccuracy");
          final int _cursorIndexOfTimestampMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "timestampMillis");
          final int _cursorIndexOfNumberOfPeople = CursorUtil.getColumnIndexOrThrow(_cursor, "numberOfPeople");
          final int _cursorIndexOfChildren = CursorUtil.getColumnIndexOrThrow(_cursor, "children");
          final int _cursorIndexOfElderly = CursorUtil.getColumnIndexOrThrow(_cursor, "elderly");
          final int _cursorIndexOfDisabled = CursorUtil.getColumnIndexOrThrow(_cursor, "disabled");
          final int _cursorIndexOfPeopleTrapped = CursorUtil.getColumnIndexOrThrow(_cursor, "peopleTrapped");
          final int _cursorIndexOfInjured = CursorUtil.getColumnIndexOrThrow(_cursor, "injured");
          final int _cursorIndexOfMedicalRequired = CursorUtil.getColumnIndexOrThrow(_cursor, "medicalRequired");
          final int _cursorIndexOfWaterDepth = CursorUtil.getColumnIndexOrThrow(_cursor, "waterDepth");
          final int _cursorIndexOfWaterLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "waterLevel");
          final int _cursorIndexOfWaterRising = CursorUtil.getColumnIndexOrThrow(_cursor, "waterRising");
          final int _cursorIndexOfMedicalEmergencyType = CursorUtil.getColumnIndexOrThrow(_cursor, "medicalEmergencyType");
          final int _cursorIndexOfPersonUnconscious = CursorUtil.getColumnIndexOrThrow(_cursor, "personUnconscious");
          final int _cursorIndexOfBreathingProblem = CursorUtil.getColumnIndexOrThrow(_cursor, "breathingProblem");
          final int _cursorIndexOfSevereBleeding = CursorUtil.getColumnIndexOrThrow(_cursor, "severeBleeding");
          final int _cursorIndexOfPregnancyRelated = CursorUtil.getColumnIndexOrThrow(_cursor, "pregnancyRelated");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfBuildingFloor = CursorUtil.getColumnIndexOrThrow(_cursor, "buildingFloor");
          final int _cursorIndexOfContactNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "contactNumber");
          final int _cursorIndexOfPhotoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "photoUrl");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAssignedResponderId = CursorUtil.getColumnIndexOrThrow(_cursor, "assignedResponderId");
          final int _cursorIndexOfResponderContact = CursorUtil.getColumnIndexOrThrow(_cursor, "responderContact");
          final int _cursorIndexOfResponseTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "responseTimeMillis");
          final int _cursorIndexOfResolutionTimeMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "resolutionTimeMillis");
          final int _cursorIndexOfResponderNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "responderNotes");
          final int _cursorIndexOfEscalationRequired = CursorUtil.getColumnIndexOrThrow(_cursor, "escalationRequired");
          final int _cursorIndexOfIsDemoData = CursorUtil.getColumnIndexOrThrow(_cursor, "isDemoData");
          final List<EmergencyRequestEntity> _result = new ArrayList<EmergencyRequestEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EmergencyRequestEntity _item;
            final String _tmpEmergencyRequestId;
            _tmpEmergencyRequestId = _cursor.getString(_cursorIndexOfEmergencyRequestId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpHazardId;
            if (_cursor.isNull(_cursorIndexOfHazardId)) {
              _tmpHazardId = null;
            } else {
              _tmpHazardId = _cursor.getString(_cursorIndexOfHazardId);
            }
            final String _tmpClusterId;
            if (_cursor.isNull(_cursorIndexOfClusterId)) {
              _tmpClusterId = null;
            } else {
              _tmpClusterId = _cursor.getString(_cursorIndexOfClusterId);
            }
            final String _tmpZoneId;
            if (_cursor.isNull(_cursorIndexOfZoneId)) {
              _tmpZoneId = null;
            } else {
              _tmpZoneId = _cursor.getString(_cursorIndexOfZoneId);
            }
            final String _tmpEmergencyType;
            _tmpEmergencyType = _cursor.getString(_cursorIndexOfEmergencyType);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final float _tmpGpsAccuracy;
            _tmpGpsAccuracy = _cursor.getFloat(_cursorIndexOfGpsAccuracy);
            final long _tmpTimestampMillis;
            _tmpTimestampMillis = _cursor.getLong(_cursorIndexOfTimestampMillis);
            final int _tmpNumberOfPeople;
            _tmpNumberOfPeople = _cursor.getInt(_cursorIndexOfNumberOfPeople);
            final boolean _tmpChildren;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfChildren);
            _tmpChildren = _tmp != 0;
            final boolean _tmpElderly;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfElderly);
            _tmpElderly = _tmp_1 != 0;
            final boolean _tmpDisabled;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfDisabled);
            _tmpDisabled = _tmp_2 != 0;
            final boolean _tmpPeopleTrapped;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfPeopleTrapped);
            _tmpPeopleTrapped = _tmp_3 != 0;
            final boolean _tmpInjured;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfInjured);
            _tmpInjured = _tmp_4 != 0;
            final boolean _tmpMedicalRequired;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfMedicalRequired);
            _tmpMedicalRequired = _tmp_5 != 0;
            final String _tmpWaterDepth;
            if (_cursor.isNull(_cursorIndexOfWaterDepth)) {
              _tmpWaterDepth = null;
            } else {
              _tmpWaterDepth = _cursor.getString(_cursorIndexOfWaterDepth);
            }
            final String _tmpWaterLevel;
            if (_cursor.isNull(_cursorIndexOfWaterLevel)) {
              _tmpWaterLevel = null;
            } else {
              _tmpWaterLevel = _cursor.getString(_cursorIndexOfWaterLevel);
            }
            final Boolean _tmpWaterRising;
            final Integer _tmp_6;
            if (_cursor.isNull(_cursorIndexOfWaterRising)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _cursor.getInt(_cursorIndexOfWaterRising);
            }
            _tmpWaterRising = _tmp_6 == null ? null : _tmp_6 != 0;
            final String _tmpMedicalEmergencyType;
            if (_cursor.isNull(_cursorIndexOfMedicalEmergencyType)) {
              _tmpMedicalEmergencyType = null;
            } else {
              _tmpMedicalEmergencyType = _cursor.getString(_cursorIndexOfMedicalEmergencyType);
            }
            final boolean _tmpPersonUnconscious;
            final int _tmp_7;
            _tmp_7 = _cursor.getInt(_cursorIndexOfPersonUnconscious);
            _tmpPersonUnconscious = _tmp_7 != 0;
            final boolean _tmpBreathingProblem;
            final int _tmp_8;
            _tmp_8 = _cursor.getInt(_cursorIndexOfBreathingProblem);
            _tmpBreathingProblem = _tmp_8 != 0;
            final boolean _tmpSevereBleeding;
            final int _tmp_9;
            _tmp_9 = _cursor.getInt(_cursorIndexOfSevereBleeding);
            _tmpSevereBleeding = _tmp_9 != 0;
            final boolean _tmpPregnancyRelated;
            final int _tmp_10;
            _tmp_10 = _cursor.getInt(_cursorIndexOfPregnancyRelated);
            _tmpPregnancyRelated = _tmp_10 != 0;
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpBuildingFloor;
            if (_cursor.isNull(_cursorIndexOfBuildingFloor)) {
              _tmpBuildingFloor = null;
            } else {
              _tmpBuildingFloor = _cursor.getString(_cursorIndexOfBuildingFloor);
            }
            final String _tmpContactNumber;
            _tmpContactNumber = _cursor.getString(_cursorIndexOfContactNumber);
            final String _tmpPhotoUrl;
            if (_cursor.isNull(_cursorIndexOfPhotoUrl)) {
              _tmpPhotoUrl = null;
            } else {
              _tmpPhotoUrl = _cursor.getString(_cursorIndexOfPhotoUrl);
            }
            final String _tmpPriority;
            _tmpPriority = _cursor.getString(_cursorIndexOfPriority);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpAssignedResponderId;
            if (_cursor.isNull(_cursorIndexOfAssignedResponderId)) {
              _tmpAssignedResponderId = null;
            } else {
              _tmpAssignedResponderId = _cursor.getString(_cursorIndexOfAssignedResponderId);
            }
            final String _tmpResponderContact;
            if (_cursor.isNull(_cursorIndexOfResponderContact)) {
              _tmpResponderContact = null;
            } else {
              _tmpResponderContact = _cursor.getString(_cursorIndexOfResponderContact);
            }
            final Long _tmpResponseTimeMillis;
            if (_cursor.isNull(_cursorIndexOfResponseTimeMillis)) {
              _tmpResponseTimeMillis = null;
            } else {
              _tmpResponseTimeMillis = _cursor.getLong(_cursorIndexOfResponseTimeMillis);
            }
            final Long _tmpResolutionTimeMillis;
            if (_cursor.isNull(_cursorIndexOfResolutionTimeMillis)) {
              _tmpResolutionTimeMillis = null;
            } else {
              _tmpResolutionTimeMillis = _cursor.getLong(_cursorIndexOfResolutionTimeMillis);
            }
            final String _tmpResponderNotes;
            if (_cursor.isNull(_cursorIndexOfResponderNotes)) {
              _tmpResponderNotes = null;
            } else {
              _tmpResponderNotes = _cursor.getString(_cursorIndexOfResponderNotes);
            }
            final boolean _tmpEscalationRequired;
            final int _tmp_11;
            _tmp_11 = _cursor.getInt(_cursorIndexOfEscalationRequired);
            _tmpEscalationRequired = _tmp_11 != 0;
            final boolean _tmpIsDemoData;
            final int _tmp_12;
            _tmp_12 = _cursor.getInt(_cursorIndexOfIsDemoData);
            _tmpIsDemoData = _tmp_12 != 0;
            _item = new EmergencyRequestEntity(_tmpEmergencyRequestId,_tmpUserId,_tmpHazardId,_tmpClusterId,_tmpZoneId,_tmpEmergencyType,_tmpLatitude,_tmpLongitude,_tmpGpsAccuracy,_tmpTimestampMillis,_tmpNumberOfPeople,_tmpChildren,_tmpElderly,_tmpDisabled,_tmpPeopleTrapped,_tmpInjured,_tmpMedicalRequired,_tmpWaterDepth,_tmpWaterLevel,_tmpWaterRising,_tmpMedicalEmergencyType,_tmpPersonUnconscious,_tmpBreathingProblem,_tmpSevereBleeding,_tmpPregnancyRelated,_tmpDescription,_tmpBuildingFloor,_tmpContactNumber,_tmpPhotoUrl,_tmpPriority,_tmpStatus,_tmpAssignedResponderId,_tmpResponderContact,_tmpResponseTimeMillis,_tmpResolutionTimeMillis,_tmpResponderNotes,_tmpEscalationRequired,_tmpIsDemoData);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
