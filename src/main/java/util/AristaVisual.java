package util;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class AristaVisual {
    private final Line linea;
    private final int peso;
    private final StackPane origen;
    private final StackPane destino;

    public AristaVisual(Line linea, int peso, StackPane origen, StackPane destino) {
        this.linea = linea;
        this.peso = peso;
        this.origen = origen;
        this.destino = destino;
    }

    public Line getLinea() {
        return linea;
    }

    public int getPeso() {
        return peso;
    }

    public StackPane getOrigen() {
        return origen;
    }

    public StackPane getDestino() {
        return destino;
    }
}
