package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import net.bytebuddy.implementation.bytecode.Throw
import redis.clients.jedis.GeoCoordinate
import java.lang.IllegalArgumentException


open class HibernateUbicacionDAO : HibernateDAO<Ubicacion>(Ubicacion::class.java),UbicacionDAO  {

    override fun crear(nombreUbicacion: String, latitud: Double, longitud : Double): Ubicacion {
        val otrasUbicaciones = recuperarTodos()
        if (otrasUbicaciones.any { it.nombre == nombreUbicacion}) {
            throw IllegalArgumentException("Ya existe una ubicacion con ese nombre")
        }
        var ubicacion = Ubicacion(nombreUbicacion,latitud, longitud);
        guardar(ubicacion)
        return ubicacion;
    }

    override fun recuperarTodos(): List<Ubicacion> {

        val session = TransactionRunner.currentSession
        val query = session.createQuery( "FROM Ubicacion"  , Ubicacion::class.java)
        return query.resultList;
    }

    override fun recuperarNombre(ubicacionId: Long): String {
        val session = TransactionRunner.currentSession
        val query = session.createQuery( "select ubic.nombre FROM Ubicacion ubic where ubic.id = : ubicacionId"  , String::class.java)
        query.setParameter("ubicacionId", ubicacionId)

        return query.uniqueResult()
    }
}