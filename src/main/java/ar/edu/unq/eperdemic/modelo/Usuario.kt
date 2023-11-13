package ar.edu.unq.eperdemic.modelo

import java.sql.Date
import java.sql.Timestamp
import javax.persistence.*
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder

@Entity
class Usuario (var nombre: String,
               @Column(unique = true)
               var nombreDeUsuario : String,
               var contraseña : String,
               var correoElectronico : String){

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null

    //ultimo inicio de sesion
    var ultimoLogeo : String? = null



    constructor() : this( "", "","","" )



}