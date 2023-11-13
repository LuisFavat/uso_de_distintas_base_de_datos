package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Evento
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.persistencia.dao.EventoDAO
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateEspecieDAO
import ar.edu.unq.eperdemic.services.PatogenoService
import ar.edu.unq.eperdemic.services.UbicacionService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import java.sql.Timestamp
import java.time.LocalDateTime


class PatogenoServiceImpl(val eventoDAO: EventoDAO,val patogenoDAO: PatogenoDAO, val ubicacionService : UbicacionServiceImpl, val especieDAO : HibernateEspecieDAO) : PatogenoService {

   override fun crear(patogeno: Patogeno): Patogeno {
       return runTrx { patogenoDAO.crear(patogeno) }
    }

    override fun recuperar(id: Long): Patogeno {
        return runTrx { patogenoDAO.recuperar(id) }
    }

    override fun recuperarTodos(): List<Patogeno> {
        return runTrx { patogenoDAO.recuperarATodos() }
    }

    override fun actualizarPatogeno(patogeno: Patogeno){
        return runTrx { patogenoDAO.actualizar(patogeno) }
    }

    override fun agregarEspecie(id: Long, nombre: String,  ubicacionId: Long): Especie {
        val patogeno = recuperar(id)
        val unaUbicacion = ubicacionService.recuperar(ubicacionId)
        val especieNueva = patogeno.crearEspecie(nombre, unaUbicacion)

        return runTrx {
            especieDAO.guardar(especieNueva)
            patogenoDAO.actualizar(patogeno)
            eventoDAO.saveEspecie(patogeno, especieNueva, Timestamp.valueOf(LocalDateTime.now()))
            especieNueva
        }




    }

    fun actualizarEspecie (id: Long, especie:Especie) {
        return runTrx {
            especieDAO.actualizar(id, especie)
        }
    }

    override fun especiesDePatogeno(patogenoId: Long): List<Especie>
    {
        return runTrx {
            patogenoDAO.especiesDePatogeno(patogenoId)
        }
    }

}