package ar.edu.unq.eperdemic.persistencia.dao.redis

import ar.edu.unq.eperdemic.modelo.Ubicacion
import redis.clients.jedis.GeoCoordinate
import redis.clients.jedis.GeoRadiusResponse
import redis.clients.jedis.GeoUnit
import redis.clients.jedis.Jedis

class RedisUbicacionDAO {

    var jedis : Jedis = Jedis("localhost",6379)

    fun agregarUbicacion(ubicacion: Ubicacion) {
        val map : MutableMap<String, GeoCoordinate> = mutableMapOf()
        map[ubicacion.nombre] = GeoCoordinate(ubicacion.latitud, ubicacion.latitud)

        jedis.geoadd("ubications",map)
    }

    fun getUbicacionesAKmsDeDistancia (ubicacion: Ubicacion, distancia: Double): List<GeoRadiusResponse>  {
        val ubicaciones = jedis.georadius("ubications", ubicacion.longitud,ubicacion.latitud,distancia,GeoUnit.KM)
        return ubicaciones
    }
}