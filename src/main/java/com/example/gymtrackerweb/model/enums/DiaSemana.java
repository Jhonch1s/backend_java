package com.example.gymtrackerweb.model.enums;

public enum DiaSemana {
    LUNES,
    MARTES,
    MIERCOLES,
    JUEVES,
    VIERNES,
    SABADO,
    DOMINGO;

    public static DiaSemana fromString(String dia) {
        if (dia == null) {
            return null;
        }
        try {
            return DiaSemana.valueOf(dia.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Valor de día inválido encontrado: " + dia);
            return null;
        }
    }
}
