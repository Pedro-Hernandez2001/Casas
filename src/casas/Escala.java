package casas;

import java.util.List;

public class Escala {
    // Variables para el control de escala
    private float escalaActual = 1.0f;
    private float[] centroModelo = new float[3]; // Centro del modelo [x, y, z]
    private float radioModelo = 0.0f; // Radio del modelo (para ajustar la vista)
    
    public Escala() {
        // Constructor por defecto
    }
    
    // Método para calcular la escala óptima basada en los vértices del modelo
    public void calcularEscalaOptima(List<float[]> vertices) {
        if (vertices == null || vertices.isEmpty()) {
            return;
        }
        
        // Encontrar los límites del modelo (min/max en cada eje)
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float minZ = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;
        float maxZ = -Float.MAX_VALUE;
        
        for (float[] vertex : vertices) {
            if (vertex[0] < minX) minX = vertex[0];
            if (vertex[0] > maxX) maxX = vertex[0];
            if (vertex[1] < minY) minY = vertex[1];
            if (vertex[1] > maxY) maxY = vertex[1];
            if (vertex[2] < minZ) minZ = vertex[2];
            if (vertex[2] > maxZ) maxZ = vertex[2];
        }
        
        // Calcular el centro del modelo
        centroModelo[0] = (minX + maxX) / 2.0f;
        centroModelo[1] = (minY + maxY) / 2.0f;
        centroModelo[2] = (minZ + maxZ) / 2.0f;
        
        // Calcular las dimensiones del modelo
        float ancho = maxX - minX;
        float alto = maxY - minY;
        float profundidad = maxZ - minZ;
        
        // Calcular el radio del modelo (la mitad de la dimensión más grande)
        radioModelo = Math.max(Math.max(ancho, alto), profundidad) / 2.0f;
        
        // Calcular la escala basada en una distancia de cámara estándar
        // Aumentamos el factor para que el modelo ocupe más del viewport (90% en vez de 80%)
        float distanciaEstandar = 30.0f; // Distancia estándar de la cámara
        float anguloVision = 45.0f; // Ángulo de visión en grados
        
        // Cálculo de la escala óptima con un factor mayor
        float tamanoVisible = 2.0f * distanciaEstandar * (float)Math.tan(Math.toRadians(anguloVision / 2.0f));
        escalaActual = (0.9f * tamanoVisible) / (2.0f * radioModelo);
        
        // Si el modelo es muy pequeño, ajustar la escala para que se vea bien
        if (escalaActual > 10.0f) {
            escalaActual = 10.0f;
        }
        
        System.out.println("Escala óptima calculada: " + escalaActual);
        System.out.println("Centro del modelo: [" + centroModelo[0] + ", " + 
                           centroModelo[1] + ", " + centroModelo[2] + "]");
        System.out.println("Radio del modelo: " + radioModelo);
    }
    
    // Método para aplicar la escala en la visualización
    public float getEscalaActual() {
        return escalaActual;
    }
    
    // Método para ajustar la distancia de la cámara basada en el modelo
    public float getDistanciaOptimaCamara() {
        // Calcular la distancia óptima para ver todo el modelo
        // Asumimos un ángulo de visión de 45 grados y reducimos ligeramente
        // la distancia para acercar más el modelo
        return (radioModelo / (float)Math.tan(Math.toRadians(22.5f))) / escalaActual * 0.9f;
    }
    
    // Getters para el centro y radio del modelo
    public float[] getCentroModelo() {
        return centroModelo;
    }
    
    public float getRadioModelo() {
        return radioModelo;
    }
    
    // Método para ajustar la escala manualmente
    public void ajustarEscala(float factor) {
        escalaActual *= factor;
        
        // Limitar la escala para evitar valores extremos
        if (escalaActual < 0.1f) {
            escalaActual = 0.1f;
        }
        if (escalaActual > 10.0f) {
            escalaActual = 10.0f;
        }
    }
}