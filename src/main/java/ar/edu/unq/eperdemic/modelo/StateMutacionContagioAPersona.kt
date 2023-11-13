package ar.edu.unq.eperdemic.modelo

import javax.persistence.Entity

@Entity
class StateMutacionContagioAPersona(nombre : String, modificador : Int) : Mutacion(nombre, modificador) {
    override fun modificarAtributos(patogeno: Patogeno): Patogeno {
        patogeno.capacidadDeContagioAPersona += modificador
        return patogeno
    }
}