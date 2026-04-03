//////////////////////////////////////////////////////////////////////////////
// AbstractAttachment.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.notification;

public abstract
class AbstractAttachment
    implements IAttachment
{
    private final String itsContentId;
    private final String itsContentType;
    private final String itsFileName;

    protected
    AbstractAttachment(
        String contentId,
        String contentType,
        String fileName)
    {
        itsContentId = contentId;
        itsContentType = contentType;
        itsFileName = fileName;
    }

    @Override
    public String
    getContentId()
    {
        return itsContentId;
    }

    @Override
    public String
    getContentType()
    {
        return itsContentType;
    }

    @Override
    public String
    getFileName()
    {
        return itsFileName;
    }

    @Override
    public boolean
    isImage()
    {
        return itsContentType.startsWith("image/");
    }
}

//////////////////////////////////////////////////////////////////////////////
