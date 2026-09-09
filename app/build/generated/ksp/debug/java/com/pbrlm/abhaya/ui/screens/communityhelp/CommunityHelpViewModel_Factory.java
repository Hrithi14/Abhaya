package com.pbrlm.abhaya.ui.screens.communityhelp;

import com.pbrlm.abhaya.data.local.dao.EmergencyRequestDao;
import com.pbrlm.abhaya.data.user.UserProvider;
import com.pbrlm.abhaya.domain.repository.HelpOfferRepository;
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
public final class CommunityHelpViewModel_Factory implements Factory<CommunityHelpViewModel> {
  private final Provider<EmergencyRequestDao> emergencyRequestDaoProvider;

  private final Provider<HelpOfferRepository> helpOfferRepositoryProvider;

  private final Provider<UserProvider> userProvider;

  public CommunityHelpViewModel_Factory(Provider<EmergencyRequestDao> emergencyRequestDaoProvider,
      Provider<HelpOfferRepository> helpOfferRepositoryProvider,
      Provider<UserProvider> userProvider) {
    this.emergencyRequestDaoProvider = emergencyRequestDaoProvider;
    this.helpOfferRepositoryProvider = helpOfferRepositoryProvider;
    this.userProvider = userProvider;
  }

  @Override
  public CommunityHelpViewModel get() {
    return newInstance(emergencyRequestDaoProvider.get(), helpOfferRepositoryProvider.get(), userProvider.get());
  }

  public static CommunityHelpViewModel_Factory create(
      Provider<EmergencyRequestDao> emergencyRequestDaoProvider,
      Provider<HelpOfferRepository> helpOfferRepositoryProvider,
      Provider<UserProvider> userProvider) {
    return new CommunityHelpViewModel_Factory(emergencyRequestDaoProvider, helpOfferRepositoryProvider, userProvider);
  }

  public static CommunityHelpViewModel newInstance(EmergencyRequestDao emergencyRequestDao,
      HelpOfferRepository helpOfferRepository, UserProvider userProvider) {
    return new CommunityHelpViewModel(emergencyRequestDao, helpOfferRepository, userProvider);
  }
}
