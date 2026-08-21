package br.com.vertice.emerion_dashboard.application.ipi.ingestion.model

data class IngestIpiBatchCommand(val batchId: String, val items: List<IngestIpiCommand>)
