package casas;

import com.jogamp.opengl.GL2;

public class Luz {
    // Propiedades de la luz
    private float[] posicion = {10.0f, 10.0f, 10.0f, 1.0f}; // Posición por defecto
    private float[] colorAmbiental = {0.3f, 0.3f, 0.3f, 1.0f}; // Luz ambiental modificada
    private float[] colorDifuso = {0.95f, 0.95f, 0.95f, 1.0f}; // Luz difusa menos intensa
    private float[] colorEspecular = {1.0f, 1.0f, 1.0f, 1.0f}; // Luz especular blanca
    
    // Propiedades globales de iluminación
    private float[] luzAmbientalGlobal = {0.4f, 0.4f, 0.4f, 1.0f}; // Luz ambiental global aumentada
    
    // Estado de la luz
    private boolean activa = true;
    
    // Constructor
    public Luz() {
        // Inicialización con valores predeterminados
    }
    
    // Constructor con posición personalizada
    public Luz(float[] posicion) {
        if (posicion != null && posicion.length >= 3) {
            this.posicion[0] = posicion[0];
            this.posicion[1] = posicion[1];
            this.posicion[2] = posicion[2];
        }
    }
    
    // Método para activar la iluminación en el contexto OpenGL
    public void activar(GL2 gl) {
        if (!activa) return;
        
        // Configurar luz ambiental global
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, luzAmbientalGlobal, 0);
        
        // Habilitar iluminación de dos caras para ver mejor las texturas
        gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_TRUE);
        
        // Habilitar iluminación
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        
        // Configurar propiedades de la luz
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, posicion, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, colorAmbiental, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, colorDifuso, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, colorEspecular, 0);
        
        // Configuración adicional para mejorar el aspecto
        float[] atenuacion = {1.0f, 0.0f, 0.0f}; // Sin atenuación
        gl.glLightf(GL2.GL_LIGHT0, GL2.GL_CONSTANT_ATTENUATION, atenuacion[0]);
        gl.glLightf(GL2.GL_LIGHT0, GL2.GL_LINEAR_ATTENUATION, atenuacion[1]);
        gl.glLightf(GL2.GL_LIGHT0, GL2.GL_QUADRATIC_ATTENUATION, atenuacion[2]);
    }
    
    // Método para desactivar la iluminación
    public void desactivar(GL2 gl) {
        gl.glDisable(GL2.GL_LIGHT0);
        gl.glDisable(GL2.GL_LIGHTING);
    }
    
    // Método para configurar que se respeten los colores del material
    public void configurarMaterialPorDefecto(GL2 gl) {
        // Configurar para que las llamadas a glColor afecten al material difuso y ambiental
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        gl.glColorMaterial(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE);
        
        // Configurar color especular y brillo por defecto
        float[] especular = {0.3f, 0.3f, 0.3f, 1.0f}; // Aumentado
        float brillo = 60.0f; // Aumentado
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, especular, 0);
        gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, brillo);
    }
    
    // Método para alternar el estado de la luz
    public void alternarEstado() {
        activa = !activa;
    }
    
    // Método para mover la posición de la luz
    public void moverLuz(float deltaX, float deltaY, float deltaZ) {
        posicion[0] += deltaX;
        posicion[1] += deltaY;
        posicion[2] += deltaZ;
    }
    
    // Getters y setters
    public boolean isActiva() {
        return activa;
    }
    
    public void setActiva(boolean activa) {
        this.activa = activa;
    }
    
    public float[] getPosicion() {
        return posicion;
    }
    
    public void setPosicion(float[] posicion) {
        this.posicion = posicion;
    }
    
    public void setColorDifuso(float r, float g, float b) {
        colorDifuso[0] = r;
        colorDifuso[1] = g;
        colorDifuso[2] = b;
    }
}