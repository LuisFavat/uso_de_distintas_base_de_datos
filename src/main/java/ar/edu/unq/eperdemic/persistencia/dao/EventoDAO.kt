package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.persistencia.dao.mongo.GenericMongoDAO
import java.sql.Timestamp

interface EventoDAO {
    fun saveEspecie(patogeno : Patogeno, especie : Especie, fecha : Timestamp)
    fun saveMutacion(patogeno : Patogeno, especie : Especie, fecha : Timestamp)
    fun saveEsPandemia(patogeno : Patogeno, especie : Especie, fecha : Timestamp)
    fun savePrimerContagioEnUbicacion(vectorContagiante: Vector?, vectorContagiado: Vector, especie: Especie, patogeno: Patogeno, fecha: Timestamp)
    fun infectoEnEsaUbicacion (idEspecie: Long, idUbicacion: Long): Boolean
    fun saveArribo(vector: Vector, ubicacion: Ubicacion, fecha : Timestamp)
    fun feedPatogeno(patogenoId : Long) : List<Evento>
    fun saveContagio(vectorContagiante: Vector?, vectorContagiado: Vector, especie: Especie, fecha: Timestamp)
    fun feedUbicacion(ubicacionId: Long): List<Evento>
    fun feedVector(vectorId: Long): List<Evento>
}