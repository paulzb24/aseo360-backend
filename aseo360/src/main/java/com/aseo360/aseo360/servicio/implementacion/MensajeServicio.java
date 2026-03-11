package com.aseo360.aseo360.servicio.implementacion;

import com.aseo360.aseo360.dto.MensajeEstadoDTO;
import com.aseo360.aseo360.dto.MensajeRegistroDTO;
import com.aseo360.aseo360.dto.MensajeResponseDTO;
import com.aseo360.aseo360.modelo.Cliente;
import com.aseo360.aseo360.modelo.Mensaje;
import com.aseo360.aseo360.repositorio.IMensajeRepositorio;
import com.aseo360.aseo360.servicio.interfaz.IMensajeServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MensajeServicio implements IMensajeServicio {
    private final IMensajeRepositorio mensajeRepositorio;

    @Autowired
    public MensajeServicio(IMensajeRepositorio mensajeRepositorio) {
        this.mensajeRepositorio = mensajeRepositorio;
    }
    @Override
    public List<MensajeResponseDTO> listarMensajes() {
        List<Mensaje> mensajes = this.mensajeRepositorio.findAll();
        List<MensajeResponseDTO> mensajeResponseDTOS = mensajes.stream().map(
                mensaje -> new MensajeResponseDTO(
                        mensaje.getIdMensaje(),
                        mensaje.getCliente().getNombreCompleto(),
                        mensaje.getCliente().getCorreo(),
                        mensaje.getCliente().getDni(),
                        mensaje.getCliente().getNumeroCelular(),
                        mensaje.getCliente().getDireccion(),
                        mensaje.getAsunto(),
                        mensaje.getCuerpo(),
                        mensaje.getFecha(),
                        mensaje.getEstado()
                )
        ).toList();
        return mensajeResponseDTOS;
    }

    @Override
    public Mensaje registrarMensaje(Cliente cliente, MensajeRegistroDTO mensajeRegistroDTO) {
        Mensaje mensaje = new Mensaje();
        LocalDate hoy = LocalDate.now();
        mensaje.setCliente(cliente);
        mensaje.setFecha(hoy);
        mensaje.setAsunto(mensajeRegistroDTO.getAsunto());
        mensaje.setCuerpo(mensajeRegistroDTO.getCuerpo());
        mensaje.setEstado("Enviado");
        return this.mensajeRepositorio.save(mensaje);
    }

    @Override
    public Mensaje modificarEstado(MensajeEstadoDTO mensajeEstadoDTO) throws Exception {
        Mensaje mensaje = this.mensajeRepositorio.findById(mensajeEstadoDTO.getIdMensaje()).orElseThrow(()->new Exception("Error: mensaje no encontrado"));
        mensaje.setEstado(mensajeEstadoDTO.getEstado());
        return this.mensajeRepositorio.save(mensaje);
    }

    @Override
    public void eliminarMensaje(Long id) {
        this.mensajeRepositorio.deleteById(id);
    }
}
