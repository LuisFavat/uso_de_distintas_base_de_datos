package ar.edu.unq.eperdemic.modelo

import redis.clients.jedis.GeoCoordinate
import java.io.Serializable
import javax.persistence.*

@Entity

class Ubicacion(
    @Column(unique = true)
    var nombre: String,
    var latitud: Double,
    var longitud: Double){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null;



}




