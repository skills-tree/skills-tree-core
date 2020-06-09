package com.github.skillstree.core.service;

import java.security.interfaces.RSAPublicKey;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Authorizer {

    private static final Logger logger = LoggerFactory.getLogger(Authorizer.class);

    private static final Pattern USERS_PATH_PATTERN = Pattern.compile("(users\\/)([a-zA-Z0-9|]*)(\\/*)");

    public boolean hasAccess(String path, String jwtToken) {
        logger.info("Path: {}", path);
        logger.info("JWT token: {}", jwtToken);

        Matcher matcher = USERS_PATH_PATTERN.matcher(path);
        if (matcher.find()) {
            try {
                DecodedJWT jwt = JWT.decode(jwtToken);

                JwkProvider provider = new UrlJwkProvider(jwt.getIssuer());
                Jwk jwk = provider.get(jwt.getKeyId());

                Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
                JWTVerifier verifier = JWT.require(algorithm).build();
                jwt = verifier.verify(jwtToken);

                final String authorizedUserId = jwt.getSubject();
                final String accessedUserId = matcher.group(2);
                logger.info("AuthorizedUserId: {}, AccessedUserId: {}", authorizedUserId, accessedUserId);

                return authorizedUserId.equals(accessedUserId);

            } catch (JWTDecodeException | JwkException e) {
                logger.error("Token is not valid", e);
                return false;
            }

        } else {
            logger.error("The path is incorrect: {}", path);
            throw new IllegalArgumentException("This path is not a valid path to check the access");
        }
    }
}
