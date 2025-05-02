package casas;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;

import java.awt.event.KeyEvent;

public class Personaje {

    private GLU glu = new GLU();
    
    // Variables del personaje
    private float personajeX = 0.0f;
    private float personajeY = 0.0f;
    private float personajeZ = 0.0f;
    private float personajeRotacion = 0.0f;
    private float velocidadMovimiento = 0.5f;
    private float velocidadRotacion = 5.0f;
    private boolean mostrarPersonaje = true;
    
    // Añadir referencia al administrador de colisiones
    private ColisionManager colisionManager;
    
    // Constructor
    public Personaje() {
        // Inicialización con valores predeterminados
        colisionManager = new ColisionManager();
        
        // Posicionar al personaje dentro de la casa en una ubicación inicial válida
        personajeX = 0.0f;
        personajeY = 0.0f;
        personajeZ = 5.0f;  // Posición inicial dentro de la casa
    }
    
    // Método para establecer el administrador de colisiones
    public void setColisionManager(ColisionManager colisionManager) {
        this.colisionManager = colisionManager;
    }
    
    // Método para dibujar el personaje
    public void dibujar(GL2 gl) {
        if (!mostrarPersonaje) return;
        
        gl.glPushMatrix();
        
        // Posicionar el personaje
        gl.glTranslatef(personajeX, personajeY, personajeZ);
        gl.glRotatef(personajeRotacion, 0, 1, 0);
        
        // 1. Cuerpo (prisma rectangular)
        gl.glPushMatrix();
        gl.glColor3f(0.0f, 0.4f, 0.8f); // Azul para el cuerpo
        gl.glTranslatef(0.0f, 2.0f, 0.0f);
        gl.glScalef(1.0f, 2.0f, 0.5f);
        dibujarCubo(gl);
        gl.glPopMatrix();
        
        // 2. Cabeza (esfera)
        gl.glPushMatrix();
        gl.glColor3f(0.9f, 0.7f, 0.6f); // Color piel para la cabeza
        gl.glTranslatef(0.0f, 4.2f, 0.0f);
        dibujarEsfera(gl, 0.7f, 20, 20);
        gl.glPopMatrix();
        
        // 3. Brazo derecho (cilindro)
        gl.glPushMatrix();
        gl.glColor3f(0.7f, 0.0f, 0.0f); // Rojo para el brazo derecho
        gl.glTranslatef(0.8f, 2.5f, 0.0f);
        gl.glRotatef(90, 0, 0, 1);
        dibujarCilindro(gl, 0.2f, 1.5f, 10);
        gl.glPopMatrix();
        
        // 4. Brazo izquierdo (cono)
        gl.glPushMatrix();
        gl.glColor3f(0.7f, 0.5f, 0.0f); // Marrón para el brazo izquierdo
        gl.glTranslatef(-0.8f, 2.5f, 0.0f);
        gl.glRotatef(-90, 0, 0, 1);
        dibujarCono(gl, 0.4f, 1.5f, 10);
        gl.glPopMatrix();
        
        // 5. Pierna derecha (cilindro)
        gl.glPushMatrix();
        gl.glColor3f(0.0f, 0.3f, 0.6f); // Azul oscuro para la pierna derecha
        gl.glTranslatef(0.4f, 0.9f, 0.0f);
        dibujarCilindro(gl, 0.25f, 1.8f, 10);
        gl.glPopMatrix();
        
        // 6. Pierna izquierda (prisma triangular)
        gl.glPushMatrix();
        gl.glColor3f(0.0f, 0.3f, 0.6f); // Azul oscuro para la pierna izquierda
        gl.glTranslatef(-0.4f, 0.9f, 0.0f);
        dibujarPrismaTriangular(gl, 0.3f, 1.8f);
        gl.glPopMatrix();
        
        // 7. Sombrero (toroide)
        gl.glPushMatrix();
        gl.glColor3f(0.3f, 0.3f, 0.3f); // Gris para el sombrero
        gl.glTranslatef(0.0f, 5.0f, 0.0f);
        dibujarToroide(gl, 0.2f, 0.8f, 10, 15);
        gl.glPopMatrix();
        
        gl.glPopMatrix();
    }
    
