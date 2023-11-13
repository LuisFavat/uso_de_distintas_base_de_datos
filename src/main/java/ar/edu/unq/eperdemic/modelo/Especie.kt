package ar.edu.unq.eperdemic.modelo

import com.jayway.jsonpath.internal.path.PathCompiler.fail
import javax.persistence.*
import kotlin.reflect.typeOf

@Entity
class Especie(
              @ManyToOne
              var patogeno: Patogeno,
              var nombre: String,
              @ManyToOne
              var paisDeOrigen: Ubicacion) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Long? = null

    var adn = 0

    @ManyToMany(fetch =FetchType.EAGER)
    var mutaciones : MutableSet<Mutacion> = mutableSetOf()

    var contadorPersona : Int = 0

    fun manejarInfectado (vector: Vector) {
        if (vector.tipo.name == "Persona") {
            contadorPersona += 1
            if (contadorPersona == 5) {
                contadorPersona = 0
                adn += 1
            }
        }
    }

    fun agregarMutacion(mutacion: Mutacion) {
        if (mutacion.puedeMutar(this)) {
            mutaciones.add(mutacion)
            patogeno = mutacion.modificarAtributos(patogeno)
            if (adn > mutacion.puntosADN) {
                adn -= mutacion.puntosADN
            } else {
                throw IllegalArgumentException("Puntos de ADN insuficientes " + adn + " - " + mutacion.puntosADN)
            }
        } else {
            throw IllegalArgumentException("No se puede mutar")
        }
    }

    fun mutoPreviamenteAEstaMutacion(m: Mutacion) : Boolean {
        return mutaciones.any{ it.nombre == m.nombre}
    }

}