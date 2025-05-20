package backendProyectoParqueo.utils;

import backendProyectoParqueo.model.TipoCliente;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TipoClienteConverter implements AttributeConverter<TipoCliente, String> {

    @Override
    public String convertToDatabaseColumn(TipoCliente tipoCliente) {
        return tipoCliente.getDbValue();
    }

    @Override
    public TipoCliente convertToEntityAttribute(String dbValue) {
        return TipoCliente.fromDbValue(dbValue);
    }
}
