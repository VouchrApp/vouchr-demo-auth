package com.vouchrtech.demo.auth.services;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.vouchrtech.demo.auth.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPrivateKey;
import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;

/*

TODO Update this class to access your private key securely

Update this class to retrieve your private key in a way that corresponds to your policies and standards.

Or set LOAD_PRIVATE_KEY to true to load the private key from your filesystem acknowledging any deployment will
meet your organizations policies.

*/

@Service
public class VouchrJwtService {

    private static final boolean LOAD_PRIVATE_KEY = false;

    @Value("${vouchr.jwt.key.pem:file:privkey.pem}")
    private Resource jwtKeyPem;
    @Value("${vouchr.jwt.exp.seconds:900}")
    private Long expSeconds;
    @Value("${vouchr.jwt.sub.hash.key}")
    private String subHashKey;

    public String get(String sub) throws GeneralSecurityException {
        try {
            if (StringUtils.isBlank(subHashKey)) {
                throw new IOException("Please read and complete `Required Setup . 3` section from README.md");
            }

            // this hashes the sub so the internal customer id will not get transmitted to vouchr, while maintaining
            // a constant id for the user from one login to the next.
            String hashedSub = Util.base64Hmac256(sub, subHashKey);

            RSAPrivateKey rsaKey = getRSAKey();

            JWSSigner signer = new RSASSASigner(rsaKey);

            // Prepare JWT with claims set
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(hashedSub)
                    .expirationTime(new Date(new Date().getTime() + expSeconds * 1000L))
                    .build();

            SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.PS256).build(), claimsSet);

            // Compute the RSA signature
            signedJWT.sign(signer);

            // serialize to a compact form
            return signedJWT.serialize();

        } catch (IOException | JOSEException ex) {
            ex.printStackTrace();
            throw new GeneralSecurityException("Unable to create jwt", ex);
        }
    }

    // please note, it is up to the partner to store and protect this key in a way that corresponds to their
    // security policies.
    private RSAPrivateKey getRSAKey() throws JOSEException, IOException {
        if (!LOAD_PRIVATE_KEY) {
            throw new IOException("Please read and complete `Required Setup . 1 & 2` section from README.md");
        }
        try (InputStreamReader inputStreamReader = new InputStreamReader(jwtKeyPem.getInputStream(), UTF_8)) {
            String pemContents = FileCopyUtils.copyToString(inputStreamReader);

            return JWK.parseFromPEMEncodedObjects(pemContents).toRSAKey().toRSAPrivateKey();
        }
    }

}
