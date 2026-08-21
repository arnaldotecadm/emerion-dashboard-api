package br.com.vertice.emerion_dashboard.application.fincre.ingestion.model

data class IngestFincreBatchCommand(val batchId: String, val items: List<IngestFincreCommand>)
