package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.EventoDAO

import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.redis.RedisUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.redis.RedisUsuarioDAO
import ar.edu.unq.eperdemic.services.EspecieService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import redis.clients.jedis.GeoCoordinate
import java.sql.Timestamp
import java.time.LocalDateTime

class UbicacionNoAlcanzable(message:String): Exception(message)

class UbicacionServiceImpl(var redisDAO: RedisUbicacionDAO, var ubicacionesConectadasDAO : Neo4jUbicacionDAO, var ubicacionDAO: UbicacionDAO, var vectorService: VectorService, var vectorDAO: VectorDAO, val especieDAO: EspecieDAO, val especieService : EspecieService, val eventoDAO: EventoDAO): UbicacionService {

    override fun mover(vectorId: Long, ubicacionid: Long) {

        var menorCamino : List<Ubicacion> = ubicacionesConectadasDAO.menorCamino(vectorId, ubicacionid)

        if (menorCamino.isEmpty()) {
            throw UbicacionNoAlcanzable("No hay caminos validos")
        } else {
            for (ubicacion in menorCamino.drop(1)) {
                runTrx {
                    vectorDAO.actualizar(vectorId, ubicacion)
                    var vector = vectorDAO.recuperar(vectorId)
                    this.correrContagios(ubicacion.id!!, vector)
                    eventoDAO.saveArribo(vector,ubicacion,Timestamp.valueOf(LocalDateTime.now()))
                }
            }
        }

    }

    override fun expandir(ubicacionId: Long) {

        val vectores = this.recuperarVectoresPorID(ubicacionId)
        val vectoresInfectados = vectores.filter { vector -> vectorService.enfermedades(vector.id!!).isNotEmpty() }
        if (!(vectoresInfectados.isEmpty())) {
            val vectorElegido = vectoresInfectados[(0..(vectoresInfectados.size-1)).random()]
            this.correrContagios(ubicacionId, vectorElegido)
        }
    }

    //Colaborador interno
    fun correrContagios(ubicacionid:Long, vectorContagiante:Vector) {

        val vectores = this.recuperarVectoresPorID(ubicacionid)
        val enfermedades = vectorService.enfermedades(vectorContagiante.id!!)

        vectores.forEach { vector ->
            try {
                if (vector.esContagioValido(vectorContagiante) && vector.id != vectorContagiante.id){
                    enfermedades.forEach {
                            enfermedad -> vectorService.infectar(vector.id!!, enfermedad.id!!, vectorContagiante)
                    }
                }
            } catch (e : Exception) {
            }
        }
    }


    fun recuperarVectoresPorID(ubicacionId: Long): List<Vector> {
        return runTrx { vectorDAO.recuperarVectoresPorID(ubicacionId) }
    }


    override fun crear(nombreUbicacion: String, latitud: Double, longitud: Double): Ubicacion {
        //TODO transaccion atomica
        val ubicacion = runTrx {
            ubicacionDAO.crear(nombreUbicacion, latitud, longitud)
        }
        redisDAO.agregarUbicacion(ubicacion)
        ubicacionesConectadasDAO.create(ubicacion)

        return ubicacion
    }

    override fun recuperarTodos(): List<Ubicacion> {
        return runTrx { ubicacionDAO.recuperarTodos() }
    }

    override fun recuperar(id: Long): Ubicacion {
        return runTrx { ubicacionDAO.recuperar(id) }
    }

    fun conectar (idUbicacion1: Long, idUbicacion2: Long, tipoCamino: String)
    {
        ubicacionesConectadasDAO.conectar(idUbicacion1, idUbicacion2,tipoCamino)
    }

    fun conectados(ubicacionId:Long): List<Ubicacion>
    {
        return ubicacionesConectadasDAO.conectados(ubicacionId)
    }

    fun capacidadDeExpansion(vectorId: Long, nombreDeUbicacion:String, movimientos:Int): Int
    {
        return ubicacionesConectadasDAO.capacidadDeExpansion(vectorId, nombreDeUbicacion, movimientos)
    }

    fun menorCamino(vectorId : Long, ubicacionId : Long) : List<Ubicacion>
    {
        return ubicacionesConectadasDAO.menorCamino(vectorId, ubicacionId)
    }
}