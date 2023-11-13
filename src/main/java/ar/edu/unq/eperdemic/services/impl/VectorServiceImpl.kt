package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.EventoDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.services.VectorService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import java.sql.Timestamp
import java.time.LocalDateTime

class VectorServiceImpl(var vectorDAO: VectorDAO, var ubicacionDAO: UbicacionDAO, var especieDAO: EspecieDAO, var eventoDAO: EventoDAO): VectorService {

    override fun infectar(vectorId: Long, especieId: Long, vectorContagiante : Vector?){
        var especie = runTrx { especieDAO.recuperar(especieId) }
        var vector = recuperar(vectorId)
        var contagioExitoso = vector.agregarEnfermedadSiPorcentajeExitoso(especie)

        if (contagioExitoso) {
            if (!eventoDAO.infectoEnEsaUbicacion(especieId,vector.ubicacion.id!!)) {
                eventoDAO.savePrimerContagioEnUbicacion(vectorContagiante,vector,especie,especie.patogeno,Timestamp.valueOf(LocalDateTime.now()))
            } else {
                eventoDAO.saveContagio(vectorContagiante,vector,especie, Timestamp.valueOf(LocalDateTime.now()))
            }
        }

        runTrx {
            vectorDAO.actualizar(vector)
            especieDAO.actualizar(especieId, especie)
        }

        //Verifica si la especie se volvio pandemia, en caso afirmativo guarda el evento
        if (contagioExitoso && runTrx { especieDAO.esPandemia(especie.id!!) }) {
            eventoDAO.saveEsPandemia(especie.patogeno, especie, Timestamp.valueOf(LocalDateTime.now()))
        }


    }

    override fun enfermedades(vectorId: Long): List<Especie> {
        return runTrx { vectorDAO.recuperarEnfermedades(vectorId).toList() }
    }

    override fun crear(tipo: TipoDeVector, ubicacionId: Long): Vector {
        var ubicacion : Ubicacion = runTrx { ubicacionDAO.recuperar(ubicacionId) }
        var vector = Vector(tipo, ubicacion)
        return runTrx { vectorDAO.crear(vector) }
    }

    override fun recuperar(vectorId: Long): Vector {
        return runTrx { vectorDAO.recuperar(vectorId) }
    }

    override fun recuperarTodos(): List<Vector> {
        return runTrx { vectorDAO.recuperarATodos() }
    }

}