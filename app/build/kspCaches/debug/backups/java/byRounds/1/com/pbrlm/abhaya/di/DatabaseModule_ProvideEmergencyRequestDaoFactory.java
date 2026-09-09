package com.pbrlm.abhaya.di;

import com.pbrlm.abhaya.data.local.AbhayaDatabase;
import com.pbrlm.abhaya.data.local.dao.EmergencyRequestDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class DatabaseModule_ProvideEmergencyRequestDaoFactory implements Factory<EmergencyRequestDao> {
  private final Provider<AbhayaDatabase> dbProvider;

  public DatabaseModule_ProvideEmergencyRequestDaoFactory(Provider<AbhayaDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public EmergencyRequestDao get() {
    return provideEmergencyRequestDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideEmergencyRequestDaoFactory create(
      Provider<AbhayaDatabase> dbProvider) {
    return new DatabaseModule_ProvideEmergencyRequestDaoFactory(dbProvider);
  }

  public static EmergencyRequestDao provideEmergencyRequestDao(AbhayaDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideEmergencyRequestDao(db));
  }
}
