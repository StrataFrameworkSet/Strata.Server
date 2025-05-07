//////////////////////////////////////////////////////////////////////////////
// SerializableAttachment.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.core.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public
class SerializableAttachment
    implements Serializable, IAttachment
{
    private String contentId;
    private String contentType;
    private byte[] bytes;

    public
    SerializableAttachment()
    {
        contentId = null;
        contentType = null;
        bytes = null;
    }

    public SerializableAttachment
    setContentId(String contentId)
    {
        this.contentId = contentId;
        return this;
    }

    public SerializableAttachment
    setContentType(String contentType)
    {
        this.contentType = contentType;
        return this;
    }

    public SerializableAttachment
    setBytes(byte[] bytes)
    {
        this.bytes = bytes;
        return this;
    }

    @Override
    public String
    getContentId()
    {
        return contentId;
    }

    @Override
    public String
    getContentType()
    {
        return contentType;
    }

    @JsonIgnore
    @Override
    public String
    getFileName()
    {
        return "";
    }

    @Override
    public byte[]
    getBytes()
    {
        return bytes;
    }

    @JsonIgnore
    @Override
    public boolean
    isImage()
    {
        return contentType.startsWith("image/");
    }

    public static SerializableAttachment
    of(IAttachment attachment)
    {
        return
            new SerializableAttachment()
                .setContentId(attachment.getContentId())
                .setContentType(attachment.getContentType())
                .setBytes(attachment.getBytes());
    }
}

//////////////////////////////////////////////////////////////////////////////
