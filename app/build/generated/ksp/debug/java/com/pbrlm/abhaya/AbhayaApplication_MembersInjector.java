package com.pbrlm.abhaya;

import com.pbrlm.abhaya.data.demo.DemoDataSeeder;
import com.pbrlm.abhaya.data.user.UserProvider;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class AbhayaApplication_MembersInjector implements MembersInjector<AbhayaApplication> {
  private final Provider<DemoDataSeeder> demoDataSeederProvider;

  private final Provider<UserProvider> userProvider;

  public AbhayaApplication_MembersInjector(Provider<DemoDataSeeder> demoDataSeederProvider,
      Provider<UserProvider> userProvider) {
    this.demoDataSeederProvider = demoDataSeederProvider;
    this.userProvider = userProvider;
  }

  public static MembersInjector<AbhayaApplication> create(
      Provider<DemoDataSeeder> demoDataSeederProvider, Provider<UserProvider> userProvider) {
    return new AbhayaApplication_MembersInjector(demoDataSeederProvider, userProvider);
  }

  @Override
  public void injectMembers(AbhayaApplication instance) {
    injectDemoDataSeeder(instance, demoDataSeederProvider.get());
    injectUserProvider(instance, userProvider.get());
  }

  @InjectedFieldSignature("com.pbrlm.abhaya.AbhayaApplication.demoDataSeeder")
  public static void injectDemoDataSeeder(AbhayaApplication instance,
      DemoDataSeeder demoDataSeeder) {
    instance.demoDataSeeder = demoDataSeeder;
  }

  @InjectedFieldSignature("com.pbrlm.abhaya.AbhayaApplication.userProvider")
  public static void injectUserProvider(AbhayaApplication instance, UserProvider userProvider) {
    instance.userProvider = userProvider;
  }
}
