package com.pbrlm.abhaya.data.integration.mock;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class MockZoneDataProvider_Factory implements Factory<MockZoneDataProvider> {
  @Override
  public MockZoneDataProvider get() {
    return newInstance();
  }

  public static MockZoneDataProvider_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static MockZoneDataProvider newInstance() {
    return new MockZoneDataProvider();
  }

  private static final class InstanceHolder {
    private static final MockZoneDataProvider_Factory INSTANCE = new MockZoneDataProvider_Factory();
  }
}
