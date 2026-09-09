package com.pbrlm.abhaya.data.repository;

import com.pbrlm.abhaya.data.local.dao.VolunteerDao;
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
public final class LocalVolunteerRepository_Factory implements Factory<LocalVolunteerRepository> {
  private final Provider<VolunteerDao> volunteerDaoProvider;

  public LocalVolunteerRepository_Factory(Provider<VolunteerDao> volunteerDaoProvider) {
    this.volunteerDaoProvider = volunteerDaoProvider;
  }

  @Override
  public LocalVolunteerRepository get() {
    return newInstance(volunteerDaoProvider.get());
  }

  public static LocalVolunteerRepository_Factory create(
      Provider<VolunteerDao> volunteerDaoProvider) {
    return new LocalVolunteerRepository_Factory(volunteerDaoProvider);
  }

  public static LocalVolunteerRepository newInstance(VolunteerDao volunteerDao) {
    return new LocalVolunteerRepository(volunteerDao);
  }
}
