package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Mutacion
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.EventoDAO
import ar.edu.unq.eperdemic.persistencia.dao.MutacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.services.MutacionService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import java.sql.Timestamp
import java.time.LocalDateTime

class MutacionServiceImpl(val mutacionDAO : MutacionDAO, val especieDAO: EspecieDAO, val patogenoDAO : PatogenoDAO, val eventoDAO: EventoDAO): MutacionService {

    override fun mutar(especieId: Long, mutacionId: Long): Especie{
        val mutacion = recuperar(mutacionId)


        return runTrx {
            val especie = especieDAO.recuperar(especieId)
            especie.agregarMutacion(mutacion)
            especieDAO.guardar(especie)
            patogenoDAO.actualizar(especie.patogeno)
            eventoDAO.saveMutacion(especie.patogeno, especie, Timestamp.valueOf(LocalDateTime.now()))
            especie
        }
    }

    override fun crear(mutacion: Mutacion): Mutacion {
        return runTrx { mutacionDAO.crear(mutacion) }
    }

    override fun recuperar(mutacionId: Long): Mutacion {
        return runTrx { mutacionDAO.recuperar(mutacionId) }
    }

    override fun recuperarTodos(): List<Mutacion> {
        return runTrx { mutacionDAO.recuperarTodos() }
    }
}