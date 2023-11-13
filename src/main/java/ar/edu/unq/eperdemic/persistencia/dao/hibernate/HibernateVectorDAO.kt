package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.services.runner.TransactionRunner

open class HibernateVectorDAO : HibernateDAO<Vector>(Vector::class.java), VectorDAO {

    override fun crear(vector: Vector): Vector {
        guardar(vector)
        return vector
    }

    override fun actualizar(vector: Vector) {
        val session = TransactionRunner.currentSession

        val vectorOld = recuperar(vector.id)
        vectorOld.enfermedades = vector.enfermedades

        session.update(vectorOld)
    }

    override fun actualizar(vectorId: Long, ubicacion : Ubicacion) {
        val session = TransactionRunner.currentSession

        val vectorOld = recuperar(vectorId)
        vectorOld.ubicacion = ubicacion

        session.update(vectorOld)
    }

    override fun recuperarATodos(): List<Vector> {
        val session = TransactionRunner.currentSession

        val hql = "from Vector"

        val query = session.createQuery(hql, Vector::class.java)

        return query.resultList
    }

    override fun recuperarEnfermedades(idDelVector: Long): List<Especie> {
        val session = TransactionRunner.currentSession

        val hql = "select Especie from Vector vector join vector.enfermedades Especie where vector.id = :idDelVector"


        val query = session.createQuery(hql, Especie::class.java)
        query.setParameter("idDelVector", idDelVector)

        return query.resultList
    }



    override fun recuperarVectoresPorID(ubicacionId: Long): List<Vector> {

        val session = TransactionRunner.currentSession
        val query = session.createQuery( "select vector from Vector vector where vector.ubicacion.id = :ubicacionId"  , Vector::class.java)
        query.setParameter("ubicacionId", ubicacionId)
        return query.resultList

    }
}