package com.escudoantiestafas.app.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class ReportePendiente(val id: Long, val numero: String, val categoria: String, val intentos: Int)

/**
 * Cola de reportes que no se pudieron enviar (sin internet, backend caído).
 * Sin esto, un reporte fallido se perdía en silencio para siempre.
 */
interface ColaDeReportes {
    fun encolar(numero: String, categoria: String)
    fun listarPendientes(): List<ReportePendiente>
    fun eliminar(id: Long)
    fun incrementarIntentos(id: Long)
}

/**
 * Implementación respaldada por SQLite (vía SQLiteOpenHelper, sin Room
 * para no agregar una dependencia pesada a un problema de una sola tabla).
 */
class ColaDeReportesSqlite(context: Context) :
    SQLiteOpenHelper(context.applicationContext, DB_NAME, null, DB_VERSION), ColaDeReportes {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE reportes_pendientes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                numero TEXT NOT NULL,
                categoria TEXT NOT NULL,
                intentos INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS reportes_pendientes")
        onCreate(db)
    }

    override fun encolar(numero: String, categoria: String) {
        val valores = ContentValues().apply {
            put("numero", numero)
            put("categoria", categoria)
        }
        writableDatabase.insert("reportes_pendientes", null, valores)
    }

    override fun listarPendientes(): List<ReportePendiente> {
        val pendientes = mutableListOf<ReportePendiente>()
        readableDatabase.rawQuery("SELECT id, numero, categoria, intentos FROM reportes_pendientes", null).use { cursor ->
            while (cursor.moveToNext()) {
                pendientes += ReportePendiente(
                    id = cursor.getLong(0),
                    numero = cursor.getString(1),
                    categoria = cursor.getString(2),
                    intentos = cursor.getInt(3),
                )
            }
        }
        return pendientes
    }

    override fun eliminar(id: Long) {
        writableDatabase.delete("reportes_pendientes", "id = ?", arrayOf(id.toString()))
    }

    override fun incrementarIntentos(id: Long) {
        writableDatabase.execSQL("UPDATE reportes_pendientes SET intentos = intentos + 1 WHERE id = ?", arrayOf(id))
    }

    companion object {
        private const val DB_NAME = "reportes_pendientes.db"
        private const val DB_VERSION = 1
    }
}
