package casas;

import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;

public class CasaCuatro {

    public static void main(String[] args) {
        try {
            // Crear el marco de la ventana
            JFrame frame = new JFrame("Casa 2");
            frame.setSize(1024, 768);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            // Crear el canvas de OpenGL
            GLCanvas canvas = new GLCanvas();
            Distribucion3 dist = new Distribucion3();
            canvas.addGLEventListener(dist);

            // Agregar los listeners para controlar la cámara con el ratón
            canvas.addMouseListener(dist);
            canvas.addMouseMotionListener(dist);
            canvas.addMouseWheelListener(dist);
            canvas.addKeyListener(dist);

            // Añadir canvas al frame (sin etiqueta de instrucciones)
            frame.add(canvas, BorderLayout.CENTER);

            // Crear un animador para actualizar la visualización
            FPSAnimator animator = new FPSAnimator(canvas, 60);

            // Mostrar todo
            frame.setVisible(true);
            canvas.requestFocusInWindow();
            canvas.requestFocusInWindow();
            animator.start();

            // Mostrar instrucciones en la consola (mantenemos esto para referencia del desarrollador)
            System.out.println("=== INSTRUCCIONES ===");
            System.out.println("Arrastrar ratón = Rotar cámara");
            System.out.println("Rueda del ratón = Zoom");
            System.out.println("W = Alternar entre wireframe y sólido");
            System.out.println("P = Alternar entre primera y tercera persona");
            System.out.println("Flechas = Mover en modo primera persona");
            System.out.println("+ / - = Ajustar escala del modelo");
            System.out.println("L = Activar/Desactivar iluminación");
            System.out.println("Teclado numérico:");
            System.out.println("  8 = Mover luz hacia arriba");
            System.out.println("  2 = Mover luz hacia abajo");
            System.out.println("  4 = Mover luz a la izquierda");
            System.out.println("  6 = Mover luz a la derecha");
            System.out.println("  5 = Mover luz hacia adelante");
            System.out.println("  0 = Mover luz hacia atrás");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error al iniciar la aplicación: " + e.getMessage()
                    + "\nSolución: Ejecute la aplicación con los siguientes parámetros de JVM:\n"
                    + "--add-exports java.base/java.lang=ALL-UNNAMED\n"
                    + "--add-exports java.desktop/sun.awt=ALL-UNNAMED\n"
                    + "--add-exports java.desktop/sun.java2d=ALL-UNNAMED",
                    "Error de configuración", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
