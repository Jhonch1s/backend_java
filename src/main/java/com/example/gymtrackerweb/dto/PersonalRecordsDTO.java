package com.example.gymtrackerweb.dto;

// Usamos ProgresoEjercicio como una forma simple de guardar un par (peso, reps)
import com.example.gymtrackerweb.model.ProgresoEjercicio;

public class PersonalRecordsDTO {

    // Bloque "Mejores RMs" (reps = 1)
    private ProgresoEjercicio bestActual1RM; // ej: 200kg x 1 rep

    // Bloque "Récords (Sets)" (reps > 1)
    private ProgresoEjercicio bestSetPeso;   // ej: 180kg x 5 reps
    private ProgresoEjercicio bestSetVolumen; // ej: 150kg x 8 reps
    private int maxVolumenCalculado; // ej: 1200

    // Getters y Setters
    public ProgresoEjercicio getBestActual1RM() { return bestActual1RM; }
    public void setBestActual1RM(ProgresoEjercicio bestActual1RM) { this.bestActual1RM = bestActual1RM; }

    public ProgresoEjercicio getBestSetPeso() { return bestSetPeso; }
    public void setBestSetPeso(ProgresoEjercicio bestSetPeso) { this.bestSetPeso = bestSetPeso; }

    public ProgresoEjercicio getBestSetVolumen() { return bestSetVolumen; }
    public void setBestSetVolumen(ProgresoEjercicio bestSetVolumen) { this.bestSetVolumen = bestSetVolumen; }

    public int getMaxVolumenCalculado() { return maxVolumenCalculado; }
    public void setMaxVolumenCalculado(int maxVolumenCalculado) { this.maxVolumenCalculado = maxVolumenCalculado; }
}