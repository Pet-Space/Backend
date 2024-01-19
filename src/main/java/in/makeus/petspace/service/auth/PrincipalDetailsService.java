package in.makeus.petspace.service.auth;

import in.makeus.petspace.domain.user.User;
import in.makeus.petspace.repository.UserRepository;
import in.makeus.petspace.util.exception.UserException;
import in.makeus.petspace.domain.user.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static in.makeus.petspace.util.BaseResponseStatus.NONE_USER;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public PrincipalDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(NONE_USER));
        log.info("loadUserByUsername, user=[{}][{}]", user.getEmail(), user.getRole());
        return new PrincipalDetails(user);
    }
}
