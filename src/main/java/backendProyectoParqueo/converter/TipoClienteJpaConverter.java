package backendProyectoParqueo.converter;

import backendProyectoParqueo.enums.TipoCliente;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TipoClienteJpaConverter implements AttributeConverter<TipoCliente, String> {

    @Override
    public String convertToDatabaseColumn(TipoCliente tipoCliente) {
        return tipoCliente != null ? tipoCliente.getLabel() : null;
    }

    @Override
    public TipoCliente convertToEntityAttribute(String dbData) {
        return dbData != null ? TipoCliente.fromLabel(dbData) : null;
    }
}
