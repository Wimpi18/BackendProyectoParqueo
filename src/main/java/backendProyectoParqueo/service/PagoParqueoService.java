package backendProyectoParqueo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import backendProyectoParqueo.dto.PagoParqueoDTO;
import backendProyectoParqueo.exception.BusinessException;
import backendProyectoParqueo.model.Cajero;
import backendProyectoParqueo.model.Cliente;
import backendProyectoParqueo.model.PagoParqueo;
import backendProyectoParqueo.model.Tarifa;
import backendProyectoParqueo.repository.PagoParqueoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PagoParqueoService {

    private final PagoParqueoRepository pagoParqueoRepository;
    private final ClienteService clienteService;
    private final TarifaService tarifaService;
    private final CajeroService cajeroService;

    public List<PagoParqueo> findAll() {
        return pagoParqueoRepository.findAll();
    }

    @Transactional
    public PagoParqueo create(PagoParqueoDTO dto) {
        PagoParqueo pagoParqueoEntity = new PagoParqueo();
        pagoParqueoEntity.setMontoPagado(dto.getMontoPagado());
        pagoParqueoEntity.setFechaHoraPago(dto.getFechaHoraPago());
        pagoParqueoEntity.setMeses(dto.getMeses());

        Cliente cliente = clienteService.findById(dto.getIdCliente());
        Tarifa tarifa = tarifaService.findTarifaByTipoClienteYVehiculo(cliente.getTipo(),
                cliente.getParqueos().getTipo());

        Cajero cajero = null;
        if (dto.getIdCajero() != null)
            cajero = cajeroService.findById(dto.getIdCajero());

        BigDecimal montoEsperado = tarifa.getMonto().multiply(BigDecimal.valueOf(dto.getMeses().length));
        if (dto.getMontoPagado().compareTo(montoEsperado) != 0) {
            throw new BusinessException(
                    "El monto pagado no coincide con la tarifa multiplicada por la cantidad de meses.", "montoPagado");
        }

        // Obtener la fecha mínima permitida
        LocalDate fechaPagoCorrespondiente = (LocalDate) getFechaCorrespondienteDePagoParqueo(cliente.getId());

        // Validar que todos los meses sean posteriores o iguales a la fecha de pago
        // mínima
        LocalDate nuevaFechaPagoCorrespondiente = fechaPagoCorrespondiente.withDayOfMonth(1);
        List<LocalDate> mesesInvalidos = Arrays.stream(dto.getMeses())
                .filter(mes -> mes.isBefore(nuevaFechaPagoCorrespondiente))
                .toList();
        
        System.out.println(nuevaFechaPagoCorrespondiente);
        System.out.println(mesesInvalidos.toString());

        if (!mesesInvalidos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Alguno(s) de los meses ya han sido pagados. Meses inválidos: " + mesesInvalidos);
        }

        if (!dto.getMeses()[0].equals(nuevaFechaPagoCorrespondiente)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El primer mes a pagar no es el correspondiente. Mes inválido: " + dto.getMeses()[0].toString());
        }

        pagoParqueoEntity.setNroEspacioPagado(cliente.getParqueos().getNroEspacio());
        pagoParqueoEntity.setTarifa(tarifa);
        pagoParqueoEntity.setParqueo(cliente.getParqueos());
        pagoParqueoEntity.setCajero(cajero);

        return pagoParqueoRepository.save(pagoParqueoEntity);
    }

    public Object getFechaCorrespondienteDePagoParqueo(UUID clienteId) {
        Object result = pagoParqueoRepository.obtenerUltimoPago(clienteId);

        if (result instanceof Object[] row) {
            LocalDate fechaInicio = LocalDate.parse(row[0].toString());
            LocalDate ultimoMesPagado = row[1] != null ? LocalDate.parse(row[1].toString()) : null;

            if (ultimoMesPagado == null || fechaInicio.isAfter(ultimoMesPagado)) {
                return fechaInicio;
            } else {
                return ultimoMesPagado.plusMonths(1);
            }
        }

        if (result == null)
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "El cliente no tiene asociado el parqueo seleccionado a su cuenta.");

        return null;
    }
}
