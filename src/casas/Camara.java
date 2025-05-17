package casas;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

public class Camara {

    private GLU glu = new GLU();

    // Variables para controlar la cámara
    private float cameraDistance = 30.0f;
    private float cameraX = 0.0f;
    private float cameraY = 0.0f;  // Cambiar de 10.0f a 0.0f para centrar mejor
    private float rotationX = 30.0f;  // Iniciar con una inclinación de 30 grados para ver mejor el plano
    private float rotationY = 0.0f;

    // Modo de cámara (true = primera persona, false = tercera persona)
    private boolean primeraPersona = false;

    // Posición en primera persona
    private float posX = 0.0f;
    private float posY = 5.0f;  // Altura aproximada de los ojos
    private float posZ = 0.0f;

    // Constructor
    public Camara() {
        // Inicialización con valores predeterminados
    }

    // Método para configurar la cámara en el contexto OpenGL
    public void configurarCamara(GL2 gl) {
        gl.glLoadIdentity();

        if (primeraPersona) {
            // Modo primera persona - calculamos hacia dónde mira
            float lookX = posX + (float) Math.sin(Math.toRadians(rotationY));
            float lookY = posY + (float) Math.sin(Math.toRadians(-rotationX));
            float lookZ = posZ + (float) Math.cos(Math.toRadians(rotationY));

            glu.gluLookAt(
                    posX, posY, posZ, // Posición de la cámara
                    lookX, lookY, lookZ, // Punto al que mira
                    0.0f, 1.0f, 0.0f // Vector "up"
            );
        } else {
            // Modo tercera persona - calculamos posición usando coordenadas esféricas
            float eyeX = cameraX + (float) (cameraDistance * Math.sin(Math.toRadians(rotationY)) * Math.cos(Math.toRadians(rotationX)));
            float eyeY = cameraY + (float) (cameraDistance * Math.sin(Math.toRadians(rotationX)));
            float eyeZ = (float) (cameraDistance * Math.cos(Math.toRadians(rotationY)) * Math.cos(Math.toRadians(rotationX)));

            glu.gluLookAt(
                    eyeX, eyeY, eyeZ, // Posición de la cámara
                    cameraX, cameraY, 0, // Punto al que mira
                    0.0f, 1.0f, 0.0f // Vector "up"
            );
        }
    }

    // Método para cambiar entre modos de cámara
    public void cambiarModo() {
        primeraPersona = !primeraPersona;

        // Si cambiamos a primera persona, ajustamos la posición inicial
        if (primeraPersona) {
            // Colocamos en una posición lógica dentro de la casa
            posX = 0.0f;
            posY = 5.0f;
            posZ = 10.0f;
        }
    }

    // Método para manejar rotación
    public void rotar(float deltaX, float deltaY) {
        rotationY += deltaX * 0.5f;
        rotationX += deltaY * 0.5f;

        // Limitar la rotación vertical para evitar giros completos
        if (rotationX > 89) {
            rotationX = 89;
        }
        if (rotationX < -89) {
            rotationX = -89;
        }
    }

    // Método para manejar zoom (solo en tercera persona)
    public void ajustarZoom(int notches) {
        if (!primeraPersona) {
            cameraDistance += notches * 1.0f;

            // Limitar el zoom para evitar valores negativos o demasiado grandes
            if (cameraDistance < 5.0f) {
                cameraDistance = 5.0f;
            }
            if (cameraDistance > 50.0f) {
                cameraDistance = 50.0f;
            }
        }
    }

    // Métodos para mover la cámara en primera persona
    public void moverAdelante(float distancia) {
        if (primeraPersona) {
            posX += distancia * (float) Math.sin(Math.toRadians(rotationY));
            posZ += distancia * (float) Math.cos(Math.toRadians(rotationY));
        }
    }

    public void moverAtras(float distancia) {
        if (primeraPersona) {
            posX -= distancia * (float) Math.sin(Math.toRadians(rotationY));
            posZ -= distancia * (float) Math.cos(Math.toRadians(rotationY));
        }
    }

    public void moverIzquierda(float distancia) {
        if (primeraPersona) {
            posX += distancia * (float) Math.sin(Math.toRadians(rotationY - 90));
            posZ += distancia * (float) Math.cos(Math.toRadians(rotationY - 90));
        }
    }

    public void moverDerecha(float distancia) {
        if (primeraPersona) {
            posX += distancia * (float) Math.sin(Math.toRadians(rotationY + 90));
            posZ += distancia * (float) Math.cos(Math.toRadians(rotationY + 90));
        }
    }

    // Métodos para subir y bajar la cámara (simular escalones)
    public void subirEscalon(float altura) {
        if (primeraPersona) {
            posY += altura;
        } else {
            cameraY += altura;
        }
    }

    public void bajarEscalon(float altura) {
        if (primeraPersona) {
            posY -= altura;
            // Evitar que la cámara baje del suelo
            //if (posY < 1.0f) {
              //  posY = 1.0f;
            //}
        } else {
            cameraY -= altura;
            // Evitar que la cámara baje del suelo
            if (cameraY < 0.0f) {
                cameraY = 0.0f;
            }
        }
    }

    // Getters y setters para acceder a las propiedades
    public boolean isPrimeraPersona() {
        return primeraPersona;
    }

    public void setCameraDistance(float distance) {
        this.cameraDistance = distance;
    }

    public float getCameraDistance() {
        return cameraDistance;
    }
}