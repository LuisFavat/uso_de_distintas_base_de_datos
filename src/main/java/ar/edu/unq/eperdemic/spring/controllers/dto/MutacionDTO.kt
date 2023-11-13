package ar.edu.unq.eperdemic.spring.controllers.dto

import ar.edu.unq.eperdemic.modelo.*

enum class Atributo {
    LETALIDAD,
    DEFENSA ,
    FACTOR_ANIMAL,
    FACTOR_INSECTO,
    FACTOR_HUMANO;
}

class MutacionDTO (val id: Long?,
                   val nombre : String,
                   val adnRequeridos: Int,
                   val atributo: Atributo,
                   val cantidad: Int ){


    companion object {
        fun desdeModelo(mutacion:Mutacion): MutacionDTO {
            return MutacionDTO(mutacion.id,mutacion.nombre,mutacion.puntosADN, getAtributo(mutacion), mutacion.modificador)
        }

        private fun getAtributo(mutacion: Mutacion): Atributo {
            lateinit var ret : Atributo
            when(mutacion){
                is StateMutacionLetalidad -> ret = Atributo.LETALIDAD
                is StateMutacionContagioAPersona -> ret = Atributo.FACTOR_HUMANO
                is StateMutacionDefensa -> ret = Atributo.DEFENSA
                is StateMutacionContagioAAnimal -> ret = Atributo.FACTOR_ANIMAL
                is StateMutacionContagioAInsecto -> ret = Atributo.FACTOR_INSECTO
            }
            return ret
        }
    }

    fun aModelo():Mutacion {
        lateinit var mutacion : Mutacion
        when (atributo) {
            Atributo.LETALIDAD -> mutacion = StateMutacionLetalidad(nombre,cantidad)
            Atributo.FACTOR_HUMANO -> mutacion = StateMutacionContagioAPersona(nombre,cantidad)
            Atributo.DEFENSA -> mutacion = StateMutacionDefensa(nombre,cantidad)
            Atributo.FACTOR_ANIMAL -> mutacion = StateMutacionContagioAAnimal(nombre,cantidad)
            Atributo.FACTOR_INSECTO -> mutacion = StateMutacionContagioAInsecto(nombre,cantidad)
        }
        mutacion.puntosADN = adnRequeridos
        return mutacion
    }

}


