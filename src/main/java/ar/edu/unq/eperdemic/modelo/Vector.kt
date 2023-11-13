package ar.edu.unq.eperdemic.modelo

import javax.persistence.*

@Entity
class Vector( @Enumerated(EnumType.ORDINAL)
              var tipo: TipoDeVector,
              ubicacion : Ubicacion) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @ManyToOne
    var ubicacion: Ubicacion = ubicacion

    @ManyToMany(fetch =FetchType.EAGER)
    var enfermedades : MutableSet<Especie> = mutableSetOf()

    fun agregarEnfermedad(enfermedad : Especie) {
        this.enfermedades.add(enfermedad)
        enfermedad.manejarInfectado(this)
    }

    fun agregarEnfermedadSiPorcentajeExitoso(enfermedad: Especie): Boolean {
            var chanceDeContagio = (1..100).random()
            var porcentajeDeContagiosExitoso = obtenerPorcentajeDeContagiosExitoso(this, enfermedad)
            if (porcentajeDeContagiosExitoso > chanceDeContagio || porcentajeDeContagiosExitoso == 100) {
                agregarEnfermedad(enfermedad)
                return true
            }
        return false
    }

    private fun obtenerPorcentajeDeContagiosExitoso(vector:Vector, enfermedad: Especie) : Int {
        var factorDeContagios = 0
        when (vector.tipo.name) {
            "Persona" -> factorDeContagios = enfermedad.patogeno.capacidadDeContagioAPersona
            "Insecto" -> factorDeContagios = enfermedad.patogeno.capacidadDeContagioAInsecto
            "Animal" -> factorDeContagios = enfermedad.patogeno.capacidadDeContagioAAnimal
        }
        var porcentajeDeContagioExitoso = ((1..10).random()) * factorDeContagios
        return porcentajeDeContagioExitoso
    }
    fun esContagioValido(otroVector: Vector) : Boolean {
        var ret = false
        when (otroVector.tipo.name) {
            "Persona" -> ret = tipo.name != "Animal"
            "Animal" -> ret = tipo.name == "Insecto" ||  tipo.name == "Persona"
            "Insecto" -> ret = tipo.name != "Insecto"
        }
        return ret
    }
}

enum class TipoDeVector {
    Persona, Insecto, Animal
}