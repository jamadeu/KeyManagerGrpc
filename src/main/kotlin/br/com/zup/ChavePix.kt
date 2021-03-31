package br.com.zup

import br.com.zup.itau.Conta
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
data class ChavePix(

    @Column(nullable = false, unique = true)
    @field:NotBlank
    var chave: String?,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @field:NotBlank
    var tipoConta: TipoConta?,

    @Column(nullable = false)
    @field:NotBlank
    var idClient: String?,

    @Embedded
    var conta: Conta
) {
    @Id
    @GeneratedValue
    val id: Long? = null

    @Column(nullable = false)
    val criadoEm: LocalDateTime = LocalDateTime.now()
}


