//////////////////////////////////////////////////////////////////////////////
// SecureAttributeConverter.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.value;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.ByteArrayFixedIvGenerator;
import org.jasypt.salt.ByteArrayFixedSaltGenerator;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Converter
public abstract
class SecureAttributeConverter<E>
    implements AttributeConverter<E,String>
{
    private final StringEncryptor encrypter;

    protected
    SecureAttributeConverter()
    {
        encrypter = createEncrypter();
    }

    protected String
    encrypt(String value)
    {
        return encrypter.encrypt(value);
    }

    protected String
    decrypt(String value)
    {
        return encrypter.decrypt(value);
    }

    protected StandardPBEStringEncryptor
    createEncrypter()
    {
        StandardPBEStringEncryptor encrypter = new StandardPBEStringEncryptor();
        String key = getPropertiesEncryptionKey();

        encrypter.setPassword(key);
        encrypter.setAlgorithm("PBEWithHMACSHA512AndAES_256");
        encrypter.setIvGenerator(new ByteArrayFixedIvGenerator(deriveBytes(key, "IV")));
        encrypter.setSaltGenerator(new ByteArrayFixedSaltGenerator(deriveBytes(key, "SALT")));

        return encrypter;
    }

    private byte[]
    deriveBytes(String key, String purpose)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            digest.update(key.getBytes(StandardCharsets.UTF_8));
            digest.update(purpose.getBytes(StandardCharsets.UTF_8));

            return Arrays.copyOf(digest.digest(), 16);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new IllegalStateException(e);
        }
    }

    protected String
    getPropertiesEncryptionKey()
    {
        return System.getenv("PROPERTIES_ENCRYPTION_KEY");
    }

}

//////////////////////////////////////////////////////////////////////////////
