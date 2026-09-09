package com.pbrlm.abhaya.ui.screens.history;

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
public final class EmergencyHistoryViewModel_Factory implements Factory<EmergencyHistoryViewModel> {
  private final Provider<EmergencyRepository> emergencyRepositoryProvider;

  private final Provider<UserProvider> userProvider;

  public EmergencyHistoryViewModel_Factory(
      Provider<EmergencyRepository> emergencyRepositoryProvider,
      Provider<UserProvider> userProvider) {
    this.emergencyRepositoryProvider = emergencyRepositoryProvider;
    this.userProvider = userProvider;
  }

  @Override
  public EmergencyHistoryViewModel get() {
    return newInstance(emergencyRepositoryProvider.get(), userProvider.get());
  }

  public static EmergencyHistoryViewModel_Factory create(
      Provider<EmergencyRepository> emergencyRepositoryProvider,
      Provider<UserProvider> userProvider) {
    return new EmergencyHistoryViewModel_Factory(emergencyRepositoryProvider, userProvider);
  }

  public static EmergencyHistoryViewModel newInstance(EmergencyRepository emergencyRepository,
      UserProvider userProvider) {
    return new EmergencyHistoryViewModel(emergencyRepository, userProvider);
  }
}
