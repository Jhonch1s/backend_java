package com.example.gymtrackerweb.utils;

import java.time.LocalDate;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class FechaUtils {
    public static String formatearMesAnio(Date fecha){

        if (fecha==null) return "";

        LocalDate fechalocal;
        if (fecha instanceof java.sql.Date){
            fechalocal = ((java.sql.Date)fecha).toLocalDate();
        }else{
            fechalocal = Instant.ofEpochMilli(fecha.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        }


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es", "ES"));
        String textoFinal = fechalocal.format(formatter);
        return textoFinal.substring(0,1).toUpperCase() + textoFinal.substring(1);
    }
}