    // Métodos para dibujar las diferentes formas geométricas
    private void dibujarCubo(GL2 gl) {
        // Parte frontal
        gl.glBegin(GL2.GL_QUADS);
        gl.glNormal3f(0.0f, 0.0f, 1.0f);
        gl.glVertex3f(-0.5f, -0.5f, 0.5f);
        gl.glVertex3f(0.5f, -0.5f, 0.5f);
        gl.glVertex3f(0.5f, 0.5f, 0.5f);
        gl.glVertex3f(-0.5f, 0.5f, 0.5f);
        gl.glEnd();
        
        // Parte trasera
        gl.glBegin(GL2.GL_QUADS);
        gl.glNormal3f(0.0f, 0.0f, -1.0f);
        gl.glVertex3f(0.5f, -0.5f, -0.5f);
        gl.glVertex3f(-0.5f, -0.5f, -0.5f);
        gl.glVertex3f(-0.5f, 0.5f, -0.5f);
        gl.glVertex3f(0.5f, 0.5f, -0.5f);
        gl.glEnd();
        
        // Parte superior
        gl.glBegin(GL2.GL_QUADS);
        gl.glNormal3f(0.0f, 1.0f, 0.0f);
        gl.glVertex3f(-0.5f, 0.5f, 0.5f);
        gl.glVertex3f(0.5f, 0.5f, 0.5f);
        gl.glVertex3f(0.5f, 0.5f, -0.5f);
        gl.glVertex3f(-0.5f, 0.5f, -0.5f);
        gl.glEnd();
        
        // Parte inferior
        gl.glBegin(GL2.GL_QUADS);
        gl.glNormal3f(0.0f, -1.0f, 0.0f);
        gl.glVertex3f(-0.5f, -0.5f, -0.5f);
        gl.glVertex3f(0.5f, -0.5f, -0.5f);
        gl.glVertex3f(0.5f, -0.5f, 0.5f);
        gl.glVertex3f(-0.5f, -0.5f, 0.5f);
        gl.glEnd();
        
        // Lado derecho
        gl.glBegin(GL2.GL_QUADS);
        gl.glNormal3f(1.0f, 0.0f, 0.0f);
        gl.glVertex3f(0.5f, -0.5f, 0.5f);
        gl.glVertex3f(0.5f, -0.5f, -0.5f);
        gl.glVertex3f(0.5f, 0.5f, -0.5f);
        gl.glVertex3f(0.5f, 0.5f, 0.5f);
        gl.glEnd();
        
        // Lado izquierdo
        gl.glBegin(GL2.GL_QUADS);
        gl.glNormal3f(-1.0f, 0.0f, 0.0f);
        gl.glVertex3f(-0.5f, -0.5f, -0.5f);
        gl.glVertex3f(-0.5f, -0.5f, 0.5f);
        gl.glVertex3f(-0.5f, 0.5f, 0.5f);
        gl.glVertex3f(-0.5f, 0.5f, -0.5f);
        gl.glEnd();
    }
    
    private void dibujarCilindro(GL2 gl, float radio, float altura, int divisiones) {
        // Parte superior del cilindro
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glNormal3f(0.0f, 1.0f, 0.0f);
        gl.glVertex3f(0.0f, altura, 0.0f);
        for (int i = 0; i <= divisiones; i++) {
            float angulo = (float) (2.0 * Math.PI * i / divisiones);
            float x = (float) Math.cos(angulo) * radio;
            float z = (float) Math.sin(angulo) * radio;
            gl.glVertex3f(x, altura, z);
        }
        gl.glEnd();
        
        // Parte inferior del cilindro
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glNormal3f(0.0f, -1.0f, 0.0f);
        gl.glVertex3f(0.0f, 0.0f, 0.0f);
        for (int i = divisiones; i >= 0; i--) {
            float angulo = (float) (2.0 * Math.PI * i / divisiones);
            float x = (float) Math.cos(angulo) * radio;
            float z = (float) Math.sin(angulo) * radio;
            gl.glVertex3f(x, 0.0f, z);
        }
        gl.glEnd();
        
        // Cuerpo del cilindro
        gl.glBegin(GL2.GL_QUAD_STRIP);
        for (int i = 0; i <= divisiones; i++) {
            float angulo = (float) (2.0 * Math.PI * i / divisiones);
            float x = (float) Math.cos(angulo) * radio;
            float z = (float) Math.sin(angulo) * radio;
            
            float nx = x / radio;
            float nz = z / radio;
            
            gl.glNormal3f(nx, 0.0f, nz);
            gl.glVertex3f(x, 0.0f, z);
            gl.glVertex3f(x, altura, z);
        }
        gl.glEnd();
    }
    
