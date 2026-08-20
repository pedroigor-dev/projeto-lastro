package dev.pedrocosta.vertex.application.port;

import dev.pedrocosta.vertex.domain.ChangeSpec;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChangeSpecRepository {

    ChangeSpec save(ChangeSpec specification);

    Optional<ChangeSpec> findById(UUID id);

    List<ChangeSpec> findAll();
}
