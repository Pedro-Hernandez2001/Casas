package casas;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import javax.swing.JFrame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Distribucion3 implements GLEventListener, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

    private GLAutoDrawable currentDrawable = null;
    private GLU glu = new GLU();
    private List<float[]> vertices = new ArrayList<>();
    private List<float[]> texCoords = new ArrayList<>();  // Coordenadas de textura
    private List<float[]> normales = new ArrayList<>();
    private List<int[]> faces = new ArrayList<>();
    private List<int[]> texIndices = new ArrayList<>();   // Índices de textura para cada cara
    private List<int[]> normIndices = new ArrayList<>();  // Índices de normales para cada cara
    private List<String> materialNames = new ArrayList<>(); // Material para cada cara

    // Estructura para almacenar propiedades de materiales
    private static class Material {
        float[] ambient = {0.2f, 0.2f, 0.2f, 1.0f};
        float[] diffuse = {0.8f, 0.8f, 0.8f, 1.0f};
        float[] specular = {0.0f, 0.0f, 0.0f, 1.0f};
        float shininess = 0.0f;
        String textureFile = null;
        Texture texture = null;
    }

    private Map<String, Material> materiales = new HashMap<>();
    private String currentMaterial = "";
    private String dirBase = "";
    private String mtlFile = null;
    private boolean texturasListas = false;
    
    // Variable para indicar que las texturas deben recargarse
    private boolean recargarTexturas = false;

    // Instancias de las clases
    private Camara camara = new Camara();
    Escala escala = new Escala();
    private Luz luz = new Luz();

    // Añadir el personaje y el administrador de colisiones
    private Personaje personaje = new Personaje();
    private ColisionManager colisionManager = new ColisionManager();
    private boolean debugColisiones = false; // Para activar visualización de colisiones

    // Variables para el control del ratón
    private int mouseX = 0;
    private int mouseY = 0;
    private boolean mousePressed = false;

    // Variables para el renderizado
    private boolean wireframeMode = false; // Por defecto, modo sólido
    public Distribucion3() {
        cargarModeloOBJ();

        // Inicializar el administrador de colisiones con los vértices y caras del modelo
        colisionManager.actualizarObstaculosDesdeModelo(vertices, faces);

        // Pasar el administrador de colisiones al personaje
        personaje.setColisionManager(colisionManager);

        // Posicionar al personaje en un lugar adecuado dentro de la casa
        personaje.setPosicion(0.0f, 0.0f, 5.0f);
    }

    private void cargarModeloOBJ() {
        try {
            // Cambia esta ruta a la ubicación de tu archivo OBJ
            File archivo = new File("C:\\Users\\pedro\\Documents\\Semestre Enero- Junio 2025\\Graficacion\\Casas\\src\\Objetos\\House - Full Interior 1 (BakedLight) (Sketchfab).OBJ");
            dirBase = archivo.getParent();
            System.out.println("Directorio base: " + dirBase);
            
            BufferedReader br = new BufferedReader(new FileReader(archivo));
            String linea;

            while ((linea = br.readLine()) != null) {
                if (linea.startsWith("mtllib ")) {
                    // Archivo de materiales - conservar espacios en el nombre
                    mtlFile = linea.split("\\s+", 2)[1]; // Usar límite 2 para mantener espacios
                    System.out.println("Archivo MTL encontrado: " + mtlFile);
                } else if (linea.startsWith("v ")) {
                    // Procesar vértices
                    String[] parts = linea.split("\\s+");
                    float x = Float.parseFloat(parts[1]);
                    float y = Float.parseFloat(parts[2]);
                    float z = Float.parseFloat(parts[3]);
                    vertices.add(new float[]{x, y, z});
                } else if (linea.startsWith("vt ")) {
                    // Procesar coordenadas de textura
                    String[] parts = linea.split("\\s+");
                    float u = Float.parseFloat(parts[1]);
                    float v = parts.length > 2 ? Float.parseFloat(parts[2]) : 0.0f;
                    texCoords.add(new float[]{u, v});
                } else if (linea.startsWith("vn ")) {
                    // Procesar normales de vértices
                    String[] parts = linea.split("\\s+");
                    float nx = Float.parseFloat(parts[1]);
                    float ny = Float.parseFloat(parts[2]);
                    float nz = Float.parseFloat(parts[3]);
                    normales.add(new float[]{nx, ny, nz});
                } else if (linea.startsWith("usemtl ")) {
                    // Cambiar material actual
                    currentMaterial = linea.split("\\s+")[1];
                } else if (linea.startsWith("f ")) {
                    // Procesar caras
                    String[] parts = linea.split("\\s+");
                    int[] face = new int[parts.length - 1];
                    int[] texFace = new int[parts.length - 1];
                    int[] normFace = new int[parts.length - 1];

                    for (int i = 1; i < parts.length; i++) {
                        String[] indices = parts[i].split("/");

                        // Índice de vértice
                        face[i - 1] = Integer.parseInt(indices[0]) - 1;

                        // Índice de textura (si existe)
                        if (indices.length > 1 && !indices[1].isEmpty()) {
                            texFace[i - 1] = Integer.parseInt(indices[1]) - 1;
                        } else {
                            texFace[i - 1] = -1; // Sin coordenada de textura
                        }

                        // Índice de normal (si existe)
                        if (indices.length > 2 && !indices[2].isEmpty()) {
                            normFace[i - 1] = Integer.parseInt(indices[2]) - 1;
                        } else {
                            normFace[i - 1] = -1; // Sin normal
                        }
                    }

                    faces.add(face);
                    texIndices.add(texFace);
                    normIndices.add(normFace);
                    materialNames.add(currentMaterial);
                }
            }
            br.close();

            // Cargar los materiales (sin texturas todavía)
            if (mtlFile != null) {
                cargarMTL(new File(dirBase, mtlFile).getAbsolutePath());
            }

            System.out.println("Modelo cargado: " + vertices.size() + " vértices, "
                    + faces.size() + " caras, " + materiales.size() + " materiales");

            // Calcular la escala óptima basada en los vértices
            escala.calcularEscalaOptima(vertices);

            // Establecer la distancia de la cámara óptima
            camara.setCameraDistance(escala.getDistanciaOptimaCamara());

        } catch (Exception e) {
            System.err.println("Error al cargar el modelo OBJ: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarMTL(String mtlFilePath) {
        try {
            System.out.println("Intentando cargar archivo MTL: " + mtlFilePath);
            File mtlFile = new File(mtlFilePath);
            
            if (!mtlFile.exists()) {
                System.out.println("Archivo MTL no encontrado. Buscando alternativas...");
                
                // Intenta buscar por coincidencia parcial
                File dir = new File(dirBase);
                File[] posiblesMtl = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".mtl"));
                
                if (posiblesMtl != null && posiblesMtl.length > 0) {
                    mtlFile = posiblesMtl[0]; // Tomar el primer archivo .mtl que encontremos
                    System.out.println("Usando archivo MTL alternativo: " + mtlFile.getName());
                }
            }
            
            if (mtlFile.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(mtlFile));
                System.out.println("Archivo MTL encontrado y abierto correctamente: " + mtlFile.getAbsolutePath());
                
                String linea;
                String currentMtl = null;
                Material material = null;

                while ((linea = br.readLine()) != null) {
                    if (linea.isEmpty() || linea.startsWith("#")) {
                        continue;
                    }

                    String[] parts = linea.split("\\s+");
                    if (parts.length < 2) {
                        continue;
                    }

                    switch (parts[0]) {
                        case "newmtl":
                            // Nuevo material
                            currentMtl = parts[1];
                            material = new Material();
                            materiales.put(currentMtl, material);
                            System.out.println("Nuevo material definido: " + currentMtl);
                            break;
                        case "Ka": // Ambiente
                            if (material != null && parts.length >= 4) {
                                material.ambient[0] = Float.parseFloat(parts[1]);
                                material.ambient[1] = Float.parseFloat(parts[2]);
                                material.ambient[2] = Float.parseFloat(parts[3]);
                            }
                            break;
                        case "Kd": // Difuso
                            if (material != null && parts.length >= 4) {
                                material.diffuse[0] = Float.parseFloat(parts[1]);
                                material.diffuse[1] = Float.parseFloat(parts[2]);
                                material.diffuse[2] = Float.parseFloat(parts[3]);
                            }
                            break;
                        case "Ks": // Especular
                            if (material != null && parts.length >= 4) {
                                material.specular[0] = Float.parseFloat(parts[1]);
                                material.specular[1] = Float.parseFloat(parts[2]);
                                material.specular[2] = Float.parseFloat(parts[3]);
                            }
                            break;
                        case "Ns": // Brillo
                            if (material != null) {
                                material.shininess = Float.parseFloat(parts[1]);
                            }
                            break;
                        case "map_Kd": // Textura difusa
                            if (material != null) {
                                // Extrae el nombre de la textura manteniendo espacios
                                StringBuilder textureFileName = new StringBuilder();
                                for (int i = 1; i < parts.length; i++) {
                                    textureFileName.append(parts[i]).append(" ");
                                }
                                material.textureFile = textureFileName.toString().trim();
                                System.out.println("Textura asignada al material " + currentMtl + ": " + material.textureFile);
                            }
                            break;
                    }
                }
                br.close();
            } else {
                System.err.println("No se pudo encontrar ningún archivo MTL válido en: " + mtlFilePath);
            }

        } catch (IOException e) {
            System.err.println("Error al cargar el archivo MTL: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // Método para cargar las texturas ahora que el contexto OpenGL está disponible
    private void cargarTexturas(GL2 gl) {
        if (texturasListas || dirBase.isEmpty()) {
            return;
        }

        // Liberar texturas existentes si estamos recargando
        if (recargarTexturas) {
            for (Map.Entry<String, Material> entry : materiales.entrySet()) {
                Material mat = entry.getValue();
                if (mat.texture != null) {
                    try {
                        mat.texture.destroy(gl);
                        mat.texture = null;
                    } catch (Exception e) {
                        System.err.println("Error al liberar textura: " + e.getMessage());
                    }
                }
            }
        }

        System.out.println("Iniciando carga de texturas. Directorio base: " + dirBase);
        int texturasExitosas = 0;
        int texturasFallidas = 0;
        
        // Listar todos los archivos de imagen en el directorio
        File dir = new File(dirBase);
        File[] imageFiles = dir.listFiles((d, name) -> 
            name.toLowerCase().endsWith(".jpg") || 
            name.toLowerCase().endsWith(".png") || 
            name.toLowerCase().endsWith(".bmp") ||
            name.toLowerCase().endsWith(".jpeg")
        );
        
        System.out.println("Archivos de imagen encontrados en el directorio: " + (imageFiles != null ? imageFiles.length : 0));
        
        for (Map.Entry<String, Material> entry : materiales.entrySet()) {
            Material mat = entry.getValue();
            if (mat.textureFile != null && !mat.textureFile.isEmpty()) {
                try {
                    System.out.println("Material: " + entry.getKey() + ", Textura: " + mat.textureFile);
                    
                    // Intenta con la ruta original
                    File textureFile = new File(dirBase, mat.textureFile);
                    System.out.println("Intentando ruta: " + textureFile.getAbsolutePath());
                    
                    if (!textureFile.exists()) {
                        // Si no existe, intenta solo con el nombre del archivo
                        String nombreArchivo = new File(mat.textureFile).getName();
                        textureFile = new File(dirBase, nombreArchivo);
                        System.out.println("Ruta alternativa 1: " + textureFile.getAbsolutePath());
                        
                        // Si aún no existe, busca un archivo con nombre similar
                        if (!textureFile.exists() && imageFiles != null) {
                            for (File imgFile : imageFiles) {
                                if (imgFile.getName().toLowerCase().contains(
                                        nombreArchivo.toLowerCase().replaceAll("\\.[^.]*$", ""))) {
                                    textureFile = imgFile;
                                    System.out.println("Ruta alternativa 2 (coincidencia parcial): " + textureFile.getAbsolutePath());
                                    break;
                                }
                            }
                        }
                    }
                    
                    if (textureFile.exists()) {
                        System.out.println("El archivo existe, cargando textura...");
                        mat.texture = TextureIO.newTexture(textureFile, true);
                        
                        // Configurar parámetros de textura
                        gl.glBindTexture(GL2.GL_TEXTURE_2D, mat.texture.getTextureObject());
                        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
                        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
                        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
                        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
                        System.out.println("¡Textura cargada exitosamente!: " + textureFile.getName());
                        texturasExitosas++;
                    } else {
                        System.out.println("¡Archivo de textura no encontrado!");
                        texturasFallidas++;
                    }
                } catch (IOException e) {
                    System.err.println("Error cargando textura " + mat.textureFile + ": " + e.getMessage());
                    e.printStackTrace();
                    texturasFallidas++;
                }
            }
        }

        System.out.println("Resumen de carga de texturas: " + texturasExitosas + " exitosas, " + texturasFallidas + " fallidas");
        texturasListas = true;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0.85f, 0.91f, 0.95f, 1.0f); // Color de fondo azul claro suave
        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glEnable(GL2.GL_DEPTH_TEST);

        // Habilitar blending para texturas con transparencia
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

        // Configuración adicional para mejorar la visualización de texturas
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);

        // Ahora podemos cargar las texturas
        cargarTexturas(gl);

        // Inicializar la cámara en una posición centrada
        camara.rotar(0.0f, 20.0f); // Inclinar ligeramente hacia abajo para ver mejor el plano
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        this.currentDrawable = drawable;
        GL2 gl = drawable.getGL().getGL2();

        // Recargar texturas si es necesario - esto es seguro porque estamos en el hilo con contexto OpenGL
        if (recargarTexturas) {
            System.out.println("Recargando texturas desde display()...");
            texturasListas = false;
            cargarTexturas(gl);
            recargarTexturas = false;  // Resetear la bandera
        }

        // Limpiar el buffer
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        // Configurar la cámara usando la clase Camara
        camara.configurarCamara(gl);

        // Aplicar escala
        float escalaActual = escala.getEscalaActual();
        gl.glScalef(escalaActual, escalaActual, escalaActual);

        // Centrar el modelo
        float[] centro = escala.getCentroModelo();
        gl.glTranslatef(-centro[0], -centro[1], -centro[2]);

        // Configurar modo de renderizado (wireframe o sólido)
        if (wireframeMode) {
            gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
        } else {
            gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

            // Activar iluminación para modo sólido
            luz.activar(gl);
            luz.configurarMaterialPorDefecto(gl);
        }

        // Renderizar cada cara con su material correspondiente
        String currentMat = "";
        boolean texturingEnabled = false;

        for (int i = 0; i < faces.size(); i++) {
            int[] face = faces.get(i);
            int[] texFace = texIndices.get(i);
            int[] normFace = normIndices.get(i);
            String matName = materialNames.get(i);

            // Cambiar material si es necesario
            if (!matName.equals(currentMat)) {
                currentMat = matName;
                Material mat = materiales.get(currentMat);

                if (mat != null) {
                    // Aplicar propiedades del material
                    gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, mat.ambient, 0);
                    gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, mat.diffuse, 0);
                    gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, mat.specular, 0);
                    gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, mat.shininess);

                    // Usar color del material
                    gl.glColor3f(mat.diffuse[0], mat.diffuse[1], mat.diffuse[2]);

                    // Aplicar textura si existe
                    if (mat.texture != null) {
                        gl.glEnable(GL2.GL_TEXTURE_2D);
                        // Configurar parámetros de textura para mejorar la visualización
                        gl.glBindTexture(GL2.GL_TEXTURE_2D, mat.texture.getTextureObject());
                        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
                        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
                        gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
                        mat.texture.bind(gl);
                        texturingEnabled = true;
                    } else if (texturingEnabled) {
                        gl.glDisable(GL2.GL_TEXTURE_2D);
                        texturingEnabled = false;
                    }
                }
            }

            gl.glBegin(GL2.GL_POLYGON);

            for (int j = 0; j < face.length; j++) {
                // Aplicar normal
                if (normFace[j] >= 0 && normFace[j] < normales.size()) {
                    float[] normal = normales.get(normFace[j]);
                    gl.glNormal3f(normal[0], normal[1], normal[2]);
                }

                // Aplicar coordenada de textura
                if (texturingEnabled && texFace[j] >= 0 && texFace[j] < texCoords.size()) {
                    float[] texCoord = texCoords.get(texFace[j]);
                    gl.glTexCoord2f(texCoord[0], texCoord[1]);
                }

                // Dibujar vértice
                float[] vertex = vertices.get(face[j]);
                gl.glVertex3f(vertex[0], vertex[1], vertex[2]);
            }

            gl.glEnd();
        }

        // Desactivar textura si estaba activa
        if (texturingEnabled) {
            gl.glDisable(GL2.GL_TEXTURE_2D);
        }
        // Dibujar el personaje dentro de la casa
        gl.glPushMatrix();

        // Guardamos el estado actual de la transformación
        gl.glLoadIdentity();

        // Configurar la cámara nuevamente
        camara.configurarCamara(gl);

        // Aplicar escala al personaje para que sea proporcional a la casa
        // Usamos un factor de escala adecuado para adaptarlo al tamaño del modelo
        gl.glScalef(0.1f, 0.1f, 0.1f);

        // Dibujar el personaje usando la clase Personaje
        personaje.dibujar(gl);
        gl.glPopMatrix();

        // Si el modo debug está activado, visualizar las cajas de colisión
        if (debugColisiones) {
            dibujarColisiones(gl);
        }

        // Desactivar iluminación después de renderizar
        if (!wireframeMode) {
            luz.desactivar(gl);
        }
    }

    // Método para visualizar las cajas de colisión (útil para depuración)
    private void dibujarColisiones(GL2 gl) {
        // Aquí deberíamos dibujar visualmente las cajas de colisión
        gl.glPushMatrix();

        // Guardar el estado de GL
        boolean prevLightingState = luz.isActiva();
        int prevPolygonMode[] = new int[2];
        gl.glGetIntegerv(GL2.GL_POLYGON_MODE, prevPolygonMode, 0);

        // Configurar para dibujar wireframes rojos
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
        gl.glColor3f(1.0f, 0.0f, 0.0f);  // Rojo

        // Dibujar las cajas de colisión definidas en ColisionManager
        List<float[]> obstaculosBoundingBoxes = colisionManager.getObstaculosBoundingBoxes();

        for (float[] box : obstaculosBoundingBoxes) {
            float minX = box[0], minY = box[1], minZ = box[2];
            float maxX = box[3], maxY = box[4], maxZ = box[5];

            // Dibujar una caja wireframe
            gl.glBegin(GL2.GL_LINE_LOOP);
            // Base inferior
            gl.glVertex3f(minX, minY, minZ);
            gl.glVertex3f(maxX, minY, minZ);
            gl.glVertex3f(maxX, minY, maxZ);
            gl.glVertex3f(minX, minY, maxZ);
            gl.glEnd();

            gl.glBegin(GL2.GL_LINE_LOOP);
            // Base superior
            gl.glVertex3f(minX, maxY, minZ);
            gl.glVertex3f(maxX, maxY, minZ);
            gl.glVertex3f(maxX, maxY, maxZ);
            gl.glVertex3f(minX, maxY, maxZ);
            gl.glEnd();

            gl.glBegin(GL2.GL_LINES);
            // Conectar bases
            gl.glVertex3f(minX, minY, minZ);
            gl.glVertex3f(minX, maxY, minZ);

            gl.glVertex3f(maxX, minY, minZ);
            gl.glVertex3f(maxX, maxY, minZ);

            gl.glVertex3f(maxX, minY, maxZ);
            gl.glVertex3f(maxX, maxY, maxZ);

            gl.glVertex3f(minX, minY, maxZ);
            gl.glVertex3f(minX, maxY, maxZ);
            gl.glEnd();
        }

        // Restaurar el estado de GL
        if (prevLightingState) {
            gl.glEnable(GL2.GL_LIGHTING);
        }
        gl.glPolygonMode(GL2.GL_FRONT, prevPolygonMode[0]);
        gl.glPolygonMode(GL2.GL_BACK, prevPolygonMode[1]);

        gl.glPopMatrix();
    }

    // Función para calcular la normal de una cara
    private float[] calcularNormalCara(int[] face) {
        if (face.length < 3) {
            return new float[]{0, 1, 0}; // Si no hay suficientes vértices
        }
        // Tomar tres vértices para calcular la normal
        float[] v1 = vertices.get(face[0]);
        float[] v2 = vertices.get(face[1]);
        float[] v3 = vertices.get(face[2]);

        // Calcular vectores para el producto cruz
        float[] vec1 = {v2[0] - v1[0], v2[1] - v1[1], v2[2] - v1[2]};
        float[] vec2 = {v3[0] - v1[0], v3[1] - v1[1], v3[2] - v1[2]};

        // Producto cruz para la normal
        float[] normal = {
            vec1[1] * vec2[2] - vec1[2] * vec2[1],
            vec1[2] * vec2[0] - vec1[0] * vec2[2],
            vec1[0] * vec2[1] - vec1[1] * vec2[0]
        };

        // Normalizar
        float length = (float) Math.sqrt(normal[0] * normal[0] + normal[1] * normal[1] + normal[2] * normal[2]);
        if (length > 0) {
            normal[0] /= length;
            normal[1] /= length;
            normal[2] /= length;
        }

        return normal;
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();

        if (height == 0) {
            height = 1;
        }
        float aspect = (float) width / height;

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluPerspective(45.0, aspect, 0.1, 100.0);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        // Liberación de recursos
    }

    // Método para alternar el modo wireframe
    public void toggleWireframeMode() {
        wireframeMode = !wireframeMode;
    }

    // Implementación de MouseListener
    @Override
    public void mousePressed(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        mousePressed = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mousePressed = false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    // Implementación de MouseMotionListener
    @Override
    public void mouseDragged(MouseEvent e) {
        if (mousePressed) {
            int deltaX = e.getX() - mouseX;
            int deltaY = e.getY() - mouseY;

            // Usar la clase Camara para manejar la rotación
            camara.rotar(deltaX * 0.5f, deltaY * 0.5f);

            mouseX = e.getX();
            mouseY = e.getY();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    // Implementación de MouseWheelListener
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        // Usar la clase Camara para manejar el zoom
        camara.ajustarZoom(e.getWheelRotation());
    }


    @Override
public void keyPressed(KeyEvent e) {
    float movSpeed = 0.5f; // Velocidad de movimiento en primera persona
    float rotSpeed = 5.0f; // Velocidad de rotación (grados) para las teclas de flecha
    float alturaEscalon = 0.5f; // Altura del escalón para subir/bajar
    
    // Verificar que tenemos un drawable válido
    if (currentDrawable == null) {
        return;
    }
    
    GL2 gl = currentDrawable.getGL().getGL2();

    switch (e.getKeyCode()) {
        case KeyEvent.VK_W:  // Cambiar entre wireframe y sólido con 'W'
            toggleWireframeMode();
            break;
        case KeyEvent.VK_P:  // Cambiar entre primera y tercera persona con 'P'
            camara.cambiarModo();
            break;
        case KeyEvent.VK_L:  // Alternar iluminación con 'L'
            luz.alternarEstado();
            break;
        case KeyEvent.VK_H:  // Alternar visibilidad del personaje con 'H'
            personaje.alternarVisibilidad();
            break;
        case KeyEvent.VK_D:  // Alternar visualización de colisiones con 'D'
            debugColisiones = !debugColisiones;
            System.out.println("Detección de colisiones: " + (debugColisiones ? "ACTIVADA" : "DESACTIVADA"));
            break;
        case KeyEvent.VK_C:  // Alternar sistema de colisiones con 'C'
            colisionManager.alternarColisiones();
            break;
        case KeyEvent.VK_UP:  // Mover adelante
            if (camara.isPrimeraPersona()) {
                camara.moverAdelante(movSpeed);
            } else {
                personaje.moverAdelante(gl);
                System.out.println("Personaje movido adelante: [" + personaje.getX() + ", " + personaje.getY() + ", " + personaje.getZ() + "]");
                currentDrawable.display(); // Forzar redibujado
            }
            break;
        case KeyEvent.VK_DOWN:  // Mover atrás
            if (camara.isPrimeraPersona()) {
                camara.moverAtras(movSpeed);
            } else {
                personaje.moverAtras(gl);
                System.out.println("Personaje movido atrás: [" + personaje.getX() + ", " + personaje.getY() + ", " + personaje.getZ() + "]");
                currentDrawable.display(); // Forzar redibujado
            }
            break;
        case KeyEvent.VK_RIGHT:  // Rotar a la izquierda (en lugar de desplazarse)
            if (camara.isPrimeraPersona()) {
                // Usar rotar en lugar de moverIzquierda
                camara.rotar(-rotSpeed, 0);
                System.out.println("Cámara rotada a la izquierda");
            } else {
                personaje.moverIzquierda(gl);
                System.out.println("Personaje movido izquierda: [" + personaje.getX() + ", " + personaje.getY() + ", " + personaje.getZ() + "]");
                currentDrawable.display(); // Forzar redibujado
            }
            break;
        case KeyEvent.VK_LEFT:  // Rotar a la derecha (en lugar de desplazarse)
            if (camara.isPrimeraPersona()) {
                // Usar rotar en lugar de moverDerecha
                camara.rotar(rotSpeed, 0);
                System.out.println("Cámara rotada a la derecha");
            } else {
                personaje.moverDerecha(gl);
                System.out.println("Personaje movido derecha: [" + personaje.getX() + ", " + personaje.getY() + ", " + personaje.getZ() + "]");
                currentDrawable.display(); // Forzar redibujado
            }
            break;
        case KeyEvent.VK_A:  // Rotar personaje a la izquierda
            personaje.rotarIzquierda();
            System.out.println("Personaje rotado izquierda: " + personaje.getRotacion() + " grados");
            currentDrawable.display(); // Forzar redibujado
            break;
        case KeyEvent.VK_S:  // Rotar personaje a la derecha
            personaje.rotarDerecha();
            System.out.println("Personaje rotado derecha: " + personaje.getRotacion() + " grados");
            currentDrawable.display(); // Forzar redibujado
            break;
        case KeyEvent.VK_PLUS:
        case KeyEvent.VK_ADD:  // Aumentar escala
            escala.ajustarEscala(1.1f);
            break;
        case KeyEvent.VK_MINUS:
        case KeyEvent.VK_SUBTRACT:  // Disminuir escala
            escala.ajustarEscala(0.9f);
            break;
        // Controles para mover la luz
        case KeyEvent.VK_NUMPAD8:  // Mover luz hacia arriba
            luz.moverLuz(0, 1, 0);
            break;
        case KeyEvent.VK_NUMPAD2:  // Mover luz hacia abajo
            luz.moverLuz(0, -1, 0);
            break;
        case KeyEvent.VK_NUMPAD4:  // Mover luz a la izquierda
            luz.moverLuz(-1, 0, 0);
            break;
        case KeyEvent.VK_NUMPAD6:  // Mover luz a la derecha
            luz.moverLuz(1, 0, 0);
            break;
        case KeyEvent.VK_NUMPAD5:  // Mover luz hacia adelante
            luz.moverLuz(0, 0, -1);
            break;
        case KeyEvent.VK_NUMPAD0:  // Mover luz hacia atrás
            luz.moverLuz(0, 0, 1);
            break;
        // Tecla R para recargar texturas (seguro)
        case KeyEvent.VK_R:
            recargarTexturas = true;  // Establecer bandera para recargar en display()
            System.out.println("Se recargarán las texturas en el próximo ciclo de renderizado...");
            break;
        // Tecla T para mostrar información de texturas
        case KeyEvent.VK_T:
            System.out.println("=== INFORMACIÓN DE TEXTURAS ===");
            for (Map.Entry<String, Material> entry : materiales.entrySet()) {
                Material mat = entry.getValue();
                System.out.println("Material: " + entry.getKey());
                System.out.println("  Textura: " + (mat.textureFile != null ? mat.textureFile : "ninguna"));
                System.out.println("  Textura cargada: " + (mat.texture != null ? "SÍ" : "NO"));
            }
            break;
        // Tecla M para avanzar una vez (primera persona) o escanear directorio (tercera persona)
        case KeyEvent.VK_M:
            if (camara.isPrimeraPersona()) {
                camara.subirEscalon(alturaEscalon);
                // Avanzar una vez
                //camara.moverAdelante(movSpeed);
                System.out.println("Cámara avanzó una vez");
            } else {
                // Mantener la funcionalidad original para modo tercera persona
                System.out.println("=== ESCANEANDO DIRECTORIO POR ARCHIVOS MTL ===");
                File dir = new File(dirBase);
                File[] mtlFiles = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".mtl"));
                if (mtlFiles != null) {
                    for (File f : mtlFiles) {
                        System.out.println("Archivo MTL encontrado: " + f.getAbsolutePath());
                    }
                }
                
                System.out.println("=== ESCANEANDO DIRECTORIO POR IMÁGENES ===");
                File[] imgFiles = dir.listFiles((d, name) -> 
                    name.toLowerCase().endsWith(".jpg") || 
                    name.toLowerCase().endsWith(".png") || 
                    name.toLowerCase().endsWith(".jpeg") || 
                    name.toLowerCase().endsWith(".bmp"));
                if (imgFiles != null) {
                    for (File f : imgFiles) {
                        System.out.println("Imagen encontrada: " + f.getAbsolutePath());
                    }
                }
            }
            break;
        // Tecla N para avanzar dos veces (primera persona)
        case KeyEvent.VK_N:
            if (camara.isPrimeraPersona()) {
                camara.bajarEscalon(alturaEscalon);
                // Avanzar dos veces
                //camara.moverAdelante(movSpeed);
                //camara.moverAdelante(movSpeed);
                System.out.println("Cámara avanzó dos veces");
            }
            break;
    }
    
    // Forzar redibujado después de cualquier cambio
    if (currentDrawable != null) {
        currentDrawable.display();
    }
}

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}