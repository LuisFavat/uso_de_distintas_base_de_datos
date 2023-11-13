package ar.edu.unq.eperdemic.services.impl


import ar.edu.unq.eperdemic.modelo.Usuario
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.UsuarioDAO
import ar.edu.unq.eperdemic.persistencia.dao.redis.RedisUsuarioDAO
import ar.edu.unq.eperdemic.services.UsuarioService
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import java.sql.Timestamp


class UsuarioServiceImp(val usuarioDAO: UsuarioDAO, val redisDAO : RedisUsuarioDAO) : UsuarioService {

    override fun crear(usuario: Usuario): Usuario {
        val user = runTrx {usuarioDAO.crear(usuario)}
        redisDAO.addUsuario(user)
        return user
    }

    override fun recuperar(nombreDeUsuario: String): Usuario {

        var usuario = Usuario()

        if(redisDAO.existsUsuario(nombreDeUsuario))
        {
            usuario = redisDAO.recuperarUsuario(nombreDeUsuario)
        }
        else
        {
            usuario = runTrx { usuarioDAO.recuperar(nombreDeUsuario) }
        }

        return usuario
    }

    override fun authUsuario(nombreUsuario: String, password: String): Boolean {
        var bool = false

        if(redisDAO.existsUsuario(nombreUsuario))
        {
            bool = redisDAO.authUsuario(nombreUsuario, password)
        }
        else
        {
            bool = runTrx { usuarioDAO.authUsuario(nombreUsuario, password) }
        }

        return bool
    }

    //actualiza cuando se logeo por ultima vez
    override fun actualizar(usuario: Usuario, ultimoLogeo :Timestamp) {

        if(redisDAO.existsUsuario(usuario.nombreDeUsuario))
        {
            redisDAO.actualizar(usuario, ultimoLogeo)
        }
        runTrx { usuarioDAO.actualizar(usuario, ultimoLogeo) }
    }

}