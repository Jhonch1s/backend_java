package com.example.gymtrackerweb.dto;
import java.util.List;
import java.util.Map;

public class GuardarRutinaPayloadDTO {
    private int rutinaId;
    private Map<String, List<EjercicioGuardadoDTO>> dias;

    // Inner class para la lista de ejercicios
    public static class EjercicioGuardadoDTO {
        private int ejercicioId;
        private int series;
        private int repeticiones;

        public int getEjercicioId() {
            return ejercicioId;
        }

        public void setEjercicioId(int ejercicioId) {
            this.ejercicioId = ejercicioId;
        }

        public int getSeries() {
            return series;
        }

        public void setSeries(int series) {
            this.series = series;
        }

        public int getRepeticiones() {
            return repeticiones;
        }

        public void setRepeticiones(int repeticiones) {
            this.repeticiones = repeticiones;
        }
    }

    public int getRutinaId() {
        return rutinaId;
    }

    public void setRutinaId(int rutinaId) {
        this.rutinaId = rutinaId;
    }

    public Map<String, List<EjercicioGuardadoDTO>> getDias() {
        return dias;
    }

    public void setDias(Map<String, List<EjercicioGuardadoDTO>> dias) {
        this.dias = dias;
    }
}

