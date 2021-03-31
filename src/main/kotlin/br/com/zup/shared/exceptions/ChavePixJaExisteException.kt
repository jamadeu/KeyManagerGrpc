package br.com.zup.shared.exceptions

class ChavePixJaExisteException(
    override val message: String
) : RuntimeException() {}