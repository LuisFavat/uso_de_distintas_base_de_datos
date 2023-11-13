package ar.edu.unq.eperdemic.modelo

import javax.persistence.Entity

@Entity
class StateMutacionDefensa(nombre : String, modificador : Int) : Mutacion(nombre, modificador) {
    override fun modificarAtributos(patogeno: Patogeno): Patogeno {
        patogeno.defensa += modificador
        return patogeno
    }
}