package ar.edu.unq.eperdemic.persistencia.dao.hibernate


import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.PatogenoDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner


class HibernateEspecieDAO : HibernateDAO<Especie> (Especie::class.java), EspecieDAO
{
    override fun cantidadDeInfectados(especieId: Long?): Int
    {
        val session = TransactionRunner.currentSession

        val hql = "select count(Especie) from Vector vector join vector.enfermedades Especie where Especie.id = :especieId"

        val query = session.createQuery(hql, java.lang.Long::class.java)
        query.setParameter("especieId", especieId)

        return query.uniqueResult().toInt()
    }

    override fun recuperarTodos():List<Especie>
    {
        val session = TransactionRunner.currentSession

        val hql = "from Especie "

        val query = session.createQuery(hql, Especie::class.java)

        return query.resultList
    }

    override fun esPandemia (especieId: Long) : Boolean
    {
        val session = TransactionRunner.currentSession

        val hql1 = "select count(Especie) from Vector vector join vector.enfermedades Especie where Especie.id = :especieId"

        val hql2 = "select count(*) from Ubicacion"

        val query1 = session.createQuery(hql1, java.lang.Long::class.java)
        query1.setParameter("especieId", especieId)

        val query2 = session.createQuery(hql2, java.lang.Long::class.java)

        val paisesInfectados = query1.uniqueResult().toInt()
        val paisesTotales    = query2.uniqueResult().toInt()

        return  2 * paisesInfectados > paisesTotales
    }

    override fun cantidadDeEspeciesPresentes(ubicacionId: Long): Int {
        val session = TransactionRunner.currentSession

        val hql = "select count(distinct Especie) from Vector.enfermedades Especie where Vector.Ubicacion.id =: ubicacionId"

        val query = session.createQuery(hql, Int::class.java)
        query.setParameter("ubicacionId", ubicacionId)

        return query.uniqueResult()
    }

    override fun actualizar (especieId: Long, especieActualizada: Especie) {
        val session = TransactionRunner.currentSession

        var especieOld = session.get(Especie::class.java, especieActualizada.id)
        especieOld.adn = especieActualizada.adn
        especieOld.contadorPersona = especieActualizada.contadorPersona

        session.save(especieOld)
    }



}