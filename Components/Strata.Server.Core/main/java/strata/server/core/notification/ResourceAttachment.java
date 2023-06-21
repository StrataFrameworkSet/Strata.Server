//////////////////////////////////////////////////////////////////////////////
// ResourceAttachment.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.notification;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public
class ResourceAttachment
    extends AbstractAttachment
{
    public ResourceAttachment(String contentId,String contentType,String fileName)
    {
        super(contentId,contentType,fileName);
    }

    @Override
    public byte[]
    getBytes()
    {
        InputStream input =
            ClassLoader.getSystemResourceAsStream(getFileName());

        try
        {
            if (getContentType().startsWith("image/"))
                return getImageBytes(input);

            return input.readAllBytes();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            try
            {
                input.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }

        }
    }

    protected byte[]
    getImageBytes(InputStream input)
        throws IOException
    {
        BufferedImage         buffer = ImageIO.read(input);
        ByteArrayOutputStream temp = new ByteArrayOutputStream(10000);
        String                encoded = null;

        try
        {
            ImageIO.write(buffer,getImageFormat(),temp);
            temp.flush();

            return Base64.getMimeEncoder().encode(temp.toByteArray());
        }
        finally
        {
            temp.close();
        }

    }

    protected String
    getImageFormat()
    {
        switch (getContentType())
        {
            case "image/jpg":
            case "image/jpeg":
                return "jpg";
            case "image/png":
                return "png";
            case "image/gif":
                return "gif";

            default:
                throw
                    new IllegalArgumentException(getContentType());
        }
    }
}

//////////////////////////////////////////////////////////////////////////////
