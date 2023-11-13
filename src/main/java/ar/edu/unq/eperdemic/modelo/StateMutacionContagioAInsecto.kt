package ar.edu.unq.eperdemic.modelo

import javax.persistence.Entity

@Entity
class StateMutacionContagioAInsecto(nombre : String, modificador : Int) : Mutacion(nombre, modificador) {
    override fun modificarAtributos(patogeno: Patogeno): Patogeno {
        patogeno.capacidadDeContagioAInsecto += modificador
        return patogeno
    }
}