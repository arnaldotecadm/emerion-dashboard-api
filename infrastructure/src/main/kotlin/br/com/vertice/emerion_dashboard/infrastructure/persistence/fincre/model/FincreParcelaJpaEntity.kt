package br.com.vertice.emerion_dashboard.infrastructure.persistence.fincre.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(
    name = "titulo_receber_parcela",
    uniqueConstraints = [UniqueConstraint(name = "uk_titulo_receber_parcela_documento_numero", columnNames = ["documento", "numero_parcela"])],
)
class FincreParcelaJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documento", referencedColumnName = "documento", nullable = false)
    var tituloReceber: FincreTituloReceberJpaEntity? = null,

    @Column(name = "numero_parcela", nullable = false)
    var numeroParcela: Int = 0,

    @Column(name = "flag_incobravel")
    var flagIncobravel: String? = null,

    @Column(name = "data_incobravel")
    var dataIncobravel: LocalDate? = null,

    @Column(name = "data_vencimento")
    var dataVencimento: LocalDate? = null,

    @Column(name = "prazo_em_dias")
    var prazoEmDias: Int? = null,

    @Column(name = "valor_parcela")
    var valorParcela: BigDecimal? = null,

    @Column(name = "numero_bancario")
    var numeroBancario: String? = null,

    @Column(name = "codigo_banco")
    var codigoBanco: String? = null,

    @Column(name = "nome_banco")
    var nomeBanco: String? = null,

    @Column(name = "observacoes")
    var observacoes: String? = null,

    @Column(name = "flag_carta_anuencia")
    var flagCartaAnuencia: String? = null,

    @Column(name = "data_carta_anuencia")
    var dataCartaAnuencia: LocalDate? = null,

    @Column(name = "flag_pago")
    var flagPago: String? = null,
)
