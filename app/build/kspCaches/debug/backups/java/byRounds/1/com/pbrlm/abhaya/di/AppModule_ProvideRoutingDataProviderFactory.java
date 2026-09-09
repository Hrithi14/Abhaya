package com.pbrlm.abhaya.di;

import com.pbrlm.abhaya.data.integration.mock.MockRoutingDataProvider;
import com.pbrlm.abhaya.domain.integration.RoutingDataProvider;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideRoutingDataProviderFactory implements Factory<RoutingDataProvider> {
  private final Provider<MockRoutingDataProvider> mockProvider;

  public AppModule_ProvideRoutingDataProviderFactory(
      Provider<MockRoutingDataProvider> mockProvider) {
    this.mockProvider = mockProvider;
  }

  @Override
  public RoutingDataProvider get() {
    return provideRoutingDataProvider(mockProvider.get());
  }

  public static AppModule_ProvideRoutingDataProviderFactory create(
      Provider<MockRoutingDataProvider> mockProvider) {
    return new AppModule_ProvideRoutingDataProviderFactory(mockProvider);
  }

  public static RoutingDataProvider provideRoutingDataProvider(MockRoutingDataProvider mock) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideRoutingDataProvider(mock));
  }
}
