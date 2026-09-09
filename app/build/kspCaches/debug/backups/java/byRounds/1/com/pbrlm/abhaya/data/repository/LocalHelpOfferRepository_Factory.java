package com.pbrlm.abhaya.data.repository;

import com.pbrlm.abhaya.data.local.dao.HelpOfferDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class LocalHelpOfferRepository_Factory implements Factory<LocalHelpOfferRepository> {
  private final Provider<HelpOfferDao> helpOfferDaoProvider;

  public LocalHelpOfferRepository_Factory(Provider<HelpOfferDao> helpOfferDaoProvider) {
    this.helpOfferDaoProvider = helpOfferDaoProvider;
  }

  @Override
  public LocalHelpOfferRepository get() {
    return newInstance(helpOfferDaoProvider.get());
  }

  public static LocalHelpOfferRepository_Factory create(
      Provider<HelpOfferDao> helpOfferDaoProvider) {
    return new LocalHelpOfferRepository_Factory(helpOfferDaoProvider);
  }

  public static LocalHelpOfferRepository newInstance(HelpOfferDao helpOfferDao) {
    return new LocalHelpOfferRepository(helpOfferDao);
  }
}
