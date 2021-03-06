package space.degtiv.voidwarp.service

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import space.degtiv.voidwarp.domain.Player
import space.degtiv.voidwarp.repository.PlayerRepository
import space.degtiv.voidwarp.security.Role
import java.lang.IllegalArgumentException

@Service
class PlayerService(
    val playerRepository: PlayerRepository,
    val passwordEncoder: PasswordEncoder
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        return playerRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("Player with username = $username not found")
    }

    fun addAndEnablePlayer(username: String, password: String, role: String?): Player {
        if (playerRepository.findByUsername(username) != null)
            throw IllegalArgumentException("Player with username = '$username' already exists")

        val player = Player(username, passwordEncoder.encode(password))
        player.isActive = true
        if (role != null) {
            val foundRole = Role.values().firstOrNull { it.name.equals(role, true) }
            if (foundRole != null) {
                player.authorities = foundRole.getGrantedAuthorities()
            }
        }
        savePlayer(player)

        return player
    }

    fun getPlayerById(uuid: String): Player? {
        return playerRepository.findByUuid(uuid)
    }

    fun savePlayer(player: Player) {
        playerRepository.save(player)
    }

    fun deletePlayerById(uuid: String) {
        val player = getPlayerById(uuid) ?: throw IllegalArgumentException("Player with uuid = $uuid not found")
        playerRepository.delete(player)
    }

    fun getAllPlayers(): Iterable<Player?> {
        println("players:" + playerRepository.findAll().toString())
        return playerRepository.findAll()
    }

}