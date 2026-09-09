package com.pbrlm.abhaya.di;

import com.pbrlm.abhaya.data.user.UserProvider;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class AppModule_ProvideUserProviderFactory implements Factory<UserProvider> {
  @Override
  public UserProvider get() {
    return provideUserProvider();
  }

  public static AppModule_ProvideUserProviderFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static UserProvider provideUserProvider() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideUserProvider());
  }

  private static final class InstanceHolder {
    private static final AppModule_ProvideUserProviderFactory INSTANCE = new AppModule_ProvideUserProviderFactory();
  }
}
