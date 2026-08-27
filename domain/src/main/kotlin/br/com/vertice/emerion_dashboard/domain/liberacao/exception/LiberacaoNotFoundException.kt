package br.com.vertice.emerion_dashboard.domain.liberacao.exception

class LiberacaoNotFoundException(id: Long) : RuntimeException("Liberacao with id $id not found")
