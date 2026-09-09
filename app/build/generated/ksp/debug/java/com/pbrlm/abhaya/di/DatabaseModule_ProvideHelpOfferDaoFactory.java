package com.pbrlm.abhaya.di;

import com.pbrlm.abhaya.data.local.AbhayaDatabase;
import com.pbrlm.abhaya.data.local.dao.HelpOfferDao;
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
public final class DatabaseModule_ProvideHelpOfferDaoFactory implements Factory<HelpOfferDao> {
  private final Provider<AbhayaDatabase> dbProvider;

  public DatabaseModule_ProvideHelpOfferDaoFactory(Provider<AbhayaDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public HelpOfferDao get() {
    return provideHelpOfferDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideHelpOfferDaoFactory create(
      Provider<AbhayaDatabase> dbProvider) {
    return new DatabaseModule_ProvideHelpOfferDaoFactory(dbProvider);
  }

  public static HelpOfferDao provideHelpOfferDao(AbhayaDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideHelpOfferDao(db));
  }
}
