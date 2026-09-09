package com.pbrlm.abhaya.ui.screens.evacuate;

import com.pbrlm.abhaya.data.location.LocationService;
import com.pbrlm.abhaya.domain.integration.RoutingDataProvider;
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
public final class EvacuateSafetyViewModel_Factory implements Factory<EvacuateSafetyViewModel> {
  private final Provider<LocationService> locationServiceProvider;

  private final Provider<RoutingDataProvider> routingDataProvider;

  public EvacuateSafetyViewModel_Factory(Provider<LocationService> locationServiceProvider,
      Provider<RoutingDataProvider> routingDataProvider) {
    this.locationServiceProvider = locationServiceProvider;
    this.routingDataProvider = routingDataProvider;
  }

  @Override
  public EvacuateSafetyViewModel get() {
    return newInstance(locationServiceProvider.get(), routingDataProvider.get());
  }

  public static EvacuateSafetyViewModel_Factory create(
      Provider<LocationService> locationServiceProvider,
      Provider<RoutingDataProvider> routingDataProvider) {
    return new EvacuateSafetyViewModel_Factory(locationServiceProvider, routingDataProvider);
  }

  public static EvacuateSafetyViewModel newInstance(LocationService locationService,
      RoutingDataProvider routingDataProvider) {
    return new EvacuateSafetyViewModel(locationService, routingDataProvider);
  }
}
