package com.aseo360.aseo360.servicio.interfaz;

import com.aseo360.aseo360.dto.ComprobanteManualDTO;
import com.aseo360.aseo360.dto.ComprobanteResponseDTO;
import com.aseo360.aseo360.dto.GuiaRemisionDTO;
import com.aseo360.aseo360.modelo.Comprobante;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IComprobanteServicio {

    ComprobanteResponseDTO emitirComprobanteManual(ComprobanteManualDTO dto) throws Exception;

    Page<Comprobante> listarComprobantes(Pageable pageable);

    ComprobanteResponseDTO anularComprobante(Long id) throws Exception;

    ComprobanteResponseDTO consultarEstado(Long id) throws Exception;

    Map<String, Object> emitirGuiaRemision(GuiaRemisionDTO dto) throws Exception;
}
