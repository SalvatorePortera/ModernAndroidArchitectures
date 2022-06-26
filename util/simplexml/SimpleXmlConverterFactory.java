package com.nereus.craftbeer.util.simplexml;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Nullable;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

import static com.nereus.craftbeer.constant.PointPlusConstants.SHIFT_JIS_CHARSET;
import static com.nereus.craftbeer.constant.PointPlusConstants.XML_SHIFT_JIS_MEDIA_TYPE;

/**
 * A {@linkplain Converter.Factory converter} which uses Simple Framework for XML.
 * <p>
 * This converter only applies for class types. Parameterized types (e.g., {@code List<Foo>}) are
 * not handled.
 */
public final class SimpleXmlConverterFactory extends Converter.Factory {
    /**
     * Create an instance using a default {@link Persister} instance for conversion.
     */
    public static SimpleXmlConverterFactory create() {
        return create(new Persister());
    }

    /**
     * Create an instance using {@code serializer} for conversion.
     */
    public static SimpleXmlConverterFactory create(Serializer serializer) {
        return new SimpleXmlConverterFactory(serializer, true, MediaType.get(XML_SHIFT_JIS_MEDIA_TYPE), SHIFT_JIS_CHARSET);
    }

    /**
     * Create an instance using a default {@link Persister} instance for non-strict conversion.
     */
    public static SimpleXmlConverterFactory createNonStrict() {
        return createNonStrict(new Persister(), MediaType.get(XML_SHIFT_JIS_MEDIA_TYPE), SHIFT_JIS_CHARSET);
    }

    /**
     * Create an instance using {@code serializer} for non-strict conversion.
     */
    @SuppressWarnings("ConstantConditions") // Guarding public API nullability.
    public static SimpleXmlConverterFactory createNonStrict(Serializer serializer, MediaType mediaType, String charset) {
        if (serializer == null) throw new NullPointerException("serializer == null");
        return new SimpleXmlConverterFactory(serializer, false, mediaType, charset);
    }

    private final Serializer serializer;
    private final MediaType mediaType;
    private final String charset;
    private SimpleXmlRequestBodyConverter requestBodyConverter = null;
    private SimpleXmlResponseBodyConverter responseBodyConverter = null;

    private final boolean strict;

    private SimpleXmlConverterFactory(Serializer serializer, boolean strict, MediaType mediaType, String charset) {
        this.serializer = serializer;
        this.strict = strict;
        this.mediaType = mediaType;
        this.charset = charset;
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public String getCharset() {
        return charset;
    }

    public boolean isStrict() {
        return strict;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        if (!(type instanceof Class)) {
            return null;
        }
        Class<?> cls = (Class<?>) type;
        responseBodyConverter = new SimpleXmlResponseBodyConverter<>(cls, serializer, strict);
        return responseBodyConverter;
    }

    @Override
    public @Nullable
    Converter<?, RequestBody> requestBodyConverter(Type type,
                                                   Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        if (!(type instanceof Class)) {
            return null;
        }
        requestBodyConverter= new SimpleXmlRequestBodyConverter<>(serializer, mediaType, charset);
        return requestBodyConverter;
    }
}
