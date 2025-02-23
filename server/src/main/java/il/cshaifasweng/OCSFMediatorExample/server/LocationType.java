package il.cshaifasweng.OCSFMediatorExample.server;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

public enum LocationType {
    INDOOR,
    OUTDOOR;

    public static String toStringValue(LocationType locationType) {
        return (locationType == null) ? null : locationType.name();
    }
}
