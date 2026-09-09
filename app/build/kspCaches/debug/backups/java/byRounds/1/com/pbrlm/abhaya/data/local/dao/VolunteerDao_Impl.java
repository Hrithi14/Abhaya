package com.pbrlm.abhaya.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.pbrlm.abhaya.data.local.entity.VolunteerEntity;
import java.lang.Class;
import java.lang.Exception;
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
public final class VolunteerDao_Impl implements VolunteerDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<VolunteerEntity> __insertionAdapterOfVolunteerEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateAvailability;

  public VolunteerDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfVolunteerEntity = new EntityInsertionAdapter<VolunteerEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `volunteers` (`volunteerId`,`userId`,`name`,`contactNumber`,`latitude`,`longitude`,`safeZoneStatus`,`availableToHelp`,`helpType`,`capacity`,`notes`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VolunteerEntity entity) {
        statement.bindString(1, entity.getVolunteerId());
        statement.bindString(2, entity.getUserId());
        statement.bindString(3, entity.getName());
        statement.bindString(4, entity.getContactNumber());
        statement.bindDouble(5, entity.getLatitude());
        statement.bindDouble(6, entity.getLongitude());
        final int _tmp = entity.getSafeZoneStatus() ? 1 : 0;
        statement.bindLong(7, _tmp);
        final int _tmp_1 = entity.getAvailableToHelp() ? 1 : 0;
        statement.bindLong(8, _tmp_1);
        statement.bindString(9, entity.getHelpType());
        statement.bindLong(10, entity.getCapacity());
        statement.bindString(11, entity.getNotes());
      }
    };
    this.__preparedStmtOfUpdateAvailability = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE volunteers SET availableToHelp = ? WHERE volunteerId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertVolunteer(final VolunteerEntity entity,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfVolunteerEntity.insert(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateAvailability(final String id, final boolean available,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateAvailability.acquire();
        int _argIndex = 1;
        final int _tmp = available ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
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
          __preparedStmtOfUpdateAvailability.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<VolunteerEntity>> observeAvailable() {
    final String _sql = "SELECT * FROM volunteers WHERE availableToHelp = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"volunteers"}, new Callable<List<VolunteerEntity>>() {
      @Override
      @NonNull
      public List<VolunteerEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfVolunteerId = CursorUtil.getColumnIndexOrThrow(_cursor, "volunteerId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfContactNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "contactNumber");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfSafeZoneStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "safeZoneStatus");
          final int _cursorIndexOfAvailableToHelp = CursorUtil.getColumnIndexOrThrow(_cursor, "availableToHelp");
          final int _cursorIndexOfHelpType = CursorUtil.getColumnIndexOrThrow(_cursor, "helpType");
          final int _cursorIndexOfCapacity = CursorUtil.getColumnIndexOrThrow(_cursor, "capacity");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<VolunteerEntity> _result = new ArrayList<VolunteerEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VolunteerEntity _item;
            final String _tmpVolunteerId;
            _tmpVolunteerId = _cursor.getString(_cursorIndexOfVolunteerId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpContactNumber;
            _tmpContactNumber = _cursor.getString(_cursorIndexOfContactNumber);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final boolean _tmpSafeZoneStatus;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfSafeZoneStatus);
            _tmpSafeZoneStatus = _tmp != 0;
            final boolean _tmpAvailableToHelp;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfAvailableToHelp);
            _tmpAvailableToHelp = _tmp_1 != 0;
            final String _tmpHelpType;
            _tmpHelpType = _cursor.getString(_cursorIndexOfHelpType);
            final int _tmpCapacity;
            _tmpCapacity = _cursor.getInt(_cursorIndexOfCapacity);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            _item = new VolunteerEntity(_tmpVolunteerId,_tmpUserId,_tmpName,_tmpContactNumber,_tmpLatitude,_tmpLongitude,_tmpSafeZoneStatus,_tmpAvailableToHelp,_tmpHelpType,_tmpCapacity,_tmpNotes);
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
  public Object getByUserId(final String userId,
      final Continuation<? super VolunteerEntity> $completion) {
    final String _sql = "SELECT * FROM volunteers WHERE userId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<VolunteerEntity>() {
      @Override
      @Nullable
      public VolunteerEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfVolunteerId = CursorUtil.getColumnIndexOrThrow(_cursor, "volunteerId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfContactNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "contactNumber");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfSafeZoneStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "safeZoneStatus");
          final int _cursorIndexOfAvailableToHelp = CursorUtil.getColumnIndexOrThrow(_cursor, "availableToHelp");
          final int _cursorIndexOfHelpType = CursorUtil.getColumnIndexOrThrow(_cursor, "helpType");
          final int _cursorIndexOfCapacity = CursorUtil.getColumnIndexOrThrow(_cursor, "capacity");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final VolunteerEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpVolunteerId;
            _tmpVolunteerId = _cursor.getString(_cursorIndexOfVolunteerId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpContactNumber;
            _tmpContactNumber = _cursor.getString(_cursorIndexOfContactNumber);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final boolean _tmpSafeZoneStatus;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfSafeZoneStatus);
            _tmpSafeZoneStatus = _tmp != 0;
            final boolean _tmpAvailableToHelp;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfAvailableToHelp);
            _tmpAvailableToHelp = _tmp_1 != 0;
            final String _tmpHelpType;
            _tmpHelpType = _cursor.getString(_cursorIndexOfHelpType);
            final int _tmpCapacity;
            _tmpCapacity = _cursor.getInt(_cursorIndexOfCapacity);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            _result = new VolunteerEntity(_tmpVolunteerId,_tmpUserId,_tmpName,_tmpContactNumber,_tmpLatitude,_tmpLongitude,_tmpSafeZoneStatus,_tmpAvailableToHelp,_tmpHelpType,_tmpCapacity,_tmpNotes);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
