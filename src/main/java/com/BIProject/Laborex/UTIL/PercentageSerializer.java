package com.BIProject.Laborex.UTIL;
import java.io.IOException;
import java.text.DecimalFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class PercentageSerializer extends JsonSerializer<Double> {
    private static final DecimalFormat df = new DecimalFormat("0.00");

    @Override
    public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            // Ecrire le double arrondi à 2 décimales directement
            double rounded = Math.round(value * 100.0) / 100.0;
            gen.writeNumber(rounded); // Jackson s'occupe du format JSON
        }
    }
}

