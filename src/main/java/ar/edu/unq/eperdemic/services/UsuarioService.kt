package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Usuario
import java.sql.Timestamp

interface UsuarioService {

    fun crear(usuario: Usuario): Usuario
    fun recuperar(nombreDeUsuario: String): Usuario
    fun authUsuario(nombreUsuario: String, password : String): Boolean
    fun actualizar(usuario: Usuario, ultimoLogeo: Timestamp)
}