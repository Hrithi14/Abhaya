package com.pbrlm.abhaya.ui.screens.medical;

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
public final class MedicalEmergencyViewModel_Factory implements Factory<MedicalEmergencyViewModel> {
  private final Provider<LocationService> locationServiceProvider;

  private final Provider<EmergencyRepository> emergencyRepositoryProvider;

  private final Provider<UserProvider> userProvider;

  public MedicalEmergencyViewModel_Factory(Provider<LocationService> locationServiceProvider,
      Provider<EmergencyRepository> emergencyRepositoryProvider,
      Provider<UserProvider> userProvider) {
    this.locationServiceProvider = locationServiceProvider;
    this.emergencyRepositoryProvider = emergencyRepositoryProvider;
    this.userProvider = userProvider;
  }

  @Override
  public MedicalEmergencyViewModel get() {
    return newInstance(locationServiceProvider.get(), emergencyRepositoryProvider.get(), userProvider.get());
  }

  public static MedicalEmergencyViewModel_Factory create(
      Provider<LocationService> locationServiceProvider,
      Provider<EmergencyRepository> emergencyRepositoryProvider,
      Provider<UserProvider> userProvider) {
    return new MedicalEmergencyViewModel_Factory(locationServiceProvider, emergencyRepositoryProvider, userProvider);
  }

  public static MedicalEmergencyViewModel newInstance(LocationService locationService,
      EmergencyRepository emergencyRepository, UserProvider userProvider) {
    return new MedicalEmergencyViewModel(locationService, emergencyRepository, userProvider);
  }
}
