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
public final class MockRoutingDataProvider_Factory implements Factory<MockRoutingDataProvider> {
  @Override
  public MockRoutingDataProvider get() {
    return newInstance();
  }

  public static MockRoutingDataProvider_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static MockRoutingDataProvider newInstance() {
    return new MockRoutingDataProvider();
  }

  private static final class InstanceHolder {
    private static final MockRoutingDataProvider_Factory INSTANCE = new MockRoutingDataProvider_Factory();
  }
}
