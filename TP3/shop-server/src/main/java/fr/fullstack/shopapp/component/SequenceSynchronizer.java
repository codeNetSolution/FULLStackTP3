package fr.fullstack.shopapp.component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
public class SequenceSynchronizer {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void synchronizeSequences() {
        String[] tables = {"shops", "categories", "localized_product", "opening_hours", "products", "translation"};
        for (String table : tables) {
            String sequenceName = table + "_id_seq";
            String sql = String.format(
                    "DO $$ BEGIN " +
                            "   PERFORM setval('%s', COALESCE((SELECT MAX(id) FROM %s), 1) + 1, false); " +
                            "END $$;",
                    sequenceName, table
            );
            entityManager.createNativeQuery(sql).executeUpdate();
        }
    }
}
