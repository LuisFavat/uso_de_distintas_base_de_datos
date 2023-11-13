package ar.edu.unq.eperdemic.modelo

import java.io.Serializable
import javax.persistence.*

@Entity//anotation
class Patogeno(tipo : String,
               capacidadDeContagioAPersona: Int,
               capacidadDeContagioAAnimal: Int,
               capacidadDeContagioAInsecto: Int,
               defensa: Int,
               letalidad: Int): Serializable {

    @Column(unique = true)
    var tipo: String = tipo
    var capacidadDeContagioAPersona: Int = capacidadDeContagioAPersona
    set(value) {
        field = establecerValorDentroDelRango(value)
    }
    var capacidadDeContagioAAnimal: Int = capacidadDeContagioAAnimal
        set(value) {
            field = establecerValorDentroDelRango(value)
        }
    var capacidadDeContagioAInsecto: Int = capacidadDeContagioAInsecto
        set(value) {
            field = establecerValorDentroDelRango(value)
        }
    var defensa: Int = defensa
        set(value) {
            field = establecerValorDentroDelRango(value)
        }
    var letalidad: Int = letalidad
        set(value) {
            field = establecerValorDentroDelRango(value)
        }

    init{
        this.capacidadDeContagioAPersona = capacidadDeContagioAPersona
        this.capacidadDeContagioAAnimal = capacidadDeContagioAAnimal
        this.capacidadDeContagioAInsecto = capacidadDeContagioAInsecto
        this.defensa = defensa
        this.letalidad = letalidad
    }



    private fun establecerValorDentroDelRango(valor: Int): Int
    {
        var valorCorrecto : Int = 0

            if(valor >= 1 && valor <= 100)
            {
                valorCorrecto = valor
            }
            else if(valor < 1)
            {
                throw IllegalArgumentException("Valor de atributo por debajo de 1 ")
            }
            else if(valor > 100)
            {
                throw IllegalArgumentException("Valor de atributo por encima de 100 ")
            }
        return valorCorrecto
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null

    var cantidadDeEspecies: Int = 0


    override fun toString(): String {
        return tipo
    }

    fun crearEspecie(nombreEspecie: String, paisDeOrigen: Ubicacion): Especie {
        cantidadDeEspecies++
        return Especie(this, nombreEspecie, paisDeOrigen)
    }
}