    private void dibujarCono(GL2 gl, float radio, float altura, int divisiones) {
        // Base del cono
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glNormal3f(0.0f, -1.0f, 0.0f);
        gl.glVertex3f(0.0f, 0.0f, 0.0f);
        for (int i = divisiones; i >= 0; i--) {
            float angulo = (float) (2.0 * Math.PI * i / divisiones);
            float x = (float) Math.cos(angulo) * radio;
            float z = (float) Math.sin(angulo) * radio;
            gl.glVertex3f(x, 0.0f, z);
        }
        gl.glEnd();
        
        // Cuerpo del cono
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glVertex3f(0.0f, altura, 0.0f);
        for (int i = 0; i <= divisiones; i++) {
            float angulo = (float) (2.0 * Math.PI * i / divisiones);
            float x = (float) Math.cos(angulo) * radio;
            float z = (float) Math.sin(angulo) * radio;
            
            // Calcular la normal para esta cara
            float nx = x;
            float ny = radio;
            float nz = z;
            float longitud = (float) Math.sqrt(nx*nx + ny*ny + nz*nz);
            
            gl.glNormal3f(nx/longitud, ny/longitud, nz/longitud);
            gl.glVertex3f(x, 0.0f, z);
        }
        gl.glEnd();
    }
    
    private void dibujarEsfera(GL2 gl, float radio, int stacks, int slices) {
        for (int i = 0; i < stacks; i++) {
            float phi1 = (float) (Math.PI * i / stacks);
            float phi2 = (float) (Math.PI * (i + 1) / stacks);
            
            gl.glBegin(GL2.GL_QUAD_STRIP);
            for (int j = 0; j <= slices; j++) {
                float theta = (float) (2.0 * Math.PI * j / slices);
                
                float x1 = (float) (Math.sin(phi1) * Math.cos(theta));
                float y1 = (float) Math.cos(phi1);
                float z1 = (float) (Math.sin(phi1) * Math.sin(theta));
                
                float x2 = (float) (Math.sin(phi2) * Math.cos(theta));
                float y2 = (float) Math.cos(phi2);
                float z2 = (float) (Math.sin(phi2) * Math.sin(theta));
                
                gl.glNormal3f(x1, y1, z1);
                gl.glVertex3f(x1 * radio, y1 * radio, z1 * radio);
                
                gl.glNormal3f(x2, y2, z2);
                gl.glVertex3f(x2 * radio, y2 * radio, z2 * radio);
            }
            gl.glEnd();
        }
    }
    
    private void dibujarPrismaTriangular(GL2 gl, float lado, float altura) {
        float h = (float) (lado * Math.sqrt(3) / 2); // Altura del triángulo equilátero
        
        // Base inferior
        gl.glBegin(GL2.GL_TRIANGLES);
        gl.glNormal3f(0.0f, -1.0f, 0.0f);
        gl.glVertex3f(0.0f, 0.0f, h/3);
        gl.glVertex3f(-lado/2, 0.0f, -h/6);
        gl.glVertex3f(lado/2, 0.0f, -h/6);
        gl.glEnd();
        
        // Base superior
        gl.glBegin(GL2.GL_TRIANGLES);
        gl.glNormal3f(0.0f, 1.0f, 0.0f);
        gl.glVertex3f(0.0f, altura, h/3);
        gl.glVertex3f(lado/2, altura, -h/6);
        gl.glVertex3f(-lado/2, altura, -h/6);
        gl.glEnd();
        
        // Lado frontal
        gl.glBegin(GL2.GL_QUADS);
        gl.glNormal3f(0.0f, 0.0f, 1.0f);
        gl.glVertex3f(0.0f, 0.0f, h/3);
        gl.glVertex3f(0.0f, altura, h/3);
        gl.glVertex3f(-lado/2, altura, -h/6);
        gl.glVertex3f(-lado/2, 0.0f, -h/6);
        gl.glEnd();
        
        // Lado derecho
        gl.glBegin(GL2.GL_QUADS);
        gl.glNormal3f(0.5f, 0.0f, -0.866f);
        gl.glVertex3f(-lado/2, 0.0f, -h/6);
        gl.glVertex3f(-lado/2, altura, -h/6);
        gl.glVertex3f(lado/2, altura, -h/6);
        gl.glVertex3f(lado/2, 0.0f, -h/6);
        gl.glEnd();
        
        // Lado izquierdo
        gl.glBegin(GL2.GL_QUADS);
        gl.glNormal3f(0.5f, 0.0f, 0.866f);
        gl.glVertex3f(lado/2, 0.0f, -h/6);
        gl.glVertex3f(lado/2, altura, -h/6);
        gl.glVertex3f(0.0f, altura, h/3);
        gl.glVertex3f(0.0f, 0.0f, h/3);
        gl.glEnd();
    }
    
