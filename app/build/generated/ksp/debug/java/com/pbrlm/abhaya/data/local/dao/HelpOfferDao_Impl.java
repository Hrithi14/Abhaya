package com.pbrlm.abhaya.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.pbrlm.abhaya.data.local.entity.HelpOfferEntity;
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
public final class HelpOfferDao_Impl implements HelpOfferDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<HelpOfferEntity> __insertionAdapterOfHelpOfferEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateStatus;

  public HelpOfferDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfHelpOfferEntity = new EntityInsertionAdapter<HelpOfferEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `help_offers` (`helpOfferId`,`emergencyRequestId`,`volunteerId`,`userId`,`helpType`,`message`,`timestampMillis`,`status`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final HelpOfferEntity entity) {
        statement.bindString(1, entity.getHelpOfferId());
        statement.bindString(2, entity.getEmergencyRequestId());
        statement.bindString(3, entity.getVolunteerId());
        statement.bindString(4, entity.getUserId());
        statement.bindString(5, entity.getHelpType());
        statement.bindString(6, entity.getMessage());
        statement.bindLong(7, entity.getTimestampMillis());
        statement.bindString(8, entity.getStatus());
      }
    };
    this.__preparedStmtOfUpdateStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE help_offers SET status = ? WHERE helpOfferId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final HelpOfferEntity entity, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfHelpOfferEntity.insert(entity);
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
  public Flow<List<HelpOfferEntity>> observeForEmergency(final String emergencyId) {
    final String _sql = "SELECT * FROM help_offers WHERE emergencyRequestId = ? ORDER BY timestampMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, emergencyId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"help_offers"}, new Callable<List<HelpOfferEntity>>() {
      @Override
      @NonNull
      public List<HelpOfferEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfHelpOfferId = CursorUtil.getColumnIndexOrThrow(_cursor, "helpOfferId");
          final int _cursorIndexOfEmergencyRequestId = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyRequestId");
          final int _cursorIndexOfVolunteerId = CursorUtil.getColumnIndexOrThrow(_cursor, "volunteerId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfHelpType = CursorUtil.getColumnIndexOrThrow(_cursor, "helpType");
          final int _cursorIndexOfMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "message");
          final int _cursorIndexOfTimestampMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "timestampMillis");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<HelpOfferEntity> _result = new ArrayList<HelpOfferEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final HelpOfferEntity _item;
            final String _tmpHelpOfferId;
            _tmpHelpOfferId = _cursor.getString(_cursorIndexOfHelpOfferId);
            final String _tmpEmergencyRequestId;
            _tmpEmergencyRequestId = _cursor.getString(_cursorIndexOfEmergencyRequestId);
            final String _tmpVolunteerId;
            _tmpVolunteerId = _cursor.getString(_cursorIndexOfVolunteerId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpHelpType;
            _tmpHelpType = _cursor.getString(_cursorIndexOfHelpType);
            final String _tmpMessage;
            _tmpMessage = _cursor.getString(_cursorIndexOfMessage);
            final long _tmpTimestampMillis;
            _tmpTimestampMillis = _cursor.getLong(_cursorIndexOfTimestampMillis);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _item = new HelpOfferEntity(_tmpHelpOfferId,_tmpEmergencyRequestId,_tmpVolunteerId,_tmpUserId,_tmpHelpType,_tmpMessage,_tmpTimestampMillis,_tmpStatus);
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
  public Flow<List<HelpOfferEntity>> observeMyOffers(final String userId) {
    final String _sql = "SELECT * FROM help_offers WHERE userId = ? ORDER BY timestampMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"help_offers"}, new Callable<List<HelpOfferEntity>>() {
      @Override
      @NonNull
      public List<HelpOfferEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfHelpOfferId = CursorUtil.getColumnIndexOrThrow(_cursor, "helpOfferId");
          final int _cursorIndexOfEmergencyRequestId = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyRequestId");
          final int _cursorIndexOfVolunteerId = CursorUtil.getColumnIndexOrThrow(_cursor, "volunteerId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfHelpType = CursorUtil.getColumnIndexOrThrow(_cursor, "helpType");
          final int _cursorIndexOfMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "message");
          final int _cursorIndexOfTimestampMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "timestampMillis");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<HelpOfferEntity> _result = new ArrayList<HelpOfferEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final HelpOfferEntity _item;
            final String _tmpHelpOfferId;
            _tmpHelpOfferId = _cursor.getString(_cursorIndexOfHelpOfferId);
            final String _tmpEmergencyRequestId;
            _tmpEmergencyRequestId = _cursor.getString(_cursorIndexOfEmergencyRequestId);
            final String _tmpVolunteerId;
            _tmpVolunteerId = _cursor.getString(_cursorIndexOfVolunteerId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpHelpType;
            _tmpHelpType = _cursor.getString(_cursorIndexOfHelpType);
            final String _tmpMessage;
            _tmpMessage = _cursor.getString(_cursorIndexOfMessage);
            final long _tmpTimestampMillis;
            _tmpTimestampMillis = _cursor.getLong(_cursorIndexOfTimestampMillis);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _item = new HelpOfferEntity(_tmpHelpOfferId,_tmpEmergencyRequestId,_tmpVolunteerId,_tmpUserId,_tmpHelpType,_tmpMessage,_tmpTimestampMillis,_tmpStatus);
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
  public Object getForEmergency(final String emergencyId,
      final Continuation<? super List<HelpOfferEntity>> $completion) {
    final String _sql = "SELECT * FROM help_offers WHERE emergencyRequestId = ? ORDER BY timestampMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, emergencyId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<HelpOfferEntity>>() {
      @Override
      @NonNull
      public List<HelpOfferEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfHelpOfferId = CursorUtil.getColumnIndexOrThrow(_cursor, "helpOfferId");
          final int _cursorIndexOfEmergencyRequestId = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyRequestId");
          final int _cursorIndexOfVolunteerId = CursorUtil.getColumnIndexOrThrow(_cursor, "volunteerId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfHelpType = CursorUtil.getColumnIndexOrThrow(_cursor, "helpType");
          final int _cursorIndexOfMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "message");
          final int _cursorIndexOfTimestampMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "timestampMillis");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<HelpOfferEntity> _result = new ArrayList<HelpOfferEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final HelpOfferEntity _item;
            final String _tmpHelpOfferId;
            _tmpHelpOfferId = _cursor.getString(_cursorIndexOfHelpOfferId);
            final String _tmpEmergencyRequestId;
            _tmpEmergencyRequestId = _cursor.getString(_cursorIndexOfEmergencyRequestId);
            final String _tmpVolunteerId;
            _tmpVolunteerId = _cursor.getString(_cursorIndexOfVolunteerId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpHelpType;
            _tmpHelpType = _cursor.getString(_cursorIndexOfHelpType);
            final String _tmpMessage;
            _tmpMessage = _cursor.getString(_cursorIndexOfMessage);
            final long _tmpTimestampMillis;
            _tmpTimestampMillis = _cursor.getLong(_cursorIndexOfTimestampMillis);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _item = new HelpOfferEntity(_tmpHelpOfferId,_tmpEmergencyRequestId,_tmpVolunteerId,_tmpUserId,_tmpHelpType,_tmpMessage,_tmpTimestampMillis,_tmpStatus);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
