package com.pbrlm.abhaya.ui.screens.myrequests;

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
public final class MyRequestsViewModel_Factory implements Factory<MyRequestsViewModel> {
  private final Provider<EmergencyRepository> emergencyRepositoryProvider;

  private final Provider<UserProvider> userProvider;

  public MyRequestsViewModel_Factory(Provider<EmergencyRepository> emergencyRepositoryProvider,
      Provider<UserProvider> userProvider) {
    this.emergencyRepositoryProvider = emergencyRepositoryProvider;
    this.userProvider = userProvider;
  }

  @Override
  public MyRequestsViewModel get() {
    return newInstance(emergencyRepositoryProvider.get(), userProvider.get());
  }

  public static MyRequestsViewModel_Factory create(
      Provider<EmergencyRepository> emergencyRepositoryProvider,
      Provider<UserProvider> userProvider) {
    return new MyRequestsViewModel_Factory(emergencyRepositoryProvider, userProvider);
  }

  public static MyRequestsViewModel newInstance(EmergencyRepository emergencyRepository,
      UserProvider userProvider) {
    return new MyRequestsViewModel(emergencyRepository, userProvider);
  }
}
