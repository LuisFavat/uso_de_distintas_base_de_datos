package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Ubicacion
import redis.clients.jedis.GeoCoordinate


interface UbicacionService {

    fun mover(vectorId: Long, ubicacionid: Long)
    fun expandir(ubicacionId: Long)
    fun recuperar(id:Long): Ubicacion
    fun crear(nombreUbicacion: String,longitud : Double, latitud: Double): Ubicacion
    fun recuperarTodos(): List<Ubicacion>
}