package br.com.vertice.emerion_dashboard.domain.customercredit.exception

class CustomerCreditNotFoundException(id: Long) : RuntimeException("CustomerCredit with id $id not found")
