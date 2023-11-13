package ar.edu.unq.eperdemic.persistencia.dao.hibernate

import ar.edu.unq.eperdemic.modelo.Usuario
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder
import java.sql.Timestamp

class UsuarioDAO (val encoder: Pbkdf2PasswordEncoder): HibernateDAO<Usuario>(Usuario::class.java) {

    fun crear(usuario: Usuario): Usuario {
        //usuario.contraseña = encoder.encode(usuario.contraseña)
        guardar(usuario)
        return usuario
    }

    fun recuperar(nombreDeUsuario: String): Usuario
    {
        val session = TransactionRunner.currentSession

        val hql = "from Usuario  where nombreDeUsuario = : unUsuario "


        val query = session.createQuery(hql, Usuario::class.java)
        query.setParameter("unUsuario", nombreDeUsuario)

        var usuario = query.singleResult
        //Por seguridad borro la contraseña
        usuario.contraseña = "encrypt"

        return usuario
    }



    //authentication usuario
    fun authUsuario(nombreUsuario: String, password : String): Boolean {
        val session = TransactionRunner.currentSession

        val hql = "select contraseña from Usuario  where nombreDeUsuario = : unUsuario "


        val query = session.createQuery(hql, String::class.java)
        query.setParameter("unUsuario", nombreUsuario)

        return encoder.matches(password, query.uniqueResult())
    }


    fun actualizar(usuario: Usuario, ultimoLogeo : Timestamp) {
        val session = TransactionRunner.currentSession

        var usuarioOld = session.get(Usuario::class.java, usuario.id)

        usuario.ultimoLogeo = ultimoLogeo.toString()
        usuarioOld.ultimoLogeo = ultimoLogeo.toString()

        session.save(usuarioOld)

    }










}