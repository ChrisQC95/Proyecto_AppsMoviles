package com.example.proyecto_gestortrabajadoresinformales.consultas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues; // Necesario si insertas datos iniciales (distritos, tipos de servicio)
import android.util.Log; // Necesario para logs

public class Conexion extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mi_app.db";
    // ¡IMPORTANTE! Incrementa esta versión cada vez que cambies el esquema de la base de datos.
    // Debe ser MAYOR que la última versión que se instaló en el emulador/dispositivo.
    private static final int DATABASE_VERSION = 1; // <<-- ¡VERSIÓN DE LA BD INCREMENTADA!

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
                    + DISTRITO_NOMBRE + " TEXT NOT NULL UNIQUE"
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
                    + PROPUESTA_CALIFICACION_PROMEDIO + " REAL DEFAULT 0.0,"
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

    public Conexion(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USUARIO);
        db.execSQL(CREATE_TABLE_DISTRITO);
        db.execSQL(CREATE_TABLE_PERFIL);
        db.execSQL(CREATE_TABLE_TIPO_SERVICIO);
        db.execSQL(CREATE_TABLE_PROPUESTA);
        db.execSQL(CREATE_TABLE_SOLICITUD);
        db.execSQL(CREATE_TABLE_CALIFICACION);
        Log.d("Conexion", "Todas las tablas creadas.");

        // Insertar distritos por defecto
        insertarDistritosIniciales(db);
        // Insertar tipos de servicio por defecto
        insertarTiposServicioIniciales(db);
        // NOTA: Se eliminó la llamada a insertarUsuarioPrueba(db) aquí
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("Conexion", "Actualizando base de datos de la versión " + oldVersion + " a " + newVersion + ". Esto eliminará los datos existentes.");
        // Elimina las tablas en el orden correcto para evitar problemas de FK
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALIFICACION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SOLICITUD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROPUESTA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERFIL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISTRITO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIPO_SERVICIO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIO);

        onCreate(db); // Recrea todas las tablas con el nuevo esquema y vuelve a insertar datos iniciales
        Log.d("Conexion", "Tablas recreadas durante la actualización.");
    }

    // Métodos para insertar datos iniciales (útiles para pruebas y datos estáticos)
    private void insertarDistritosIniciales(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        String[] distritos = {
                "Ancón", "Ate", "Barranco", "Breña", "Carabayllo", "Cercado de Lima", "Chaclacayo", "Chorrillos",
                "Cieneguilla", "Comas", "El Agustino", "Independencia", "Jesús María", "La Molina", "La Victoria",
                "Lince", "Los Olivos", "Lurigancho", "Lurín", "Magdalena del Mar", "Miraflores", "Pachacámac",
                "Pucusana", "Pueblo Libre", "Puente Piedra", "Punta Hermosa", "Punta Negra", "Rímac", "San Bartolo",
                "San Borja", "San Isidro", "San Juan de Lurigancho", "San Juan de Miraflores", "San Luis",
                "San Martín de Porres", "San Miguel", "Santa Anita", "Santa María del Mar", "Santa Rosa",
                "Santiago de Surco", "Surquillo", "Villa El Salvador", "Villa María del Triunfo"
        };
        for (String nombre : distritos) {
            values.clear(); // Limpiar valores anteriores
            values.put(DISTRITO_NOMBRE, nombre);
            db.insert(TABLE_DISTRITO, null, values);
        }
        Log.d("Conexion", "Distritos iniciales insertados.");
    }

    private void insertarTiposServicioIniciales(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        String[] tiposServicio = {
                "Fontanería", "Pintura", "Electricidad", "Carpintería", "Gasfitería",
                "Jardinería", "Limpieza", "Cerrajería", "Albañilería", "Otros"
        };
        for (String servicio : tiposServicio) {
            values.clear(); // Limpiar valores anteriores
            values.put(TIPO_SERVICIO_NOMBRE, servicio);
            db.insert(TABLE_TIPO_SERVICIO, null, values);
        }
        Log.d("Conexion", "Tipos de servicio iniciales insertados.");
    }
}
