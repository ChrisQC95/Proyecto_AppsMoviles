package com.example.proyecto_gestortrabajadoresinformales;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Conexion extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mi_app.db";
    private static final int DATABASE_VERSION = 1;

    // Constantes para la tabla usuario
    public static final String TABLE_USUARIO = "usuario";
    public static final String USUARIO_ID = "id";
    public static final String USUARIO_NOMBRES = "nombres";
    public static final String USUARIO_APELLIDOS = "apellidos";
    public static final String USUARIO_TELEFONO = "telefono";
    public static final String USUARIO_CORREO = "correo";
    public static final String USUARIO_CONTRASENA = "contrasena";
    public static final String USUARIO_TIPO = "tipo_usuario";
    public static final String CREATE_TABLE_USUARIO =
            "CREATE TABLE " + TABLE_USUARIO + "("
                    + USUARIO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + USUARIO_NOMBRES + " TEXT NOT NULL,"
                    + USUARIO_APELLIDOS + " TEXT NOT NULL,"
                    + USUARIO_TELEFONO + " TEXT NOT NULL,"
                    + USUARIO_CORREO + " TEXT NOT NULL UNIQUE,"
                    + USUARIO_CONTRASENA + " TEXT NOT NULL,"
                    + USUARIO_TIPO + " TEXT NOT NULL CHECK (" + USUARIO_TIPO + " IN ('CLIENTE', 'TRABAJADOR'))"
                    + ")";



    // Constantes para la tabla distrito
    public static final String TABLE_DISTRITO = "distrito";
    public static final String DISTRITO_ID = "id";
    public static final String DISTRITO_NOMBRE = "nombre";
    public static final String CREATE_TABLE_DISTRITO =
            "CREATE TABLE " + TABLE_DISTRITO + "("
                    + DISTRITO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + DISTRITO_NOMBRE + " TEXT NOT NULL"
                    + ")";

    // Constantes para la tabla perfil
    public static final String TABLE_PERFIL = "perfil";
    public static final String PERFIL_ID = "id";
    public static final String PERFIL_USUARIO_ID = "usuario_id";
    public static final String PERFIL_DISTRITO_ID = "distrito_id";
    public static final String PERFIL_ESPECIALIDAD = "especialidad";
    public static final String PERFIL_FOTO = "foto_perfil";

    public static final String CREATE_TABLE_PERFIL =
            "CREATE TABLE " + TABLE_PERFIL + "("
                    + PERFIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + PERFIL_USUARIO_ID + " INTEGER NOT NULL UNIQUE,"
                    + PERFIL_DISTRITO_ID + " INTEGER,"
                    + PERFIL_ESPECIALIDAD + " TEXT,"
                    + PERFIL_FOTO + " TEXT,"
                    + "FOREIGN KEY (" + PERFIL_USUARIO_ID + ") REFERENCES " + TABLE_USUARIO + "(" + USUARIO_ID + ") ON DELETE CASCADE,"
                    + "FOREIGN KEY (" + PERFIL_DISTRITO_ID + ") REFERENCES " + TABLE_DISTRITO + "(" + DISTRITO_ID + ")"
                    + ")";

    // Constantes para la tabla tipo_servicio
    public static final String TABLE_TIPO_SERVICIO = "tipo_servicio";
    public static final String TIPO_SERVICIO_ID = "id";
    public static final String TIPO_SERVICIO_NOMBRE = "nombre";
    public static final String CREATE_TABLE_TIPO_SERVICIO =
            "CREATE TABLE " + TABLE_TIPO_SERVICIO + "("
                    + TIPO_SERVICIO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + TIPO_SERVICIO_NOMBRE + " TEXT NOT NULL UNIQUE"
                    + ")";

    // Constantes para la tabla propuesta
    public static final String TABLE_PROPUESTA = "propuesta";
    public static final String PROPUESTA_ID = "id";
    public static final String PROPUESTA_USUARIO_ID = "usuario_id";
    public static final String PROPUESTA_TITULO = "titulo";
    public static final String PROPUESTA_PRECIO = "precio";
    public static final String PROPUESTA_DESCRIPCION = "descripcion";
    public static final String PROPUESTA_TIPO_SERVICIO_ID = "tipo_servicio_id";
    public static final String PROPUESTA_DISPONIBILIDAD = "disponibilidad";
    public static final String PROPUESTA_CALIFICACION_PROMEDIO = "calificacion_promedio";
    public static final String CREATE_TABLE_PROPUESTA =
            "CREATE TABLE " + TABLE_PROPUESTA + "("
                    + PROPUESTA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + PROPUESTA_USUARIO_ID + " INTEGER NOT NULL,"
                    + PROPUESTA_TITULO + " TEXT NOT NULL,"
                    + PROPUESTA_PRECIO + " REAL NOT NULL,"
                    + PROPUESTA_DESCRIPCION + " TEXT,"
                    + PROPUESTA_TIPO_SERVICIO_ID + " INTEGER,"
                    + PROPUESTA_DISPONIBILIDAD + " INTEGER DEFAULT 0,"
                    + PROPUESTA_CALIFICACION_PROMEDIO + " INTEGER DEFAULT 0,"
                    + "FOREIGN KEY (" + PROPUESTA_USUARIO_ID + ") REFERENCES " + TABLE_USUARIO + "(" + USUARIO_ID + "),"
                    + "FOREIGN KEY (" + PROPUESTA_TIPO_SERVICIO_ID + ") REFERENCES " + TABLE_TIPO_SERVICIO + "(" + TIPO_SERVICIO_ID + ")"
                    + ")";

    // Constantes para la tabla solicitud
    public static final String TABLE_SOLICITUD = "solicitud";
    public static final String SOLICITUD_ID = "id";
    public static final String SOLICITUD_USUARIO_ID = "usuario_id";
    public static final String SOLICITUD_PROPUESTA_ID = "propuesta_id";
    public static final String SOLICITUD_MENSAJE = "mensaje";
    public static final String SOLICITUD_ESTADO = "estado";
    public static final String CREATE_TABLE_SOLICITUD =
            "CREATE TABLE " + TABLE_SOLICITUD + "("
                    + SOLICITUD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + SOLICITUD_USUARIO_ID + " INTEGER NOT NULL,"
                    + SOLICITUD_PROPUESTA_ID + " INTEGER NOT NULL,"
                    + SOLICITUD_MENSAJE + " TEXT,"
                    + SOLICITUD_ESTADO + " TEXT DEFAULT 'ENVIADA' CHECK (" + SOLICITUD_ESTADO + " IN ('ENVIADA', 'ACEPTADA', 'RECHAZADA', 'FINALIZADA')),"
                    + "FOREIGN KEY (" + SOLICITUD_USUARIO_ID + ") REFERENCES " + TABLE_USUARIO + "(" + USUARIO_ID + "),"
                    + "FOREIGN KEY (" + SOLICITUD_PROPUESTA_ID + ") REFERENCES " + TABLE_PROPUESTA + "(" + PROPUESTA_ID + ")"
                    + ")";

    // Constantes para la tabla calificacion
    public static final String TABLE_CALIFICACION = "calificacion";
    public static final String CALIFICACION_ID = "id";
    public static final String CALIFICACION_SOLICITUD_ID = "solicitud_id";
    public static final String CALIFICACION_CLIENTE_ID = "cliente_id";
    public static final String CALIFICACION_PUNTUACION = "puntuacion";
    public static final String CALIFICACION_COMENTARIO = "comentario";
    public static final String CREATE_TABLE_CALIFICACION =
            "CREATE TABLE " + TABLE_CALIFICACION + "("
                    + CALIFICACION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + CALIFICACION_SOLICITUD_ID + " INTEGER NOT NULL,"
                    + CALIFICACION_CLIENTE_ID + " INTEGER NOT NULL,"
                    + CALIFICACION_PUNTUACION + " INTEGER CHECK (" + CALIFICACION_PUNTUACION + " BETWEEN 1 AND 5),"
                    + CALIFICACION_COMENTARIO + " TEXT,"
                    + "FOREIGN KEY (" + CALIFICACION_SOLICITUD_ID + ") REFERENCES " + TABLE_SOLICITUD + "(" + SOLICITUD_ID + "),"
                    + "FOREIGN KEY (" + CALIFICACION_CLIENTE_ID + ") REFERENCES " + TABLE_USUARIO + "(" + USUARIO_ID + ")"
                    + ")";

    // **** INICIO DE LA ADICIÓN DE CÓDIGO ****
    // Una sola instancia de SQLiteDatabase para toda la aplicación
    private SQLiteDatabase databaseInstance;

    // Constructor
    public Conexion(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Sobrescribir getWritableDatabase para gestionar la instancia
    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        if (databaseInstance == null || !databaseInstance.isOpen()) {
            databaseInstance = super.getWritableDatabase();
        }
        return databaseInstance;
    }

    // Sobrescribir getReadableDatabase para gestionar la instancia
    @Override
    public synchronized SQLiteDatabase getReadableDatabase() {
        if (databaseInstance == null || !databaseInstance.isOpen()) {
            databaseInstance = super.getReadableDatabase();
        }
        return databaseInstance;
    }

    // Método para cerrar la base de datos de forma segura
    @Override
    public synchronized void close() {
        if (databaseInstance != null && databaseInstance.isOpen()) {
            databaseInstance.close();
            databaseInstance = null; // Limpiar la referencia
        }
        super.close(); // Llama al método close() de la clase padre
    }
    // **** FIN DE LA ADICIÓN DE CÓDIGO ****

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Ejecuta las sentencias CREATE TABLE aquí
        db.execSQL(CREATE_TABLE_USUARIO);
        db.execSQL(CREATE_TABLE_DISTRITO);
        // Insertar distritos por defecto
        String[] distritos = {
                "Ancón", "Ate", "Barranco", "Breña", "Carabayllo", "Cercado de Lima", "Chaclacayo", "Chorrillos",
                "Cieneguilla", "Comas", "El agustino", "Independencia", "Jesús maría", "La molina", "La victoria",
                "Lince", "Los olivos", "Lurigancho", "Lurín", "Magdalena del mar", "Miraflores", "Pachacámac",
                "Pucusana", "Pueblo libre", "Puente piedra", "Punta hermosa", "Punta negra", "Rímac", "San bartolo",
                "San borja", "San isidro", "San Juan de Lurigancho", "San Juan de Miraflores", "San Luis",
                "San Martin de Porres", "San Miguel", "Santa Anita", "Santa María del Mar", "Santa Rosa",
                "Santiago de Surco", "Surquillo", "Villa el Salvador", "Villa Maria del Triunfo"
        };

        for (String nombre : distritos) {
            db.execSQL("INSERT INTO " + TABLE_DISTRITO + " (" + DISTRITO_NOMBRE + ") VALUES ('" + nombre + "')");
        }
        db.execSQL(CREATE_TABLE_PERFIL);
        db.execSQL(CREATE_TABLE_TIPO_SERVICIO);
        // Insertar tipos de servicio por defecto
        String[] tiposServicio = {
                "Fontanería", "Pintura", "Electricidad", "Carpintería", "Gasfitería",
                "Jardinería", "Limpieza", "Cerrajería", "Albañilería", "Otros"
        };

        for (String servicio : tiposServicio) {
            db.execSQL("INSERT INTO " + TABLE_TIPO_SERVICIO + " (" + TIPO_SERVICIO_NOMBRE + ") VALUES ('" + servicio + "')");
        }
        db.execSQL(CREATE_TABLE_PROPUESTA);
        db.execSQL(CREATE_TABLE_SOLICITUD);
        db.execSQL(CREATE_TABLE_CALIFICACION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Lógica para actualizar la base de datos si cambia la versión
        // Por ejemplo, podrías eliminar las tablas antiguas y volver a crearlas
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALIFICACION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SOLICITUD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROPUESTA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIPO_SERVICIO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERFIL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISTRITO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIO);
        onCreate(db);
    }

}