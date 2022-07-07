package com.volmit.iris.engine.dimension;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.volmit.iris.engine.resolver.EngineResolvable;
import com.volmit.iris.engine.resolver.Resolvable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.IOException;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Accessors(fluent = true, chain = true)
@Resolvable.Entity(id = "range", jsonTypes = {JsonToken.NUMBER, JsonToken.BEGIN_OBJECT})
public class IrisRange extends EngineResolvable implements TypeAdapterFactory {
    @Builder.Default
    @TokenConstructor(JsonToken.NUMBER)
    private double max = 1;

    @Builder.Default
    private double min = 1;

    @Builder.Default
    private IrisGenerator generator = IrisGenerator.NATURAL;

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);

        if(!type.getRawType().equals(getClass())) {
            return null;
        }

        return new TypeAdapter<>() {
            public void write(JsonWriter out, T value) {writeSafeJson(delegate, out, value);}

            @SuppressWarnings("unchecked")
            public T read(JsonReader in) throws IOException {
                JsonToken token = in.peek();

                if(token == JsonToken.NUMBER) {
                    double d = in.nextDouble();
                    return (T) IrisRange.builder().min(d).max(d).generator(IrisGenerator.FLAT).build();
                }

                return delegate.read(in);
            }
        };
    }

    public static IrisRange flat(double v) {
        return IrisRange.builder()
            .max(v)
            .min(v)
            .generator(IrisGenerator.FLAT)
            .build();
    }
}