package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.EventoDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateEspecieDAO
import ar.edu.unq.eperdemic.services.EspecieService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import java.sql.Timestamp
import java.time.LocalDateTime

class EspecieServiceImpl(val especieDAO: EspecieDAO, var eventoDAO: EventoDAO) : EspecieService {

    override fun cantidadDeInfectados(especieId: Long): Int {
        return runTrx { especieDAO.cantidadDeInfectados(especieId) }
    }
    override fun recuperar(id: Long): Especie {
        return runTrx { especieDAO.recuperar(id) }
    }

    override fun recuperarTodos(): List<Especie> {
        return runTrx {especieDAO.recuperarTodos()}
    }

    override fun esPandemia(especieId: Long): Boolean {

        return runTrx {especieDAO.esPandemia(especieId)}
    }
}