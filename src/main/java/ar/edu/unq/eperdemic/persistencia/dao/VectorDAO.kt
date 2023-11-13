package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.HibernateDAO

interface VectorDAO {
    fun crear(vector: Vector): Vector
    fun actualizar(vector : Vector)
    fun actualizar(vectorId: Long, ubicacion : Ubicacion)
    fun recuperar(vectorId : Long?) : Vector
    fun recuperarATodos() : List<Vector>
    fun recuperarEnfermedades(idDelVector: Long) : List<Especie>
    fun recuperarVectoresPorID(ubicacionId: Long): List<Vector>
}