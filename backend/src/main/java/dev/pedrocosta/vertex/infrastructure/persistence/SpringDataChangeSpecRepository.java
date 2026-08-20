package dev.pedrocosta.vertex.infrastructure.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataChangeSpecRepository extends JpaRepository<ChangeSpecEntity, UUID> {
}