    private void dibujarToroide(GL2 gl, float radioInterior, float radioExterior, int lados, int anillos) {
        float radioMedio = (radioInterior + radioExterior) / 2;
        float radioTubo = (radioExterior - radioInterior) / 2;
        
        for (int i = 0; i < lados; i++) {
            float phi1 = (float) (2 * Math.PI * i / lados);
            float phi2 = (float) (2 * Math.PI * (i + 1) / lados);
            
            for (int j = 0; j < anillos; j++) {
                float theta1 = (float) (2 * Math.PI * j / anillos);
                float theta2 = (float) (2 * Math.PI * (j + 1) / anillos);
                
                gl.glBegin(GL2.GL_QUADS);
                
                // Vértice 1
                float x1 = (float) ((radioMedio + radioTubo * Math.cos(theta1)) * Math.cos(phi1));
                float y1 = (float) (radioTubo * Math.sin(theta1));
                float z1 = (float) ((radioMedio + radioTubo * Math.cos(theta1)) * Math.sin(phi1));
                
                // Vértice 2
                float x2 = (float) ((radioMedio + radioTubo * Math.cos(theta1)) * Math.cos(phi2));
                float y2 = (float) (radioTubo * Math.sin(theta1));
                float z2 = (float) ((radioMedio + radioTubo * Math.cos(theta1)) * Math.sin(phi2));
                
                // Vértice 3
                float x3 = (float) ((radioMedio + radioTubo * Math.cos(theta2)) * Math.cos(phi2));
                float y3 = (float) (radioTubo * Math.sin(theta2));
                float z3 = (float) ((radioMedio + radioTubo * Math.cos(theta2)) * Math.sin(phi2));
                
                // Vértice 4
                float x4 = (float) ((radioMedio + radioTubo * Math.cos(theta2)) * Math.cos(phi1));
                float y4 = (float) (radioTubo * Math.sin(theta2));
                float z4 = (float) ((radioMedio + radioTubo * Math.cos(theta2)) * Math.sin(phi1));
                
                // Calcular normales (apuntando desde el centro del tubo)
                float nx1 = (float) (Math.cos(theta1) * Math.cos(phi1));
                float ny1 = (float) Math.sin(theta1);
                float nz1 = (float) (Math.cos(theta1) * Math.sin(phi1));
                
                float nx2 = (float) (Math.cos(theta1) * Math.cos(phi2));
                float ny2 = (float) Math.sin(theta1);
                float nz2 = (float) (Math.cos(theta1) * Math.sin(phi2));
                
                float nx3 = (float) (Math.cos(theta2) * Math.cos(phi2));
                float ny3 = (float) Math.sin(theta2);
                float nz3 = (float) (Math.cos(theta2) * Math.sin(phi2));
                
                float nx4 = (float) (Math.cos(theta2) * Math.cos(phi1));
                float ny4 = (float) Math.sin(theta2);
                float nz4 = (float) (Math.cos(theta2) * Math.sin(phi1));
                
                // Dibujar el cuadrilátero con normales
                gl.glNormal3f(nx1, ny1, nz1);
                gl.glVertex3f(x1, y1, z1);
                
                gl.glNormal3f(nx2, ny2, nz2);
                gl.glVertex3f(x2, y2, z2);
                
                gl.glNormal3f(nx3, ny3, nz3);
                gl.glVertex3f(x3, y3, z3);
                
                gl.glNormal3f(nx4, ny4, nz4);
                gl.glVertex3f(x4, y4, z4);
                
                gl.glEnd();
            }
        }
    }
    
