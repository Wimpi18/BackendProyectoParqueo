package backendProyectoParqueo.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet; // Necesitas este repo
import java.util.List;
import java.util.Set; // Para excepciones
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backendProyectoParqueo.dto.DetalleMesEstadoCuentaDTO;
import backendProyectoParqueo.dto.ReporteEstadoCuentaVehiculoDTO;
import backendProyectoParqueo.dto.VehiculoParqueoActivoDTO;
import backendProyectoParqueo.model.PagoParqueo;
import backendProyectoParqueo.model.Parqueo;
import backendProyectoParqueo.model.Tarifa;
import backendProyectoParqueo.repository.PagoParqueoRepository;
import backendProyectoParqueo.repository.ParqueoRepository;
import backendProyectoParqueo.repository.TarifaRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ReporteService {

    private final ParqueoRepository parqueoRepository;
    private final PagoParqueoRepository pagoParqueoRepository;
    private final TarifaRepository tarifaRepository; // Inyectar TarifaRepository

    @Autowired
    public ReporteService(ParqueoRepository parqueoRepository,
            PagoParqueoRepository pagoParqueoRepository,
            TarifaRepository tarifaRepository) {
        this.parqueoRepository = parqueoRepository;
        this.pagoParqueoRepository = pagoParqueoRepository;
        this.tarifaRepository = tarifaRepository;
    }

    @Transactional(readOnly = true)
    public List<VehiculoParqueoActivoDTO> getVehiculosActivosParaReporte(UUID clienteId) {
        return parqueoRepository.findVehiculosActivosByClienteId(clienteId);
    }

    @Transactional(readOnly = true)
    public ReporteEstadoCuentaVehiculoDTO getEstadoCuentaVehiculo(UUID clienteId, String placa) {
        Parqueo parqueo = parqueoRepository.findActivoByClienteIdAndVehiculoPlacaWithDetails(clienteId, placa)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Parqueo activo no encontrado para el cliente " + clienteId + " y placa " + placa));

        List<PagoParqueo> pagosDelParqueo = pagoParqueoRepository.findAllByParqueoIdWithTarifa(parqueo.getId());

        ReporteEstadoCuentaVehiculoDTO reporte = new ReporteEstadoCuentaVehiculoDTO();
        reporte.setPlacaVehiculo(parqueo.getVehiculo().getPlaca());
        reporte.setTipoCliente(parqueo.getCliente().getTipo());
        reporte.setTipoVehiculo(parqueo.getVehiculo().getTipo());
        reporte.setUltimaActualizacion(LocalDateTime.now());

        List<DetalleMesEstadoCuentaDTO> detallesMes = new ArrayList<>();
        Set<YearMonth> mesesPagadosRegistrados = new HashSet<>();
        DateTimeFormatter periodoFormatter = DateTimeFormatter.ofPattern("MM/yyyy");

        // Procesar meses pagados
        for (PagoParqueo pago : pagosDelParqueo) {
            if (pago.getMeses() != null && pago.getMeses().length > 0) {
                // El monto_pagado en la tabla es el total por ESE pago.
                // Si el pago cubre varios meses, el monto por mes es monto_pagado /
                // cantidad_de_meses_en_ese_pago
                BigDecimal montoPorMesEnEstePago = BigDecimal.valueOf(pago.getMontoPagado())
                        .divide(BigDecimal.valueOf(pago.getMeses().length), 2, BigDecimal.ROUND_HALF_UP);

                for (Date mesSqlDate : pago.getMeses()) {
                    LocalDate mesLocalDate = mesSqlDate.toLocalDate();
                    YearMonth periodoPago = YearMonth.from(mesLocalDate);
                    mesesPagadosRegistrados.add(periodoPago);

                    detallesMes.add(new DetalleMesEstadoCuentaDTO(
                            periodoPago.format(periodoFormatter),
                            "Pagado",
                            montoPorMesEnEstePago, // Usar el monto por mes calculado
                            pago.getFechaHoraPago() != null ? pago.getFechaHoraPago().toLocalDateTime() : null));
                }
            }
        }

        // Determinar meses pendientes y calcular saldo total pendiente
        BigDecimal saldoTotalPendiente = BigDecimal.ZERO;
        LocalDate fechaInicioParqueo = parqueo.getFechaInicio();
        YearMonth mesInicioParqueo = YearMonth.from(fechaInicioParqueo);
        YearMonth mesActual = YearMonth.now();

        Tarifa tarifaAplicable = tarifaRepository.obtenerTarifaVigente(
                parqueo.getCliente().getTipo(),
                parqueo.getVehiculo().getTipo());

        if (tarifaAplicable == null) {
            // Log o advertencia: No se puede calcular monto pendiente si no hay tarifa
            System.err.println("Advertencia: No se encontró tarifa vigente para cliente tipo " +
                    parqueo.getCliente().getTipo() + " y vehículo tipo " +
                    parqueo.getVehiculo().getTipo() + ". Los montos pendientes pueden no ser precisos.");
        }

        YearMonth mesIterador = mesInicioParqueo;
        while (!mesIterador.isAfter(mesActual)) {
            if (!mesesPagadosRegistrados.contains(mesIterador)) {
                BigDecimal montoEsteMesPendiente = BigDecimal.ZERO;
                if (tarifaAplicable != null) {
                    montoEsteMesPendiente = tarifaAplicable.getMonto();
                }
                // Si no hay tarifa, el monto pendiente será 0, o podrías manejarlo de otra
                // forma

                detallesMes.add(new DetalleMesEstadoCuentaDTO(
                        mesIterador.format(periodoFormatter),
                        "Pendiente",
                        montoEsteMesPendiente,
                        null // No hay fecha de pago para pendientes
                ));
                saldoTotalPendiente = saldoTotalPendiente.add(montoEsteMesPendiente);
            }
            mesIterador = mesIterador.plusMonths(1);
        }

        // Ordenar la lista de detalles por periodo
        detallesMes.sort(Comparator.comparing(detalle -> YearMonth.parse(detalle.getPeriodo(), periodoFormatter)));

        reporte.setDetallesMes(detallesMes);
        reporte.setSaldoTotalPendiente(saldoTotalPendiente);

        return reporte;
    }
}