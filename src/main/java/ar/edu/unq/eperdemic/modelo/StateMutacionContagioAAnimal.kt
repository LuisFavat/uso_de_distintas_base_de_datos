package ar.edu.unq.eperdemic.modelo

import javax.persistence.Entity

@Entity
class StateMutacionContagioAAnimal(nombre : String, modificador : Int) : Mutacion(nombre, modificador) {
    override fun modificarAtributos(patogeno: Patogeno): Patogeno {
        patogeno.capacidadDeContagioAAnimal += modificador
        return patogeno
    }
}