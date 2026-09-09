package com.pbrlm.abhaya.domain.service;

import com.pbrlm.abhaya.domain.repository.EmergencyRepository;
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
public final class EscalationService_Factory implements Factory<EscalationService> {
  private final Provider<EmergencyRepository> emergencyRepositoryProvider;

  public EscalationService_Factory(Provider<EmergencyRepository> emergencyRepositoryProvider) {
    this.emergencyRepositoryProvider = emergencyRepositoryProvider;
  }

  @Override
  public EscalationService get() {
    return newInstance(emergencyRepositoryProvider.get());
  }

  public static EscalationService_Factory create(
      Provider<EmergencyRepository> emergencyRepositoryProvider) {
    return new EscalationService_Factory(emergencyRepositoryProvider);
  }

  public static EscalationService newInstance(EmergencyRepository emergencyRepository) {
    return new EscalationService(emergencyRepository);
  }
}
