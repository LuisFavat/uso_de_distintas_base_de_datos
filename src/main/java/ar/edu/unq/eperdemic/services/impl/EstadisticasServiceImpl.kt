package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.ReporteDeContagios
import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.persistencia.dao.EstadisticaDAO
import ar.edu.unq.eperdemic.services.EstadisticasService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx


class EstadisticasServiceImpl (var estadisticaDAO : EstadisticaDAO): EstadisticasService {

    override fun especieLider(): Especie {


        return runTrx {  estadisticaDAO.especieLider() }

    }

    override fun lideres(): List<Especie> {
        return runTrx {  estadisticaDAO.lideres() }
    }

    override fun reporteDeContagios(ubicacionId: Long): ReporteDeContagios {
        return runTrx {  estadisticaDAO.reporteDeContagios(ubicacionId) }
    }



}