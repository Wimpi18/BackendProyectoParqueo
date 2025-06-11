package backendProyectoParqueo.converter;

import backendProyectoParqueo.enums.TipoCliente;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class TipoClienteConverter implements Converter<String, TipoCliente> {
    @Override
    public TipoCliente convert(@NonNull String source) {
        return TipoCliente.fromLabel(source);
    }
}
