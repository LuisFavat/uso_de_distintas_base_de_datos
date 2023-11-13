package ar.edu.unq.eperdemic.spring.controllers.dto

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Mutacion
import ar.edu.unq.eperdemic.modelo.Ubicacion

class EspecieDTO (val id: Long?,
                  val nombre : String,
                  val patogeno: PatogenoDTO,
                  val paisDeOrigen: UbicacionDTO,
                  val adn: Int,
                  val mutaciones: List<String> ){


    companion object {
        fun desdeModelo(especie:Especie): EspecieDTO {

            val patogenoDTO = PatogenoDTO.desdeModelo(especie.patogeno)

            val pais = especie.paisDeOrigen
            val ubicacionDTO = ar.edu.unq.eperdemic.spring.controllers.dto.UbicacionDTO(pais.id, pais.nombre)
            val mutaciones = this.mutacionesToStringList(especie.mutaciones)
            return EspecieDTO(especie.id, especie.nombre, patogenoDTO, ubicacionDTO, especie.adn ,mutaciones)

        }

        fun mutacionesToStringList(mutaciones :MutableSet<Mutacion>): List<String>
        {
            var listaResultado :  MutableSet<String> = mutableSetOf()

            for (mutacion in mutaciones)
            {
                listaResultado.add(mutacion.nombre)
            }

            return listaResultado.toList()
        }
    }


    fun aModelo(): Especie {

        val patogenoModelo = patogeno.aModelo()
        val ubicacion = Ubicacion(paisDeOrigen.nombre, 0.0, 0.0)
        val especie = patogenoModelo.crearEspecie(nombre,ubicacion)
        return  especie
    }



}


