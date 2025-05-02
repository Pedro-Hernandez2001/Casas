package casas;

import com.jogamp.opengl.GL2;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import com.jogamp.opengl.util.GLBuffers;

public class ColisionManager {
    
    private static final float ALTURA_PERSONAJE = 3.6f; // Altura aproximada del personaje
    private static final float RADIO_PERSONAJE = 0.5f;  // Radio de colisión del personaje
    
    // Representación simplificada de las paredes y objetos de la casa
    private List<Obstaculo> obstaculos = new ArrayList<>();
    
    // Variable para activar/desactivar colisiones
    private boolean colisionesActivas = true;
    
    // Constructor
    public ColisionManager() {
        // Inicialmente definimos algunos obstáculos básicos basados en la imagen
        inicializarObstaculos();
    }
    
    // Método para inicializar los obstáculos básicos (paredes exteriores)
    private void inicializarObstaculos() {
        // Estos valores son aproximados basados en la imagen, se pueden ajustar
        
        // Paredes exteriores (definidas como cajas alineadas con los ejes)
        // Formato: new Obstaculo(minX, minY, minZ, maxX, maxY, maxZ)
        
        // Pared frontal
        obstaculos.add(new Obstaculo(-15, 0, -10, 15, 10, -9.5f));
        
        // Pared trasera
        obstaculos.add(new Obstaculo(-15, 0, 10, 15, 10, 10.5f));
        
        // Pared izquierda
        obstaculos.add(new Obstaculo(-15, 0, -10, -14.5f, 10, 10));
        
        // Pared derecha
        obstaculos.add(new Obstaculo(15, 0, -10, 15.5f, 10, 10));
        
        // Paredes interiores (aproximadas)
        obstaculos.add(new Obstaculo(-5, 0, -5, 5, 10, -4.5f));
        obstaculos.add(new Obstaculo(0, 0, 0, 0.5f, 10, 10));
        
        // Objetos (aproximados)
        obstaculos.add(new Obstaculo(-10, 0, -5, -8, 2, -3));  // Algún mueble
        obstaculos.add(new Obstaculo(8, 0, 5, 10, 2, 8));      // Otro mueble
    }
    
    // Método para alternar el estado de colisiones (activado/desactivado)
    public void alternarColisiones() {
        colisionesActivas = !colisionesActivas;
        System.out.println("Sistema de colisiones: " + (colisionesActivas ? "ACTIVADO" : "DESACTIVADO"));
    }
    
    // Método para detectar colisiones basadas en el muestreo de píxeles
    public boolean detectarColision(GL2 gl, float x, float y, float z) {
        // Si las colisiones están desactivadas, siempre permitir el movimiento
        if (!colisionesActivas) {
            return false;
        }
        
        // Verificar colisiones con obstáculos predefinidos
        for (Obstaculo obs : obstaculos) {
            if (obs.colisionaCon(x, y, z, RADIO_PERSONAJE)) {
                System.out.println("¡COLISIÓN detectada en [" + x + ", " + y + ", " + z + "] con obstáculo!");
                return true;
            }
        }
        
        // Usar muestreo de píxeles para detectar colisiones en tiempo real
        return detectarColisionPixel(gl, x, z);
    }
    
    // Método para detectar colisiones basado en el color del píxel
    private boolean detectarColisionPixel(GL2 gl, float x, float z) {
        // Desactivar temporalmente la detección por píxel
        return false;
        
        /* Código original comentado:
        // Esta es una implementación simplificada que lee el color del píxel
        // en la posición donde está el personaje
        
        IntBuffer viewport = GLBuffers.newDirectIntBuffer(4);
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport);
        
        // Convertir coordenadas 3D a coordenadas de viewport
        int screenX = (int)((x + 15) / 30 * viewport.get(2));  // Adaptado al tamaño de la escena
        int screenZ = (int)((z + 10) / 20 * viewport.get(3));  // Adaptado al tamaño de la escena
        
        // Leer el píxel
        ByteBuffer pixel = GLBuffers.newDirectByteBuffer(4);
        gl.glReadPixels(screenX, screenZ, 1, 1, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, pixel);
        
        int r = pixel.get(0) & 0xFF;
        int g = pixel.get(1) & 0xFF;
        int b = pixel.get(2) & 0xFF;
        
        // Si el color es similar al del suelo (marrón claro en la imagen), no hay colisión
        // Estos valores dependen del color real del suelo en tu modelo
        boolean esSuelo = (r > 150 && g > 100 && b < 100);  // Color marrón del suelo
        
        return !esSuelo;  // Si no es suelo, hay colisión
        */
    }
    
    // Método para actualizar los obstáculos basados en los vértices del modelo
    public void actualizarObstaculosDesdeModelo(List<float[]> vertices, List<int[]> faces) {
        // Este método analizaría los vértices y caras del modelo para actualizar
        // automáticamente los obstáculos basados en las paredes reales
        // Es una implementación avanzada que requeriría más información sobre el modelo
        
        // Simplemente dejamos los obstáculos predefinidos por ahora
    }
    
    // Clase interna para representar un obstáculo (como una caja de colisión)
    private class Obstaculo {
        float minX, minY, minZ;
        float maxX, maxY, maxZ;
        
        public Obstaculo(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
        }
        
        // Verifica si hay colisión entre el obstáculo y una esfera (personaje)
        public boolean colisionaCon(float x, float y, float z, float radio) {
            // Encontrar el punto más cercano dentro del cubo al centro de la esfera
            float closestX = Math.max(minX, Math.min(x, maxX));
            float closestY = Math.max(minY, Math.min(y, maxY));
            float closestZ = Math.max(minZ, Math.min(z, maxZ));
            
            // Calcular la distancia entre el punto más cercano y el centro de la esfera
            float distanceX = x - closestX;
            float distanceY = y - closestY;
            float distanceZ = z - closestZ;
            
            float distanceSquared = (distanceX * distanceX) + 
                                    (distanceY * distanceY) + 
                                    (distanceZ * distanceZ);
            
            // Si la distancia es menor que el radio, hay colisión
            return distanceSquared < (radio * radio);
        }
    }
    
    // Añadir este método a la clase ColisionManager
    public List<float[]> getObstaculosBoundingBoxes() {
        List<float[]> boundingBoxes = new ArrayList<>();
        
        for (Obstaculo obs : obstaculos) {
            boundingBoxes.add(new float[]{
                obs.minX, obs.minY, obs.minZ,
                obs.maxX, obs.maxY, obs.maxZ
            });
        }
        
        return boundingBoxes;
    }
    
    // Getter para el estado de colisiones
    public boolean isColisionesActivas() {
        return colisionesActivas;
    }
}