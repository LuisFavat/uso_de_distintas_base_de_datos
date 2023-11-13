package ar.edu.unq.eperdemic.modelo

import javax.persistence.Entity

@Entity
class StateMutacionLetalidad(nombre : String, modificador : Int) : Mutacion(nombre, modificador) {
    override fun modificarAtributos(patogeno: Patogeno): Patogeno {
        patogeno.letalidad += modificador
        return patogeno
    }
}