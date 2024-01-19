package in.makeus.petspace.domain.user.oauth;

import in.makeus.petspace.domain.user.OauthProvider;

public interface OauthUserInfo {
    String getProviderId();

    OauthProvider getProvider();

    String getEmail();

    String getNickName();
}
