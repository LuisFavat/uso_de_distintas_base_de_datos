package ar.edu.unq.eperdemic.spring.configuration

import ar.edu.unq.eperdemic.persistencia.dao.*
import ar.edu.unq.eperdemic.persistencia.dao.hibernate.*
import ar.edu.unq.eperdemic.persistencia.dao.mongo.MongoEventoDAO
import ar.edu.unq.eperdemic.services.*
import ar.edu.unq.eperdemic.services.impl.*
import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.redis.RedisUbicacionDAO
import jdk.jfr.Event
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfiguration {

    @Bean
    fun groupName() : String {
        val groupName :String?  = System.getenv()["GROUP_NAME"]
        return "" //groupName!!
    }

    @Bean
    fun patogenoDAO(): PatogenoDAO {
        return HibernatePatogenoDAO()
    }

    @Bean
    fun redisUbicacionDAO(): RedisUbicacionDAO {
        return RedisUbicacionDAO()
    }

    @Bean
    fun especieDAO(): EspecieDAO {
        return HibernateEspecieDAO()
    }

    @Bean
    fun estadisticaDAO(): EstadisticaDAO {
        return HibernateEstadisticaDAO()
    }

    @Bean
    fun ubicacionDAON4j(): Neo4jUbicacionDAO {
        return Neo4jUbicacionDAO(vector())
    }

    @Bean
    fun ubicacionDAO(): UbicacionDAO {
        return HibernateUbicacionDAO()
    }

    @Bean
    fun vector(): VectorDAO {
        return HibernateVectorDAO()
    }

    @Bean
    fun mutacionDAO(): MutacionDAO {
        return HibernateMutacionDAO()
    }

    @Bean
    fun eventoDAO(): EventoDAO {
        return MongoEventoDAO(especieDAO(), ubicacionDAO())
    }

    //modificado @pk
    @Bean
    fun patogenoService(eventoDAO: EventoDAO,patogenoDAO: PatogenoDAO, ubicacionService : UbicacionServiceImpl, especieDAO: HibernateEspecieDAO): PatogenoService {
        return PatogenoServiceImpl(eventoDAO,patogenoDAO, ubicacionService, especieDAO)
    }

    @Bean
    fun estadisticasServices(estadisticaDAO : EstadisticaDAO): EstadisticasService {
        return EstadisticasServiceImpl(estadisticaDAO)
    }

    @Bean
    fun mutacionService(mutacionDAO : MutacionDAO, especieDAO: EspecieDAO, patogenoDAO: PatogenoDAO, eventoDAO: EventoDAO): MutacionService {
        return MutacionServiceImpl(mutacionDAO, especieDAO, patogenoDAO, eventoDAO)
    }

    @Bean

    fun ubicacionService(redisDAO: RedisUbicacionDAO,ubicacionConectadasDAO : Neo4jUbicacionDAO, ubicacionDAO: UbicacionDAO, vectorService : VectorService, vectorDAO: VectorDAO,especieDAO : EspecieDAO,especieService: EspecieService,  eventoDAO: EventoDAO): UbicacionService {
        return UbicacionServiceImpl(redisDAO,ubicacionConectadasDAO, ubicacionDAO, vectorService, vectorDAO, especieDAO,especieService,eventoDAO)
    }

    @Bean
    fun vectorService(vectorDAO : VectorDAO, ubicacionDAO: UbicacionDAO, especieDAO: EspecieDAO, eventoDAO: EventoDAO): VectorService {
        return VectorServiceImpl(vectorDAO,ubicacionDAO,especieDAO,eventoDAO)
    }

    @Bean
    fun especieService(especieDAO: EspecieDAO, eventoDAO: EventoDAO): EspecieService {
        return EspecieServiceImpl(especieDAO, eventoDAO)
    }

}
