package com.example.gymtrackerweb.dto;

import java.util.Objects;

public class EjercicioMin {
    public int id;
    public String nombre;

    public EjercicioMin(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EjercicioMin that = (EjercicioMin) o;
        return id == that.id && Objects.equals(nombre, that.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre);
    }

    @Override
    public String toString() {
        return "EjercicioMin{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}


