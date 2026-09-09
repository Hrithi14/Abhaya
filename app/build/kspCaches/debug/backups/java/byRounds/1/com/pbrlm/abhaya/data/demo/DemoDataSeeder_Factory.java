package com.pbrlm.abhaya.data.demo;

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
public final class DemoDataSeeder_Factory implements Factory<DemoDataSeeder> {
  private final Provider<EmergencyRequestDao> daoProvider;

  public DemoDataSeeder_Factory(Provider<EmergencyRequestDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public DemoDataSeeder get() {
    return newInstance(daoProvider.get());
  }

  public static DemoDataSeeder_Factory create(Provider<EmergencyRequestDao> daoProvider) {
    return new DemoDataSeeder_Factory(daoProvider);
  }

  public static DemoDataSeeder newInstance(EmergencyRequestDao dao) {
    return new DemoDataSeeder(dao);
  }
}
