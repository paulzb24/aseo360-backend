package com.aseo360.aseo360.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GuiaRemisionDTO {
    private String documento; // "guia_remision_remitente"
    private String serie;
    private String numero;
    private String fecha_de_emision;
    private String hora_de_emision;
    private String modalidad_de_transporte; // "01" público, "02" privado
    private String motivo_de_traslado; // "01" venta, etc.
    private String fecha_inicio_de_traslado;
    private String fecha_entrega_a_transportista;

    // Destinatario
    private String destinatario_tipo_de_documento;
    private String destinatario_numero_de_documento;
    private String destinatario_denominacion;
    private String destinatario_direccion;

    // Partida y Llegada
    private String punto_de_partida_ubigeo;
    private String punto_de_partida_direccion;
    private String punto_de_llegada_ubigeo;
    private String punto_de_llegada_direccion;

    // Pesos y bultos
    private String peso_bruto_total;
    private String peso_bruto_unidad_de_medida; // KGM
    private Integer numero_de_bultos;
    private String observaciones;

    // Relaciones (Opcionales dependiendo de modalidad)
    private List<DocumentoRelacionadoDTO> documentos_relacionados;
    private TransportistaDTO transportista;
    private List<ConductorDTO> conductores;
    private List<VehiculoDTO> vehiculos;

    // Items
    private List<ItemGuiaDTO> items;
}
