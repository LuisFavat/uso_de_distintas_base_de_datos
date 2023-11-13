package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import redis.clients.jedis.GeoCoordinate

interface UbicacionDAO {
    fun crear(nombreUbicacion : String, latitud: Double, longitud: Double): Ubicacion
    fun recuperar(idUbicacion : Long?): Ubicacion
    fun recuperarTodos(): List<Ubicacion>
    fun recuperarNombre(ubicacionId : Long) : String
}