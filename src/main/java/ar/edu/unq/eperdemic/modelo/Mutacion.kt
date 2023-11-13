package ar.edu.unq.eperdemic.modelo

import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
abstract class Mutacion(var nombre:String, var modificador: Int) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToMany(fetch =FetchType.EAGER)
    var mutacionesRequeridas: MutableSet<Mutacion> = mutableSetOf()
    var puntosADN: Int = 0

    fun tieneMutacionesRequeridas(especie: Especie) : Boolean {
        return mutacionesRequeridas.all { especie.mutoPreviamenteAEstaMutacion(it) && (it.mutacionesRequeridas.size == 0 || it.tieneMutacionesRequeridas(especie)) }
    }

    fun puedeMutar(especie: Especie): Boolean {
        return especie.adn >= puntosADN && tieneMutacionesRequeridas(especie)
    }

    fun addMutacionRequerida(mutacion: Mutacion) {
        mutacionesRequeridas.add(mutacion)
    }

    abstract fun modificarAtributos(patogeno: Patogeno) : Patogeno

}