package de.elomagic.spps.shared;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SimpleCryptProviderMock extends AbstractSimpleCryptProvider {

    @Override
    @Nonnull
    public byte[] createPrivateKey() throws SimpleCryptException {
        return Base64.getEncoder().encode("ThisIsAPrivateKeyMock".getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public @Nullable String encrypt(byte[] decrypted) throws SimpleCryptException {
        return "{" + Base64.getEncoder().encodeToString(decrypted) + "}";
    }

    @Override
    public @Nullable byte[] decrypt(@Nullable String encryptedBase64) throws SimpleCryptException {
        if (encryptedBase64 == null) {
            return null;
        }

        return Base64.getDecoder().decode(encryptedBase64.substring(1, encryptedBase64.length()-1));
    }

    public byte[] callReadPrivateKey() {
        return readPrivateKey();
    }

}
