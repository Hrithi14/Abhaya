package com.pbrlm.abhaya.ui.screens.waterrescue;

import com.pbrlm.abhaya.data.location.LocationService;
import com.pbrlm.abhaya.data.user.UserProvider;
import com.pbrlm.abhaya.domain.repository.EmergencyRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class WaterRescueViewModel_Factory implements Factory<WaterRescueViewModel> {
  private final Provider<LocationService> locationServiceProvider;

  private final Provider<EmergencyRepository> emergencyRepositoryProvider;

  private final Provider<UserProvider> userProvider;

  public WaterRescueViewModel_Factory(Provider<LocationService> locationServiceProvider,
      Provider<EmergencyRepository> emergencyRepositoryProvider,
      Provider<UserProvider> userProvider) {
    this.locationServiceProvider = locationServiceProvider;
    this.emergencyRepositoryProvider = emergencyRepositoryProvider;
    this.userProvider = userProvider;
  }

  @Override
  public WaterRescueViewModel get() {
    return newInstance(locationServiceProvider.get(), emergencyRepositoryProvider.get(), userProvider.get());
  }

  public static WaterRescueViewModel_Factory create(
      Provider<LocationService> locationServiceProvider,
      Provider<EmergencyRepository> emergencyRepositoryProvider,
      Provider<UserProvider> userProvider) {
    return new WaterRescueViewModel_Factory(locationServiceProvider, emergencyRepositoryProvider, userProvider);
  }

  public static WaterRescueViewModel newInstance(LocationService locationService,
      EmergencyRepository emergencyRepository, UserProvider userProvider) {
    return new WaterRescueViewModel(locationService, emergencyRepository, userProvider);
  }
}
