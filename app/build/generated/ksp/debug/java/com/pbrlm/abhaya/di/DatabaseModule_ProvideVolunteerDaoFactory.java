package com.pbrlm.abhaya.di;

import com.pbrlm.abhaya.data.local.AbhayaDatabase;
import com.pbrlm.abhaya.data.local.dao.VolunteerDao;
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
public final class DatabaseModule_ProvideVolunteerDaoFactory implements Factory<VolunteerDao> {
  private final Provider<AbhayaDatabase> dbProvider;

  public DatabaseModule_ProvideVolunteerDaoFactory(Provider<AbhayaDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public VolunteerDao get() {
    return provideVolunteerDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideVolunteerDaoFactory create(
      Provider<AbhayaDatabase> dbProvider) {
    return new DatabaseModule_ProvideVolunteerDaoFactory(dbProvider);
  }

  public static VolunteerDao provideVolunteerDao(AbhayaDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideVolunteerDao(db));
  }
}
