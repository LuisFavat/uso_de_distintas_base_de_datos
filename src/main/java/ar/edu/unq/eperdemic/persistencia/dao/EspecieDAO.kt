package ar.edu.unq.eperdemic.persistencia.dao

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno

interface EspecieDAO {
    fun guardar(especie : Especie)
    fun recuperar(id : Long?) : Especie
    fun actualizar(id: Long, especie: Especie)
    fun cantidadDeInfectados(especieId: Long?) : Int
    fun recuperarTodos() : List<Especie>
    fun esPandemia (especieId: Long) : Boolean
    fun cantidadDeEspeciesPresentes(ubicacionId : Long) : Int
}