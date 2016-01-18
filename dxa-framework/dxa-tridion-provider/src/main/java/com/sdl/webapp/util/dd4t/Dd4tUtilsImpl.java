package com.sdl.webapp.util.dd4t;

import com.google.common.base.Function;
import com.sdl.webapp.common.util.Dd4tUtils;
import com.sdl.webapp.common.util.NestedCustomMap;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.impl.EmbeddedField;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Dd4tUtilsImpl implements Dd4tUtils {
    @Override
    public Object getFromNestedMultiLevelMapOrAlternative(Map<String, Object> multiLevelMap, String key, Object alternative) {
        final Object obj = new NestedCustomMap(multiLevelMap, new Function<Map.Entry<String, Map<String, Object>>, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(Map.Entry<String, Map<String, Object>> pair) {
                Map<String, Object> map = pair.getValue();

                Object o = map.get(pair.getKey());
                Map content;

                if (o instanceof FieldSet) {
                    content = ((FieldSet) o).getContent();
                } else if (o instanceof EmbeddedField) {
                    content = ((EmbeddedField) o).getEmbeddedValues().get(0).getContent();
                } else {
                    throw new UnsupportedOperationException("Unsupported format of ECL metadata structure");
                }
                //noinspection unchecked
                return content;
            }
        }).get(key);

        if (obj == null) {
            return alternative;
        }

        if (obj instanceof Field) {
            return FieldUtils.getStringValue((Field) obj);
        }

        return alternative;
    }
}
