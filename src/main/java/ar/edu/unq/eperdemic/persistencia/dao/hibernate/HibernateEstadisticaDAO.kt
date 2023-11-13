package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.ReporteDeContagios
import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.persistencia.dao.EstadisticaDAO
import ar.edu.unq.eperdemic.services.impl.EspecieServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import com.mysql.cj.x.protobuf.MysqlxExpr

class HibernateEstadisticaDAO (): EstadisticaDAO{

    override fun especieLider(): Especie {

        val session = TransactionRunner.currentSession

        val hql = "select Especie from Vector vector join vector.enfermedades Especie where vector.tipo = :Persona group by Especie Order by count(Especie.id) desc"

        val query = session.createQuery(hql, Especie::class.java)
        query.setParameter("Persona", TipoDeVector.Persona)
        query.maxResults = 1

        return query.singleResult
    }

    override fun lideres(): List<Especie> {

        val session = TransactionRunner.currentSession

        val hql = "select Especie from Vector vector join vector.enfermedades Especie where vector.tipo <> :Insecto group by Especie Order by count(Especie.id) desc"

        val query = session.createQuery(hql, Especie::class.java)
        query.setParameter("Insecto", TipoDeVector.Insecto)
        query.maxResults = 10

        return query.resultList
    }

    override fun reporteDeContagios(ubicacionId: Long): ReporteDeContagios {



        val session = TransactionRunner.currentSession



        val hql1 = "select count(distinct vector) from Vector vector left join vector.enfermedades Especie where vector.ubicacion.id = :ubicacionId"

        val hql2 = "select count(distinct vector) from Vector vector join vector.enfermedades Especie where vector.ubicacion.id = :ubicacionId"

        val hql3 = "select Especie.nombre from Vector vector join vector.enfermedades Especie where vector.ubicacion.id = :ubicacionId  group by Especie Order by count(Especie.id) desc"


        val query1 = session.createQuery(hql1, java.lang.Long::class.java)
        query1.setParameter("ubicacionId", ubicacionId)

        val query2 = session.createQuery(hql2, java.lang.Long::class.java)
        query2.setParameter("ubicacionId", ubicacionId)

        val query3 = session.createQuery(hql3, String::class.java)
        query3.setParameter("ubicacionId", ubicacionId)
        query3.maxResults = 1

        val vectoresPresentes=     query1.uniqueResult().toInt()
        val vectoresInfectados = query2.uniqueResult().toInt()
        var especieMasIfecciosa  = ""

        try {
            especieMasIfecciosa = query3.singleResult
        } catch (e : Exception) {
            especieMasIfecciosa = ""
        }

        var reporteDeContagios = ReporteDeContagios(vectoresPresentes, vectoresInfectados ,especieMasIfecciosa )

        return reporteDeContagios

    }




}