    // Métodos para controlar el personaje con detección de colisiones
    public void moverAdelante(GL2 gl) {
        // Calcular la nueva posición (CORREGIDO - invertido signos)
        float nuevaX = personajeX + velocidadMovimiento * (float) Math.sin(Math.toRadians(personajeRotacion));
        float nuevaZ = personajeZ + velocidadMovimiento * (float) Math.cos(Math.toRadians(personajeRotacion));
        
        // Verificar si hay colisión en la nueva posición
        boolean hayColision = colisionManager != null && colisionManager.detectarColision(gl, nuevaX, personajeY, nuevaZ);
        
        // Si no hay colisión, actualizar la posición
        if (!hayColision) {
            personajeX = nuevaX;
            personajeZ = nuevaZ;
            System.out.println("Posición actualizada a [" + personajeX + ", " + personajeY + ", " + personajeZ + "]");
        }
    }
    
    public void moverAtras(GL2 gl) {
        // Calcular la nueva posición (CORREGIDO - invertido signos)
        float nuevaX = personajeX - velocidadMovimiento * (float) Math.sin(Math.toRadians(personajeRotacion));
        float nuevaZ = personajeZ - velocidadMovimiento * (float) Math.cos(Math.toRadians(personajeRotacion));
        
        // Verificar si hay colisión en la nueva posición
        boolean hayColision = colisionManager != null && colisionManager.detectarColision(gl, nuevaX, personajeY, nuevaZ);
        
        // Si no hay colisión, actualizar la posición
        if (!hayColision) {
            personajeX = nuevaX;
            personajeZ = nuevaZ;
            System.out.println("Posición actualizada a [" + personajeX + ", " + personajeY + ", " + personajeZ + "]");
        }
    }
    
    public void moverIzquierda(GL2 gl) {
        // Calcular la nueva posición (CORREGIDO - invertido signos)
        float nuevaX = personajeX + velocidadMovimiento * (float) Math.sin(Math.toRadians(personajeRotacion - 90));
        float nuevaZ = personajeZ + velocidadMovimiento * (float) Math.cos(Math.toRadians(personajeRotacion - 90));
        
        // Verificar si hay colisión en la nueva posición
        boolean hayColision = colisionManager != null && colisionManager.detectarColision(gl, nuevaX, personajeY, nuevaZ);
        
        // Si no hay colisión, actualizar la posición
        if (!hayColision) {
            personajeX = nuevaX;
            personajeZ = nuevaZ;
            System.out.println("Posición actualizada a [" + personajeX + ", " + personajeY + ", " + personajeZ + "]");
        }
    }
    
    public void moverDerecha(GL2 gl) {
        // Calcular la nueva posición (CORREGIDO - invertido signos)
        float nuevaX = personajeX + velocidadMovimiento * (float) Math.sin(Math.toRadians(personajeRotacion + 90));
        float nuevaZ = personajeZ + velocidadMovimiento * (float) Math.cos(Math.toRadians(personajeRotacion + 90));
        
        // Verificar si hay colisión en la nueva posición
        boolean hayColision = colisionManager != null && colisionManager.detectarColision(gl, nuevaX, personajeY, nuevaZ);
        
        // Si no hay colisión, actualizar la posición
        if (!hayColision) {
            personajeX = nuevaX;
            personajeZ = nuevaZ;
            System.out.println("Posición actualizada a [" + personajeX + ", " + personajeY + ", " + personajeZ + "]");
        }
    }
    
    public void rotarIzquierda() {
        personajeRotacion += velocidadRotacion;
    }
    
    public void rotarDerecha() {
        personajeRotacion -= velocidadRotacion;
    }
    
    public void alternarVisibilidad() {
        mostrarPersonaje = !mostrarPersonaje;
    }
    
    // Getters y setters
    public boolean estaMostrandose() {
        return mostrarPersonaje;
    }
    
    public void setPosicion(float x, float y, float z) {
        personajeX = x;
        personajeY = y;
        personajeZ = z;
    }
    
    public void setRotacion(float rotacion) {
        personajeRotacion = rotacion;
    }
    
    public float getX() {
        return personajeX;
    }
    
    public float getY() {
        return personajeY;
    }
    
    public float getZ() {
        return personajeZ;
    }
    
    public float getRotacion() {
        return personajeRotacion;
    }
    
    public void setVelocidadMovimiento(float velocidad) {
        this.velocidadMovimiento = velocidad;
    }
    
    public void setVelocidadRotacion(float velocidad) {
        this.velocidadRotacion = velocidad;
    }
}