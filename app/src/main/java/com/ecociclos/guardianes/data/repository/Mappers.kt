package com.ecociclos.guardianes.data.repository

import com.ecociclos.guardianes.data.local.entity.*
import com.ecociclos.guardianes.domain.model.*

internal fun BiomaEntity.aDominio(
    estado: EstadoModulo,
    porcentaje: Int,
    totalNiveles: Int,
    nivelesCompletados: Int
): Bioma = Bioma(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    tipoCiclo = TipoCiclo.valueOf(tipoCiclo),
    orden = orden,
    colorHex = colorHex,
    estado = estado,
    porcentajeRestaurado = porcentaje,
    totalNiveles = totalNiveles,
    nivelesCompletados = nivelesCompletados
)

internal fun NivelEntity.aDominio(
    estado: EstadoModulo,
    estrellas: Int,
    intentos: Int
): Nivel = Nivel(
    id = id,
    biomaId = biomaId,
    orden = orden,
    titulo = titulo,
    tipoReto = TipoReto.valueOf(tipoReto),
    dificultad = dificultad,
    instruccion = instruccion,
    estado = estado,
    estrellas = estrellas,
    intentos = intentos
)

internal fun ElementoNivelEntity.aDominio(): ElementoNivel = ElementoNivel(
    id = id,
    nivelId = nivelId,
    clave = clave,
    etiqueta = etiqueta,
    esOrigen = esOrigen,
    destinoCorrectoClave = destinoCorrectoClave
)

internal fun NodoRutaEntity.aDominio(): NodoRuta = NodoRuta(
    id = id,
    nivelId = nivelId,
    clave = clave,
    etiqueta = etiqueta,
    tipoRecurso = TipoRecurso.valueOf(tipoRecurso),
    esFuente = esFuente,
    destinoCorrectoClave = destinoCorrectoClave
)

internal fun VariableLabEntity.aDominio(): VariableLab = VariableLab(
    id = id,
    nivelId = nivelId,
    nombre = nombre,
    valorMin = valorMin,
    valorMax = valorMax,
    valorInicial = valorInicial,
    umbralCritico = umbralCritico,
    mensajeNormal = mensajeNormal,
    mensajeCritico = mensajeCritico
)

internal fun CartaEntity.aDominio(desbloqueada: Boolean): Carta = Carta(
    id = id,
    nombre = nombre,
    tipo = TipoCarta.valueOf(tipo),
    biomaId = biomaId,
    descripcion = descripcion,
    nivelDesbloqueoId = nivelDesbloqueoId,
    desbloqueada = desbloqueada
)

internal fun InsigniaEntity.aDominio(obtenida: Boolean): Insignia = Insignia(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    biomaId = biomaId,
    obtenida = obtenida
)

internal fun PerfilEntity.aDominio(): Perfil = Perfil(
    id = id,
    alias = alias,
    avatarId = avatarId,
    xpTotal = xpTotal
)
