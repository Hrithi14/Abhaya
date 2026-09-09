package com.pbrlm.abhaya.di;

import com.pbrlm.abhaya.data.integration.mock.MockZoneDataProvider;
import com.pbrlm.abhaya.domain.integration.ZoneDataProvider;
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
public final class AppModule_ProvideZoneDataProviderFactory implements Factory<ZoneDataProvider> {
  private final Provider<MockZoneDataProvider> mockProvider;

  public AppModule_ProvideZoneDataProviderFactory(Provider<MockZoneDataProvider> mockProvider) {
    this.mockProvider = mockProvider;
  }

  @Override
  public ZoneDataProvider get() {
    return provideZoneDataProvider(mockProvider.get());
  }

  public static AppModule_ProvideZoneDataProviderFactory create(
      Provider<MockZoneDataProvider> mockProvider) {
    return new AppModule_ProvideZoneDataProviderFactory(mockProvider);
  }

  public static ZoneDataProvider provideZoneDataProvider(MockZoneDataProvider mock) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideZoneDataProvider(mock));
  }
}
