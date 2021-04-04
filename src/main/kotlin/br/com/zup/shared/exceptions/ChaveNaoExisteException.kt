package br.com.zup.shared.exceptions

class ChaveNaoExisteException(
    override val message: String
) : RuntimeException() {}