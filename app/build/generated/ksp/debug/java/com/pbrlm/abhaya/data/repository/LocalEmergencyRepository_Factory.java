package com.pbrlm.abhaya.data.repository;

import com.pbrlm.abhaya.data.local.dao.EmergencyRequestDao;
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
public final class LocalEmergencyRepository_Factory implements Factory<LocalEmergencyRepository> {
  private final Provider<EmergencyRequestDao> daoProvider;

  public LocalEmergencyRepository_Factory(Provider<EmergencyRequestDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public LocalEmergencyRepository get() {
    return newInstance(daoProvider.get());
  }

  public static LocalEmergencyRepository_Factory create(Provider<EmergencyRequestDao> daoProvider) {
    return new LocalEmergencyRepository_Factory(daoProvider);
  }

  public static LocalEmergencyRepository newInstance(EmergencyRequestDao dao) {
    return new LocalEmergencyRepository(dao);
  }
}
