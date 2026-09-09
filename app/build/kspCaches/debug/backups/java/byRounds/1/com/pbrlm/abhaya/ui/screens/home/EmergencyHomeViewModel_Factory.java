package com.pbrlm.abhaya.ui.screens.home;

import android.content.Context;
import com.pbrlm.abhaya.data.location.LocationService;
import com.pbrlm.abhaya.data.user.UserProvider;
import com.pbrlm.abhaya.domain.integration.ZoneDataProvider;
import com.pbrlm.abhaya.domain.repository.EmergencyRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class EmergencyHomeViewModel_Factory implements Factory<EmergencyHomeViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<LocationService> locationServiceProvider;

  private final Provider<EmergencyRepository> emergencyRepositoryProvider;

  private final Provider<ZoneDataProvider> zoneDataProvider;

  private final Provider<UserProvider> userProvider;

  public EmergencyHomeViewModel_Factory(Provider<Context> contextProvider,
      Provider<LocationService> locationServiceProvider,
      Provider<EmergencyRepository> emergencyRepositoryProvider,
      Provider<ZoneDataProvider> zoneDataProvider, Provider<UserProvider> userProvider) {
    this.contextProvider = contextProvider;
    this.locationServiceProvider = locationServiceProvider;
    this.emergencyRepositoryProvider = emergencyRepositoryProvider;
    this.zoneDataProvider = zoneDataProvider;
    this.userProvider = userProvider;
  }

  @Override
  public EmergencyHomeViewModel get() {
    return newInstance(contextProvider.get(), locationServiceProvider.get(), emergencyRepositoryProvider.get(), zoneDataProvider.get(), userProvider.get());
  }

  public static EmergencyHomeViewModel_Factory create(Provider<Context> contextProvider,
      Provider<LocationService> locationServiceProvider,
      Provider<EmergencyRepository> emergencyRepositoryProvider,
      Provider<ZoneDataProvider> zoneDataProvider, Provider<UserProvider> userProvider) {
    return new EmergencyHomeViewModel_Factory(contextProvider, locationServiceProvider, emergencyRepositoryProvider, zoneDataProvider, userProvider);
  }

  public static EmergencyHomeViewModel newInstance(Context context, LocationService locationService,
      EmergencyRepository emergencyRepository, ZoneDataProvider zoneDataProvider,
      UserProvider userProvider) {
    return new EmergencyHomeViewModel(context, locationService, emergencyRepository, zoneDataProvider, userProvider);
  }
}
