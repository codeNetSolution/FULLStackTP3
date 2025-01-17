package fr.fullstack.shopapp.component;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {

    private final SequenceSynchronizer sequenceSynchronizer;

    public StartupRunner(SequenceSynchronizer sequenceSynchronizer) {
        this.sequenceSynchronizer = sequenceSynchronizer;
    }

    @Override
    public void run(String... args) throws Exception {
        sequenceSynchronizer.synchronizeSequences();
    }
}
