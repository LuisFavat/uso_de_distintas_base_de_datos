package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner

class HibernatePatogenoDAO : HibernateDAO<Patogeno> (Patogeno::class.java), PatogenoDAO{

    override fun crear(patogeno: Patogeno): Patogeno {
        if (patogeno.id == null)
        {
            super.guardar(patogeno)
        }
        else
        {
            actualizar(patogeno)
        }
        return patogeno
    }

    override fun actualizar(patogeno: Patogeno) {
        val session = TransactionRunner.currentSession

        var patogenoOld = session.get(Patogeno::class.java, patogeno.id)

        patogenoOld.cantidadDeEspecies = patogeno.cantidadDeEspecies

        session.save(patogenoOld)

    }



    override fun recuperarATodos(): List<Patogeno> {
        val session = TransactionRunner.currentSession

        val hql = "from Patogeno "

        val query = session.createQuery(hql, Patogeno::class.java)

        return query.resultList
    }

    override fun especiesDePatogeno(patogenoId: Long): List<Especie>{

        val session = TransactionRunner.currentSession

        val hql = "from Especie  where patogeno_id = : patogenoId"

        val query = session.createQuery(hql, Especie::class.java)
        query.setParameter("patogenoId", patogenoId)

        return query.resultList
    }

}