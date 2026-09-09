package com.pbrlm.abhaya.ui.screens.detail;

import android.content.Context;
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
public final class EmergencyDetailViewModel_Factory implements Factory<EmergencyDetailViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<EmergencyRepository> emergencyRepositoryProvider;

  public EmergencyDetailViewModel_Factory(Provider<Context> contextProvider,
      Provider<EmergencyRepository> emergencyRepositoryProvider) {
    this.contextProvider = contextProvider;
    this.emergencyRepositoryProvider = emergencyRepositoryProvider;
  }

  @Override
  public EmergencyDetailViewModel get() {
    return newInstance(contextProvider.get(), emergencyRepositoryProvider.get());
  }

  public static EmergencyDetailViewModel_Factory create(Provider<Context> contextProvider,
      Provider<EmergencyRepository> emergencyRepositoryProvider) {
    return new EmergencyDetailViewModel_Factory(contextProvider, emergencyRepositoryProvider);
  }

  public static EmergencyDetailViewModel newInstance(Context context,
      EmergencyRepository emergencyRepository) {
    return new EmergencyDetailViewModel(context, emergencyRepository);
  }
}
