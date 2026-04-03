//////////////////////////////////////////////////////////////////////////////
// SecureAttributeConverter.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.value;

import jakarta.persistence.AttributeConverter;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;

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

        encrypter.setPassword(getPropertiesEncryptionKey());
        encrypter.setAlgorithm("PBEWithHMACSHA512AndAES_256");
        encrypter.setIvGenerator(new RandomIvGenerator());

        return encrypter;
    }

    protected String
    getPropertiesEncryptionKey()
    {
        return System.getenv("PROPERTIES_ENCRYPTION_KEY");
    }

}

//////////////////////////////////////////////////////////////////////////////
