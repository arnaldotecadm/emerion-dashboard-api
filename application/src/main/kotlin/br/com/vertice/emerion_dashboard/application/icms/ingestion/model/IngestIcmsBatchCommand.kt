package br.com.vertice.emerion_dashboard.application.icms.ingestion.model

data class IngestIcmsBatchCommand(val batchId: String, val items: List<IngestIcmsCommand>)
