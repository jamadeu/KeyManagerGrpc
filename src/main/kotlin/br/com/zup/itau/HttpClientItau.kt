package br.com.zup.itau

import br.com.zup.TipoConta
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("\${itau.url}")
interface HttpClientItau {

    @Get("/api/v1/clientes/{clienteId}/contas{?tipo}")
    fun buscaConta(
        @PathVariable clienteId: String,
        @QueryValue tipo: String
    ): HttpResponse<ContaItauResponse>
}