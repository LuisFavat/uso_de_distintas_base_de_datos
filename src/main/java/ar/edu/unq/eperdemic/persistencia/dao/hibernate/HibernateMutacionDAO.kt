package ar.edu.unq.eperdemic.persistencia.dao.hibernate;

import ar.edu.unq.eperdemic.modelo.Mutacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.MutacionDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner

open class HibernateMutacionDAO : HibernateDAO<Mutacion>(Mutacion::class.java), MutacionDAO {

    override fun crear(mutacion: Mutacion): Mutacion {
        guardar(mutacion)
        return mutacion
    }

    override fun recuperarTodos(): List<Mutacion> {
        val session = TransactionRunner.currentSession

        val hql = "from Mutacion"

        val query = session.createQuery(hql, Mutacion::class.java)

        return query.resultList
    }
}
