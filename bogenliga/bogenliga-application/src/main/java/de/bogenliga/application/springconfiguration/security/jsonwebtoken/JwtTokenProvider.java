package de.bogenliga.application.springconfiguration.security.jsonwebtoken;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import de.bogenliga.application.business.user.api.types.UserWithPermissionsDO;
import de.bogenliga.application.common.configuration.SecurityJsonWebTokenConfiguration;
import de.bogenliga.application.common.errorhandling.ErrorCode;
import de.bogenliga.application.common.errorhandling.exception.TechnicalException;
import de.bogenliga.application.services.v1.user.model.UserSignInDTO;
import de.bogenliga.application.springconfiguration.security.authentication.UserAuthenticationProvider;
import de.bogenliga.application.springconfiguration.security.permissions.RequiresPermission;
import de.bogenliga.application.springconfiguration.security.types.UserPermission;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * I handle all JSON Web Token processing steps.
 * <p>
 * I extract the JWT from the request header and decrypt the token. The token contains the user information and
 * permissions.
 * <p>
 * The permissions can be checked with the {@link RequiresPermission} annotation.
 *
 * @author Andre Lehnert, eXXcellent solutions consulting & software gmbh
 */
@Component
public class JwtTokenProvider {

    private static final String REFRESH_COUNTER = "refreshCounter";
    private static final String USER_INFO = "usr";
    private static final String USER_INFO_VERSION = "version";
    private static final String USER_INFO_ID = "id";
    private static final Logger LOG = LoggerFactory.getLogger(JwtTokenProvider.class);
    private static final String AUTH = "auth";
    private final SecurityJsonWebTokenConfiguration securityJsonWebTokenConfiguration;
    private final UserAuthenticationProvider userAuthenticationProvider;

    // defined in resources/application-<PROFILE>.properties
    private long validityInMilliseconds;
    private String secretKey;
    private int maxTokenRefresh;


    @Autowired
    public JwtTokenProvider(
            final SecurityJsonWebTokenConfiguration securityJsonWebTokenConfiguration,
            final UserAuthenticationProvider userAuthenticationProvider) {
        this.securityJsonWebTokenConfiguration = securityJsonWebTokenConfiguration;
        this.userAuthenticationProvider = userAuthenticationProvider;
    }


    public static String resolveToken(final HttpServletRequest req) {
        final String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            // remove "Bearer" prefix
            return bearerToken.substring(7);
        }
        return null;
    }


    public String getUsername(final String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        } catch (final RuntimeException e) {
            throw new TechnicalException(ErrorCode.UNEXPECTED_ERROR, "User information could not parsed from JWT", e);
        }
    }


    public UserSignInDTO resolveUserSignInDTO(final String token) {

        final Object userInfo = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get(
                USER_INFO);

        if (userInfo instanceof Map) {
            final Map<String, String> userInfoMap = (HashMap<String, String>) userInfo;

            final long id = Long.parseLong(userInfoMap.get(USER_INFO_ID));
            final long version = Long.parseLong(userInfoMap.get(USER_INFO_VERSION));

            final String email = getUsername(token);
            final Set<UserPermission> permissions = getPermissions(token);

            return new UserSignInDTO(id, version, email, token, permissions);
        }

        return null;
    }


    public Set<UserPermission> getPermissions(final String token) {
        Set<UserPermission> userPermissions = new HashSet<>();

        try {
            final Object permissions = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get(AUTH);

            if (permissions instanceof ArrayList) {
                final List<String> permissionStringList = (ArrayList<String>) permissions;
                userPermissions = permissionStringList.stream()
                        .map(UserPermission::fromValue)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
            }
        } catch (final RuntimeException e) {
            throw new TechnicalException(ErrorCode.UNEXPECTED_ERROR, "Permissions could not parsed from JWT", e);
        }

        return userPermissions;
    }


    public String createToken(final Authentication authentication) {
        final UserWithPermissionsDO userWithPermissionsDO = ((UserWithPermissionsDO) authentication.getPrincipal());
        final Set<UserPermission> permissions = authentication.getAuthorities().stream()
                .map(authority -> (UserPermission) authority)
                .collect(Collectors.toSet());

        return createToken(userWithPermissionsDO, permissions);
    }


    private String createToken(final UserWithPermissionsDO userWithPermissionsDO,
                               final Set<UserPermission> permissions) {
        return createToken(userWithPermissionsDO.getEmail(), userWithPermissionsDO.getId(),
                userWithPermissionsDO.getVersion(), permissions, 0);
    }


    private String createToken(final String username, final long id, final long version,
                               final Set<UserPermission> permissions,
                               final int refreshCounter) {
        // subject
        final Claims claims = Jwts.claims().setSubject(username);

        // add permissions
        claims.put(AUTH, permissions.stream()
                .map(UserPermission::getAuthority)
                .collect(Collectors.toList()));

        // add user info
        final Map<String, String> userInfo = new HashMap<>();
        userInfo.put(USER_INFO_VERSION, String.valueOf(version));
        userInfo.put(USER_INFO_ID, String.valueOf(id));

        claims.put(USER_INFO, userInfo);

        // add refresh counter
        claims.put(REFRESH_COUNTER, refreshCounter);

        // expiration time
        final Date now = new Date();
        final Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }


    Authentication getAuthentication(final String token) {
        return userAuthenticationProvider.createAuthenticationPlaceholder(getUsername(token), getPermissions(token));
    }


    boolean validateToken(final String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (final JwtException | IllegalArgumentException e) {
            LOG.warn("Received invalid JWT token with error message: {}", e.getMessage());
            // send 403 "Forbidden"
            return false;
        }
    }


    String refreshToken(final String token) {

        final long remainingTimeInMilliSeconds = getRemainingValidityTime(token);
        int refreshCounter = getRefreshCounter(token);

        LOG.trace("Refresh token ? refreshCounter = {} and remainingTime = {} s", refreshCounter,
                (remainingTimeInMilliSeconds / 1000));

        // check, if refresh necessary and possible
        // expiration reached (last 90 %) and refresh allowed -> refresh
        if (refreshCounter < maxTokenRefresh && remainingTimeInMilliSeconds < (validityInMilliseconds * 0.9)) {

            final UserSignInDTO userSignInDTO = resolveUserSignInDTO(token);
            refreshCounter++;

            LOG.trace("Token refreshed. Please use the new token");

            return createToken(userSignInDTO.getEmail(), userSignInDTO.getId(), userSignInDTO.getVersion(),
                    userSignInDTO.getPermissions(), refreshCounter);
        } else {
            return token;
        }
    }


    int getRefreshCounter(final String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody()
                .get(REFRESH_COUNTER, Integer.class);
    }


    long getRemainingValidityTime(final String token) {

        final Date expirationDate = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(
                token).getBody().getExpiration();
        final Date now = new Date();

        return expirationDate.getTime() - now.getTime();
    }


    @PostConstruct
    protected void init() {
        validityInMilliseconds = securityJsonWebTokenConfiguration.getExpiration();
        secretKey = Base64.getEncoder().encodeToString(securityJsonWebTokenConfiguration.getSecret().getBytes());
        maxTokenRefresh = securityJsonWebTokenConfiguration.getRefresh();
    }


}